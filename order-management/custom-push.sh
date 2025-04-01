#!/bin/bash

echo "🔍 Running tests before pushing..."
mvn test

if [ $? -eq 0 ]; then
  echo "✅ Tests passed! Proceeding to push..."
  git push "$@"
else
  echo "❌ Tests failed! Push aborted."
  exit 1
fi
