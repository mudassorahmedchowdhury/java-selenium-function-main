#!/usr/bin/env bash
# Creates/updates the project labels with the GitHub CLI (run once after `gh auth login`).
set -e
gh label create "good first issue" -c 7057ff -d "Good for newcomers" --force
gh label create "help wanted" -c 008672 -d "Extra attention is needed" --force
gh label create "bug" -c d73a4a -d "Something is not working" --force
gh label create "enhancement" -c a2eeef -d "New feature or request" --force
gh label create "documentation" -c 0075ca -d "Docs improvements" --force
gh label create "tests" -c fbca04 -d "Test coverage or flakiness" --force
gh label create "component" -c 1d76db -d "New or changed UI component" --force
gh label create "question" -c d876e3 -d "Further information requested" --force
