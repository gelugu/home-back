package com.gelugu.home.plugins

import com.gelugu.home.configurations.ApplicationConfig
import org.jetbrains.exposed.sql.Database

fun connectDatabase() {
  Database.connect(
    url = "jdbc:postgresql://${ApplicationConfig.dbUrl}:${ApplicationConfig.dbPort}/${ApplicationConfig.dbName}",
    driver = "org.postgresql.Driver",
    user = ApplicationConfig.dbUser,
    password = ApplicationConfig.dbPassword
  )
}