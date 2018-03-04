package com.chip2n.kolibri.chat

data class ViewModel(
        val recipient: String,
        val messages: List<String> = listOf(),
        val isTyping: Boolean = false)
