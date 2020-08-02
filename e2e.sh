#!/bin/bash

image="tenjaa/concourse-github-app-token:${GITHUB_SHA}"

docker pull "${image}"

checkEmpty=$(echo '{}' | docker run -i "${image}" /opt/resource/check)
checkReturnGiven=$(echo "{\"version\":{\"date\":\"some-date\"}}" | docker run -i "${image}" /opt/resource/check)

if [[ "$checkEmpty" != "[]" ]]; then
  echo "Invalid checkEmpty: ${checkEmpty}"
  exit 1
fi

if [[ "$checkReturnGiven" != '[{"date":"some-date"}]' ]]; then
  echo "Invalid checkReturnGiven: ${checkReturnGiven}"
  exit 1
fi

echo "Testing CHECK successfully done"

outDate=$(echo 'stdin is ignored anyway' | docker run -i "${image}" /opt/resource/out | jq -r '.version.date')

if ! date --iso-8601 -d "${outDate}"; then
  echo "Error parsing date: outDate"
  exit 1
fi

echo "Testing OUT successfully done"

inRequest="{\"source\":{\"appId\":${APP_ID},\"user\":\"tenjaa\",\"privateKey\":\"${PRIVATE_KEY}\"},\"version\":{\"date\":\"some-date\"}}"
inResponse=$(echo "${inRequest}" | docker run -i "${image}" /opt/resource/in "/")

if [[ "$inResponse" != '{"version":{"date":"some-date"}}' ]]; then
  echo "Invalid inResponse: ${inResponse}"
  exit 1
fi

containerId=$(docker ps -a -q -l)
docker cp "${containerId}:/token" .
repo=$(curl -s -H "Accept: application/vnd.github.machine-man-preview+json" -H "Authorization: token $(cat token)" "https://api.github.com/installation/repositories" | jq -r '.repositories[0].name')
if [[ "$repo" != 'concourse-github-app-token' ]]; then
  echo "Invalid repo: ${repo}"
  exit 1
fi

echo "Testing IN successfully done"
