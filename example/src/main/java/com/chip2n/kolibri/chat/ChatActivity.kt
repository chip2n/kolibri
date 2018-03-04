package com.chip2n.kolibri.chat

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.util.DiffUtil
import android.support.v7.widget.CardView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import com.chip2n.kolibri.R
import com.chip2n.kolibri.common.displayMetrics
import com.chip2n.kolibri.common.dpToPx
import com.jakewharton.rxbinding2.widget.editorActionEvents
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.content_chat.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.view_chat_bubble.view.*

class ChatActivity : AppCompatActivity(), ChatView {
    override val events: Observable<Event>

    private val eventSubject = PublishSubject.create<Event>()
    private val presenter = ChatPresenter()

    private val adapter = ChatAdapter()

    init {
        events = eventSubject
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        setSupportActionBar(toolbar)

        chatRecyclerView.layoutManager = LinearLayoutManager(this).apply {
            stackFromEnd = true
        }
        chatRecyclerView.adapter = adapter
        chatRecyclerView.addItemDecoration(ChatItemDecoration())

        // TODO: dispose
        messageInput.editorActionEvents()
                .filter { it.actionId() == EditorInfo.IME_ACTION_SEND }
                .map {
                    val text = it.view().text.toString()
                    // TODO: Move this side effect into kolibri
                    it.view().text = ""
                    Event.SendMessage(text)
                }
                .subscribe(eventSubject)
    }

    override fun onStart() {
        super.onStart()
        presenter.attach(this)
    }

    override fun onStop() {
        super.onStop()
        presenter.detach(this)
    }

    override fun render(model: ViewModel) {
        Log.i("ChatExample", "model: $model")
        adapter.bind(model.messages)
        typingIndicator.text = getString(R.string.chat_typing_indicator, model.recipient)

        chatRecyclerView.scrollToPosition(model.messages.lastIndex)

        if (model.isTyping) {
            typingIndicator.visibility = View.VISIBLE
        } else {
            typingIndicator.visibility = View.INVISIBLE
        }
    }
}

class ChatAdapter : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {
    class ChatViewHolder(private val view: ChatBubbleView) : RecyclerView.ViewHolder(view) {
        fun bind(model: String) {
            view.bind(model)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = ChatBubbleView(parent.context).apply {
            layoutParams = RecyclerView.LayoutParams(
                    RecyclerView.LayoutParams.MATCH_PARENT,
                    RecyclerView.LayoutParams.WRAP_CONTENT).apply {
            }
        }
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val model = currentItems[position]
        holder.bind(model)
    }

    private var currentItems = listOf<String>()

    fun bind(items: List<String>) {
        val oldItems = currentItems
        currentItems = items
        val callback = ChatDiffUtilCallback(oldItems, items)
        val result = DiffUtil.calculateDiff(callback)
        result.dispatchUpdatesTo(this)
    }

    override fun getItemCount() = currentItems.size
}

class ChatItemDecoration : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State?) {
        outRect.left = 8.dpToPx(parent.displayMetrics)
        outRect.top = 4.dpToPx(parent.displayMetrics)
        outRect.right = 8.dpToPx(parent.displayMetrics)
        outRect.bottom = 4.dpToPx(parent.displayMetrics)
    }
}

private class ChatDiffUtilCallback(val oldItems: List<String>, val newItems: List<String>) : DiffUtil.Callback() {
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldItems[oldItemPosition] == newItems[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldItems[oldItemPosition] == newItems[newItemPosition]
    }

    override fun getOldListSize(): Int = oldItems.size
    override fun getNewListSize(): Int = newItems.size
}

class ChatBubbleView : CardView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        inflate(context, R.layout.view_chat_bubble, this)

        radius = 8f.dpToPx(displayMetrics)
    }

    fun bind(model: String) {
        chatMessage.text = model
    }
}
