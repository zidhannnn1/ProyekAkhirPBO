#!/usr/bin/env bash
# Script peluncur aplikasi Java EcoTrash v4 (Pertemuan 12)
set -e
export PATH="/opt/homebrew/opt/openjdk@17/bin:$PATH"
DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$DIR"
java -jar "$DIR/ecotrash/ecotrash.jar" "$@"
