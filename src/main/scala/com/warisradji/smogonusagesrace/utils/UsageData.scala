package com.warisradji.smogonusagesrace.utils

case class UsageData(date: String, pokemon: String, value: Int) {
  def serialize: String = s"""{"date": "$date", "pokemon": "$pokemon", "value": $value}"""
}
