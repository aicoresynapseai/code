gcp-tf-cloudbuild-provisioner

This project demonstrates how to automate Google Cloud Platform (GCP) infrastructure provisioning using a Cloud Build pipeline that orchestrates Terraform. It sets up a basic GKE (Google Kubernetes Engine) cluster and a Cloud SQL (PostgreSQL) instance.

Purpose:
The primary goal is to illustrate a Continuous Delivery (CD) pipeline for Infrastructure as Code (IaC) on GCP. Changes to your Terraform configurations can be automatically applied by Cloud Build, ensuring infrastructure consistency and reducing manual effort.

Components:
*   Cloud Build: GCP's serverless CI/CD platform.
*   Terraform: HashiCorp's IaC tool for provisioning and managing cloud resources.
*   Google Kubernetes Engine (GKE): Managed Kubernetes service.
*   Cloud SQL: Managed relational database service.
*   Google Cloud Storage (GCS): Used as a backend for Terraform state files.

Prerequisites:
Before you begin, ensure you have the following:

1.  A GCP Project: Note your Project ID.
2.  gcloud CLI: Installed and authenticated to your GCP project.
3.  APIs Enabled:
    *   Cloud Build API
    *   Container Registry API (needed for Cloud Build images)
    *   Cloud Source Repositories API (if you plan to trigger from there)
    *   Kubernetes Engine API
    *   Cloud SQL Admin API
    *   Compute Engine API
    *   Service Usage API
4.  Terraform State Backend Bucket: A GCS bucket dedicated to storing Terraform state. This bucket must be created manually *before* running Cloud Build.
    *   Example command:
        gcloud storage buckets create gs://your-terraform-state-bucket --project=your-gcp-project-id --uniform-bucket-level-access --location=US
5.  Cloud Build Service Account Permissions: The Cloud Build service account (typically PROJECT_NUMBER@cloudbuild.gserviceaccount.com) needs the following IAM roles on your project:
    *   Storage Object Admin (for the Terraform state bucket)
    *   Kubernetes Engine Admin
    *   Cloud SQL Admin
    *   Compute Network Admin (required for GKE to manage networks and IP ranges)
    *   Service Account User (required for GKE node pools to use service accounts)

Setup and Deployment:

1.  Clone the repository:
    git clone https://github.com/your-repo/gcp-tf-cloudbuild-provisioner.git
    cd gcp-tf-cloudbuild-provisioner

2.  Configure Terraform Variables:
    *   Navigate to the terraform/ directory:
        cd terraform/
    *   Copy the sample variables file:
        cp terraform.tfvars.sample terraform.tfvars
    *   Edit terraform.tfvars: Open terraform.tfvars and update the values for region, zone, gke_cluster_name, sql_instance_name, etc., according to your preferences. The project_id will be passed directly by Cloud Build.

3.  Trigger the Cloud Build Pipeline:
    *   Go back to the root of the project:
        cd ..
    *   Replace YOUR_GCP_PROJECT_ID with your actual project ID and YOUR_TF_STATE_BUCKET_NAME with the name of the GCS bucket you created for Terraform state.
    *   Initiate the build:
        gcloud builds submit --project=YOUR_GCP_PROJECT_ID --substitutions=_TF_STATE_BUCKET=YOUR_TF_STATE_BUCKET_NAME

    Cloud Build will:
    *   Initialize Terraform (connecting to the GCS backend).
    *   Plan the infrastructure changes.
    *   Apply the Terraform configuration, creating the GKE cluster and Cloud SQL instance.

    You can monitor the build progress in the GCP Console under Cloud Build > History.

4.  Verify Resources:
    Once the build completes successfully, you can verify the created resources in the GCP Console:
    *   GKE Clusters: Navigation menu > Kubernetes Engine > Clusters
    *   Cloud SQL Instances: Navigation menu > Databases > SQL

Cleanup:
To destroy the created infrastructure, you can manually trigger a Cloud Build with a 'destroy' step or modify cloudbuild.yaml to include a destroy phase. For a quick cleanup:

1.  Modify cloudbuild.yaml:
    *   Open cloudbuild.yaml.
    *   Comment out or remove the 'apply' step.
    *   Add a new step for 'terraform destroy' *after* the 'init' step.
        For example, replace the `Terraform Apply` step with:
        - name: 'gcr.io/cloud-builders/terraform'
          id: 'Terraform Destroy'
          args: ['destroy', '-auto-approve']
          dir: 'terraform'
          env:
            - 'GOOGLE_CLOUD_PROJECT=${PROJECT_ID}'

2.  Trigger the Cloud Build again:
    gcloud builds submit --project=YOUR_GCP_PROJECT_ID --substitutions=_TF_STATE_BUCKET=YOUR_TF_STATE_BUCKET_NAME

    **Caution**: This will permanently delete your GKE cluster and Cloud SQL instance, including all data. Ensure you have backed up any critical data before proceeding.

Troubleshooting:
*   Permissions Errors: Ensure your Cloud Build service account has all the necessary IAM roles as listed in the 'Prerequisites' section.
*   Terraform State Issues: If the build fails due to state locking or consistency issues, check the GCS bucket for lock files or manually resolve state conflicts if any.
*   Timeout Errors: Complex infrastructure provisioning can take time. Cloud Build steps have a default timeout. If needed, you can increase the timeout in cloudbuild.yaml for specific steps.