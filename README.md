# concourse-github-app-token
![Docker Image Size (tag)](https://img.shields.io/docker/image-size/tenjaa/concourse-github-app-token/latest)

## What?
This concourse resource gets you an installation token for your GitHub App.

## Why?
Accessing the GitHub API typically requires some authentication, like [setting a commit status](https://docs.github.com/en/rest/reference/repos#statuses).
Using a shared [machine user](https://developer.github.com/v3/guides/managing-deploy-keys/#machine-users) might not always be wanted or possible (e.g. GitHub Enterprise restrictions).

Also I just wanted to use GraalVM and needed a project with actual use :)

## Example
```yaml
resource_types:
- name: github-token-resource
  type: registry-image
  source:
    repository: tenjaa/concourse-github-app-token

resources:
- name: github-token
  type: github-token-resource
  source:
    appId: ((github-app-id)) # can be looked up on the overview page of your app
    privateKey: ((github-app-private-key)) # https://docs.github.com/en/developers/apps/authenticating-with-github-apps#generating-a-private-key
    user: tenjaa # get a token for an app installed to a user account
      # OR
    org: my-org # get a token for an app installed to an org account

jobs:
- name: my-job
  plan:
  - put: github-token # put forces to get a new token, even when rerunning a build (https://concourse-ci.org/builds.html#build-rerunning)
  - load_var: token
    file: github-token/token
  - task: print-env
    config:
      platform: linux
      image_resource:
        type: registry-image
        source: { repository: alpine }
      params:
        TOKEN: ((.:token))
      run:
        path: env
```

## Future use
A combination of [var sources](https://github.com/concourse/rfcs/blob/master/039-var-sources/proposal.md) and [prototypes](https://github.com/concourse/rfcs/blob/master/037-prototypes/proposal.md) will greatly improve the usability of this resource.

## Links
- https://docs.github.com/en/developers/apps
- https://docs.github.com/en/rest/reference/apps#create-an-installation-access-token-for-an-app
- https://developer.github.com/changes/2020-02-14-deprecating-password-auth/
