package com.rmd.media.ml.tf.myapplication

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import android.os.BatteryManager
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.provider.Settings
import android.telephony.TelephonyManager
import android.text.format.Formatter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.rmd.media.ml.tf.myapplication.databinding.ActivityMainBinding
import java.net.InetAddress
import java.net.NetworkInterface
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var publicIP: String = ""
    private var systemInfos: String = ""
    private var connectionInfos: String = ""


    @SuppressLint("HardwareIds")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.checkConnectionBtn.setOnClickListener {
            getMyPublicIPAddress()
            checkConnectionAndShowInfosIfConnected(this)
        }
        binding.showSystemInfosBtn.setOnClickListener {
            getHardwareInfos()
        }
    }

    private fun getMyPublicIPAddress() {
        val interfaces: List<NetworkInterface> =
            Collections.list(NetworkInterface.getNetworkInterfaces())
        for (networkInterface in interfaces) {
            val addressList: List<InetAddress> = Collections.list(networkInterface.inetAddresses)
            for (address in addressList) {
                if (!address.isLoopbackAddress) {
                    publicIP = address.hostAddress?.toString() ?: ""
                }
            }
        }
    }

    @SuppressLint("HardwareIds")
    private fun getHardwareInfos() {

        val memoryManager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val batteryManager = getSystemService(BATTERY_SERVICE) as BatteryManager

        val memInfo = ActivityManager.MemoryInfo()
        memoryManager.getMemoryInfo(memInfo)

        // Fetching the available and total memory and converting into Giga Bytes
        val availMemory = memInfo.availMem.toDouble() / (1024 * 1024 * 1024)
        val totalMemory = memInfo.totalMem.toDouble() / (1024 * 1024 * 1024)

        val batteryLevel = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)

        if (SDK_INT >= Build.VERSION_CODES.M) {
            systemInfos = "\tBrand: ${Build.BRAND} \n" +
                    "\tDeviceID: ${
                        Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
                    } \n" +
                    "\tModel: ${Build.MODEL} \n" +
                    "\tID: ${Build.ID} \n" +
                    "\tSDK: $SDK_INT \n" +
                    "\tManufacture: ${Build.MANUFACTURER} \n" +
                    "\tBrand: ${Build.BRAND} \n" +
                    "\tUser: ${Build.USER} \n" +
                    "\tType: ${Build.TYPE} \n" +
                    "\tBase: ${Build.VERSION_CODES.BASE} \n" +
                    "\tIncremental: ${Build.VERSION.INCREMENTAL} \n" +
                    "\tBoard: ${Build.BOARD} \n" +
                    "\tHost: ${Build.HOST} \n" +
                    //"\tFingerPrint: ${Build.FINGERPRINT} \n" +
                    "\tVersion Code: ${Build.VERSION.RELEASE} \n" +
                    "\tTotal Memory : $totalMemory GB\n" +
                    "\tAvailable Memory : $availMemory GB\n" +
                    "\tBattery Level : $batteryLevel %"

        } else {
            TODO("VERSION.SDK_INT < M")
        }
        binding.showSystemInfosTv.text = systemInfos
    }


    @SuppressLint("SetTextI18n")
    private fun checkConnectionAndShowInfosIfConnected(context: Context) {

        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (SDK_INT >= Build.VERSION_CODES.M) {

            val network = connectivityManager.activeNetwork
            val activeNetwork = connectivityManager.getNetworkCapabilities(network)

            if (activeNetwork != null) {
                Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show()
                //val s : String = activeNetwork.
                if (activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {

                    // Invoking the Wifi Manager
                    val wifiManager =
                        applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
                    // Method to get the current connection info
                    val wInfo = wifiManager.connectionInfo

                    // Extracting the information from the received connection info
                    val ipAddress = Formatter.formatIpAddress(wInfo.ipAddress)
                    val linkSpeed = wInfo.linkSpeed
                    val networkID = wInfo.networkId
                    val ssid = wInfo.ssid
                    val hssid = wInfo.hiddenSSID

                    connectionInfos = "Type :\tWIFI\n" +
                            "IP Address:\t$ipAddress\n" +
                            "Link Speed:\t$linkSpeed\n" +
                            "Network ID:\t$networkID\n" +
                            "SSID:\t$ssid\n" +
                            "Hidden SSID:\t$hssid\n"

                    binding.showConnectionInfosTv.text = connectionInfos

                } else if (activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {


                    val type = when (connectivityManager.activeNetworkInfo?.subtype) {
                        TelephonyManager.NETWORK_TYPE_GPRS, TelephonyManager.NETWORK_TYPE_EDGE, TelephonyManager.NETWORK_TYPE_CDMA, TelephonyManager.NETWORK_TYPE_1xRTT, TelephonyManager.NETWORK_TYPE_IDEN, TelephonyManager.NETWORK_TYPE_GSM -> "2G"
                        TelephonyManager.NETWORK_TYPE_UMTS, TelephonyManager.NETWORK_TYPE_EVDO_0, TelephonyManager.NETWORK_TYPE_EVDO_A, TelephonyManager.NETWORK_TYPE_HSDPA, TelephonyManager.NETWORK_TYPE_HSUPA, TelephonyManager.NETWORK_TYPE_HSPA, TelephonyManager.NETWORK_TYPE_EVDO_B, TelephonyManager.NETWORK_TYPE_EHRPD, TelephonyManager.NETWORK_TYPE_HSPAP, TelephonyManager.NETWORK_TYPE_TD_SCDMA -> "3G"
                        TelephonyManager.NETWORK_TYPE_LTE, TelephonyManager.NETWORK_TYPE_IWLAN, 19 -> "4G"
                        TelephonyManager.NETWORK_TYPE_NR -> "5G"
                        else -> "?"
                    }
                    val infos = "\tCellular : $type\n" +
                            "\tPublic IP : $publicIP\n"
                    binding.showConnectionInfosTv.text = infos

                }
            } else {
                Toast.makeText(this, "Disconnected", Toast.LENGTH_SHORT).show()
                binding.showConnectionInfosTv.text = ""
            }
        } else {
            // if the android version is below M
            TODO("Create an emulator with SDK Version < M ")
        }
    }

    companion object {
        private const val REQUEST_CODE = 100
    }


    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED
                ) {
                    Toast.makeText(this, "Permission granted.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Permission denied.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}