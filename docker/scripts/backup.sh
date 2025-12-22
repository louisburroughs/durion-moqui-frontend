#!/bin/bash

# Agent Backup Script
# Runs every 4 hours to backup agent data and database

set -e

BACKUP_DIR="/backup"
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
DB_HOST="postgres"
DB_NAME="moqui"
DB_USER="moqui_user"

echo "Starting backup at $(date)"

# Create backup directory
mkdir -p "$BACKUP_DIR/$TIMESTAMP"

# Backup PostgreSQL database
echo "Backing up database..."
pg_dump -h "$DB_HOST" -U "$DB_USER" -d "$DB_NAME" > "$BACKUP_DIR/$TIMESTAMP/moqui_db_$TIMESTAMP.sql"

# Backup agent registry from Redis
echo "Backing up agent registry..."
redis-cli -h redis --rdb "$BACKUP_DIR/$TIMESTAMP/agent_registry_$TIMESTAMP.rdb"

# Create backup manifest
cat > "$BACKUP_DIR/$TIMESTAMP/backup_manifest.json" << EOF
{
  "timestamp": "$TIMESTAMP",
  "backup_date": "$(date -Iseconds)",
  "database_backup": "moqui_db_$TIMESTAMP.sql",
  "registry_backup": "agent_registry_$TIMESTAMP.rdb",
  "backup_type": "scheduled",
  "retention_days": 30
}
EOF

# Compress backup
echo "Compressing backup..."
cd "$BACKUP_DIR"
tar -czf "agent_backup_$TIMESTAMP.tar.gz" "$TIMESTAMP/"
rm -rf "$TIMESTAMP/"

# Cleanup old backups (keep 30 days)
echo "Cleaning up old backups..."
find "$BACKUP_DIR" -name "agent_backup_*.tar.gz" -mtime +30 -delete

echo "Backup completed successfully at $(date)"

# Verify backup integrity
if [ -f "$BACKUP_DIR/agent_backup_$TIMESTAMP.tar.gz" ]; then
    echo "Backup verification: SUCCESS"
    exit 0
else
    echo "Backup verification: FAILED"
    exit 1
fi
