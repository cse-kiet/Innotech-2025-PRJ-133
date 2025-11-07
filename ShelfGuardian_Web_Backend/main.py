from fastapi import FastAPI, Request
from fastapi.middleware.cors import CORSMiddleware
from app.database import engine, Base, SessionLocal
from app.routers import auth, products, chat
import logging
from datetime import datetime, timedelta
from apscheduler.schedulers.background import BackgroundScheduler
from sqlalchemy.orm import Session
from app.models import Product



# Create database tables
Base.metadata.create_all(bind=engine)

app = FastAPI(
    title="Expiry Tracker API",
    description="Backend API for tracking product expiry dates",
    version="1.0.0"
)

# basic logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger("expirytracker")

# ------------------------ EXPIRY CLEANUP LOGIC ------------------------

def delete_expired_products():
    db: Session = SessionLocal()
    try:
        threshold_date = datetime.now() - timedelta(days=7)
        deleted = db.query(Product).filter(Product.expiry_date < threshold_date).delete()
        db.commit()

        if deleted:
            print(f"Deleted {deleted} expired products older than 7 days")
        else:
            print("No expired products to delete")
    except Exception as e:
        print("Error during cleanup:", e)
    finally:
        db.close()

def start_scheduler():
    scheduler = BackgroundScheduler()
    scheduler.add_job(delete_expired_products, "interval", days=1)
    scheduler.start()
    print("Scheduler started — expired product cleanup running daily")

# Start scheduler
start_scheduler()

# -------------------------- REQUEST LOGGING --------------------------
@app.middleware("http")
async def log_requests(request: Request, call_next):
    origin = request.headers.get("origin")
    logger.info(f"Incoming request: {request.method} {request.url.path} Origin={origin}")
    response = await call_next(request)
    aca = response.headers.get("access-control-allow-origin")
    logger.info(f"Response for {request.method} {request.url.path}: access-control-allow-origin={aca}")
    return response

# --------------------------- CORS SETTINGS ----------------------------
app.add_middleware(
    CORSMiddleware,
    allow_origins=[
        "http://localhost:5174",
        "http://127.0.0.1:5174",
        "http://localhost:5173",
        "http://127.0.0.1:5173",
        "http://localhost:4173",
        "http://127.0.0.1:4173",
    ],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
    expose_headers=["*"],
    max_age=600,
)

# --------------------------- DEBUG ROUTES -----------------------------
@app.options("/api/debug", include_in_schema=False)
async def debug_options(request: Request):
    return {
        "ok": True,
        "note": "preflight handled",
        "origin": request.headers.get("origin")
    }

@app.post("/api/debug")
async def debug_echo(request: Request):
    body = await request.body()
    return {
        "headers": dict(request.headers),
        "body": body.decode("utf-8", errors="ignore")
    }

# ----------------------------- ROUTERS -------------------------------
app.include_router(auth.router, prefix="/api/auth", tags=["authentication"])
app.include_router(products.router, prefix="/api/products", tags=["products"])
app.include_router(chat.router, prefix="/api/chat", tags=["chat"])
# ----------------------------- HEALTH -------------------------------
@app.get("/")
async def root():
    return {"message": "Expiry Tracker API"}

@app.get("/api/health")
async def health_check():
    return {"status": "healthy"}
