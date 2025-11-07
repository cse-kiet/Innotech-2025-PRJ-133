from fastapi import FastAPI
from app.routers import products_router, users_router, chat_router


app = FastAPI()


app.include_router(products_router, prefix="/products", tags=["products"])
app.include_router(users_router, prefix="/users", tags=["users"])
app.include_router(chat_router, prefix="/chat", tags=["chat"])


