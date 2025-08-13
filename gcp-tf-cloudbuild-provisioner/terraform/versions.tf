# This file specifies the required Terraform version and provider configurations.

terraform {
  required_version = ">= 1.0.0" # Specify a minimum required Terraform version

  # Configure the Google Cloud Storage (GCS) backend for storing Terraform state.
  # This centralizes the state file, making it accessible for Cloud Build and team collaboration.
  # The 'bucket' name will be provided dynamically via Cloud Build substitution variable during 'terraform init'.
  backend "gcs" {
    prefix = "terraform/state" # Optional: Prefix for state objects within the bucket
  }

  required_providers {
    google = {
      source  = "hashicorp/google"
      version = "~> 5.0" # Specify a compatible version range for the Google Cloud provider
    }
  }
}

# Configure the Google Cloud provider.
# The 'project' is dynamically set via the GOOGLE_CLOUD_PROJECT environment variable
# that Cloud Build passes to the Terraform steps.
provider "google" {
  project = var.project_id # Referencing the project_id variable defined in variables.tf
  region  = var.region     # Referencing the region variable
  zone    = var.zone       # Referencing the zone variable (for resources requiring a zone, like node pools)
}