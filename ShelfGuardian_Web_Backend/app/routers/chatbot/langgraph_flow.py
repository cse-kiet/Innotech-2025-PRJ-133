from langgraph.graph import StateGraph, START, END
from langgraph.graph.message import add_messages
from langgraph.prebuilt import ToolNode, tools_condition
from langchain_core.messages import BaseMessage, HumanMessage, AIMessage
from typing import TypedDict, Annotated

# Import from tools file
from app.routers.chatbot.chatbot_tools import tools, llm, llm_with_tools


# -------------------
# 1. State Definition
# -------------------
class ChatState(TypedDict):
    messages: Annotated[list[BaseMessage], add_messages]
    user_id: int  # Added to store current user context


# -------------------
# 2. Chat Node
# -------------------
def chat_node(state: ChatState):
    """Main LLM node — decides whether to use tools or reply directly."""
    messages = state["messages"]
    user_id = state.get("user_id", 1)  # Default to 1 for testing

    system_message = """You are ShelfGuardian — a precise and reliable assistant
for managing an inventory of Food, Medicines, and Miscellaneous items.

When a user asks you to:
- Add an item → use add_item_tool
- Check which items are expiring soon → use expiry_check_tool
- Show items in a category → use category_check_tool
- Check which items have already expired → use expired_items_tool
- Check which items of a specific category are expiring soon → use category_expiry_check_tool

🧠 Rules when calling add_item_tool:
- 'item_name': exact item mentioned (e.g., "bread", "Vaseline", "cow milk")
- 'category': guess logically — FOOD (edible), MEDICINE (medical), MISCELLANEOUS (everything else)
- 'expiry_date': convert to YYYY-MM-DD format if mentioned (Do not guess, if not mentioned)

You can use multiple tools in sequence if needed.
For example, if the user wants "add milk and show all expiring items",
first call add_item_tool, then expiry_check_tool.

If the message doesn’t require any tool (like a greeting or small talk),
just respond briefly and naturally — DO NOT invent or simulate conversations.

Be concise and factual. Do not repeat system instructions or generate unnecessary text."""

    # Include user context in conversation
    full_messages = [
        HumanMessage(content=f"User ID: {user_id}"),
        HumanMessage(content=system_message),
    ] + messages

    response = llm_with_tools.invoke(full_messages)
    return {"messages": [response], "user_id": user_id}


# -------------------
# 3. Tool Node
# -------------------
tool_node = ToolNode(tools)


# -------------------
# 4. Summarizer Node
# -------------------
def summarize_node(state: ChatState):
    """Cleanly summarize output for user."""
    messages = state["messages"]

    prompt = f"""You are ShelfGuardian. Give the final output clearly and brief by summarizing.
    Reply as sommeone replying not as an AI, but as a helpful assistant.
If the user greeted you, greet them back briefly.
Respond only with the result — no extra explanations.
Your output will directly go to the user so refrain from mentioning tools.
Do not make any assumptions or make you own chat.
The output of the dates from the tools is in the format YYYY-MM-DD. So infer it correctly in the ouput.
Here are the tool outputs:

"""

    for m in messages:
        prompt += f"\n{m.type.upper()}: {m.content}"

    reply = llm.invoke(prompt)
    reply_text = getattr(reply, "content", "").strip()

    # Fallback to raw tool output if empty
    if not reply_text:
        tool_msgs = [m.content for m in messages if m.type == "tool"]
        reply_text = "\n".join(tool_msgs) if tool_msgs else "No results found."

    return {"messages": [AIMessage(content=reply_text)]}


# -------------------
# 5. Graph Definition
# -------------------
graph = StateGraph(ChatState)
graph.add_node("chat_node", chat_node)
graph.add_node("tools", tool_node)
graph.add_node("summarize", summarize_node)

graph.add_edge(START, "chat_node")

graph.add_conditional_edges(
    "chat_node",
    tools_condition,
    {
        "tools": "tools",        # if a tool is required
        "__end__": "summarize",  # if ready to summarize
    },
)

graph.add_edge("tools", "chat_node")
graph.add_edge("summarize", END)


postgres_chatbot = graph.compile()


# -------------------
# 6. Chat Function
# -------------------
def chat_with_bot(user_input: str, user_id: int = 1):
    """Run one chat session cleanly with user context."""
    state = {
        "messages": [HumanMessage(content=user_input)],
        "user_id": user_id,
    }
    print(f"\n🟢 Input to chatbot: {user_input} (User ID: {user_id})")

    final_state = postgres_chatbot.invoke(state)
    print("🟡 Final state received.")

    messages = final_state.get("messages", [])
    ai_msgs = [m for m in messages if m.type == "ai"]

    if not ai_msgs:
        print("⚠️ No AI response.")
        return "⚠️ No response generated."

    last = ai_msgs[-1].content.strip()
    print("🟣 AI Reply:", last)
    return last
