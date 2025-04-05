#!/bin/bash
# Quick and dirty script to test the announcement API endpoints
# I cannot fucking BELIEVE that this works lmao

AUTH_TOKEN="Bearer eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJ0ZXN0bG9naW4iLCJpYXQiOjE3NDM4NzM1MDYsInJvbGVzIjpbIlVTRVIiXSwiZXhwIjoxNzQ0NDc4MzA2fQ.taVdXHUicw4KpM938ffQlDYXoo5rYtVMNEhRcazByZod221M2K_ydM3-_F25C7Qk"
ADMIN_TOKEN="Bearer eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTc0MzYyNDA0Nywicm9sZXMiOlsiQURNSU4iLCJVU0VSIl0sImV4cCI6MTc0NDIyODg0N30.W3h8MGWEGS_BRSHfjB1e4kaf_g-oRhypS8vw0Iwup2tyhe_lW2e7597PZet-LPqn"
BASE_URL="http://localhost:8080"

ANNOUNCEMENT_ID=""

echo "=== Testing Announcement API ==="

echo -e "\n\n=== Creating Announcement ==="
RESPONSE=$(curl -s -X POST "${BASE_URL}/api/announcements/create" \
  -H "Authorization: ${AUTH_TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Test Announcement",
    "body": "This is a test announcement body with detailed information.",
    "tags": ["test", "announcement", "api"],
    "roleRestrictions": ["USER"]
  }')
echo $RESPONSE
ANNOUNCEMENT_ID=$(echo $RESPONSE | grep -o '"announcementId":[0-9]*' | grep -o '[0-9]*')
echo "Created announcement with ID: $ANNOUNCEMENT_ID"

echo -e "\n\n=== Getting Announcement ==="
curl -s -X POST "${BASE_URL}/api/announcements/get" \
  -H "Authorization: ${AUTH_TOKEN}" \
  -H "Content-Type: application/json" \
  -d "{
    \"id\": $ANNOUNCEMENT_ID
  }" | jq .

echo -e "\n\n=== Searching Announcements ==="
curl -s -X POST "${BASE_URL}/api/announcements/search" \
  -H "Authorization: ${AUTH_TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "searchTerm": "test"
  }' | jq .

echo -e "\n\n=== Updating Announcement ==="
curl -s -X POST "${BASE_URL}/api/announcements/update" \
  -H "Authorization: ${AUTH_TOKEN}" \
  -H "Content-Type: application/json" \
  -d "{
    \"id\": $ANNOUNCEMENT_ID,
    \"title\": \"Updated Announcement\",
    \"body\": \"This announcement has been updated with new content.\",
    \"tags\": [\"updated\", \"test\", \"api\"],
    \"roleRestrictions\": [\"USER\", \"STUDENT\"]
  }" | jq .

echo -e "\n\n=== Reporting Announcement ==="
curl -s -X POST "${BASE_URL}/api/announcements/report" \
  -H "Authorization: ${AUTH_TOKEN}" \
  -H "Content-Type: application/json" \
  -d "{
    \"announcementId\": $ANNOUNCEMENT_ID,
    \"reason\": \"This announcement contains inappropriate content\"
  }" | jq .

echo -e "\n\n=== Getting Reports (Admin Only) ==="
curl -s -X POST "${BASE_URL}/api/announcements/get-reports" \
  -H "Authorization: ${ADMIN_TOKEN}" \
  -H "Content-Type: application/json" \
  -d "{
    \"id\": $ANNOUNCEMENT_ID
  }" | jq .

echo -e "\n\n=== Deleting Announcement ==="
curl -s -X POST "${BASE_URL}/api/announcements/delete" \
  -H "Authorization: ${AUTH_TOKEN}" \
  -H "Content-Type: application/json" \
  -d "{
    \"id\": $ANNOUNCEMENT_ID
  }" | jq .

echo -e "\n\nTesting complete!"

