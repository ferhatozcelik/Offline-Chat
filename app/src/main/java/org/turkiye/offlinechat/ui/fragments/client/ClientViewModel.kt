package org.turkiye.offlinechat.ui.fragments.client

import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import org.turkiye.offlinechat.R
import org.turkiye.offlinechat.data.entity.User
import org.turkiye.offlinechat.repository.UsersRepository
import org.turkiye.offlinechat.util.Constants
import javax.inject.Inject

/**
 * Created by ferhat.ozcelik on 12-02-2023.
 */

@HiltViewModel
class ClientViewModel @Inject constructor(
    private val deviceRepository: UsersRepository, @ApplicationContext private val context: Context
) : ViewModel() {

    private var mNsdManager: NsdManager? = null
    private var mCurrentDiscoveryStatus = DISCOVERY_STATUS.OFF

    companion object {
        val TAG = "ClientViewModel"
    }

    private enum class DISCOVERY_STATUS {
        ON, OFF
    }

    init {
        mNsdManager = context.getSystemService(Context.NSD_SERVICE) as NsdManager
    }

    fun discoverServices() {
        if (mCurrentDiscoveryStatus == DISCOVERY_STATUS.ON) return
        Toast.makeText(context, context.getString(R.string.discover_user), Toast.LENGTH_LONG).show()
        mCurrentDiscoveryStatus = DISCOVERY_STATUS.ON
        mNsdManager?.discoverServices(
            Constants.SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, mDiscoveryListener
        )
    }

    val mDiscoveryListener: NsdManager.DiscoveryListener = object : NsdManager.DiscoveryListener {
        override fun onDiscoveryStarted(regType: String) {
            Log.d(TAG, "Service discovery started")
        }

        override fun onServiceFound(service: NsdServiceInfo) {
            Log.d(TAG, "Service discovery success$service")
            if (service.serviceType != Constants.SERVICE_TYPE) {
                Log.d(TAG, "Unknown Service Type: " + service.serviceType)
            } else {
                mNsdManager?.resolveService(service, mResolveListener)
            }
        }

        override fun onServiceLost(service: NsdServiceInfo) {
            Log.e(TAG, "service lost$service")
        }

        override fun onDiscoveryStopped(serviceType: String) {
            Log.i(TAG, "Discovery stopped: $serviceType")
        }

        override fun onStartDiscoveryFailed(serviceType: String, errorCode: Int) {
            Log.e(TAG, "Discovery failed: Error code:$errorCode")
            mNsdManager?.stopServiceDiscovery(this)
        }

        override fun onStopDiscoveryFailed(serviceType: String, errorCode: Int) {
            Log.e(TAG, "Discovery failed: Error code:$errorCode")
            mCurrentDiscoveryStatus = DISCOVERY_STATUS.OFF
            mNsdManager?.stopServiceDiscovery(this)
        }
    }

    var mResolveListener: NsdManager.ResolveListener = object : NsdManager.ResolveListener {
        override fun onResolveFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
            Log.e(TAG, "Resolve failed$errorCode")
        }

        override fun onServiceResolved(serviceInfo: NsdServiceInfo) {
            Log.e(TAG, "Resolve Succeeded. $serviceInfo")
            mNsdManager?.stopServiceDiscovery(mDiscoveryListener)
            viewModelScope.launch {
                deviceRepository.deviceInsert(
                    User(
                        userName = serviceInfo.serviceName,
                        userHost = serviceInfo.host.toString()
                            .substring(1, serviceInfo.host.toString().length)
                    )
                )
            }
        }
    }

    fun shutdown() {
        try {
            mNsdManager?.stopServiceDiscovery(mDiscoveryListener)
            mCurrentDiscoveryStatus = DISCOVERY_STATUS.OFF
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}

