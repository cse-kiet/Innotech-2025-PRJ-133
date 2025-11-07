from pydantic import BaseModel, EmailStr, ConfigDict
from datetime import date
from typing import Optional
from app.models import ProductCategory


# User Schemas
class UserBase(BaseModel):
    email: EmailStr
    username: str


class UserCreate(UserBase):
    password: str


class UserResponse(UserBase):
    id: int
    
    model_config = ConfigDict(from_attributes=True)


# Auth Schemas
class Token(BaseModel):
    access_token: str
    token_type: str


class TokenData(BaseModel):
    email: Optional[str] = None


# Product Schemas
class ProductBase(BaseModel):
    name: str
    category: ProductCategory
    expiry_date: date
    quantity: int = 1
    description: Optional[str] = None


class ProductCreate(ProductBase):
    pass


class ProductUpdate(BaseModel):
    name: Optional[str] = None
    category: Optional[ProductCategory] = None
    expiry_date: Optional[date] = None
    quantity: Optional[int] = None
    description: Optional[str] = None


class ProductResponse(ProductBase):
    id: int
    user_id: int
    
    model_config = ConfigDict(from_attributes=True)

