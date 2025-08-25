#!/bin/bash
set -eo pipefail

echo "--- AWS S3 Security Demonstrator: Terraform Module ---"

# --- Functions for Terraform operations ---
terraform_action() {
  local dir=$1
  local action=$2
  local skip_confirm=$3

  echo ""
  echo ">>> Entering directory: $dir <<<"
  cd "$dir" || { echo "Error: Directory $dir not found. Exiting."; exit 1; }

  echo "Initializing Terraform for $dir..."
  terraform init

  if [ "$action" == "apply" ]; then
    echo "Applying Terraform configuration for $dir..."
    if [ "$skip_confirm" == "true" ]; then
      terraform apply -auto-approve
    else
      terraform apply
    fi
    echo "Terraform apply completed for $dir."
    terraform output # Show outputs after apply
  elif [ "$action" == "destroy" ]; then
    echo "Destroying Terraform resources for $dir..."
    # Attempt to empty S3 buckets before destroy, especially for versioned buckets
    # This prevents Terraform destroy failures if buckets are not empty.
    BUCKET_NAME=""
    if terraform output -raw "${dir}_bucket_name" >/dev/null 2>&1; then
      BUCKET_NAME=$(terraform output -raw "${dir}_bucket_name")
    fi
    LOG_BUCKET_NAME=""
    if terraform output -raw "log_bucket_name" >/dev/null 2>&1; then
      LOG_BUCKET_NAME=$(terraform output -raw "log_bucket_name")
    fi

    if [ -n "$BUCKET_NAME" ] && aws s3api head-bucket --bucket "$BUCKET_NAME" 2>/dev/null; then
      echo "Emptying main bucket $BUCKET_NAME before destroy..."
      if [ "$dir" == "secure" ]; then
          # For secure bucket, suspend versioning and delete all versions
          echo "Suspending versioning for $BUCKET_NAME to allow deletion of all versions..."
          aws s3api put-bucket-versioning \
              --bucket "$BUCKET_NAME" \
              --versioning-configuration Status=Suspended 2>/dev/null || true
          echo "Deleting all object versions and delete markers from $BUCKET_NAME..."
          OBJECT_VERSIONS=$(aws s3api list-object-versions \
                              --bucket "$BUCKET_NAME" \
                              --query 'concat(Versions[], DeleteMarkers[])' \
                              --output json)
          if [ "$(echo "$OBJECT_VERSIONS" | jq 'length')" -gt 0 ]; then
            echo "$OBJECT_VERSIONS" | jq -c '.[] | {"Key": .Key, "VersionId": (.VersionId // "null")}' | \
            while read -r obj; do
              KEY=$(echo "$obj" | jq -r '.Key')
              VERSION_ID=$(echo "$obj" | jq -r '.VersionId')
              if [ "$VERSION_ID" = "null" ]; then
                aws s3api delete-object --bucket "$BUCKET_NAME" --key "$KEY"
              else
                aws s3api delete-object --bucket "$BUCKET_NAME" --key "$KEY" --version-id "$VERSION_ID"
              fi
            done
          fi
      else # Insecure bucket - likely not versioned, or simply delete
          aws s3 rm "s3://$BUCKET_NAME/" --recursive || true
      fi
    fi
    if [ -n "$LOG_BUCKET_NAME" ] && aws s3api head-bucket --bucket "$LOG_BUCKET_NAME" 2>/dev/null; then
      echo "Emptying log bucket $LOG_BUCKET_NAME before destroy..."
      aws s3 rm "s3://$LOG_BUCKET_NAME/" --recursive || true
    fi

    if [ "$skip_confirm" == "true" ]; then
      terraform destroy -auto-approve
    else
      terraform destroy
    fi
    echo "Terraform destroy completed for $dir."
  else
    echo "Invalid action: $action. Use 'apply' or 'destroy'."
  fi

  echo ">>> Exiting directory: $dir <<<"
  cd .. || { echo "Error: Failed to return to parent directory. Exiting."; exit 1; }
}

# --- Main script logic ---
cd "$(dirname "$0")" # Ensure script runs from its directory

echo "Starting Terraform operations."

# --- Insecure Bucket Demonstration ---
read -p "Deploy insecure S3 bucket? (y/N): " deploy_insecure
if [[ "$deploy_insecure" =~ ^[yY]$ ]]; then
  terraform_action "insecure" "apply" "false" # Ask for confirmation for apply
  INSECURE_URL=$(terraform -chdir=insecure output -raw public_object_url)
  echo ""
  echo "--- INSECURE BUCKET DEPLOYED ---"
  echo "Verify public access using the 'public_object_url' output from above. For example:"
  echo "curl \"$INSECURE_URL\""
  echo "(Expected to succeed, showing the content 'This is a test object in an insecure S3 bucket.')"
  read -p "Press Enter to proceed with destroying the insecure bucket..."
  terraform_action "insecure" "destroy" "true" # Auto-approve destroy for quicker cleanup
else
  echo "Skipping insecure S3 bucket deployment."
fi

echo ""

# --- Secure Bucket Demonstration ---
read -p "Deploy secure S3 bucket? (y/N): " deploy_secure
if [[ "$deploy_secure" =~ ^[yY]$ ]]; then
  terraform_action "secure" "apply" "false" # Ask for confirmation for apply
  SECURE_URL=$(terraform -chdir=secure output -raw private_object_url_example)
  echo ""
  echo "--- SECURE BUCKET DEPLOYED ---"
  echo "Attempting to access the private object from the secure bucket. This should FAIL."
  echo "curl \"$SECURE_URL\" # Expected to fail with a 403 Forbidden error."
  echo ""
  echo "You can manually verify settings by checking Block Public Access, Encryption, Versioning, and Logging in the AWS Console for buckets:"
  echo "  - Main Secure Bucket: $(terraform -chdir=secure output -raw secure_bucket_name)"
  echo "  - Log Bucket: $(terraform -chdir=secure output -raw log_bucket_name)"
  read -p "Press Enter to proceed with destroying the secure bucket..."
  terraform_action "secure" "destroy" "true" # Auto-approve destroy for quicker cleanup
else
  echo "Skipping secure S3 bucket deployment."
fi

echo ""
echo "--- Terraform demonstration complete. All resources should be cleaned up. ---"