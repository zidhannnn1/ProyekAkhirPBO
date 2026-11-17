#!/usr/bin/env bash
# Script peluncur aplikasi Java EcoTrash Bank Sampah RT (Pertemuan 11)
export PATH="/opt/homebrew/opt/openjdk@17/bin:$PATH"
DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
java -jar "$DIR/ecotrash/ecotrash.jar" "$@"
