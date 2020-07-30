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

outDate=$(echo 'stdin is ignored anyway' | docker run -i "${image}" /opt/resource/out | jq '.version.date')

if ! date --iso-8601 -d "${outDate}"; then
  echo "Error parsing date: outDate"
fi
