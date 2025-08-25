#!/bin/bash
set -eo pipefail

# --- Configuration ---
# Read bucket names from the files created by previous scripts
BUCKET_NAME=$(cat .bucket_name.txt 2>/dev/null)
LOG_BUCKET_NAME=$(cat .log_bucket_name.txt 2>/dev/null)

echo "--- Cleaning up S3 Resources ---"

# Cleanup the primary bucket
if [ -z "$BUCKET_NAME" ]; then
  echo "No primary bucket name found. Nothing to clean up for the main bucket."
else
  echo "Cleaning up primary bucket: $BUCKET_NAME"
  # Suspend versioning for the primary bucket to enable deletion of all object versions
  echo "Suspending versioning for $BUCKET_NAME to allow deletion of all versions..."
  aws s3api put-bucket-versioning \
    --bucket "$BUCKET_NAME" \
    --versioning-configuration Status=Suspended 2>/dev/null || true

  # Delete all object versions and delete markers in the primary bucket
  echo "Deleting all object versions and delete markers in $BUCKET_NAME..."
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
        # This case handles current versions without a specific version ID in list-object-versions output
        aws s3api delete-object --bucket "$BUCKET_NAME" --key "$KEY"
      else
        aws s3api delete-object --bucket "$BUCKET_NAME" --key "$KEY" --version-id "$VERSION_ID"
      fi
    done
    echo "All objects and versions deleted from $BUCKET_NAME."
  else
    echo "No object versions or delete markers found in $BUCKET_NAME."
  fi

  # Delete the primary bucket itself
  echo "Deleting bucket: $BUCKET_NAME..."
  aws s3api delete-bucket \
    --bucket "$BUCKET_NAME"

  echo "Primary bucket $BUCKET_NAME cleaned up."
  rm -f .bucket_name.txt # Remove the temporary file
fi

# Cleanup the log bucket
if [ -z "$LOG_BUCKET_NAME" ]; then
  echo "No log bucket name found. Nothing to clean up for the log bucket."
else
  echo "Cleaning up log bucket: $LOG_BUCKET_NAME"
  # Empty the log bucket first (log buckets typically don't have versioning enabled)
  echo "Emptying log bucket: $LOG_BUCKET_NAME..."
  aws s3 rm "s3://$LOG_BUCKET_NAME/" --recursive || true # Use --recursive for s3 rm command

  # Delete the log bucket itself
  echo "Deleting log bucket: $LOG_BUCKET_NAME..."
  aws s3api delete-bucket \
    --bucket "$LOG_BUCKET_NAME"

  echo "Log bucket $LOG_BUCKET_NAME cleaned up."
  rm -f .log_bucket_name.txt # Remove the temporary file
fi

echo "--- All S3 resources for this demo have been removed. ---"