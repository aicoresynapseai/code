#!/bin/bash

# cleanup.sh: A script to remove all files and directories created by the demo.

# Enable strict mode
set -euo pipefail

# Define the variable for the demonstration area
DEMO_AREA="demo_area"

echo "--- Cleaning Up Demonstration Area ---"

# Check if the demo area exists
if [ -d "$DEMO_AREA" ]; then
    echo "Removing '$DEMO_AREA' directory and all its contents..."
    # -r: recursive (for directories)
    # -f: force (do not prompt for confirmation)
    rm -rf "$DEMO_AREA"
    echo "'$DEMO_AREA' successfully removed."
else
    echo "No '$DEMO_AREA' directory found. Nothing to clean up."
fi

echo "--- Cleanup Complete ---"