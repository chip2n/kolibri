package com.chip2n.kolibri.chat

data class ChatMessage(
        val sender: String,
        val message: String
)
data class State(
        val messages: List<ChatMessage> = listOf(),
        val isTyping: Boolean = false
) {
    fun withNewMessage(message: ChatMessage) = copy(messages = messages.plus(message))
}

