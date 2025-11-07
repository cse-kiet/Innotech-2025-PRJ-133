# In-memory chat history storage (resets when server restarts)
user_chat_history = {}

def add_message(user_id: str, user_message: str, bot_response: str = None):
    if user_id not in user_chat_history:
        user_chat_history[user_id] = []

    user_chat_history[user_id].append({
        "user": user_message,
        "bot": bot_response if bot_response is not None else "Hello"
    })


def get_history(user_id: str):
    return user_chat_history.get(user_id, [])


def reset_history(user_id: str):
    if user_id in user_chat_history:
        del user_chat_history[user_id]
