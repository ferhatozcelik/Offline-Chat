package org.turkiye.offlinechat.ui.adapters

import android.content.Context
import android.os.Handler
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import org.turkiye.offlinechat.R
import org.turkiye.offlinechat.data.model.ChatMessage

/**
 * Created by ferhat.ozcelik on 12-02-2023.
 */

class ChatArrayAdapter(context: Context, textViewResourceId: Int) : ArrayAdapter<ChatMessage>(
    context, textViewResourceId
) {
    private var chatText: TextView? = null
    private val chatMessageList: MutableList<ChatMessage> = ArrayList()
    var handler = Handler()


    override fun add(chatMessage: ChatMessage?) {
        handler.post {
            if (chatMessage != null) {
                chatMessageList.add(chatMessage)
            }
            super@ChatArrayAdapter.add(chatMessage)
        }
    }
    override fun getCount(): Int {
        return chatMessageList.size
    }

    override fun getItem(index: Int): ChatMessage {
        return chatMessageList[index]
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val (isMeMessage, messageText) = getItem(position)
        var row: View? = convertView
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        row = if (isMeMessage) {
            inflater.inflate(R.layout.right, parent, false)
        } else {
            inflater.inflate(R.layout.left, parent, false)
        }
        chatText = row.findViewById<View>(R.id.msgr) as TextView
        chatText!!.setText(Html.fromHtml(messageText), TextView.BufferType.SPANNABLE)
        return row
    }
}