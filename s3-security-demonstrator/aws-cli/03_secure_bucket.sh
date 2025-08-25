#!/bin/bash
set -eo pipefail

# --- Configuration ---
# Read bucket name from the file created by the previous script
BUCKET_NAME=$(cat .bucket_name.txt)
AWS_REGION="us-east-1" # Ensure this matches the region used for bucket creation
LOG_BUCKET_NAME="${BUCKET_NAME}-logs"

echo "--- Securing S3 Bucket: $BUCKET_NAME ---"

if [ -z "$BUCKET_NAME" ]; then
  echo "Error: Primary bucket name not found. Please run 01_create_insecure_bucket.sh first."
  exit 1
fi

# 1. Enable S3 Block Public Access
# This is the most crucial step. It blocks public ACLs, ignores public ACLs,
# blocks public policies, and restricts public buckets. This overrides any existing public settings.
echo "1. Enabling S3 Block Public Access settings for $BUCKET_NAME..."
cat << EOF > public-access-block-config-secure.json
{
  "BlockPublicAcls": true,
  "IgnorePublicAcls": true,
  "BlockPublicPolicy": true,
  "RestrictPublicBuckets": true
}
EOF

aws s3api put-public-access-block \
  --bucket "$BUCKET_NAME" \
  --public-access-block-configuration file://public-access-block-config-secure.json

# 2. Remove any existing public bucket policy
echo "2. Deleting any existing bucket policy for $BUCKET_NAME..."
# Using --ignore-not-found to prevent script failure if no policy exists (e.g., if re-run)
aws s3api delete-bucket-policy \
  --bucket "$BUCKET_NAME" || true

# 3. Enable Server-Side Encryption (SSE-S3 as default)
echo "3. Enabling default Server-Side Encryption (SSE-S3) for $BUCKET_NAME..."
cat << EOF > encryption-config.json
{
  "ServerSideEncryptionConfiguration": {
    "Rules": [
      {
        "ApplyServerSideEncryptionByDefault": {
          "SSEAlgorithm": "AES256"
        }
      }
    ]
  }
}
EOF

aws s3api put-bucket-encryption \
  --bucket "$BUCKET_NAME" \
  --server-side-encryption-configuration file://encryption-config.json

# 4. Enable Versioning
echo "4. Enabling Versioning for $BUCKET_NAME..."
aws s3api put-bucket-versioning \
  --bucket "$BUCKET_NAME" \
  --versioning-configuration Status=Enabled

# 5. Configure Access Logging
# First, create a separate bucket for logs if it doesn't exist.
# A log bucket should also be secured.
echo "5. Configuring Access Logging for $BUCKET_NAME..."
echo "Creating log bucket: $LOG_BUCKET_NAME..."
if ! aws s3api head-bucket --bucket "$LOG_BUCKET_NAME" 2>/dev/null; then
  aws s3api create-bucket \
    --bucket "$LOG_BUCKET_NAME" \
    --region "$AWS_REGION" \
    --create-bucket-configuration LocationConstraint="$AWS_REGION"
  aws s3api wait bucket-exists --bucket "$LOG_BUCKET_NAME"
  echo "Log bucket created. Applying log delivery policy..."

  # For the log bucket, explicitly enable S3 Block Public Access as well.
  echo "Enabling S3 Block Public Access for log bucket $LOG_BUCKET_NAME..."
  aws s3api put-public-access-block \
    --bucket "$LOG_BUCKET_NAME" \
    --public-access-block-configuration file://public-access-block-config-secure.json

  # Apply a policy to the log bucket allowing S3 Log Delivery group to write logs.
  # It is crucial to restrict this policy to only allow logging from *our* bucket
  # and from *our* account, following the principle of least privilege.
  # The S3 Log Delivery group requires specific permissions to write.
  ACCOUNT_ID=$(aws sts get-caller-identity --query Account --output text)
  cat << EOF > log-bucket-policy.json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Principal": {
        "Service": "logging.s3.amazonaws.com"
      },
      "Action": "s3:PutObject",
      "Resource": "arn:aws:s3:::$LOG_BUCKET_NAME/*",
      "Condition": {
        "StringEquals": {
          "aws:SourceAccount": "$ACCOUNT_ID"
        },
        "ArnLike": {
          "aws:SourceArn": "arn:aws:s3:::$BUCKET_NAME"
        }
      }
    },
    {
      "Effect": "Allow",
      "Principal": {
        "Service": "logging.s3.amazonaws.com"
      },
      "Action": "s3:GetBucketAcl",
      "Resource": "arn:aws:s3:::$LOG_BUCKET_NAME"
    }
  ]
}
EOF
  aws s3api put-bucket-policy \
    --bucket "$LOG_BUCKET_NAME" \
    --policy file://log-bucket-policy.json
else
  echo "Log bucket $LOG_BUCKET_NAME already exists, skipping creation and policy application."
fi

# Configure the original bucket to send logs to the log bucket
aws s3api put-bucket-logging \
  --bucket "$BUCKET_NAME" \
  --bucket-logging-status "{\"LoggingEnabled\":{\"TargetBucket\":\"$LOG_BUCKET_NAME\",\"TargetPrefix\":\"$BUCKET_NAME/\"}}"

echo "--- Bucket '$BUCKET_NAME' has been secured with best practices. ---"
echo "Log bucket '$LOG_BUCKET_NAME' created and configured."
echo "Proceed to '04_verify_secured_bucket.sh' to confirm the new security posture."

# Store log bucket name for subsequent scripts
echo "$LOG_BUCKET_NAME" > .log_bucket_name.txt
# Clean up temporary files
rm public-access-block-config-secure.json encryption-config.json log-bucket-policy.json 2>/dev/null