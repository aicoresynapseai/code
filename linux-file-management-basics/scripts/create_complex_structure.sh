#!/bin/bash

# create_complex_structure.sh: A helper script to create a more complex
# file and directory structure for demonstration purposes.

# Enable strict mode
set -euo pipefail

# Check if the demo area path is provided as an argument
if [ -z "${1:-}" ]; then
  echo "Usage: $0 <path_to_demo_area>"
  exit 1
fi

DEMO_AREA="$1"
TARGET_DIR="${DEMO_AREA}/complex_data"

echo "Creating a complex file and directory structure in '$TARGET_DIR'..."

# Create the main target directory
mkdir -p "$TARGET_DIR"

# Create subdirectories
mkdir -p "$TARGET_DIR/docs"
mkdir -p "$TARGET_DIR/images"
mkdir -p "$TARGET_DIR/code"
mkdir -p "$TARGET_DIR/code/frontend"
mkdir -p "$TARGET_DIR/code/backend"
mkdir -p "$TARGET_DIR/config"

# Create various files
touch "$TARGET_DIR/readme.txt"
touch "$TARGET_DIR/docs/user_guide.pdf"
touch "$TARGET_DIR/docs/api_reference.md"
touch "$TARGET_DIR/images/logo.png"
touch "$TARGET_DIR/images/banner.jpg"
touch "$TARGET_DIR/code/frontend/app.js"
touch "$TARGET_DIR/code/frontend/index.html"
touch "$TARGET_DIR/code/backend/server.py"
touch "$TARGET_DIR/code/backend/database.sql"
touch "$TARGET_DIR/config/settings.ini"
touch "$TARGET_DIR/config/config.json"
touch "$TARGET_DIR/hidden_file.txt" # A file to demonstrate hidden files if 'ls -a' were used

echo "Complex structure created successfully."