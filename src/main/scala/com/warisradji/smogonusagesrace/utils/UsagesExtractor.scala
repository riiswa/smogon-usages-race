package com.warisradji.smogonusagesrace.utils

import zio.{IO, Task}

import java.io.{BufferedWriter, File, FileWriter}
import scala.collection.parallel.CollectionConverters._
import scala.collection.parallel.immutable.ParSeq
import java.net.URL
import sys.process._


case class UsagesExtractor(outputFile: String, filters: List[String => Boolean], limit: Int = 50) {
  private val smogonStatsUrl = "https://www.smogon.com/stats/"

  private val readUrl = (url: String) => new URL(url).cat.lazyLines

  private def getSourceFromUrl: Task[ParSeq[String]] =
    IO.effect(readUrl(smogonStatsUrl).par)

  private def getFoldersFromSource(source: ParSeq[String]): Task[ParSeq[String]] =
    IO.effect(source.collect {
      case line if line.contains("href") =>
        """"((?:[^"\\]|\\[\\"ntbrf])+)"""".r.findFirstMatchIn(line).map(_.toString.drop(1).dropRight(1))
    }.flatten.tail)

  private def getFilesFromFolders(folders: ParSeq[String]): Task[ParSeq[(String, String)]] =
    IO.effect(folders.map {
      date =>
        date -> readUrl(smogonStatsUrl + date).collect {
          case line if line.contains(".txt") =>
            """"((?:[^"\\]|\\[\\"ntbrf])+)"""".r.findFirstMatchIn(line).map(_.toString.drop(1).dropRight(1))
        }.toList.flatten.find(s => filters.forall(_ (s))).getOrElse("")
    })

  private def parseData(url: String, date: String, limit: Int = 50): List[UsageData] =
    readUrl(url).toList.slice(5, limit + 5).map(l => {
      val data = l.split('|')
      UsageData(date, data(2).trim, data(4).trim.toInt)
    })

  private def getUsagesFromFiles(files: ParSeq[(String, String)]): Task[ParSeq[UsageData]] = IO.effect(
    files.flatMap {
      case (date, fileName) => parseData(smogonStatsUrl + date + fileName, date.init, limit)
    })

  private def write(usages: ParSeq[UsageData]): Task[Unit] = IO.effect(new BufferedWriter(new FileWriter(
    new File(outputFile))) {
      write(usages.map(_.serialize).mkString("[", ",", "]"))
    }.close())

  def extract: Task[Unit] = for {
    source <- getSourceFromUrl
    folders <- getFoldersFromSource(source)
    files <- getFilesFromFolders(folders)
    usages <- getUsagesFromFiles(files)
    w <- write(usages)
  } yield w
}

object X extends App {
  //def run(args: List[String]) =
  //  UsagesExtractor("x.json", List(x => x.contains("1825"), x => !x.contains("double"))).extract.exitCode

  println(new URL("https://www.smogon.com/stats/").cat.lazyLines)
}
