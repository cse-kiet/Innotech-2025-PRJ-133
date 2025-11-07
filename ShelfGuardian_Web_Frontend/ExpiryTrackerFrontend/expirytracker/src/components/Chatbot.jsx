import React, { useState, useRef, useEffect } from "react";
import axios from "axios";
import { MessageCircle, X, Minus } from "lucide-react";
import { getToken, getUserId } from "../services/authService";

const Chatbot = () => {
  const [open, setOpen] = useState(false);
  const [messages, setMessages] = useState([]);
  const [input, setInput] = useState("");
  const [isThinking, setIsThinking] = useState(false);
  const messagesEndRef = useRef(null);

  const token = getToken();
  const userId = getUserId();
  const BASE_URL = "http://127.0.0.1:8000/api/chat";

  // Test connection on component mount
  useEffect(() => {
    const testConnection = async () => {
      try {
        console.log("Testing backend connection...");
        const axiosInstance = axios.create({
          timeout: 5000,
          headers: {
            'Accept': 'application/json',
          },
        });
        const response = await axiosInstance.get("http://127.0.0.1:8000/");
        console.log("Backend connection successful:", response.data);
      } catch (error) {
        console.error("Backend connection failed:", error);
        console.error("Connection error details:", {
          message: error.message,
          code: error.code,
          response: error.response,
        });

        // Show user-friendly error message
        setMessages([{
          sender: "bot",
          text: "⚠️ Unable to connect to server. Please check if the backend server is running."
        }]);
      }
    };

    testConnection();
  }, []);

  useEffect(() => scrollToBottom(), [messages]);

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  };

  const sendMessage = async () => {
    if (!input.trim()) return;

    const userMsg = { sender: "user", text: input };
    setMessages((prev) => [...prev, userMsg]);
    setInput("");
    setIsThinking(true);

    console.log("Sending message to backend:", {
      url: `${BASE_URL}/ask`,
      userId,
      token: token ? "present" : "missing",
      message: userMsg.text,
    });

    try {
      const axiosInstance = axios.create({
        timeout: 100000,
        headers: {
          "Content-Type": "application/json",
          "Authorization": token ? `Bearer ${token}` : undefined
        },
      });

      console.log("Axios instance created with headers:", axiosInstance.defaults.headers);
      console.log("Request payload:", {
        user_id: userId,
        message: userMsg.text,
      });

      const res = await axiosInstance.post(`${BASE_URL}/ask`, {
        user_id: userId,
        message: userMsg.text,
      });

      console.log("Backend response received:", res.data);

      setMessages((prev) => [...prev, {
        sender: "bot",
        text: res.data.response || "No response",
      }]);
    } catch (error) {
      console.error("Chat API Error:", error);
      console.error("Error details:", {
        message: error.message,
        code: error.code,
        response: error.response,
        request: error.request,
      });

      // Additional logging for network errors
      if (error.code === "ERR_NETWORK") {
        console.error("Network error details:", {
          readyState: error.request?.readyState,
          status: error.request?.status,
          statusText: error.request?.statusText,
          url: `${BASE_URL}/ask`,
          tokenPresent: !!token,
          userId: userId,
        });
      } else if (error.response) {
        console.error("Server error details:", {
          status: error.response.status,
          statusText: error.response.statusText,
          data: error.response.data,
        });
      }

      let errorMessage = "⚠️ Unable to connect. Check server.";
      if (error.code === "ERR_NETWORK") {
        errorMessage = "⚠️ Network error - please check if the backend server is running";
      } else if (error.response && error.response.status) {
        if (error.response.status === 422) {
          errorMessage = "⚠️ Request format error - please check authentication";
        } else if (error.response.status === 401) {
          errorMessage = "⚠️ Authentication required - please log in";
        } else {
          errorMessage = `⚠️ Server error: ${error.response.status} ${error.response.statusText || 'Unknown error'}`;
        }
      }

      setMessages((prev) => [...prev, {
        sender: "bot",
        text: errorMessage
      }]);
    }

    setIsThinking(false);
  };

  return (
    <>
      {/* Floating Button */}
      {!open && (
        <button
          onClick={() => setOpen(true)}
          className="fixed bottom-6 right-6 p-4 rounded-full bg-indigo-600 text-white shadow-2xl hover:bg-indigo-700 transition transform hover:scale-110 animate-float"
        >
          <MessageCircle size={28} />
        </button>
      )}

      {/* Chat Popup */}
      {open && (
        <div className="fixed inset-0 flex justify-center items-center z-50 bg-black/10 backdrop-blur-sm">
          <div className="w-[600px] h-[600px] bg-white/90 backdrop-blur-xl rounded-2xl shadow-[0_10px_40px_rgba(0,0,0,0.25)] border border-indigo-300 overflow-hidden animate-fadeIn relative">

            {/* Glow Border */}
            <div className="absolute inset-0 rounded-2xl pointer-events-none border border-indigo-500/30 shadow-[0_0_25px_2px_rgba(99,102,241,0.3)]"></div>

            {/* Header */}
            <div className="flex justify-between items-center bg-indigo-600 text-white px-5 py-3 rounded-t-2xl shadow-md">
              <h3 className="font-semibold text-lg">ShelfGuardian AI</h3>
              <div className="flex gap-2">
                <button onClick={() => setOpen(false)} className="hover:bg-indigo-700 px-2 py-1 rounded transition">
                  <Minus />
                </button>
                <button onClick={() => { setOpen(false); setMessages([]); }} 
                  className="hover:bg-indigo-700 px-2 py-1 rounded transition">
                  <X />
                </button>
              </div>
            </div>

            {/* Chat + Input Fixed Layout */}
            <div className="flex flex-col h-[calc(100%-56px)]"> 
              {/* Scrollable Chat Area */}
              <div className="flex-1 overflow-y-auto p-4 space-y-3 bg-gray-50">
                {messages.map((msg, i) => (
                  <div key={i} className={`flex ${msg.sender === "user" ? "justify-end" : "justify-start"} items-start gap-2`}>
                    {msg.sender === "bot" && (
                      <div className="bg-indigo-600 text-white p-2 rounded-full text-xs">🤖</div>
                    )}

                    <div
                      className={`p-3 rounded-xl text-sm shadow transition hover:scale-[1.02] ${
                        msg.sender === "user"
                          ? "bg-indigo-600 text-white max-w-[75%]"
                          : "bg-white border border-gray-300 text-gray-800 max-w-[75%]"
                      }`}
                    >
                      {msg.sender === "bot" ? (
                        <div dangerouslySetInnerHTML={{ __html: msg.text.replace(/\n/g, "<br>") }} />
                      ) : (
                        msg.text
                      )}
                    </div>


                    {msg.sender === "user" && (
                      <div className="bg-purple-500 text-white p-2 rounded-full text-xs">👤</div>
                    )}
                  </div>
                ))}

                {/* Typing animation */}
                {isThinking && (
                  <div className="flex items-center gap-2 bg-gray-200 text-gray-700 px-3 py-2 rounded-xl w-fit text-xs animate-pulse">
                    🤖 ShelfGuardian typing...
                  </div>
                )}

                <div ref={messagesEndRef} />
              </div>

              {/* Fixed Input Bar */}
              <div className="flex gap-2 p-3 border-t bg-white">
                <input
                  className="flex-1 border border-gray-300 p-2.5 rounded-lg focus:ring-2 focus:ring-indigo-500"
                  placeholder="Ask something..."
                  value={input}
                  onChange={(e) => setInput(e.target.value)}
                  onKeyDown={(e) => e.key === "Enter" && sendMessage()}
                />
                <button
                  onClick={sendMessage}
                  className="bg-indigo-600 text-white px-5 py-2 rounded-lg hover:bg-indigo-700 transition shadow hover:shadow-lg"
                >
                  Send
                </button>
              </div>
            </div>

          </div>
        </div>
      )}
    </>
  );
};

export default Chatbot;
