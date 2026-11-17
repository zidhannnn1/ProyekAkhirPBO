#!/usr/bin/env bash
# Script penjalankan seluruh pengujian unit EcoTrash v3 (Pertemuan 11)
export PATH="/opt/homebrew/opt/openjdk@17/bin:$PATH"
DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
java -cp "$DIR/ecotrash/bin" id.ac.kampus.ecotrash.KodeUjiSederhana

