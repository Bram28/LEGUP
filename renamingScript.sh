#!/bin/sh
# WARNING: Does not do backups. Be careful with regexes, espescially if you have uncommitted changes.
# Usage: ./renamingScript.sh FROM TO
FROM="$1"
TO="$2"
grep -r "$FROM" code/edu/rpi/phil/legup | awk '{print $1}' | sort -u | grep .java | sed 's|:$||' | xargs sed -i "s|$FROM|$TO|g"
