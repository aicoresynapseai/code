# This script demonstrates uploading and downloading files to a Google Cloud Storage (GCS)
# bucket configured with Customer-Managed Encryption Keys (CMEK).
# The GCS bucket and KMS key should be created via Terraform before running this script.

import os
from google.cloud import storage
from google.cloud import kms_v1

# --- Configuration ---
# Replace with your GCP Project ID
GCP_PROJECT_ID = os.environ.get("GCP_PROJECT_ID", "your-gcp-project-id")
# Replace with the name of your GCS bucket created by Terraform
# You can find this in `terraform output gcs_bucket_name`
GCS_BUCKET_NAME = os.environ.get("GCS_BUCKET_NAME", "cloudsecureencrypt-cmek-data-xxxx")
# Replace with the full resource ID of your KMS CryptoKey created by Terraform
# You can find this in `terraform output kms_key_id`
KMS_KEY_ID = os.environ.get("KMS_KEY_ID", "projects/your-gcp-project-id/locations/us-central1/keyRings/cloudsecureencrypt-cmek-keyring/cryptoKeys/cloudsecureencrypt-cmek-key")


INPUT_FILE_NAME = "gcp_sample_data.txt"
UPLOADED_BLOB_NAME = "encrypted_gcp_sample_data.txt"
DOWNLOADED_FILE_NAME = "gcp_sample_data.txt.downloaded"

# Initialize GCS client
storage_client = storage.Client(project=GCP_PROJECT_ID)

def create_sample_file():
    """Creates a sample text file for upload."""
    if not os.path.exists(INPUT_FILE_NAME):
        with open(INPUT_FILE_NAME, "w") as f:
            f.write("This is confidential data for GCP.\n")
            f.write("It should be protected by CMEK in Google Cloud Storage.\n")
        print(f"Created sample file: {INPUT_FILE_NAME}")
    else:
        print(f"Sample file {INPUT_FILE_NAME} already exists.")

def upload_file_to_gcs_cmek():
    """Uploads a local file to the GCS bucket, protected by CMEK."""
    try:
        bucket = storage_client.bucket(GCS_BUCKET_NAME)
        blob = bucket.blob(UPLOADED_BLOB_NAME)

        print(f"\nUploading {INPUT_FILE_NAME} to gs://{GCS_BUCKET_NAME}/{UPLOADED_BLOB_NAME}")
        print(f"Using CMEK: {KMS_KEY_ID}")

        # When the bucket is configured with default CMEK, specifying `kms_key_name`
        # during upload is not strictly necessary as it will use the bucket's default.
        # However, it explicitly shows that the key is intended to be used.
        blob.upload_from_filename(INPUT_FILE_NAME, kms_key_name=KMS_KEY_ID)

        print(f"File {INPUT_FILE_NAME} uploaded to {UPLOADED_BLOB_NAME} with CMEK enabled.")
        print("Verification: Data uploaded to a bucket with default CMEK will be encrypted using that key.")
        print("Note: The GCS client automatically handles encryption/decryption on the service side.")

    except FileNotFoundError:
        print(f"Error: {INPUT_FILE_NAME} not found. Please create it first or run `create_sample_file()`.")
    except Exception as e:
        print(f"Error during GCS upload: {e}")
        if "google.api_core.exceptions.NotFound" in str(e) and "Bucket" in str(e):
            print(f"Bucket '{GCS_BUCKET_NAME}' not found. Please ensure it exists and is correct.")
        if "google.api_core.exceptions.Forbidden" in str(e):
            print("Permission denied. Ensure your GCP credentials have Storage Admin and KMS CryptoKey Encrypter/Decrypter roles.")
        if "google.api_core.exceptions.FailedPrecondition" in str(e) and "kmsKey" in str(e):
            print(f"KMS Key '{KMS_KEY_ID}' might not be valid or accessible by the Storage service account.")


def download_file_from_gcs_cmek():
    """Downloads a file from the CMEK-enabled GCS bucket."""
    try:
        bucket = storage_client.bucket(GCS_BUCKET_NAME)
        blob = bucket.blob(UPLOADED_BLOB_NAME)

        print(f"\nDownloading {UPLOADED_BLOB_NAME} from gs://{GCS_BUCKET_NAME} to {DOWNLOADED_FILE_NAME}")
        print(f"Using CMEK: {KMS_KEY_ID}")

        # When downloading from a CMEK-enabled bucket, the GCS service handles decryption
        # transparently using the associated CMEK. No explicit key specification needed here.
        blob.download_to_filename(DOWNLOADED_FILE_NAME)

        print(f"File {UPLOADED_BLOB_NAME} downloaded to {DOWNLOADED_FILE_NAME}.")

        print(f"\nContent of downloaded file ({DOWNLOADED_FILE_NAME}):")
        with open(DOWNLOADED_FILE_NAME, "r") as f:
            print(f.read())

    except FileNotFoundError:
        print(f"Error: Blob {UPLOADED_BLOB_NAME} not found in bucket {GCS_BUCKET_NAME}. Please upload it first.")
    except Exception as e:
        print(f"Error during GCS download: {e}")
        if "google.api_core.exceptions.NotFound" in str(e):
            print(f"Blob '{UPLOADED_BLOB_NAME}' not found in bucket '{GCS_BUCKET_NAME}'.")
        if "google.api_core.exceptions.Forbidden" in str(e):
            print("Permission denied. Ensure your GCP credentials have Storage Object Viewer and KMS CryptoKey Encrypter/Decrypter roles.")

def cleanup_files_and_blob():
    """Deletes local files and the uploaded blob from GCS."""
    files_to_delete = [INPUT_FILE_NAME, DOWNLOADED_FILE_NAME]
    print("\nCleaning up local files...")
    for filename in files_to_delete:
        if os.path.exists(filename):
            os.remove(filename)
            print(f"Deleted local file: {filename}")
        else:
            print(f"{filename} not found, skipping.")

    print(f"\nAttempting to delete blob '{UPLOADED_BLOB_NAME}' from GCS bucket '{GCS_BUCKET_NAME}'...")
    try:
        bucket = storage_client.bucket(GCS_BUCKET_NAME)
        blob = bucket.blob(UPLOADED_BLOB_NAME)
        if blob.exists():
            blob.delete()
            print(f"Deleted GCS blob: {UPLOADED_BLOB_NAME}")
        else:
            print(f"GCS blob {UPLOADED_BLOB_NAME} not found, skipping deletion.")
    except Exception as e:
        print(f"Error deleting GCS blob: {e}")
        print("Ensure your GCP credentials have Storage Object Admin permissions.")

if __name__ == "__main__":
    print("--- GCP GCS CMEK Encryption Demo ---")
    print(f"Using GCP Project ID: {GCP_PROJECT_ID}")
    print(f"Using GCS Bucket Name: {GCS_BUCKET_NAME}")
    print(f"Using KMS Key ID: {KMS_KEY_ID}")

    if "your-gcp-project-id" in GCP_PROJECT_ID or "cloudsecureencrypt-cmek-data-xxxx" in GCS_BUCKET_NAME or "your-gcp-project-id" in KMS_KEY_ID:
        print("\nWARNING: GCP_PROJECT_ID, GCS_BUCKET_NAME, or KMS_KEY_ID are still using placeholder values.")
        print("Please set these environment variables or modify the script with your actual values.")
        print("Example for environment variables:")
        print("  export GCP_PROJECT_ID=your-actual-project-id")
        print("  export GCS_BUCKET_NAME=$(terraform output -raw gcs_bucket_name)")
        print("  export KMS_KEY_ID=$(terraform output -raw kms_key_id)")
        sys.exit(1)

    while True:
        print("\n--- Menu ---")
        print("1. Create local sample file")
        print("2. Upload file to GCS (CMEK-enabled bucket)")
        print("3. Download file from GCS")
        print("4. Cleanup local files and GCS blob")
        print("5. Exit")

        choice = input("Enter your choice: ")

        if choice == '1':
            create_sample_file()
        elif choice == '2':
            upload_file_to_gcs_cmek()
        elif choice == '3':
            download_file_from_gcs_cmek()
        elif choice == '4':
            cleanup_files_and_blob()
        elif choice == '5':
            print("Exiting.")
            break
        else:
            print("Invalid choice. Please try again.")