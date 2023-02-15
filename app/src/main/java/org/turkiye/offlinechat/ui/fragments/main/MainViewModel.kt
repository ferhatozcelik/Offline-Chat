package org.turkiye.offlinechat.ui.fragments.main

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import org.turkiye.offlinechat.data.entity.User
import org.turkiye.offlinechat.interfaces.DiscoveryListener
import org.turkiye.offlinechat.repository.UsersRepository
import javax.inject.Inject

/**
 * Created by ferhat.ozcelik on 12-02-2023.
 */

@HiltViewModel
class MainViewModel @Inject constructor(
    private val deviceRepository: UsersRepository, @ApplicationContext private val context: Context
) : ViewModel(), DiscoveryListener {

    init {
        viewModelScope.launch {
            deviceRepository.deleteListDevice()
        }
    }

    fun getDeviceList() = deviceRepository.getListDevice()

    override fun serviceDiscovered(user: User) {
        viewModelScope.launch {
            deviceRepository.deviceInsert(user)
        }
    }
}

