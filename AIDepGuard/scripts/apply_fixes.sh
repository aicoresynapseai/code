#!/bin/bash

# This script reads a JSON file of dependency recommendations
# and applies the 'upgrade' fixes to the pom.xml file.
# It uses 'xmlstarlet' for safe XML manipulation.

# --- Prerequisites ---
# 1. xmlstarlet must be installed (e.g., `sudo apt-get install xmlstarlet` or `brew install xmlstarlet`)
# 2. Git must be configured with user name and email for committing.

RECOMMENDATIONS_FILE="$1"
POM_FILE="pom.xml"

if [ -z "$RECOMMENDATIONS_FILE" ]; then
    echo "Usage: $0 <recommendations_json_file>"
    exit 1
fi

if [ ! -f "$RECOMMENDATIONS_FILE" ]; then
    echo "Error: Recommendations file '$RECOMMENDATIONS_FILE' not found."
    exit 1
fi

if [ ! -f "$POM_FILE" ]; then
    echo "Error: pom.xml not found in the current directory."
    exit 1
fi

echo "Applying dependency fixes from '$RECOMMENDATIONS_FILE' to '$POM_FILE'..."

# Parse the JSON and iterate over recommendations
# Use `jq` to parse JSON and `read` to iterate line by line
# Check if jq is available. If not, use a Python script to parse and output
if command -v jq &> /dev/null; then
    JSON_PARSER="jq -c '.[] | select(.recommended_action == \"upgrade\")'"
else
    echo "Warning: 'jq' not found. Using Python for JSON parsing, which might be slower."
    JSON_PARSER="python3 -c 'import json, sys; [print(json.dumps(d)) for d in json.load(sys.stdin) if d.get(\"recommended_action\") == \"upgrade\"]'"
fi

# Flag to check if any changes were made
CHANGES_MADE=false

cat "$RECOMMENDATIONS_FILE" | eval "$JSON_PARSER" | while read -r recommendation; do
    GROUP_ID=$(echo "$recommendation" | jq -r '.group_id')
    ARTIFACT_ID=$(echo "$recommendation" | jq -r '.artifact_id')
    CURRENT_VERSION=$(echo "$recommendation" | jq -r '.current_version')
    RECOMMENDED_VERSION=$(echo "$recommendation" | jq -r '.recommended_version')
    STATUS=$(echo "$recommendation" | jq -r '.status')
    DETAILS=$(echo "$recommendation" | jq -r '.details')

    if [ "$CURRENT_VERSION" != "$RECOMMENDED_VERSION" ]; then
        echo "Found '$GROUP_ID:$ARTIFACT_ID' (current: $CURRENT_VERSION, recommended: $RECOMMENDED_VERSION, status: $STATUS)"
        echo "  Details: $DETAILS"

        # Construct XPath for the specific dependency
        # We need to find the <dependency> element that matches both groupId and artifactId
        XPATH="/project/dependencies/dependency[groupId='$GROUP_ID' and artifactId='$ARTIFACT_ID']/version"

        # Check if the dependency with current version exists in pom.xml
        CURRENT_POM_VERSION=$(xmlstarlet sel -t -v "$XPATH" "$POM_FILE" 2>/dev/null)

        if [ "$CURRENT_POM_VERSION" == "$CURRENT_VERSION" ]; then
            echo "  Updating '$GROUP_ID:$ARTIFACT_ID' from $CURRENT_VERSION to $RECOMMENDED_VERSION..."

            # Update the version in pom.xml using xmlstarlet
            # This directly sets the text content of the <version> tag
            xmlstarlet ed -P -S -L -u "$XPATH" -v "$RECOMMENDED_VERSION" "$POM_FILE"

            if [ $? -eq 0 ]; then
                echo "  Successfully updated."
                CHANGES_MADE=true
            else
                echo "  Failed to update. Check xmlstarlet output or XPath."
            fi
        elif [ -n "$CURRENT_POM_VERSION" ]; then
            echo "  Skipping update for '$GROUP_ID:$ARTIFACT_ID'. Current POM version ($CURRENT_POM_VERSION) does not match expected current version ($CURRENT_VERSION)."
        else
            echo "  Skipping update for '$GROUP_ID:$ARTIFACT_ID'. Dependency not found or version element missing in pom.xml."
        fi
    else
        echo "No upgrade needed for '$GROUP_ID:$ARTIFACT_ID' (already at $CURRENT_VERSION)."
    fi
done

if "$CHANGES_MADE"; then
    echo "Dependency updates applied to '$POM_FILE'."
    echo "Staging changes..."
    git add "$POM_FILE"
    echo "Committing changes..."
    # Set Git user for CI/CD context if not already set
    git config --global user.email "aid-bot@example.com"
    git config --global user.name "AIDepGuard Bot"
    git commit -m "AIDepGuard: Auto-fixed dependency vulnerabilities and outdated versions"
    if [ $? -eq 0 ]; then
        echo "Changes committed successfully."
        # In a real CI/CD, you might push here. For this demo, we let the CI/CD workflow handle the push.
        # git push origin HEAD
    else
        echo "Failed to commit changes. Check Git status."
    fi
else
    echo "No dependency updates were applied."
fi

exit 0