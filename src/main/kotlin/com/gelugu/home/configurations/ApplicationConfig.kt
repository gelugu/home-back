package com.gelugu.home.configurations

import java.util.regex.Pattern

object ApplicationConfig {
  // application
  val serverPort = System.getenv("SERVER_PORT") ?: "8080"
  const val tokenExpirationTime = 600000 // 10 minutes in milliseconds
  const val codeExpirationTime = 300000 // 5 minutes in milliseconds
  val loginRegex = Pattern.compile("^[a-zA-Z][a-zA-Z\\d-_]$")
  val passwordRegex = Pattern.compile("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#()\$%!\\-_?&]).{8,24}$")

  // JWT
  val jwtSecret = System.getenv("JWT_SECRET") ?: "secret"

  // database
  val dbUrl = System.getenv("DB_URL") ?: "localhost"
  val dbPort = System.getenv("DB_PORT") ?: "5432"
  val dbName = System.getenv("DB_NAME") ?: "home-postgres-db"
  val dbUser = System.getenv("DB_USER") ?: "home-postgres-user"
  val dbPassword = System.getenv("DB_PASSWORD") ?: "pass"
}
