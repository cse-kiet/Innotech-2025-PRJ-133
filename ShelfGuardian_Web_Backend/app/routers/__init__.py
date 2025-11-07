from app.routers.products import router as products_router
from app.routers.users import router as users_router
from app.routers.chat import router as chat_router

__all__ = ['products_router', 'users_router', 'chat_router']