package com.gelugu.home.plugins

import com.gelugu.home.configurations.ApplicationConfig

fun checkEnvironment() {
  // application
  try {
    ApplicationConfig.serverPort.toInt()
  } catch (e: NumberFormatException) {
    throw NumberFormatException("Wrong application port \"${ApplicationConfig.serverPort}\"")
  }
}