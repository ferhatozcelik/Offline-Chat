package org.turkiye.offlinechat.ui.fragments.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import dagger.hilt.android.AndroidEntryPoint
import org.turkiye.offlinechat.R
import org.turkiye.offlinechat.data.entity.User
import org.turkiye.offlinechat.databinding.FragmentMainBinding
import org.turkiye.offlinechat.interfaces.ItemClickListener
import org.turkiye.offlinechat.ui.adapters.UserListAdapter
import org.turkiye.offlinechat.ui.dialogs.ClientDialog
import org.turkiye.offlinechat.ui.fragments.client.ClientViewModel

/**
 * Created by ferhat.ozcelik on 12-02-2023.
 */

@AndroidEntryPoint
class MainFragment : Fragment(R.layout.fragment_main) {

    private val viewModel: MainViewModel by viewModels()
    private val clientViewModel: ClientViewModel by viewModels()

    private lateinit var binding: FragmentMainBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)

        binding.roomCreateButton.setOnClickListener {
            if (binding.roomNameEdittext.text.isNotEmpty()) {
                val bundle = Bundle()
                bundle.putString("userName", binding.roomNameEdittext.text.toString())
                activity?.findNavController(R.id.nav_host_fragment)
                    ?.navigate(R.id.action_mainFragment_to_serverFragment, bundle)
            } else {
                Toast.makeText(
                    context, getString(R.string.please_room_name_alert), Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.onlineDeviceRefresh.setOnClickListener {
            clientViewModel.discoverServices()
        }

        viewModel.getDeviceList().observe(viewLifecycleOwner) {
            val adapter = UserListAdapter(it, object : ItemClickListener {
                override fun customItemListener(objects: Any) {
                    val user = objects as User
                    ClientDialog(context!!, activity!!).createGoalsDialog(
                        user.userName,
                        object : ItemClickListener {
                            override fun customItemListener(objects: Any) {
                                val clientUserName = objects as String

                                val bundle = Bundle()
                                bundle.putString("clientUserName", clientUserName)
                                bundle.putString("userHost", user.userHost)
                                activity?.findNavController(R.id.nav_host_fragment)
                                    ?.navigate(R.id.action_mainFragment_to_clientFragment, bundle)
                            }
                        })
                }
            })
            binding.onlineDeviceList.adapter = adapter
        }

        return binding.root
    }
}