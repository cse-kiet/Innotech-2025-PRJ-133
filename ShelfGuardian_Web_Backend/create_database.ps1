# PowerShell script to create PostgreSQL database
# Make sure PostgreSQL is installed and running

# Replace 'postgres' with your PostgreSQL username if different
# Replace 'your_password' with your PostgreSQL password
$env:PGPASSWORD="your_password"

# Create the database
createdb -U postgres expiry_tracker

Write-Host "Database 'expiry_tracker' created successfully!"
Write-Host "If you see an error, you can also create it manually using:"
Write-Host "psql -U postgres"
Write-Host "Then run: CREATE DATABASE expiry_tracker;"

