#!/bin/sh
set -eu

api_base_url="${VITE_API_BASE_URL:-http://localhost/api}"
shell_url="${VITE_SHELL_URL:-http://localhost}"
auth_remote_url="${VITE_AUTH_MFE_REMOTE_URL:-http://localhost/auth/remoteEntry.js}"
wallet_remote_url="${VITE_WALLET_MFE_REMOTE_URL:-http://localhost/wallet/remoteEntry.js}"
transfer_remote_url="${VITE_TRANSFER_MFE_REMOTE_URL:-http://localhost/transfer/remoteEntry.js}"
notification_remote_url="${VITE_NOTIFICATION_MFE_REMOTE_URL:-http://localhost/notification/remoteEntry.js}"

for file in $(find /usr/share/nginx/html -type f \( -name '*.js' -o -name '*.html' \)); do
  sed -i "s#http://localhost:8080/api#${api_base_url}#g" "$file"
  sed -i "s#http://localhost:5173#${shell_url}#g" "$file"
  sed -i "s#http://localhost/auth/remoteEntry.js#${auth_remote_url}#g" "$file"
  sed -i "s#http://localhost/auth#${auth_remote_url%/remoteEntry.js}#g" "$file"
  sed -i "s#http://localhost/wallet/remoteEntry.js#${wallet_remote_url}#g" "$file"
  sed -i "s#http://localhost/wallet#${wallet_remote_url%/remoteEntry.js}#g" "$file"
  sed -i "s#http://localhost/transfer/remoteEntry.js#${transfer_remote_url}#g" "$file"
  sed -i "s#http://localhost/transfer#${transfer_remote_url%/remoteEntry.js}#g" "$file"
  sed -i "s#http://localhost/notification/remoteEntry.js#${notification_remote_url}#g" "$file"
  sed -i "s#http://localhost/notification#${notification_remote_url%/remoteEntry.js}#g" "$file"
done
