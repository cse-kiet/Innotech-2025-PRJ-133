# PostgreSQL Setup Guide

## Step 1: Install PostgreSQL

If you haven't installed PostgreSQL yet:

1. Download PostgreSQL from: https://www.postgresql.org/download/windows/
2. Run the installer
3. During installation, remember:
   - **Port**: Default is `5432`
   - **Username**: Default is `postgres`
   - **Password**: Set a password (remember it!)

## Step 2: Verify PostgreSQL is Running

Open PowerShell or Command Prompt and check if PostgreSQL service is running:

```powershell
# Check PostgreSQL service status
Get-Service postgresql*
```

Or check if you can connect:
```powershell
psql -U postgres
```

## Step 3: Create the Database

Open PowerShell and connect to PostgreSQL:

```powershell
# Connect to PostgreSQL (you'll be prompted for password)
psql -U postgres
```

Then create the database:

```sql
CREATE DATABASE expiry_tracker;
```

Exit psql:
```sql
\q
```

**Alternative method** (without entering psql):
```powershell
# Windows PowerShell
$env:PGPASSWORD="your_password"; createdb -U postgres expiry_tracker
```

## Step 4: Configure Database Connection

You have three options:

### Option A: Use Default Settings (Easiest)
If your PostgreSQL has:
- Username: `postgres`
- Password: `postgres`
- Host: `localhost`
- Port: `5432`

Just run the application - it will use these defaults.

### Option B: Set Environment Variable (Recommended)

**Windows PowerShell:**
```powershell
$env:DATABASE_URL="postgresql://postgres:YOUR_PASSWORD@localhost:5432/expiry_tracker"
```

**Windows Command Prompt:**
```cmd
set DATABASE_URL=postgresql://postgres:YOUR_PASSWORD@localhost:5432/expiry_tracker
```

### Option C: Create .env File

Create a `.env` file in the project root:
```
DATABASE_URL=postgresql://postgres:YOUR_PASSWORD@localhost:5432/expiry_tracker
```

Then install python-dotenv:
```bash
pip install python-dotenv
```

And update `app/database.py` to load from .env (optional).

## Step 5: Test the Connection

1. Make sure PostgreSQL is running
2. Make sure the database `expiry_tracker` exists
3. Start your FastAPI application:
```bash
uvicorn main:app --reload
```

If you see no database connection errors, you're good to go!

## Troubleshooting

### Error: "connection refused" or "could not connect"
- Check if PostgreSQL service is running
- Verify the host and port (default: localhost:5432)

### Error: "database does not exist"
- Make sure you created the database: `CREATE DATABASE expiry_tracker;`

### Error: "password authentication failed"
- Check your username and password
- Make sure the DATABASE_URL has the correct credentials

### Check PostgreSQL Connection String Format
```
postgresql://username:password@host:port/database_name
```

Example:
```
postgresql://postgres:mypassword@localhost:5432/expiry_tracker
```

