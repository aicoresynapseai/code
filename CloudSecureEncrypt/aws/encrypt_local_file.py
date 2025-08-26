# This script demonstrates encrypting and decrypting a local file
# using AWS KMS. It requires the AWS CLI to be configured with credentials
# and the KMS key created via Terraform to be available.

import boto3
import base64
import os
import sys

# --- Configuration ---
# Replace with the ARN of your KMS key created by Terraform
# You can find this in the `terraform output kms_key_arn` result.
KMS_KEY_ARN = os.environ.get("KMS_KEY_ARN", "arn:aws:kms:us-east-1:123456789012:key/your-kms-key-id")
# Make sure to update the region if different from your Terraform setup
AWS_REGION = os.environ.get("AWS_REGION", "us-east-1")

INPUT_FILE_NAME = "sample_data.txt"
ENCRYPTED_FILE_NAME = "sample_data.txt.encrypted"
DECRYPTED_FILE_NAME = "sample_data.txt.decrypted"

# Initialize the KMS client
kms_client = boto3.client('kms', region_name=AWS_REGION)

def create_sample_file():
    """Creates a sample text file for encryption demonstration."""
    if not os.path.exists(INPUT_FILE_NAME):
        with open(INPUT_FILE_NAME, "w") as f:
            f.write("This is highly sensitive data that needs to be encrypted.\n")
            f.write("It contains secrets that must be protected at all costs.\n")
        print(f"Created sample file: {INPUT_FILE_NAME}")
    else:
        print(f"Sample file {INPUT_FILE_NAME} already exists.")

def encrypt_file():
    """Reads a file, encrypts its content using AWS KMS, and saves the ciphertext."""
    try:
        with open(INPUT_FILE_NAME, "rb") as f:
            plaintext = f.read()

        print(f"\nEncrypting {INPUT_FILE_NAME} using KMS key: {KMS_KEY_ARN}...")
        response = kms_client.encrypt(
            KeyId=KMS_KEY_ARN,
            Plaintext=plaintext
        )

        ciphertext_blob = response['CiphertextBlob']

        with open(ENCRYPTED_FILE_NAME, "wb") as f:
            f.write(ciphertext_blob)

        print(f"File encrypted successfully to {ENCRYPTED_FILE_NAME}")
        print(f"Size of original: {len(plaintext)} bytes")
        print(f"Size of encrypted: {len(ciphertext_blob)} bytes")

    except FileNotFoundError:
        print(f"Error: {INPUT_FILE_NAME} not found. Please create it first or run `create_sample_file()`.")
    except Exception as e:
        print(f"Error during encryption: {e}")
        # Specific KMS errors might include AccessDeniedException, NotFoundException
        if "NotFoundException" in str(e):
            print(f"Please ensure the KMS Key ARN '{KMS_KEY_ARN}' is correct and the key exists.")
        if "AccessDeniedException" in str(e):
            print("Please ensure your AWS credentials have permissions to use the specified KMS key.")

def decrypt_file():
    """Reads an encrypted file, decrypts its content using AWS KMS, and saves the plaintext."""
    try:
        with open(ENCRYPTED_FILE_NAME, "rb") as f:
            ciphertext_blob = f.read()

        print(f"\nDecrypting {ENCRYPTED_FILE_NAME} using KMS key: {KMS_KEY_ARN}...")
        response = kms_client.decrypt(
            CiphertextBlob=ciphertext_blob,
            KeyId=KMS_KEY_ARN # Specifying KeyId is optional for decryption but good practice
        )

        plaintext = response['Plaintext']

        with open(DECRYPTED_FILE_NAME, "wb") as f:
            f.write(plaintext)

        print(f"File decrypted successfully to {DECRYPTED_FILE_NAME}")
        print(f"Size of encrypted: {len(ciphertext_blob)} bytes")
        print(f"Size of decrypted: {len(plaintext)} bytes")

        print(f"\nContent of decrypted file ({DECRYPTED_FILE_NAME}):")
        with open(DECRYPTED_FILE_NAME, "r") as f:
            print(f.read())

    except FileNotFoundError:
        print(f"Error: {ENCRYPTED_FILE_NAME} not found. Please encrypt a file first.")
    except Exception as e:
        print(f"Error during decryption: {e}")
        if "IncorrectKeyException" in str(e):
            print("The ciphertext was not encrypted with the specified KMS key or the key is unavailable.")
        if "AccessDeniedException" in str(e):
            print("Please ensure your AWS credentials have permissions to decrypt with the specified KMS key.")

def cleanup_files():
    """Deletes the created sample, encrypted, and decrypted files."""
    files_to_delete = [INPUT_FILE_NAME, ENCRYPTED_FILE_NAME, DECRYPTED_FILE_NAME]
    print("\nCleaning up files...")
    for filename in files_to_delete:
        if os.path.exists(filename):
            os.remove(filename)
            print(f"Deleted {filename}")
        else:
            print(f"{filename} not found, skipping.")

if __name__ == "__main__":
    print("--- AWS KMS Local File Encryption/Decryption Demo ---")
    print(f"Using KMS Key ARN: {KMS_KEY_ARN}")
    print(f"Using AWS Region: {AWS_REGION}")

    if "your-kms-key-id" in KMS_KEY_ARN:
        print("\nWARNING: KMS_KEY_ARN is still using placeholder value.")
        print("Please set the KMS_KEY_ARN environment variable or modify the script with your actual key ARN.")
        print("Example: export KMS_KEY_ARN=$(terraform output -raw kms_key_arn)")
        sys.exit(1)

    while True:
        print("\n--- Menu ---")
        print("1. Create sample file")
        print("2. Encrypt file")
        print("3. Decrypt file")
        print("4. View original sample file")
        print("5. Cleanup files")
        print("6. Exit")

        choice = input("Enter your choice: ")

        if choice == '1':
            create_sample_file()
        elif choice == '2':
            encrypt_file()
        elif choice == '3':
            decrypt_file()
        elif choice == '4':
            if os.path.exists(INPUT_FILE_NAME):
                with open(INPUT_FILE_NAME, "r") as f:
                    print(f"\nContent of {INPUT_FILE_NAME}:\n{f.read()}")
            else:
                print(f"Error: {INPUT_FILE_NAME} not found.")
        elif choice == '5':
            cleanup_files()
        elif choice == '6':
            print("Exiting.")
            break
        else:
            print("Invalid choice. Please try again.")