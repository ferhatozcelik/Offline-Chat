package org.turkiye.offlinechat.ui.fragments.server

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
import org.turkiye.offlinechat.data.entity.User
import org.turkiye.offlinechat.data.model.ChatMessage
import org.turkiye.offlinechat.databinding.FragmentChatBinding
import org.turkiye.offlinechat.interfaces.RoomListener
import org.turkiye.offlinechat.ui.adapters.ChatArrayAdapter
import org.turkiye.offlinechat.util.Constants
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintStream
import java.net.InetAddress
import java.net.ServerSocket
import java.net.Socket

/**
 * Created by ferhat.ozcelik on 12-02-2023.
 */

@AndroidEntryPoint
class ServerFragment : Fragment(R.layout.fragment_chat) {

    private val viewModel: ServerViewModel by viewModels()
    private lateinit var binding: FragmentChatBinding

    private var chatArrayAdapter: ChatArrayAdapter? = null

    companion object {
        var num = 0
        var userHost: String? = null
        var userName: String? = null
        var arr = arrayOfNulls<Socket>(100)
        var handler = Handler()
        var serverSocket: ServerSocket? = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentChatBinding.inflate(inflater, container, false)

        chatArrayAdapter = context?.let { ChatArrayAdapter(it, R.layout.right) }
        binding.messageListView.transcriptMode = AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL
        binding.messageListView.adapter = chatArrayAdapter

        chatArrayAdapter?.registerDataSetObserver(object : DataSetObserver() {
            override fun onChanged() {
                super.onChanged()
                binding.messageListView.setSelection(chatArrayAdapter!!.count - 1)
            }
        })

        userName = arguments?.getString("userName")
        viewModel.ServerCreate(userName.toString(), object : RoomListener {
            override fun onCreate(user: User) {
                userName = user.userName
                userHost = user.userHost
                val sc = Thread(StartCommunication())
                sc.start()
            }

        })

        return binding.root
    }

    fun sendChatMessage(str: String): Boolean {
        val arr = str.split(":")
        if (arr.size == 1) {
            if (str.contains(getString(R.string.server_stared)) || str.contains(getString(R.string.joined))) chatArrayAdapter?.add(
                ChatMessage(
                    false, "<font color='#00AA00'>*** $str ***</font>"
                )
            ) else chatArrayAdapter?.add(
                ChatMessage(
                    false, "<font color='#AA0000'>*** $str ***</font>"
                )
            )
        } else if (arr[0] != userName) chatArrayAdapter?.add(
            ChatMessage(
                false, "<font color='#0077CC'>" + arr[0] + "</font><br/>" + arr[1]
            )
        ) else chatArrayAdapter?.add(
            ChatMessage(
                true, arr[1]
            )
        )
        return true
    }

    inner class StartCommunication : Runnable {
        override fun run() {
            try {
                val inetAddress: InetAddress = InetAddress.getByName(userHost)

                serverSocket = ServerSocket(Constants.PORT, 50, inetAddress)
                sendChatMessage(getString(R.string.server_stared_at) + userName.toString())

                binding.sendButton.setOnClickListener {
                    var message: String = binding.writeMessage.text.toString()
                    binding.writeMessage.setText("")
                    message = "${userName}: $message"
                    val mes = message
                    sendChatMessage(mes.trimIndent())
                    for (i in 0 until num) {
                        val temp: Socket? = arr[i]
                        val thread = temp?.let { it1 -> SendToAll(it1, message) }
                        thread?.start()
                    }
                }
                while (true) {
                    val clientSocket: Socket = serverSocket!!.accept()
                    val thread = ServerThread(clientSocket)
                    arr[num++] = clientSocket
                    thread.start()
                }
            } catch (e: Exception) {
                sendChatMessage(e.toString())
            }
        }
    }

    inner class SendToAll(var s: Socket, var msg: String) : Thread() {
        override fun run() {
            try {
                val ps = PrintStream(s.getOutputStream())
                ps.println(msg)
                if (msg.equals("exit", ignoreCase = true)) for (i in 0 until num) {
                    if (arr[i] === s) {
                        s.close()

                        break
                    }
                }
                ps.flush()
            } catch (e: Exception) {
                handler.post {
                    sendChatMessage(getString(R.string.error) + e.localizedMessage)
                    try {
                        sleep(2000)
                    } catch (exx: Exception) {
                    }
                }
            }
        }
    }

    inner class ServerThread(var clientSocket: Socket) : Thread() {
        override fun run() {
            try {
                var str: String
                val br = BufferedReader(InputStreamReader(clientSocket.getInputStream()))
                while (true) {
                    str = br.readLine()
                    if (str.startsWith("Ex1+:")) {
                        str = str.substring(5, str.length) + getString(R.string.leave)
                        for (i in 0 until num) {
                            if (arr[i] === clientSocket) for (j in i until num - 1) arr[j] =
                                arr[j + 1]
                            num--
                        }
                        clientSocket.close()
                        for (i in 0 until num) {
                            val temp: Socket? = arr[i]
                            val thread = temp?.let { SendToAll(it, str) }
                            thread?.start()
                        }
                        sendChatMessage(str.trimIndent())
                        break
                    }
                    if (str.substring(0, 6) == "j01ne6") str =
                        str.substring(7, str.length) + " " + getString(R.string.joined)
                    sendChatMessage(str.trimIndent())
                    for (i in 0 until num) {
                        val temp: Socket? = arr.get(i)
                        val thread = temp?.let { SendToAll(it, str) }
                        thread?.start()
                    }
                }
            } catch (e: Exception) {
                try {
                    var i = 0
                    while (i < num) {
                        if (arr[i] === clientSocket) {
                            var j = i
                            while (j < num - 1) {
                                arr[j] = arr[j + 1]
                                j++
                            }
                        }
                        i++
                    }
                    num--
                    clientSocket.close()
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        for (i in 0 until num) {
            val temp: Socket? = arr[i]
            val thread = temp?.let { SendToAll(it, "exit") }
            thread?.start()
        }
        try {
            serverSocket!!.close()
        } catch (_: Exception) {
        }

        viewModel.shutdown()
    }
}