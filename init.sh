#!/usr/bin/env bash

# reset git
rm -rf .git
git init

# set project name
echo -n "What is your project name?"
read answer
sed -i -e "s/spotless-kotlin/$answer/g" ./settings.gradle

# set owning organization
echo -n "What is your organization name?"
read answer
sed -i -e "s/<insert-company-name-here>/$answer/g" ./gradle/spotless.kotlin.license

# clear the readme
> README.md

# set-up git remote
echo -n "What is your git remote?"
read answer
git remote add origin ${answer}

# initial commit
git add -A
git commit -m "Initial commit"
git push