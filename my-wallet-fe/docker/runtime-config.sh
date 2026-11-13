#!/bin/sh
set -eu

api_base_url="${VITE_API_BASE_URL:-http://localhost:8080/api}"
shell_url="${VITE_SHELL_URL:-http://localhost:5173}"
auth_remote_url="${VITE_AUTH_MFE_REMOTE_URL:-http://localhost:5174/remoteEntry.js}"
wallet_remote_url="${VITE_WALLET_MFE_REMOTE_URL:-http://localhost:5175/remoteEntry.js}"
transfer_remote_url="${VITE_TRANSFER_MFE_REMOTE_URL:-http://localhost:5176/remoteEntry.js}"
notification_remote_url="${VITE_NOTIFICATION_MFE_REMOTE_URL:-http://localhost:5177/remoteEntry.js}"

for file in $(find /usr/share/nginx/html -type f \( -name '*.js' -o -name '*.html' \)); do
  sed -i "s#http://localhost:8080/api#${api_base_url}#g" "$file"
  sed -i "s#http://localhost:5173#${shell_url}#g" "$file"
  sed -i "s#http://localhost:5174/remoteEntry.js#${auth_remote_url}#g" "$file"
  sed -i "s#http://localhost:5174#${auth_remote_url%/remoteEntry.js}#g" "$file"
  sed -i "s#http://localhost:5175/remoteEntry.js#${wallet_remote_url}#g" "$file"
  sed -i "s#http://localhost:5175#${wallet_remote_url%/remoteEntry.js}#g" "$file"
  sed -i "s#http://localhost:5176/remoteEntry.js#${transfer_remote_url}#g" "$file"
  sed -i "s#http://localhost:5176#${transfer_remote_url%/remoteEntry.js}#g" "$file"
  sed -i "s#http://localhost:5177/remoteEntry.js#${notification_remote_url}#g" "$file"
  sed -i "s#http://localhost:5177#${notification_remote_url%/remoteEntry.js}#g" "$file"
  sed -i "s#http://localhost:5174/remoteEntry.js#${auth_remote_url}#g" "$file"
done
