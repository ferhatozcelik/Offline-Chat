package org.turkiye.offlinechat.ui.fragments.server

import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import org.turkiye.offlinechat.data.entity.User
import org.turkiye.offlinechat.interfaces.RoomListener
import org.turkiye.offlinechat.repository.UsersRepository
import org.turkiye.offlinechat.util.Constants
import org.turkiye.offlinechat.util.Utils
import java.net.InetAddress
import javax.inject.Inject

/**
 * Created by ferhat.ozcelik on 12-02-2023.
 */

@HiltViewModel
class ServerViewModel @Inject constructor(
    private val deviceRepository: UsersRepository, @ApplicationContext private val context: Context
) : ViewModel() {

    private var mCurrentRegistrationStatus: REGISTRATION_STATUS? = null
    private var mNsdManager: NsdManager? = null
    private var roomListener: RoomListener? = null

    private enum class REGISTRATION_STATUS {
        REGISTERED, NON_REGISTERED
    }

    init {

        mCurrentRegistrationStatus = REGISTRATION_STATUS.NON_REGISTERED
        mNsdManager = context.getSystemService(Context.NSD_SERVICE) as NsdManager
    }

    fun ServerCreate(serviceName: String, roomListener: RoomListener) {
        this.roomListener = roomListener
        if (mCurrentRegistrationStatus == REGISTRATION_STATUS.REGISTERED) return
        val serviceInfo = NsdServiceInfo()
        serviceInfo.port = Constants.PORT
        serviceInfo.host = InetAddress.getByName(Utils.getIPAddress(true))
        serviceInfo.serviceType = Constants.SERVICE_TYPE
        serviceInfo.serviceName = serviceName
        mNsdManager?.registerService(serviceInfo, NsdManager.PROTOCOL_DNS_SD, mRegistrationListener)

    }

    private val mRegistrationListener: NsdManager.RegistrationListener =
        object : NsdManager.RegistrationListener {
            override fun onServiceRegistered(nsdServiceInfo: NsdServiceInfo) {
                nsdServiceInfo.port = Constants.PORT
                nsdServiceInfo.host = InetAddress.getByName(Utils.getIPAddress(true))
                nsdServiceInfo.serviceType = Constants.SERVICE_TYPE
                nsdServiceInfo.serviceName = nsdServiceInfo.serviceName
                mCurrentRegistrationStatus = REGISTRATION_STATUS.REGISTERED
                roomListener?.onCreate(
                    User(
                        userName = nsdServiceInfo.serviceName.toString(),
                        userHost = nsdServiceInfo.host.toString()
                            .substring(1, nsdServiceInfo.host.toString().length),
                    )
                )
            }

            override fun onRegistrationFailed(arg0: NsdServiceInfo, arg1: Int) {}
            override fun onServiceUnregistered(arg0: NsdServiceInfo) {}
            override fun onUnregistrationFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {}
        }

    fun shutdown() {
        try {
            mNsdManager?.unregisterService(mRegistrationListener)
            mCurrentRegistrationStatus = REGISTRATION_STATUS.NON_REGISTERED
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}

