#!/bin/bash

# Script to create Teleport users for all application users
# This ensures all users can authenticate via Teleport

TELEPORT_CONFIG_DIR="/home/kali/MMANM/teleport-test"
DB_PATH="/home/kali/MMANM/warehouse-manager/warehouse.db"

echo "Creating Teleport users for all application users..."
echo "=================================================="

# Get all users from database
users=$(sqlite3 "$DB_PATH" "SELECT username, role FROM users;" 2>/dev/null)

if [ -z "$users" ]; then
    echo "Error: Could not read users from database"
    exit 1
fi

# Create Teleport users
while IFS='|' read -r username role; do
    if [ -z "$username" ]; then
        continue
    fi
    
    echo ""
    echo "Processing user: $username (Role: $role)"
    
    # Check if user already exists in Teleport
    if tctl --config="$TELEPORT_CONFIG_DIR/teleport.yaml" users ls 2>/dev/null | grep -q "^$username "; then
        echo "  ✓ User '$username' already exists in Teleport, skipping..."
    else
        echo "  → Creating Teleport user: $username"
        
        # Create user in Teleport with config file
        if tctl --config="$TELEPORT_CONFIG_DIR/teleport.yaml" users add "$username" --roles=access 2>&1; then
            echo "  ✓ Successfully created Teleport user: $username"
        else
            # Check if it's because user already exists (different check)
            if tctl --config="$TELEPORT_CONFIG_DIR/teleport.yaml" users ls 2>/dev/null | grep -q "^$username "; then
                echo "  ✓ User '$username' already exists in Teleport"
            else
                echo "  ✗ Failed to create Teleport user: $username"
                echo "    Try manually: tctl --config=$TELEPORT_CONFIG_DIR/teleport.yaml users add $username --roles=access"
            fi
        fi
    fi
done <<< "$users"

echo ""
echo "=================================================="
echo "Done! All users have been processed."
echo ""
echo "Note: New Teleport users need to complete registration:"
echo "  1. They will receive an invitation email/link"
echo "  2. Or you can generate invitation tokens with:"
echo "     tctl users add <username> --roles=access"
echo ""
echo "To reset a user's password in Teleport:"
echo "  tctl users reset <username>"

