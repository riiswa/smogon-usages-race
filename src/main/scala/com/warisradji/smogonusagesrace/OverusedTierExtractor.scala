package com.warisradji.smogonusagesrace

object OverusedTierExtractor extends App with ExtractorAncestor {
  extract("assets/ou-1825.json", List(x => x.contains("1825"), x => !x.contains("double")))
}
