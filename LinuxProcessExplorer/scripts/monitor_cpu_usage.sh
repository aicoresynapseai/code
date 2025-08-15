#!/bin/bash

# This script monitors the CPU usage of a specified process.
# It can take either a Process ID (PID) or a process name as an argument.

# --- Usage check ---
if [ -z "$1" ]; then
    echo "Usage: $0 <PID_or_PROCESS_NAME>"
    echo "Example: $0 12345"
    echo "Example: $0 dummy_process.sh"
    exit 1
fi

PROCESS_IDENTIFIER="$1"
TARGET_PID=""

# --- Determine the target PID ---
# Check if the identifier is purely numeric (assume it's a PID)
if [[ "$PROCESS_IDENTIFIER" =~ ^[0-9]+$ ]]; then
    TARGET_PID="$PROCESS_IDENTIFIER"
    # Verify if the PID actually exists
    if ! ps -p "$TARGET_PID" > /dev/null; then
        echo "Error: Process with PID $TARGET_PID does not exist."
        exit 1
    fi
else
    # Assume it's a process name, try to find PID using pgrep
    # pgrep -f: Search the full command line for the pattern.
    # pgrep -o: Output only the process IDs.
    # head -n 1: In case multiple processes match, take the first one found.
    TARGET_PID=$(pgrep -f "$PROCESS_IDENTIFIER" | head -n 1)

    if [ -z "$TARGET_PID" ]; then
        echo "Error: No process found matching '$PROCESS_IDENTIFIER'."
        echo "Please ensure the process is running and the name is accurate."
        exit 1
    fi
    echo "Found process matching '$PROCESS_IDENTIFIER' with PID: $TARGET_PID. Monitoring..."
fi

echo "Monitoring CPU usage for PID: $TARGET_PID. Press Ctrl+C to stop."
echo "---------------------------------------------------------"

# --- Monitoring Loop ---
# Loop indefinitely to continuously fetch and display CPU usage
while true; do
    # Check if the process still exists before attempting to get its stats.
    # This prevents errors if the process is terminated while monitoring.
    if ! ps -p "$TARGET_PID" > /dev/null; then
        echo "Process with PID $TARGET_PID no longer exists. Exiting monitor."
        break # Exit the loop if the process is gone
    fi

    # Get CPU usage using the 'ps' command.
    # 'ps -p $TARGET_PID': Focus on the specific process ID.
    # '-o %cpu': Output only the CPU usage percentage.
    # 'tail -n 1': Get the last line (actual CPU value, skipping header).
    # 'xargs': Trim any leading/trailing whitespace.
    CPU_USAGE=$(ps -p "$TARGET_PID" -o %cpu | tail -n 1 | xargs)

    # Get the current timestamp for logging purposes.
    TIMESTAMP=$(date +'%Y-%m-%d %H:%M:%S')

    # Print the monitoring data to the console.
    echo "[$TIMESTAMP] PID: $TARGET_PID, CPU: ${CPU_USAGE}%"

    # Wait for a few seconds before the next check.
    # This interval can be adjusted to change the monitoring frequency.
    sleep 2
done