#!/bin/bash

# Define the data directory
DATA_DIR="../data"

echo "--- Cleaning up demonstration files and directories ---"

# Check if the data directory exists
if [ -d "$DATA_DIR" ]; then
    # Remove the data directory and all its contents recursively
    rm -rf "$DATA_DIR"
    echo "Removed directory: $DATA_DIR"
else
    echo "Directory $DATA_DIR not found. Nothing to clean up."
fi

echo "Cleanup complete."