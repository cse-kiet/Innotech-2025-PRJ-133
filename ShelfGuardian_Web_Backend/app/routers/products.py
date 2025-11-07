from datetime import date, timedelta
from typing import List, Optional
from fastapi import APIRouter, Depends, HTTPException, status, Query
from sqlalchemy.orm import Session

from app.database import get_db
from app.models import Product, ProductCategory, User
from app.schemas import ProductCreate, ProductResponse, ProductUpdate
from app.auth import get_current_user  # ✅ Import your auth dependency

router = APIRouter()

@router.post("/", response_model=ProductResponse, status_code=status.HTTP_201_CREATED)
async def create_product(
    product: ProductCreate,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user)  # ✅ Authenticated user
):
    """Create a new product with authenticated user"""
    db_product = Product(**product.model_dump())
    db_product.user_id = current_user.id  # ✅ Proper foreign key reference

    db.add(db_product)
    db.commit()
    db.refresh(db_product)
    return db_product


@router.get("/", response_model=List[ProductResponse])
async def get_products(
    skip: int = Query(0, ge=0),
    limit: int = Query(100, ge=1, le=100),
    category: Optional[ProductCategory] = Query(None),
    db: Session = Depends(get_db),
):
    """Get all products (no auth) + optional category filter"""
    query = db.query(Product)

    if category:
        query = query.filter(Product.category == category)

    return query.offset(skip).limit(limit).all()


@router.get("/{product_id}", response_model=ProductResponse)
async def get_product(
    product_id: int,
    db: Session = Depends(get_db),
):
    """Get product by ID (no auth)"""
    product = db.query(Product).filter(Product.id == product_id).first()

    if not product:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Product not found")

    return product

@router.get("/user/{user_id}")
def get_products_by_user(
    user_id: int,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    if current_user.id != user_id:
        raise HTTPException(
            status_code=403,
            detail="Not allowed to view other users' products"
        )

    products = db.query(Product).filter(Product.user_id == user_id).all()
    return products

@router.put("/{product_id}", response_model=ProductResponse)
async def update_product(
    product_id: int,
    product_update: ProductUpdate,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user)  # ✅ Only owner/admin can update
):
    """Update product"""
    product = db.query(Product).filter(Product.id == product_id).first()

    if not product:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Product not found")

    # ✅ Check ownership (optional but recommended)
    if product.user_id != current_user.id and current_user.role != "admin":
        raise HTTPException(status_code=status.HTTP_403_FORBIDDEN, detail="Not allowed")

    for field, value in product_update.model_dump(exclude_unset=True).items():
        setattr(product, field, value)

    db.commit()
    db.refresh(product)
    return product


@router.delete("/{product_id}", status_code=status.HTTP_204_NO_CONTENT)
async def delete_product(
    product_id: int,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    """Delete product"""
    product = db.query(Product).filter(Product.id == product_id).first()

    if not product:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Product not found")

    if product.user_id != current_user.id and current_user.role != "admin":
        raise HTTPException(status_code=status.HTTP_403_FORBIDDEN, detail="Not allowed")

    db.delete(product)
    db.commit()


@router.get("/category/{category}", response_model=List[ProductResponse])
async def get_products_by_category(
    category: ProductCategory,
    db: Session = Depends(get_db),
):
    """Get products by category (no auth)"""
    return db.query(Product).filter(Product.category == category).all()


@router.get("/expiring/soon", response_model=List[ProductResponse])
async def get_expiring_products(
    days: int = Query(7, ge=1),
    db: Session = Depends(get_db),
):
    """Get products expiring within X days (no auth)"""
    today = date.today()
    expiry_date = today + timedelta(days=days)

    return db.query(Product).filter(
        Product.expiry_date >= today,
        Product.expiry_date <= expiry_date
    ).order_by(Product.expiry_date).all()
