from fastapi import APIRouter
from pydantic import BaseModel
from app.schema.chat_schema import ChatRequest, ChatResponse
from app.services.chat_service import add_message, get_history, reset_history
from app.routers.chatbot.langgraph_flow import chat_with_bot

router = APIRouter(tags=["chat"])

class ResetRequest(BaseModel):
    user_id: str

@router.post("/ask", response_model=ChatResponse)
def ask_chatbot(req: ChatRequest):
    try:
        user_message = req.message.strip()
        bot_response = chat_with_bot(user_message, int(req.user_id))

        add_message(req.user_id, req.message, bot_response)
        history = get_history(req.user_id)

        return ChatResponse(response=bot_response, history=history)

    except Exception as e:
        import traceback
        print("[ERROR] Inside /ask route")
        traceback.print_exc()  # print full backend error
        return {"response": "⚠️ Internal server error in chatbot", "history": []}


@router.post("/reset")
def reset_chat(req: ResetRequest):
    reset_history(req.user_id)
    return {"message": "Chat history cleared"}
