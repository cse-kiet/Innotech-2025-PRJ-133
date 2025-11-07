from app.database import SessionLocal
from app.models import User

def show_users():
    db = SessionLocal()
    try:
        users = db.query(User).all()
        if not users:
            print("No users found")
            return
        print("id\tusername\temail")
        for u in users:
            print(f"{u.id}\t{u.username}\t{u.email}")
    finally:
        db.close()

if __name__ == "__main__":
    show_users()