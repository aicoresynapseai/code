# This file defines the output values that will be displayed after Terraform applies the configuration.

# Output for GKE Cluster
output "gke_cluster_name" {
  description = "The name of the provisioned GKE cluster."
  value       = google_container_cluster.primary_gke_cluster.name
}

output "gke_cluster_endpoint" {
  description = "The endpoint of the GKE cluster's control plane."
  value       = google_container_cluster.primary_gke_cluster.endpoint
}

# Note: Master auth username/password are deprecated and less secure for production.
# They are included here for completeness but ideally, access should be managed via IAM.
output "gke_cluster_master_auth_username" {
  description = "The username for master authentication (if enabled, usually deprecated)."
  value       = google_container_cluster.primary_gke_cluster.master_auth[0].username
  sensitive   = true # Mark as sensitive to prevent logging
}

output "gke_cluster_master_auth_password" {
  description = "The password for master authentication (if enabled, usually deprecated)."
  value       = google_container_cluster.primary_gke_cluster.master_auth[0].password
  sensitive   = true # Mark as sensitive to prevent logging
}

# Output for Cloud SQL Instance
output "sql_instance_connection_name" {
  description = "The connection name of the Cloud SQL instance (ProjectID:Region:InstanceName)."
  value       = google_sql_database_instance.main_sql_instance.connection_name
}

output "sql_instance_public_ip_address" {
  description = "The public IP address of the Cloud SQL instance (if enabled)."
  value       = google_sql_database_instance.main_sql_instance.public_ip_address
}

output "sql_database_name" {
  description = "The name of the database created in Cloud SQL."
  value       = google_sql_database.app_database.name
}

output "sql_username" {
  description = "The username for the Cloud SQL database user."
  value       = google_sql_user.app_user.name
}