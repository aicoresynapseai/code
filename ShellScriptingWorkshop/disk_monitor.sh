#!/bin/bash
#
# ShellScriptingWorkshop/disk_monitor.sh
# A script to monitor disk space and send a warning if it exceeds a threshold.

# --- Configuration Loading ---
# Source the configuration file to load variables.
# 'source' or '.' command executes the script in the current shell context.
CONFIG_FILE="config.conf"

if [[ -f "$CONFIG_FILE" ]]; then
  source "$CONFIG_FILE"
  echo "Configuration loaded from $CONFIG_FILE"
else
  echo "Error: Configuration file '$CONFIG_FILE' not found."
  echo "Please create $CONFIG_FILE with DISK_THRESHOLD, EMAIL_RECIPIENT, and PARTITION_TO_CHECK."
  exit 1 # Exit with an error code
fi

# --- Validate Configuration ---
# Check if essential variables are set.
if [[ -z "$DISK_THRESHOLD" ]]; then
  echo "Error: DISK_THRESHOLD is not set in $CONFIG_FILE."
  exit 1
fi

if [[ -z "$PARTITION_TO_CHECK" ]]; then
  echo "Error: PARTITION_TO_CHECK is not set in $CONFIG_FILE."
  exit 1
fi

# Convert threshold to an integer for numeric comparison
if ! [[ "$DISK_THRESHOLD" =~ ^[0-9]+$ ]]; then
    echo "Error: DISK_THRESHOLD must be a number."
    exit 1
fi

# --- Functions ---

# Function to send an email notification.
send_email_notification() {
  local subject="$1"
  local body="$2"
  local recipient="$3"

  # Check if an email recipient is configured.
  if [[ -z "$recipient" || "$recipient" == "your_email@example.com" ]]; then
    echo "No valid email recipient configured. Skipping email notification."
    return 0 # Not an error if no email is configured
  fi

  # Check if 'mail' command is available.
  if ! command -v mail &> /dev/null; then
    echo "Warning: 'mail' command not found. Cannot send email notification."
    echo "Please install 'mailx' or 'mailutils' (e.g., 'sudo apt-get install mailutils' or 'sudo yum install mailx')."
    return 1 # Indicate that email sending failed due to missing command
  fi

  echo "Sending email to $recipient with subject: '$subject'..."
  # Use 'echo' to pipe the body into the 'mail' command.
  echo "$body" | mail -s "$subject" "$recipient"
  if [[ $? -eq 0 ]]; then
    echo "Email sent successfully."
  else
    echo "Failed to send email."
  fi
}

# --- Main Logic ---

echo "--- Disk Space Monitor ---"
echo "Checking disk usage for: $PARTITION_TO_CHECK"
echo "Warning threshold set to: $DISK_THRESHOLD%"

# Get disk usage for the specified partition.
# 'df -h' displays disk usage in human-readable format.
# 'grep' filters for the specific partition.
# 'awk' is used to extract the 5th column (Use%) and the 6th column (Mounted on).
# 'tr -d %' removes the percentage sign from the usage value.
# 'head -n 1' ensures we only get the first line in case of multiple matches (e.g., submounts).

# Example df -h output line:
# /dev/sda1        98G   75G   18G  81% /
# Column 5: 81%, Column 6: /
DISK_INFO=$(df -h "$PARTITION_TO_CHECK" 2>/dev/null | awk 'NR==2 {print $5 " " $6}' | head -n 1)

# Check if df command returned any data.
if [[ -z "$DISK_INFO" ]]; then
  echo "Error: Could not retrieve disk information for '$PARTITION_TO_CHECK'."
  echo "Please ensure the partition path is correct and accessible (e.g., '/')."
  send_email_notification \
    "Disk Monitor Error on $(hostname)" \
    "Failed to retrieve disk information for $PARTITION_TO_CHECK. Please check the script configuration." \
    "$EMAIL_RECIPIENT"
  exit 1
fi

# Split DISK_INFO into usage percentage and mount point.
# 'read' command is used to parse the string into variables.
read -r USAGE_PERCENT MOUNT_POINT <<< "$DISK_INFO"

# Remove the '%' sign from USAGE_PERCENT for numeric comparison.
USAGE_VALUE=$(echo "$USAGE_PERCENT" | tr -d '%')

echo "Current usage for $MOUNT_POINT: $USAGE_VALUE%"

# Compare current usage with the threshold.
if [[ "$USAGE_VALUE" -ge "$DISK_THRESHOLD" ]]; then
  echo "WARNING: Disk usage on $MOUNT_POINT is at $USAGE_VALUE%, which is at or above the threshold of $DISK_THRESHOLD%!"
  
  # Prepare email subject and body.
  SUBJECT="DISK SPACE ALERT: $MOUNT_POINT Usage at ${USAGE_VALUE}% on $(hostname)"
  BODY="High disk usage detected on ${MOUNT_POINT}.\n\n"
  BODY+="Current usage: ${USAGE_VALUE}%\n"
  BODY+="Threshold: ${DISK_THRESHOLD}%\n\n"
  BODY+="Please take action to free up space.\n\n"
  BODY+="Full df -h output for ${MOUNT_POINT}:\n"
  BODY+="$(df -h "$PARTITION_TO_CHECK" 2>/dev/null)" # Include full df output in body

  # Send the email notification.
  send_email_notification "$SUBJECT" "$BODY" "$EMAIL_RECIPIENT"
else
  echo "Disk usage on $MOUNT_POINT is acceptable ($USAGE_VALUE% < $DISK_THRESHOLD%). No warning needed."
fi

echo "--- Disk Space Monitor Finished ---"

# To automate this script, you can add it to your cron jobs.
# Run 'crontab -e' and add a line like:
# 0 * * * * /path/to/ShellScriptingWorkshop/disk_monitor.sh >> /var/log/disk_monitor.log 2>&1
# This would run the script every hour and log its output.