#!/bin/bash

# demo.sh: A script to demonstrate basic file and directory management commands in Linux.

# Enable strict mode:
# -e: Exit immediately if a command exits with a non-zero status.
# -u: Treat unset variables as an error.
# -o pipefail: The return value of a pipeline is the status of the last command to exit with a non-zero status, or zero if all commands in the pipeline exit successfully.
set -euo pipefail

# Define a variable for the demonstration area
DEMO_AREA="demo_area"

echo "--- Linux File and Directory Management Demonstration ---"
echo ""

# --- 1. Preparation: Create a dedicated area for the demo ---
echo "1. Preparation: Setting up the demonstration area."
echo "   Creating directory '$DEMO_AREA' to perform all operations within it."
mkdir -p "$DEMO_AREA" # -p creates parent directories if they don't exist
cd "$DEMO_AREA"       # Change into the demo directory
echo "   Current working directory: $(pwd)"
echo "----------------------------------------------------"
sleep 1

# --- 2. Creating Files with 'touch' ---
echo "2. Creating Files with 'touch'"
echo "   'touch' creates new, empty files or updates the access/modification times of existing ones."
echo "   Creating 'document.txt', 'report.txt', and 'notes.md'..."
touch document.txt report.txt notes.md
echo "   Files created:"
ls -l | grep -E 'document.txt|report.txt|notes.md' # List newly created files
echo "----------------------------------------------------"
sleep 1

# --- 3. Creating Directories with 'mkdir' ---
echo "3. Creating Directories with 'mkdir'"
echo "   'mkdir' creates new directories (folders)."
echo "   Creating 'projects', 'data', and 'archive' directories..."
mkdir projects data archive
echo "   Directories created:"
ls -F | grep '/' # List directories (ending with /)
echo "   Creating a nested directory 'projects/my_project'..."
mkdir -p projects/my_project # -p creates parent directories if they don't exist
echo "   Directory structure after creation:"
# Check if 'tree' command is available, otherwise use 'ls -R'
if command -v tree &> /dev/null; then
    tree -L 2 # Display directory tree up to 2 levels
else
    echo "   'tree' command not found. Install it for a better visualization (e.g., 'sudo apt install tree' on Debian/Ubuntu)."
    echo "   Listing recursively with 'ls -R':"
    ls -R
fi
echo "----------------------------------------------------"
sleep 1

# --- 4. Copying Files and Directories with 'cp' ---
echo "4. Copying Files and Directories with 'cp'"
echo "   'cp' copies files or directories from one location to another."

echo "   a) Copying 'document.txt' to 'data/document_copy.txt'..."
cp document.txt data/document_copy.txt
echo "      Content of 'data' directory:"
ls -l data/
sleep 1

echo "   b) Copying 'report.txt' to 'archive/' (keeping the same name)..."
cp report.txt archive/
echo "      Content of 'archive' directory:"
ls -l archive/
sleep 1

echo "   c) Copying an entire directory (and its contents) 'projects/my_project' to 'projects/my_project_backup' recursively using 'cp -r'..."
mkdir projects/my_project/src # Create a dummy subdirectory inside my_project
touch projects/my_project/src/main.c # Create a dummy file inside my_project
cp -r projects/my_project projects/my_project_backup
echo "      Original directory structure:"
ls -R projects/my_project/
echo "      Copied directory structure:"
ls -R projects/my_project_backup/
echo "----------------------------------------------------"
sleep 1

# --- 5. Moving and Renaming Files/Directories with 'mv' ---
echo "5. Moving and Renaming Files/Directories with 'mv'"
echo "   'mv' moves files or directories from one location to another, or renames them."

echo "   a) Renaming a file: Renaming 'notes.md' to 'read_me.md'..."
mv notes.md read_me.md
echo "      Files in current directory after rename:"
ls -l | grep -E 'notes.md|read_me.md' # Check if notes.md is gone and read_me.md exists
sleep 1

echo "   b) Moving a file: Moving 'report.txt' into 'projects/my_project/'..."
mv report.txt projects/my_project/
echo "      'report.txt' is now inside 'projects/my_project/':"
ls -l projects/my_project/
sleep 1

echo "   c) Renaming a directory: Renaming 'data' to 'important_data'..."
mv data important_data
echo "      Directories after rename:"
ls -F | grep '/' # List directories again
echo "      Content of 'important_data' (formerly 'data'):"
ls -l important_data/
sleep 1

echo "   d) Moving a directory: Moving 'archive' into 'important_data/'..."
mv archive important_data/
echo "      'archive' is now inside 'important_data/':"
ls -F important_data/
echo "----------------------------------------------------"
sleep 1

# --- 6. Deleting Files and Directories with 'rm' ---
echo "6. Deleting Files and Directories with 'rm'"
echo "   'rm' removes files. 'rm -r' (recursive) is needed for directories."

echo "   a) Deleting a file: Deleting 'document.txt'..."
rm document.txt
echo "      'document.txt' removed. Remaining files:"
ls -l | grep -E 'document.txt|read_me.md' # Check if document.txt is gone
sleep 1

echo "   b) Deleting an empty directory: 'rmdir' can delete empty directories."
echo "      Creating a temporary empty directory 'temp_empty_dir'..."
mkdir temp_empty_dir
echo "      Deleting 'temp_empty_dir' with 'rmdir'..."
rmdir temp_empty_dir
echo "      'temp_empty_dir' removed. Remaining directories:"
ls -F | grep '/'
sleep 1

echo "   c) Deleting a non-empty directory: 'rm -r' is required for non-empty directories."
echo "      Deleting 'projects/my_project_backup' (which contains files and subdirectories)..."
rm -r projects/my_project_backup
echo "      'projects/my_project_backup' removed. Remaining directories in 'projects/':"
ls -F projects/
echo "----------------------------------------------------"
sleep 1

# --- 7. Automation with Scripts: Creating a complex structure ---
echo "7. Automation with Scripts: Using a helper script to create a complex structure."
echo "   This demonstrates how you can automate repetitive file/directory creation tasks."
echo "   Running 'scripts/create_complex_structure.sh'..."
sleep 1
# Go back to the root of the project to call the script
cd ../
./scripts/create_complex_structure.sh "$DEMO_AREA" # Pass DEMO_AREA as argument
cd "$DEMO_AREA" # Go back into the demo area
echo "   New complex structure created inside '$DEMO_AREA/complex_data'."
echo "   Here's the new structure:"
if command -v tree &> /dev/null; then
    tree complex_data
else
    echo "   'tree' command not found. Listing recursively with 'ls -R complex_data':"
    ls -R complex_data
fi
echo "----------------------------------------------------"
sleep 1

echo "--- Demonstration Complete ---"
echo "You have successfully explored basic Linux file and directory management commands."
echo "Feel free to explore the '$DEMO_AREA' directory to see the results."
echo "To clean up all created files and directories, run: ./scripts/cleanup.sh"
echo ""

# Return to original directory (important for subsequent runs or cleanup script)
cd ../