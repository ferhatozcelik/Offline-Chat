package org.turkiye.offlinechat.ui.fragments.client

import android.database.DataSetObserver
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import org.turkiye.offlinechat.R
import org.turkiye.offlinechat.data.model.ChatMessage
import org.turkiye.offlinechat.databinding.FragmentChatBinding
import org.turkiye.offlinechat.ui.adapters.ChatArrayAdapter
import org.turkiye.offlinechat.util.Constants
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintStream
import java.net.InetSocketAddress
import java.net.Socket

/**
 * Created by ferhat.ozcelik on 12-02-2023.
 */

@AndroidEntryPoint
class ClientFragment : Fragment(R.layout.fragment_chat) {

    private val viewModel: ClientViewModel by viewModels()
    private lateinit var binding: FragmentChatBinding

    private var chatArrayAdapter: ChatArrayAdapter? = null

    companion object {
        private var userHost: String? = null
        private var clientUserName: String? = null
        private var handler = Handler()
        private var ps: PrintStream? = null
        private var clientSocket: Socket? = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentChatBinding.inflate(inflater, container, false)

        clientUserName = arguments?.getString("clientUserName")
        userHost = arguments?.getString("userHost")

        chatArrayAdapter = context?.let { ChatArrayAdapter(it, R.layout.right) }
        binding.messageListView.transcriptMode = AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL
        binding.messageListView.adapter = chatArrayAdapter

        chatArrayAdapter?.registerDataSetObserver(object : DataSetObserver() {
            override fun onChanged() {
                super.onChanged()
                binding.messageListView.setSelection(chatArrayAdapter!!.count - 1)
            }
        })

        val sc = Thread(StartCommunication())
        sc.start()

        return binding.root
    }

    fun sendChatMessage(str: String): Boolean {
        val arr = str.split(":")
        if (arr.size == 1) {
            if (str.contains("Joined") || str.contains("Connected to")) chatArrayAdapter!!.add(
                ChatMessage(
                    false, "<font color='#00AA00'>*** $str ***</font>"
                )
            ) else chatArrayAdapter!!.add(
                ChatMessage(
                    false, "<font color='#AA0000'>*** $str ***</font>"
                )
            )
        } else if (arr[0] != clientUserName) chatArrayAdapter!!.add(
            ChatMessage(
                false, "<font color='#0077CC'>" + arr[0] + "</font><br/>" + arr[1]
            )
        ) else chatArrayAdapter!!.add(
            ChatMessage(
                true, arr[1]
            )
        )
        return true
    }

    inner class StartCommunication : Runnable {
        override fun run() {
            try {
                val inetAddress = InetSocketAddress(userHost, Constants.PORT)
                clientSocket = Socket()
                clientSocket!!.connect(inetAddress, 7000)
                ps = PrintStream(clientSocket!!.getOutputStream())
                sendChatMessage(getString(R.string.connected_to) + clientUserName.toString())
                ps!!.println("j01ne6:$clientUserName")
                binding.sendButton.setOnClickListener {
                    val st = Thread(SendThread())
                    st.start()
                }
                val br = BufferedReader(InputStreamReader(clientSocket!!.getInputStream()))
                while (true) {
                    val str = br.readLine()
                    if (str.equals("exit", ignoreCase = true)) {
                        sendChatMessage(getString(R.string.server_closed_the_con))
                        Thread.sleep(2000)
                        break
                    }
                    sendChatMessage(str)
                }
            } catch (e: Exception) {
                sendChatMessage(getString(R.string.not_able_to_connect))
                try {
                    Thread.sleep(2000)
                } catch (exx: Exception) {
                }
            }
        }
    }

    inner class SendThread : Runnable {
        override fun run() {
            try {
                var message: String = binding.writeMessage.text.toString()
                handler.post { binding.writeMessage.setText("") }
                message = "$clientUserName: $message"
                ps!!.println(message)
                ps!!.flush()
            } catch (e: Exception) {
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        val thread = Thread {
            try {
                if (ps != null) {
                    ps!!.println("Ex1+:$clientUserName")
                    ps!!.close()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        thread.start()
        viewModel.shutdown()

    }

}