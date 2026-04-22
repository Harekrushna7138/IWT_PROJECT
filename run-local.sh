#!/usr/bin/env bash
set -euo pipefail

PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
DEFAULT_TOMCAT_WEBAPPS="/opt/homebrew/opt/tomcat/libexec/webapps"
LEGACY_TOMCAT_WEBAPPS="/opt/homebrew/var/lib/tomcat/webapps"
TOMCAT_WEBAPPS="${TOMCAT_WEBAPPS:-$DEFAULT_TOMCAT_WEBAPPS}"

if [ ! -d "$TOMCAT_WEBAPPS" ] && [ -d "$LEGACY_TOMCAT_WEBAPPS" ]; then
  TOMCAT_WEBAPPS="$LEGACY_TOMCAT_WEBAPPS"
fi

echo "==> Checking required commands"
for cmd in java mvn mysql; do
  if ! command -v "$cmd" >/dev/null 2>&1; then
    echo "Missing command: $cmd"
    echo "Install prerequisites first, then rerun."
    exit 1
  fi
done

echo "==> Building WAR"
cd "$PROJECT_DIR"
mvn -q -DskipTests package

echo "==> Copying WAR to Tomcat webapps"
mkdir -p "$TOMCAT_WEBAPPS"
cp -f "$PROJECT_DIR/target/library-room-booking.war" "$TOMCAT_WEBAPPS/"
rm -rf "$TOMCAT_WEBAPPS/library-room-booking"

echo "==> Starting Tomcat service"
brew services restart tomcat >/dev/null 2>&1 || true

echo "==> Done"
echo "Tomcat webapps: $TOMCAT_WEBAPPS"
echo "Open: http://localhost:8080/library-room-booking"
echo "Admin: admin@college.edu / admin123"
