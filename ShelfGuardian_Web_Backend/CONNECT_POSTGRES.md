# Connect to Your Existing PostgreSQL

Since you already have PostgreSQL installed, follow these steps:

## Step 1: Create the Database

You need to create the `expiry_tracker` database. Choose one method:

### Method A: Using psql (Recommended)
```powershell
# Connect to PostgreSQL (will ask for your password)
psql -U postgres

# Then run:
CREATE DATABASE expiry_tracker;

# Exit psql
\q
```

### Method B: One-line command
```powershell
# This will prompt for password
psql -U postgres -c "CREATE DATABASE expiry_tracker;"
```

## Step 2: Configure Connection String

Check your PostgreSQL credentials:
- **Username**: Usually `postgres` (or your custom username)
- **Password**: The password you set during PostgreSQL installation
- **Port**: Usually `5432` (default)
- **Host**: Usually `localhost`

### Option A: If your credentials match the default
If your PostgreSQL has:
- Username: `postgres`
- Password: `postgres`
- Port: `5432`

Then **you're ready!** Just run:
```bash
uvicorn main:app --reload
```

### Option B: If your credentials are different
Set the environment variable before running the app:

**Windows PowerShell:**
```powershell
$env:DATABASE_URL="postgresql://YOUR_USERNAME:YOUR_PASSWORD@localhost:5432/expiry_tracker"
uvicorn main:app --reload
```

**Example:**
```powershell
$env:DATABASE_URL="postgresql://postgres:mypassword123@localhost:5432/expiry_tracker"
```

## Step 3: Test the Connection

Run your FastAPI app:
```bash
uvicorn main:app --reload
```

If you see no errors, the connection is working!

## Quick Check Commands

To verify your PostgreSQL setup:
```powershell
# Check PostgreSQL version (already confirmed: 17.5)
psql --version

# List all databases
psql -U postgres -l

# Connect and check if expiry_tracker exists
psql -U postgres -c "\l" | findstr expiry_tracker
```

