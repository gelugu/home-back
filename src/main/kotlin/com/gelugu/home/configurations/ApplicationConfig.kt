package com.gelugu.home.configurations

object ApplicationConfig {
  // application
  val serverPort = System.getenv("SERVER_PORT") ?: "8080"
  var telegramBotToken = System.getenv("TELEGRAM_BOT_API") ?: ""
  var telegramChatId = System.getenv("TELEGRAM_CHAT_ID") ?: ""
  const val codeExpirationTime = 300000 // 5 minutes in milliseconds
  const val tokenExpirationTime = 3.6e+6 // 5 minutes in milliseconds

  // database
  val dbUrl = System.getenv("DB_URL") ?: "localhost"
  val dbPort = System.getenv("DB_PORT") ?: "5432"
  val dbName = System.getenv("DB_NAME") ?: "home-postgres-db"
  val dbUser = System.getenv("DB_USER") ?: "home-postgres-user"
  val dbPassword = System.getenv("DB_PASSWORD") ?: "home-postgres-pass"
}
