# This file defines input variables for the Terraform configuration.

# GCP Project ID:
# This variable receives its value from the GOOGLE_CLOUD_PROJECT environment variable,
# which Cloud Build automatically passes based on the project where the build runs.
variable "project_id" {
  description = "The GCP project ID where resources will be created. Auto-detected by Cloud Build."
  type        = string
}

# GCP Region: For regional resources like GKE clusters (regional) and Cloud SQL instances.
variable "region" {
  description = "The GCP region to deploy resources (e.g., us-central1)."
  type        = string
  default     = "us-central1"
}

# GCP Zone: For zonal resources or where a zone is required (e.g., GKE node pools default location).
variable "zone" {
  description = "The GCP zone to deploy zonal resources (e.g., us-central1-c). Required for GKE node pools."
  type        = string
  default     = "us-central1-c"
}

# --- GKE Cluster Variables ---
variable "gke_cluster_name" {
  description = "Name for the GKE cluster."
  type        = string
  default     = "my-cloudbuild-gke-cluster"
}

variable "gke_node_count" {
  description = "Number of nodes in the GKE cluster's default node pool."
  type        = number
  default     = 1
}

variable "gke_node_machine_type" {
  description = "Machine type for GKE cluster nodes (e.g., e2-medium)."
  type        = string
  default     = "e2-medium"
}

variable "gke_node_disk_size_gb" {
  description = "Disk size in GB for GKE cluster nodes."
  type        = number
  default     = 50
}

# --- Cloud SQL Variables ---
variable "sql_instance_name" {
  description = "Name for the Cloud SQL instance."
  type        = string
  default     = "my-cloudbuild-sql-instance"
}

variable "sql_database_version" {
  description = "Database version for Cloud SQL instance (e.g., POSTGRES_14, MYSQL_8_0)."
  type        = string
  default     = "POSTGRES_14"
}

variable "sql_instance_tier" {
  description = "Machine type for the Cloud SQL instance (e.g., db-f1-micro, db-g1-small)."
  type        = string
  default     = "db-f1-micro"
}

variable "sql_disk_size_gb" {
  description = "Disk size in GB for Cloud SQL instance."
  type        = number
  default     = 20
}

variable "sql_disk_type" {
  description = "Disk type for Cloud SQL instance (e.g., PD_SSD, PD_HDD)."
  type        = string
  default     = "PD_SSD"
}

variable "sql_database_name" {
  description = "Name of the database to create within the Cloud SQL instance."
  type        = string
  default     = "app_db"
}

variable "sql_username" {
  description = "Username for the Cloud SQL database user."
  type        = string
  default     = "app_user"
}

variable "sql_password" {
  description = "Password for the Cloud SQL database user. **WARNING: For production, use Secret Manager!**"
  type        = string
  default     = "securepassword123" # CHANGE THIS FOR PRODUCTION!
  sensitive   = true # Mark as sensitive so it's not shown in logs/outputs
}