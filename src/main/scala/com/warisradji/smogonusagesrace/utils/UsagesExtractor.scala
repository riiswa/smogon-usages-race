package com.warisradji.smogonusagesrace.utils

import java.io.{BufferedWriter, File, FileWriter}
import scala.io.Source

object UsagesExtractor {
  val smogonStatsUrl = "https://www.smogon.com/stats/"

  def getFolders: List[String] = Source.fromURL(smogonStatsUrl).getLines().collect {
    case line if line.contains("href") =>
      """"((?:[^"\\]|\\[\\"ntbrf])+)"""".r.findFirstMatchIn(line).map(_.toString.drop(1).dropRight(1))
  }.toList.flatten.tail

  def getUsagesDataFiles(folders: List[String])(filters: List[String => Boolean]): List[(String, String)] = folders.map {
    date =>
      date -> Source.fromURL(smogonStatsUrl + date).getLines().collect {
        case line if line.contains(".txt") =>
          """"((?:[^"\\]|\\[\\"ntbrf])+)"""".r.findFirstMatchIn(line).map(_.toString.drop(1).dropRight(1))
      }.toList.flatten.find(s => filters.forall(_ (s))).getOrElse("")
  }

  def parseData(url: String, date: String, limit: Int = 50): List[UsageData] =
    Source.fromURL(url).getLines().toList.slice(5, limit + 5).map(l => {
      val data = l.split('|')
      UsageData(date, data(2).trim, data(4).trim.toInt)
    })


  def getUsagesData(usagesDataFiles: List[(String, String)], limit: Int = 50): List[UsageData] = usagesDataFiles.flatMap {
    case (date, fileName) => parseData(smogonStatsUrl + date + fileName, date.init, limit)
  }

  def write(fileName: String, ud: List[UsageData]): Unit = {
    val file = new File(fileName)
    val bw = new BufferedWriter(new FileWriter(file))
    bw.write(ud.map(_.serialize).mkString("[", ",", "]"))
    bw.close()
  }
}
