# This file defines the GCP infrastructure resources to be provisioned.

# --- GKE Cluster ---
resource "google_container_cluster" "primary_gke_cluster" {
  name     = var.gke_cluster_name
  location = var.region # For regional cluster, location is region; for zonal, it's zone.
  project  = var.project_id

  # Define the default node pool configuration.
  # For more granular control, consider creating a separate `google_container_node_pool` resource.
  initial_node_count = var.gke_node_count
  
  node_config {
    machine_type = var.gke_node_machine_type
    disk_size_gb = var.gke_node_disk_size_gb
    
    # OAuth scopes for node VMs. Default scopes are usually sufficient for basic operations.
    oauth_scopes = [
      "https://www.googleapis.com/auth/cloud-platform" # Broad scope for demonstration.
                                                       # In production, use specific scopes like compute.readonly, logging.write, monitoring.write.
    ]
  }

  # Enable workload identity for secure access to GCP services from pods (recommended for production).
  # This requires the GKE cluster to be created with Workload Identity enabled.
  workload_identity_config {
    identity_namespace = "${var.project_id}.svc.id.goog"
  }

  # Release channel for automatic cluster upgrades.
  release_channel {
    channel = "REGULAR" # Other options: "STABLE", "RAPID"
  }

  # Configure logging and monitoring components.
  logging_config {
    enable_components = ["SYSTEM_COMPONENTS", "WORKLOADS"]
  }

  monitoring_config {
    enable_components = ["SYSTEM_COMPONENTS"]
  }

  # Example of private cluster configuration (commented out by default for simplicity)
  # enable_private_endpoint = true # Private endpoint for the control plane
  # enable_private_nodes    = true # Nodes only have private IPs
  # master_ipv4_cidr_block  = "172.16.0.0/28" # CIDR range for the master's private IP (must not overlap with VPC network)
  # ip_allocation_policy { # Required for VPC-native clusters
  #   cluster_ipv4_cidr_block = "/19"
  #   services_ipv4_cidr_block = "/22"
  # }

  # By default, GKE uses the default VPC network and subnetwork.
  # For production, define and use a custom VPC network and subnets.
  # network    = google_compute_network.custom_vpc.id
  # subnetwork = google_compute_subnetwork.custom_subnet.id
}

# --- Cloud SQL Instance (PostgreSQL) ---
resource "google_sql_database_instance" "main_sql_instance" {
  name             = var.sql_instance_name
  database_version = var.sql_database_version
  region           = var.region
  project          = var.project_id

  # Tier defines the machine type and resources for the instance.
  # See https://cloud.google.com/sql/docs/postgres/instance-settings#machine-type-2ndgen
  settings {
    tier      = var.sql_instance_tier
    disk_size = var.sql_disk_size_gb
    disk_type = var.sql_disk_type
    
    # Enable automatic backups
    backup_configuration {
      enabled            = true
      binary_log_enabled = true # Required for point-in-time recovery for PostgreSQL
      start_time         = "03:00" # Example: 3 AM UTC
    }

    # IP Configuration: By default, public IP is enabled.
    # To restrict access, configure authorized networks or disable public IP for private IP.
    ip_configuration {
      ipv4_enabled = true # Enable public IP
      # authorized_networks { # Example: Allow access from specific IP range (WARNING: 0.0.0.0/0 allows all)
      #   value = "0.0.0.0/0"
      #   name  = "Allow all - WARNING"
      # }
      # private_network = google_compute_network.your_vpc.id # For private IP setup
      # require_ssl     = true # Enforce SSL connections
    }

    # For high availability (HA)
    # availability_type = "REGIONAL" # Or "ZONAL" for non-HA
  }
}

# Optional: Create a database within the Cloud SQL instance
resource "google_sql_database" "app_database" {
  name      = var.sql_database_name
  instance  = google_sql_database_instance.main_sql_instance.name
  project   = var.project_id
  charset   = "UTF8"
  collation = "en_US.UTF8"
}

# Optional: Create a user for the Cloud SQL instance
resource "google_sql_user" "app_user" {
  name     = var.sql_username
  instance = google_sql_database_instance.main_sql_instance.name
  host     = "%" # Allows access from any host (WARNING: less secure, specify source IP or VPC for production)
  password = var.sql_password # Store securely using Secret Manager in production!
  project  = var.project_id
}