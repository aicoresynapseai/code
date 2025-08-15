#!/bin/bash

# This is a simple dummy process that runs indefinitely.
# It simulates a long-running background task by performing
# some calculations to consume a bit of CPU, then pauses.

echo "Dummy process started. PID: $$"
echo "To terminate this process, use 'kill $$' or 'killall dummy_process.sh'."
echo "To put it in the background, run it as './dummy_process.sh &'."
echo "To monitor its CPU usage, use './monitor_cpu_usage.sh $$' or './monitor_cpu_usage.sh dummy_process.sh'"
echo "----------------------------------------------------------"

# Infinite loop to keep the process running until manually terminated
while true; do
    # Perform a CPU-intensive operation.
    # 'seq 1 500000' generates numbers from 1 to 500,000.
    # 'md5sum' calculates the MD5 hash for each number, consuming CPU.
    # '> /dev/null' redirects the output to prevent flooding the terminal.
    # Adjust '500000' to increase/decrease CPU load.
    seq 1 500000 | md5sum > /dev/null

    # Sleep for a short period.
    # This prevents the script from consuming 100% CPU constantly and
    # allows other system processes to run, making monitoring more visible.
    sleep 0.5
done