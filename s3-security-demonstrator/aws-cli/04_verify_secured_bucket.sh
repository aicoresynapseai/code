#!/bin/bash
set -eo pipefail

# --- Configuration ---
# Read bucket name from the file created by the previous script
BUCKET_NAME=$(cat .bucket_name.txt)
AWS_REGION="us-east-1" # Ensure this matches the region used for bucket creation
SAMPLE_FILE_NAME="public-data.txt"

echo "--- Verifying Secured S3 Bucket: $BUCKET_NAME ---"

if [ -z "$BUCKET_NAME" ]; then
  echo "Error: Bucket name not found. Please run 01_create_insecure_bucket.sh and 03_secure_bucket.sh first."
  exit 1
fi

# 1. Check Public Access Block settings
echo "1. Retrieving S3 Block Public Access configuration for $BUCKET_NAME..."
aws s3api get-public-access-block \
  --bucket "$BUCKET_NAME" \
  --query 'PublicAccessBlockConfiguration' \
  --output json

echo ""
echo "Expected output for secured bucket: All 'true' values, confirming public access is fully blocked."
echo ""

# 2. Check Bucket Policy
echo "2. Retrieving bucket policy for $BUCKET_NAME..."
# Expect this to fail (no policy found) or return an empty object,
# as we removed any public policies during the securing step.
if aws s3api get-bucket-policy --bucket "$BUCKET_NAME" --query 'Policy' --output text 2>/dev/null; then
  echo "Bucket policy found (this might be an internal policy, but it should NOT allow public access):"
  aws s3api get-bucket-policy --bucket "$BUCKET_NAME" --query 'Policy' --output text | jq '.'
else
  echo "No bucket policy found, as expected for a bucket intended to be publicly inaccessible via policy."
fi
echo ""

# 3. Check Server-Side Encryption
echo "3. Retrieving Server-Side Encryption configuration for $BUCKET_NAME..."
aws s3api get-bucket-encryption \
  --bucket "$BUCKET_NAME" \
  --query 'ServerSideEncryptionConfiguration.Rules[0].ApplyServerSideEncryptionByDefault.SSEAlgorithm' \
  --output text

echo ""
echo "Expected output for secured bucket: AES256"
echo ""

# 4. Check Versioning
echo "4. Retrieving Versioning configuration for $BUCKET_NAME..."
aws s3api get-bucket-versioning \
  --bucket "$BUCKET_NAME" \
  --query 'Status' \
  --output text

echo ""
echo "Expected output for secured bucket: Enabled"
echo ""

# 5. Check Access Logging
echo "5. Retrieving Access Logging configuration for $BUCKET_NAME..."
aws s3api get-bucket-logging \
  --bucket "$BUCKET_NAME" \
  --query 'LoggingEnabled' \
  --output json

echo ""
echo "Expected output for secured bucket: 'TargetBucket' and 'TargetPrefix' should be set, indicating logging is active."
echo ""

# 6. Attempt to access the object via public URL (simulating an anonymous user)
S3_PUBLIC_URL="https://$BUCKET_NAME.s3.$AWS_REGION.amazonaws.com/$SAMPLE_FILE_NAME"
echo "6. Attempting to retrieve object from public URL: $S3_PUBLIC_URL"
echo "--- BEGIN PUBLIC ACCESS TEST ---"
if curl --fail -s "$S3_PUBLIC_URL"; then
  echo ""
  echo "--- PUBLIC ACCESS TEST FAILED: The object is still publicly accessible, which is UNEXPECTED! ---"
  echo "This indicates a security misconfiguration or an active public policy/ACL despite security measures."
else
  echo ""
  echo "--- PUBLIC ACCESS TEST SUCCESS: The object is NOT publicly accessible, as expected. ---"
  echo "You should see a '403 Forbidden' error from curl, indicating proper blocking."
fi
echo "--- END PUBLIC ACCESS TEST ---"

echo ""
echo "--- Security verification complete for bucket: $BUCKET_NAME ---"
echo "The bucket should now be securely configured."
echo "Proceed to '05_cleanup_bucket.sh' to remove all resources."