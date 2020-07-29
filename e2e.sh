#!/bin/bash

image="tenjaa/concourse-github-app-token:${GITHUB_SHA}"

docker pull "${image}"

checkEmpty=$(echo '{}' | docker run "${image}" /opt/resource/check)
checkReturnGiven=$(echo '{"version":{"date":"some-date"}}' | docker run "${image}" /opt/resource/check)

if [[ "$checkEmpty" != "[]" ]]; then
  echo "Invalid checkEmpty: ${checkEmpty}"
  exit 1
fi

if [[ "$checkReturnGiven" != '[{"date":"some-date"}]' ]]; then
  echo "Invalid checkEmpty: ${checkReturnGiven}"
  exit 1
fi
