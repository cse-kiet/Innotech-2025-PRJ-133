from langchain_core.tools import tool
from langchain_google_genai import ChatGoogleGenerativeAI
from datetime import datetime, timedelta
from sqlalchemy import cast, String
from app.routers.database.db import SessionLocal
from app.routers.database.chat_models import Product, ProductCategory  # ✅ Import enum
from dotenv import load_dotenv
import re

load_dotenv()

# ------------------------------------------------
# 🤖 LLM Definition
# ------------------------------------------------
llm = ChatGoogleGenerativeAI(model="gemini-2.5-pro")

# ------------------------------------------------
# 🧩 Helper: Category Normalizer
# ------------------------------------------------
def normalize_category(cat: str) -> ProductCategory | None:
    if not cat:
        return None
    cat = cat.lower().strip()

    mapping = {
        # 🥖 Food
        "food": ProductCategory.FOOD,
        "foods": ProductCategory.FOOD,
        "grocery": ProductCategory.FOOD,
        "groceries": ProductCategory.FOOD,
        "edible": ProductCategory.FOOD,

        # 💊 Medicine
        "medicine": ProductCategory.MEDICINE,
        "medicines": ProductCategory.MEDICINE,
        "drug": ProductCategory.MEDICINE,
        "drugs": ProductCategory.MEDICINE,
        "pharmacy": ProductCategory.MEDICINE,

        # 🧼 MISCELLANEOUS
        "MISCELLANEOUS": ProductCategory.MISCELLANEOUS,
        "miscellaneous": ProductCategory.MISCELLANEOUS,
        "non-food": ProductCategory.MISCELLANEOUS,
        "nonfood": ProductCategory.MISCELLANEOUS,
        "other": ProductCategory.MISCELLANEOUS,
        "others": ProductCategory.MISCELLANEOUS,
    }

    return mapping.get(cat)



# ------------------------------------------------
# 🧩 Tool 1: Expiry Check
# ------------------------------------------------
@tool
def expiry_check_tool(user_id: int = 1) -> dict:
    """Fetch products expiring within the next 7 days for a specific user."""
    db = SessionLocal()
    today = datetime.now().date()
    upcoming = today + timedelta(days=7)

    products = (
        db.query(Product)
        .filter(Product.user_id == user_id, Product.expiry_date <= upcoming)
        .order_by(Product.expiry_date.asc())
        .all()
    )
    db.close()

    if not products:
        return {"items": ["✅ No products expiring within the next 7 days."]}

    return {
        "items": [
            f"🕒 {p.name} ({p.category.value.upper()}) → expires on {p.expiry_date.strftime('%d-%m-%Y')}"
            for p in products
        ]
    }


# ------------------------------------------------
# 🧩 Tool 2: Category Check
# ------------------------------------------------
@tool
def category_check_tool(category: str = "food", user_id: int = 1) -> dict:
    """Fetch products belonging to a specific category for a specific user."""
    db = SessionLocal()
    cat_enum = normalize_category(category)
    if not cat_enum:
        db.close()
        return {"items": [f"⚠️ Invalid category '{category}'. Try: FOOD, MEDICINE, or MISCELLANEOUS."]}

    products = (
        db.query(Product)
        .filter(Product.user_id == user_id, Product.category == cat_enum)
        .order_by(Product.expiry_date.asc())
        .all()
    )
    db.close()

    if not products:
        return {"items": [f"❌ No products found in category '{cat_enum.value.upper()}' for this user."]}

    return {
        "items": [
            f"📦 {p.name} ({p.category.value.upper()}) → expires on {p.expiry_date.strftime('%d-%m-%Y')}"
            for p in products
        ]
    }


# ------------------------------------------------
# 🧩 Tool 3: Category Expiry Check
# ------------------------------------------------
@tool
def category_expiry_check_tool(category: str = "food", user_id: int = 1) -> dict:
    """Fetch products of a specific category that are expiring within the next 7 days."""
    db = SessionLocal()
    today = datetime.now().date()
    upcoming = today + timedelta(days=7)
    cat_enum = normalize_category(category)
    if not cat_enum:
        db.close()
        return {"items": [f"⚠️ Invalid category '{category}'. Try: FOOD, MEDICINE, or MISCELLANEOUS."]}

    products = (
        db.query(Product)
        .filter(
            Product.user_id == user_id,
            Product.expiry_date <= upcoming,
            Product.category == cat_enum,
        )
        .order_by(Product.expiry_date.asc())
        .all()
    )
    db.close()

    if not products:
        return {"items": [f"✅ No {cat_enum.value.upper()} items expiring within 7 days."]}

    return {
        "items": [
            f"🕒 {p.name} ({p.category.value.upper()}) → expires on {p.expiry_date.strftime('%d-%m-%Y')}"
            for p in products
        ]
    }


# ------------------------------------------------
# 🧩 Tool 4: Add New Product
# ------------------------------------------------
_NUMBER_WORDS = {
    "one": 1, "two": 2, "three": 3, "four": 4, "five": 5,
    "six": 6, "seven": 7, "eight": 8, "nine": 9, "ten": 10
}

@tool
def add_item_tool(item_description: str, user_id: int) -> dict:
    """Add a new product for a user to the PostgreSQL database."""
    from app.routers.database.chat_models import User  # local import to avoid circular issues
    db = SessionLocal()
    today = datetime.now().date()
    desc = (item_description or "").strip().lower()
    print("Item Description Input : ", item_description)

    # Defensive: remove any trailing "(user id: ...)" or similar noise often appended in testing
    desc = re.sub(r"\(\s*user\s*id[:=]?\s*\d+\s*\)$", "", desc, flags=re.IGNORECASE).strip()
    # Remove stray trailing punctuation
    desc = desc.rstrip(" .,")

    # --- quick user existence check ---
    user = db.query(User).filter(User.id == user_id).first()
    if not user:
        db.close()
        return {"status": f"❌ Failed to add product: Could not find user with id {user_id}."}

    # --- 1) Extract product name ---
    clean_desc = re.sub(r"\(\s*user\s*id[:=]?\s*\d+\s*\)$", "", desc, flags=re.IGNORECASE).strip()
    clean_desc = clean_desc.rstrip(" .,")
    print("🧠 DEBUG: Clean description:", clean_desc)

    # Try to find product name (works with or without 'add')
    name_pattern = re.search(
    r"(?:add|please add|insert|create)?\s*([a-zA-Z0-9\s]+?)(?=\s*(?:in\s+\d+\s+days|in\s+(?:one|two|three|four|five|six|seven|eight|nine|ten)\s+days|expir|expire|expires|expiring|on\s+\d|with|category|$))",
    clean_desc,
    flags=re.IGNORECASE
    )

    raw_name = name_pattern.group(1).strip() if name_pattern else None

    if not raw_name:
        # fallback to first word or phrase before 'expiry'
        fallback_match = re.split(r"\s*(?:expir|expire|expires|expiry|on)\s*", clean_desc)
        raw_name = fallback_match[0].strip() if fallback_match and fallback_match[0] else "Unnamed Product"

    product_name = re.sub(r"[(),]+$", "", raw_name).strip().title()
    print("🔥 DEBUG: Extracted product name:", product_name)



    # --- 2) Determine expiry date ---
    expiry_date = None
    # explicit date
    date_match = re.search(r"(\d{1,2}[-/]\d{1,2}[-/]\d{2,4})", desc)
    if date_match:
        date_str = date_match.group(1)
        for fmt in ("%d-%m-%Y", "%d/%m/%Y", "%Y-%m-%d", "%d-%m-%y", "%d/%m/%y"):
            try:
                expiry_date = datetime.strptime(date_str, fmt).date()
                break
            except ValueError:
                continue

    # day phrases
    if not expiry_date and "day after tomorrow" in desc:
        expiry_date = today + timedelta(days=2)
    elif not expiry_date and "tomorrow" in desc:
        expiry_date = today + timedelta(days=1)

    # "in X days"
    if not expiry_date:
        match_in_days = re.search(r"in\s+(\d+)\s+days?", desc)
        if match_in_days:
            expiry_date = today + timedelta(days=int(match_in_days.group(1)))
        else:
            match_in_days_word = re.search(r"in\s+(one|two|three|four|five|six|seven|eight|nine|ten)\s+days?", desc)
            if match_in_days_word:
                expiry_date = today + timedelta(days=_NUMBER_WORDS.get(match_in_days_word.group(1), 0))

    if not expiry_date:
        db.close()
        return {"status": f"⚠️ Couldn't determine expiry date from: '{item_description}'. Please use 'in X days', 'tomorrow', 'day after tomorrow', or an explicit date."}

    # --- 3) Infer category (same logic as before but using enum) ---
    food_keywords = ["bread", "flour", "milk", "biscuit", "egg", "eggs", "rice", "cheese", "cookie", "juice", "butter", "noodle", "noodles",
    "curd", "yogurt", "vegetable", "vegetables", "fruit", "fruits", "meat", "chicken", "fish", "grocery"]
    med_keywords = ["paracetamol", "tablet", "syrup", "medicine", "ibuprofen", "antacid", "cough", "pain killer"]
    # anything else -> MISCELLANEOUS
    lowered = product_name.lower()
    if any(k in lowered for k in food_keywords):
        category = ProductCategory.FOOD
    elif any(k in lowered for k in med_keywords):
        category = ProductCategory.MEDICINE
    else:
        category = ProductCategory.MISCELLANEOUS

    # --- 4) Insert into DB ---
    try:
        new_product = Product(
            name=product_name,
            category=category,
            expiry_date=expiry_date,
            quantity=1,
            description="",
            user_id=user_id,
        )
        db.add(new_product)
        db.commit()
        # refresh to ensure id populated if needed
        db.refresh(new_product)
        print("🔥 DEBUG: DB commit successful, new_product.id =", new_product.id)
    except Exception as e:
        import traceback
        print("🔥 DEBUG ERROR:", repr(e))
        traceback.print_exc()
        db.rollback()
        db.close()
        return {"status": f"❌ Failed to add product: {e}"}

    db.close()
    return {"status": f"✅ Added '{product_name}' to category '{category.value}' with expiry on {expiry_date.strftime('%d-%m-%Y')} for user {user_id}."}

# ------------------------------------------------
# 🧩 Tool 5: Expired Items Check
# ------------------------------------------------
@tool
def expired_items_tool(user_id: int = 1) -> dict:
    """Fetch products that have already expired for a specific user."""
    db = SessionLocal()
    today = datetime.now().date()

    products = (
        db.query(Product)
        .filter(Product.user_id == user_id, Product.expiry_date < today)
        .order_by(Product.expiry_date.asc())
        .all()
    )
    db.close()

    if not products:
        return {"items": ["✅ No expired products found. Everything is up to date!"]}

    return {
        "items": [
            f"⚰️ {p.name} ({p.category.value.upper()}) → expired on {p.expiry_date.strftime('%d-%m-%Y')}"
            for p in products
        ]
    }



# ------------------------------------------------
# 📦 Exported Objects
# ------------------------------------------------
tools = [expiry_check_tool, category_check_tool, add_item_tool, category_expiry_check_tool, expired_items_tool]
llm_with_tools = llm.bind_tools(tools)
