package com.warisradji.smogonusagesrace

import com.warisradji.smogonusagesrace.utils.UsagesExtractor
import zio.{ExitCode, URIO, ZIO}

object RunExtractions extends zio.App {
  def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = ZIO.collectAllPar(Array[UsagesExtractor](
    UsagesExtractor(
      "public/data/ou-1825.json",
      List(x => x.contains("ou"), x => x.contains("1825"), x => !x.contains("doubles")), 1
    )
  ).map(_.extract)).exitCode
}
