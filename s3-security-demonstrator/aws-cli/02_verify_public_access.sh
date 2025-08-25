#!/bin/bash
set -eo pipefail

# --- Configuration ---
# Read bucket name from the file created by the previous script
BUCKET_NAME=$(cat .bucket_name.txt)
AWS_REGION="us-east-1" # Ensure this matches the region used for bucket creation
SAMPLE_FILE_NAME="public-data.txt"

echo "--- Verifying Public Access for Bucket: $BUCKET_NAME ---"

if [ -z "$BUCKET_NAME" ]; then
  echo "Error: Bucket name not found. Please run 01_create_insecure_bucket.sh first."
  exit 1
fi

# 1. Check Public Access Block settings
echo "1. Retrieving S3 Block Public Access configuration for $BUCKET_NAME..."
aws s3api get-public-access-block \
  --bucket "$BUCKET_NAME" \
  --query 'PublicAccessBlockConfiguration' \
  --output json

echo ""
echo "Expected output for insecure bucket: All 'false' values, confirming public access is not blocked."
echo "If any are 'true', then account-level settings might be overriding bucket settings, or the bucket was not configured correctly."
echo ""

# 2. Check Bucket Policy
echo "2. Retrieving bucket policy for $BUCKET_NAME..."
# Using jq for pretty printing JSON output
aws s3api get-bucket-policy \
  --bucket "$BUCKET_NAME" \
  --query 'Policy' \
  --output text | jq '.'

echo ""
echo "Expected output for insecure bucket: A policy allowing 'Principal: *' and 'Action: s3:GetObject' on the bucket's resources."
echo ""

# 3. Check Object ACL (for the public-read object)
echo "3. Retrieving ACL for object: $SAMPLE_FILE_NAME in $BUCKET_NAME..."
aws s3api get-object-acl \
  --bucket "$BUCKET_NAME" \
  --key "$SAMPLE_FILE_NAME" \
  --query 'Grants[?Grantee.Type==`Group` && Grantee.URI==`http://acs.amazonaws.com/groups/global/AllUsers`]' \
  --output json

echo ""
echo "Expected output for insecure object: A grant for 'AllUsers' with 'READ' permission, explicitly making the object public via ACL."
echo ""

# 4. Attempt to access the object via public URL (simulating an anonymous user)
# This method requires the bucket to be in a region supported by direct S3 URLs
# and all Public Access Blocks to be disabled.
S3_PUBLIC_URL="https://$BUCKET_NAME.s3.$AWS_REGION.amazonaws.com/$SAMPLE_FILE_NAME"
echo "4. Attempting to retrieve object from public URL: $S3_PUBLIC_URL"
echo "--- BEGIN PUBLIC ACCESS TEST ---"
if curl --fail -s "$S3_PUBLIC_URL"; then
  echo ""
  echo "--- PUBLIC ACCESS TEST SUCCESS: The object is publicly accessible! ---"
else
  echo ""
  echo "--- PUBLIC ACCESS TEST FAILED: The object is NOT publicly accessible. ---"
  echo "This might indicate account-level S3 Block Public Access is active, or a misconfiguration."
fi
echo "--- END PUBLIC ACCESS TEST ---"

echo ""
echo "--- Public access verification complete for bucket: $BUCKET_NAME ---"
echo "Proceed to '03_secure_bucket.sh' to apply security best practices."