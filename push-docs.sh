#!/bin/sh

if [[ "$TRAVIS_REPO_SLUG" == "smarter-travel-media/st-metrics" ]] && [[ "$TRAVIS_BRANCH" == "master" ]] && [[ "$TRAVIS_PULL_REQUEST" == "false" ]]; then
    mvn -P publish -D github.site.oauth2Token=$GH_CI_TOKEN clean site-deploy
fi
