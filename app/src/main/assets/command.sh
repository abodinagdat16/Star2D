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
find "$LOCAL_PATH" -type f | while IFS= read -r FILE; do
  # Increment the current file counter
  CURRENT_FILE=$((CURRENT_FILE + 1))
  
  # Extract the file name only (not the full path)
  FILE_NAME=$(basename "$FILE")
  
  # Display the progress and file name
  echo -ne "Processing file $CURRENT_FILE/$TOTAL_FILES: $FILE_NAME\r"

  # Check if the file exists (extra safety)
  if [ ! -f "$FILE" ]; then
    echo -e "\nError: File not found - $FILE"
    continue
  fi

  # Get the relative file path
  RELATIVE_PATH=${FILE#$LOCAL_PATH/}

  # Encode the file content
  CONTENT=$(base64 -w 0 "$FILE")
  if [ $? -ne 0 ]; then
    echo -e "\nError: Failed to encode file - $FILE_NAME"
    continue
  fi

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