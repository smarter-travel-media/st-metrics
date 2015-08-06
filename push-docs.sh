#!/bin/sh

mvn -P publish -D github.site.oauth2Token=$GH_CI_TOKEN clean site-deploy

