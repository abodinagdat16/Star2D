#!/bin/bash

# Define variables
REPO="abodinagdat16/Star2D"
BRANCH="master"
TARGET_PATH="app/src/main/assets"
LOCAL_PATH="./"

# Ensure the script uses bash
if [ -z "$BASH_VERSION" ]; then
  echo "Please run this script with bash."
  exit 1
fi

# Calculate the total number of files
TOTAL_FILES=$(find "$LOCAL_PATH" -type f | wc -l)
CURRENT_FILE=0

# Iterate over files in the local folder
for FILE in $(find "$LOCAL_PATH" -type f); do
  # Increment the current file counter
  CURRENT_FILE=$((CURRENT_FILE + 1))
  
  # Display the progress at the bottom
  echo -ne "Processing file $CURRENT_FILE/$TOTAL_FILES\r"

  # Get the relative file path
  RELATIVE_PATH=${FILE#$LOCAL_PATH/}

  # Read file content
  CONTENT=$(base64 -w 0 "$FILE")

  # Check if the file exists in the repository
  API_RESPONSE=$(gh api "/repos/$REPO/contents/$TARGET_PATH/$RELATIVE_PATH" --jq '.sha' 2>/dev/null)

  # If the file exists, include the sha; otherwise, create a new file
  if [ -n "$API_RESPONSE" ]; then
    gh api \
      -X PUT \
      -H "Accept: application/vnd.github.v3+json" \
      "/repos/$REPO/contents/$TARGET_PATH/$RELATIVE_PATH" \
      -F message="Update $RELATIVE_PATH" \
      -F content="$CONTENT" \
      -F sha="$API_RESPONSE" \
      -F branch="$BRANCH"
  else
    gh api \
      -X PUT \
      -H "Accept: application/vnd.github.v3+json" \
      "/repos/$REPO/contents/$TARGET_PATH/$RELATIVE_PATH" \
      -F message="Add $RELATIVE_PATH" \
      -F content="$CONTENT" \
      -F branch="$BRANCH"
  fi
done

# Print completion message
echo -e "\nAll files processed: $TOTAL_FILES"