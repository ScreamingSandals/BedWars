#!/bin/bash

doc_version=$(cat .version | xargs)

git config user.name github-actions[bot]
git config user.email 41898282+github-actions[bot]@users.noreply.github.com
git fetch origin gh-pages --depth=1

grep -rl 'LATEST_VERSION_HERE' ./docs | xargs sed -i "s/LATEST_VERSION_HERE/${doc_version}/g"
mike set-default latest

if [[ "$doc_version" == *SNAPSHOT ]]; then
  mike deploy --push --branch gh-pages "$doc_version"
else
  doc_version="$(cat .version | xargs | cut -d. -f1,2).x"

  latest=$(mike list --branch gh-pages | grep "[latest]" | cut -d '[' -f1 | xargs)

  if [[ "$(printf '%s\n' "$doc_version" "$latest" | sort -V | head -n1)" == "$doc_version" ]]; then
    mike deploy --push --branch gh-pages "$doc_version"
  else
    mike deploy --push --branch gh-pages --update-aliases "$doc_version" latest
  fi
fi
