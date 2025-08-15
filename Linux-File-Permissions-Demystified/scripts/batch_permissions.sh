#!/bin/bash

# Define the data directory
DATA_DIR="../data"

echo "--- Changing Permissions for Multiple Files in Batch ---"

# Create a few more files for batch operations
echo "Creating additional files for batch processing..."
echo "Temp file 1" > "$DATA_DIR/batch_temp1.txt"
echo "Temp file 2" > "$DATA_DIR/batch_temp2.txt"
echo "Another script" > "$DATA_DIR/another_script.sh"
echo "Just a log" > "$DATA_DIR/app.log"
chmod 640 "$DATA_DIR/batch_temp1.txt" # rw-r-----
chmod 600 "$DATA_DIR/batch_temp2.txt" # rw-------
chmod 644 "$DATA_DIR/another_script.sh" # -rw-r--r--
chmod 664 "$DATA_DIR/app.log" # rw-rw-r--

echo ""
echo "Initial permissions of batch files:"
ls -l "$DATA_DIR/batch_temp*.txt" "$DATA_DIR/another_script.sh" "$DATA_DIR/app.log"
echo ""

# Example 1: Make all .sh files executable for owner and group
echo "1. Making all .sh files executable for owner and group (u+x, g+x)"
# This uses a simple for loop to iterate over files matching a pattern
for script_file in "$DATA_DIR"/*.sh; do
    echo "  Applying chmod u+x,g+x to $script_file"
    chmod u+x,g+x "$script_file"
done
ls -l "$DATA_DIR"/*.sh
echo ""

# Example 2: Set all .txt files to read-only for others (o-w,o-x)
echo "2. Setting all .txt files to read-only for others (o-w,o-x)"
# This uses 'find' to locate files and 'exec' to run chmod on each found file.
# The {} is a placeholder for the found file name, and \; terminates the -exec command.
find "$DATA_DIR" -maxdepth 1 -type f -name "*.txt" -exec chmod o-w,o-x {} \;
ls -l "$DATA_DIR"/*.txt
echo ""

# Example 3: Set all files to 644 (rw-r--r--)
echo "3. Setting all files (in the data directory) to 644 (rw-r--r--)"
# Another common way to use find, targeting all regular files and setting a numeric permission.
find "$DATA_DIR" -maxdepth 1 -type f -exec chmod 644 {} \;
ls -l "$DATA_DIR"
echo ""

# Example 4: Make a specific set of files writable by owner and group, no access for others
echo "4. Setting 'app.log' and 'group_document.md' to 660 (rw-rw----)"
chmod 660 "$DATA_DIR/app.log" "$DATA_DIR/group_document.md"
ls -l "$DATA_DIR/app.log" "$DATA_DIR/group_document.md"
echo ""

echo "Batch permission changes complete."
echo "You can now run cleanup.sh to remove the demonstration files."