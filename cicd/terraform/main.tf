terraform {
  required_providers {
    yandex = {
      source  = "yandex-cloud/yandex"
      version = "~> 0.77.0"
    }
  }
}

locals {
  zone      = "ru-central1-b"
  folder_id = "b1grcftunkvuadhl5368"

  username        = "gelugu"
  public_key_path = "./yandex-cloud.pub"

  network_id = "enpkvkbo6ov19dt8mp1q"
  subnet_id  = "e2lka90u06fgfcv5m6qs"

  # platform ids for compute instances
  platforms = {
    IntelBroadwell   = "standard-v1"
    IntelCascadeLake = "standard-v2"
    IntelIceLake     = "standard-v3"
  }

  service_name = "home"
}

provider "yandex" {
  cloud_id  = "cloud-gelugu"
  folder_id = "default"
  zone      = local.zone
}

resource "yandex_mdb_postgresql_cluster" "home_db" {
  name        = "${local.service_name}-db"
  description = "${local.service_name} service database"

  folder_id = local.folder_id

  environment         = "PRODUCTION"
  network_id          = local.network_id
  deletion_protection = false

  config {
    version                   = "14"
    backup_retain_period_days = 7
    resources {
      disk_type_id       = "network-hdd"
      resource_preset_id = "b2.nano"
      disk_size          = 10
    }

    access {
      data_lens  = true
      serverless = false
      web_sql    = true
    }

    backup_window_start {
      hours   = 22
      minutes = 0
    }
  }

  host {
    zone      = local.zone
    subnet_id = local.subnet_id
    priority  = 0
  }
}

resource "yandex_mdb_postgresql_database" "home_db" {
  cluster_id = yandex_mdb_postgresql_cluster.home_db.id
  name       = "${local.service_name}-db"
  owner      = yandex_mdb_postgresql_user.home_db_user.name
}

resource "yandex_mdb_postgresql_user" "home_db_user" {
  cluster_id = yandex_mdb_postgresql_cluster.home_db.id
  name       = "${local.service_name}-db"
  password   = "548246232"
}
