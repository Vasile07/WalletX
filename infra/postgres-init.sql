SELECT 'CREATE DATABASE walletx_user OWNER walletx'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'walletx_user')\gexec;

SELECT 'CREATE DATABASE walletx_wallet OWNER walletx'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'walletx_wallet')\gexec;

SELECT 'CREATE DATABASE walletx_transfer OWNER walletx'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'walletx_transfer')\gexec;
