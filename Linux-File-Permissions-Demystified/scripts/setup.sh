#!/bin/bash

# Define the data directory
DATA_DIR="../data"

echo "--- Setting up demonstration files and directories ---"

# Create the data directory if it doesn't exist
mkdir -p "$DATA_DIR"
echo "Created directory: $DATA_DIR"

# Create some dummy files with various initial contents
echo "This is a regular text file." > "$DATA_DIR/file1.txt"
echo "Another text file, perhaps for a different purpose." > "$DATA_DIR/file2.txt"
echo "echo 'Hello, Executable World!'" > "$DATA_DIR/my_script.sh"
echo "Sensitive financial data." > "$DATA_DIR/private_data.conf"
echo "Shared document for the team." > "$DATA_DIR/group_document.md"

echo "Created initial files:"
ls -l "$DATA_DIR"

# Set initial permissions for some files for demonstration purposes
# file1.txt: default permissions (usually 644)
# file2.txt: default permissions (usually 644)
# my_script.sh: make it executable for the owner (u+x)
chmod u+x "$DATA_DIR/my_script.sh"
echo "Changed permissions for $DATA_DIR/my_script.sh to be executable by owner."

# private_data.conf: make it readable only by the owner (600)
chmod 600 "$DATA_DIR/private_data.conf"
echo "Changed permissions for $DATA_DIR/private_data.conf to 600 (owner read/write)."

# group_document.md: make it readable/writable by owner and group, no access for others (660)
chmod 660 "$DATA_DIR/group_document.md"
echo "Changed permissions for $DATA_DIR/group_document.md to 660 (owner/group read/write)."

echo "Setup complete. Files are in $DATA_DIR with initial permissions."
echo "You can now run view_permissions.sh to see the initial state."