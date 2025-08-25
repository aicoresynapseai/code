#!/bin/bash
set -eo pipefail

# --- Configuration ---
# Generate a unique bucket name to avoid conflicts
BUCKET_NAME="s3-insecure-demo-$(date +%s%3N)" 
AWS_REGION="us-east-1" # Specify your desired AWS region
SAMPLE_FILE_NAME="public-data.txt"
SAMPLE_FILE_CONTENT="This is publicly accessible data in the insecure bucket!"

echo "--- Creating Insecure S3 Bucket ---"

# 1. Create the bucket
# Note: Newer AWS accounts often have account-level Public Access Block enabled by default.
# For this demonstration, we create the bucket and then explicitly add a *public bucket policy*
# and *disable the bucket's own public access block settings* to ensure it's truly insecure.
echo "Creating bucket: $BUCKET_NAME in region $AWS_REGION..."
aws s3api create-bucket \
  --bucket "$BUCKET_NAME" \
  --region "$AWS_REGION" \
  --create-bucket-configuration LocationConstraint="$AWS_REGION" # Required for regions other than us-east-1

echo "Bucket created. Waiting for bucket to be ready..."
aws s3api wait bucket-exists --bucket "$BUCKET_NAME"

# 2. Upload a sample file and make it publicly readable via an ACL.
# While ACLs are less common for public access today (bucket policies are preferred),
# they are a common historical pitfall and demonstrate public access at the object level.
echo "Creating a sample file: $SAMPLE_FILE_NAME"
echo "$SAMPLE_FILE_CONTENT" > "$SAMPLE_FILE_NAME"

echo "Uploading $SAMPLE_FILE_NAME to s3://$BUCKET_NAME/$SAMPLE_FILE_NAME with public-read ACL..."
aws s3 cp "$SAMPLE_FILE_NAME" "s3://$BUCKET_NAME/$SAMPLE_FILE_NAME" --acl public-read

# 3. Apply an intentionally permissive bucket policy
# This policy explicitly allows anyone (the principal "*") to perform "s3:GetObject"
# actions on all objects within the bucket. This is a major security risk!
echo "Applying a public read bucket policy to $BUCKET_NAME..."
cat << EOF > bucket-policy.json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Principal": "*",
      "Action": "s3:GetObject",
      "Resource": "arn:aws:s3:::$BUCKET_NAME/*"
    }
  ]
}
EOF

aws s3api put-bucket-policy \
  --bucket "$BUCKET_NAME" \
  --policy file://bucket-policy.json

# 4. Explicitly disable S3 Block Public Access settings for the bucket.
# This ensures that our public policy and ACLs are effective.
# In a real-world scenario, you *always* want these enabled.
echo "Disabling S3 Block Public Access settings for $BUCKET_NAME (for demonstration purposes)..."
cat << EOF > public-access-block-config.json
{
  "BlockPublicAcls": false,
  "IgnorePublicAcls": false,
  "BlockPublicPolicy": false,
  "RestrictPublicBuckets": false
}
EOF

aws s3api put-public-access-block \
  --bucket "$BUCKET_NAME" \
  --public-access-block-configuration file://public-access-block-config.json

echo "--- Insecure bucket '$BUCKET_NAME' created and configured with public read access. ---"
echo "You can now proceed to '02_verify_public_access.sh' to confirm its insecurity."
echo "Bucket name for verification: $BUCKET_NAME"

# Store bucket name for subsequent scripts
echo "$BUCKET_NAME" > .bucket_name.txt
# Clean up temporary files
rm "$SAMPLE_FILE_NAME" bucket-policy.json public-access-block-config.json