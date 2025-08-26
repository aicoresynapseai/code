# This Terraform configuration sets up a GCP KMS KeyRing, a Key,
# and a Google Cloud Storage (GCS) bucket configured to use that CMEK.

# Configure the Google Cloud provider
provider "google" {
  project = var.gcp_project_id # Replace with your GCP Project ID
  region  = var.gcp_region     # You can change your preferred GCP region here
}

# Define variables for project ID and region
variable "gcp_project_id" {
  description = "Your Google Cloud Project ID"
  type        = string
  # IMPORTANT: Replace "your-gcp-project-id" with your actual GCP Project ID
  default     = "your-gcp-project-id" 
}

variable "gcp_region" {
  description = "Your Google Cloud Region"
  type        = string
  default     = "us-central1" # You can change this to your desired region
}

# 1. Create a GCP KMS KeyRing
# KeyRings are a logical grouping of keys. They are specific to a location.
resource "google_kms_key_ring" "cmek_keyring" {
  name     = "cloudsecureencrypt-cmek-keyring"
  location = var.gcp_region # The KeyRing must be in the same region as the services it protects

  project = var.gcp_project_id
}

# 2. Create a GCP KMS CryptoKey (CMEK) within the KeyRing
# This is the actual encryption key that will be used.
resource "google_kms_crypto_key" "cmek_key" {
  name            = "cloudsecureencrypt-cmek-key"
  key_ring        = google_kms_key_ring.cmek_keyring.id
  rotation_period = "100000s" # Rotate key every ~27 hours (for demo, can be longer)
  version_template {
    algorithm        = "GOOGLE_SYMMETRIC_ENCRYPTION" # Standard symmetric encryption algorithm
    protection_level = "SOFTWARE"                   # Software-backed key
  }
}

# 3. Create a Google Cloud Storage (GCS) bucket
# This bucket will be configured to use the CMEK for default encryption.
resource "google_storage_bucket" "cmek_bucket" {
  name                        = "cloudsecureencrypt-cmek-data-${random_id.bucket_suffix.hex}" # Unique bucket name
  location                    = var.gcp_region
  project                     = var.gcp_project_id
  uniform_bucket_level_access = true # Recommended for security
  force_destroy               = true # Allows deleting non-empty buckets for easy cleanup

  # Configure default server-side encryption for the GCS bucket using CMEK
  encryption {
    default_kms_key_name = google_kms_crypto_key.cmek_key.id # Link to our CMEK key
  }

  labels = {
    environment = "demo"
    purpose     = "cmek"
  }
}

# Add a random suffix to the bucket name to ensure global uniqueness
resource "random_id" "bucket_suffix" {
  byte_length = 8
}

# Output the name of the KMS key and the GCS bucket name
output "kms_key_id" {
  description = "The ID of the GCP KMS CryptoKey (CMEK)."
  value       = google_kms_crypto_key.cmek_key.id
}

output "gcs_bucket_name" {
  description = "The name of the GCS bucket configured with CMEK."
  value       = google_storage_bucket.cmek_bucket.name
}