package com.warisradji.smogonusagesrace

import com.warisradji.smogonusagesrace.utils.UsagesExtractor._

trait ExtractorAncestor{
  def extract(outputFile: String, filters: List[String => Boolean], limit: Int = 50): Unit =
    write(outputFile, getUsagesData(getUsagesDataFiles(getFolders)(filters), limit))

}
