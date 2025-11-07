from app.database import SessionLocal
from app.models import User
from passlib.context import CryptContext

pwd_context = CryptContext(schemes=["bcrypt"], deprecated="auto")

def create_default_user():
    db = SessionLocal()
    try:
        # Check if default user exists
        default_user = db.query(User).filter(User.email == "admin@example.com").first()
        if not default_user:
            # Create default user
            hashed_password = pwd_context.hash("admin123")
            default_user = User(
                email="admin@example.com",
                username="admin",
                hashed_password=hashed_password
            )
            db.add(default_user)
            db.commit()
            print("Default user created successfully")
            return default_user
        print("Default user already exists")
        return default_user
    except Exception as e:
        print(f"Error creating default user: {e}")
        db.rollback()
        raise
    finally:
        db.close()

if __name__ == "__main__":
    create_default_user()