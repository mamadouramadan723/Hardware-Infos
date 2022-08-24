package com.rmd.media.ml.tf.myapplication

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build.*
import android.os.Build.VERSION.*
import android.os.Bundle
import android.provider.Settings
import android.telephony.TelephonyManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.rmd.media.ml.tf.myapplication.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var systemInfos: String = ""


    @SuppressLint("HardwareIds")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.checkConnectionBtn.setOnClickListener {
            if (checkForInternet(this)) {
                Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show()
                showConnectionInfos(this)
            } else {
                Toast.makeText(this, "Disconnected", Toast.LENGTH_SHORT).show()
                binding.showConnectionInfosTv.text = ""
            }
        }
        binding.showSystemInfosBtn.setOnClickListener {

            //get ANDROID_ID
            val telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as
                    TelephonyManager
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_PHONE_STATE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_PHONE_STATE), REQUEST_CODE
                )
                return@setOnClickListener
            }
            if (SDK_INT >= VERSION_CODES.O) {
                systemInfos = Settings.Secure.getString(
                    this.contentResolver,
                    Settings.Secure.ANDROID_ID
                ).toString()
            } else {
                binding.showConnectionInfosTv.text = telephonyManager.deviceId
            }
            //get other infos
            getOtherInfos(this)

        }
    }

    private fun getOtherInfos(activity: MainActivity) {
        if (SDK_INT >= VERSION_CODES.M) {
            systemInfos += "\nModel : $MODEL\nHardware : $HARDWARE\nOS : $BASE_OS" +
                    "\nPatch Security : $SECURITY_PATCH\nBuild : $DEVICE\nBrand: $BRAND" +
                    "\nCPU : $CPU_ABI\nDisplay : $DISPLAY\nFingerprint : $FINGERPRINT"+
                    "\nHost : $HOST\nID : $ID\nMANUFACTURER : $MANUFACTURER\nPRODUCT : $PRODUCT"+
                    "\nTIME : $TIME"
        } else {
            TODO("VERSION.SDK_INT < M")
        }

        binding.showSystemInfosTv.text = systemInfos
    }


    @SuppressLint("SetTextI18n")
    private fun showConnectionInfos(context: Context) {
        // register activity with the connectivity manager service
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        // if the android version is equal to M or greater we need to use the NetworkCapabilities
        // to check what type of network has the internet connection
        if (SDK_INT >= VERSION_CODES.M) {

            // Returns a Network object corresponding to the currently active default data network.
            val network = connectivityManager.activeNetwork

            // Representation of the capabilities of an active network.
            val activeNetwork = connectivityManager.getNetworkCapabilities(network)
            if (activeNetwork != null) {
                if (activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    binding.showConnectionInfosTv.text = "TRANSPORT_WIFI"

                } else if (activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    binding.showConnectionInfosTv.text = "TRANSPORT_CELLULAR"
                }

            }
        } else {
            // if the android version is below M

            val networkInfo = connectivityManager.activeNetworkInfo
            if (networkInfo != null) {

                if (networkInfo.isConnected) {
                    TODO("Create an emulator with SDK Version < M ")
                }
            }
        }
    }

    private fun checkForInternet(context: Context): Boolean {

        // register activity with the connectivity manager service
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        // if the android version is equal to M or greater we need to use the NetworkCapabilities
        // to check what type of network has the internet connection
        if (SDK_INT >= VERSION_CODES.M) {

            // Returns a Network object corresponding to the currently active default data network.
            val network = connectivityManager.activeNetwork ?: return false

            // Representation of the capabilities of an active network.
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

            return when {
                // Indicates this network uses a Wi-Fi transport, or WiFi has network connectivity
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true

                // Indicates this network uses a Cellular transport. or
                // Cellular has network connectivity
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true

                // else return false
                else -> false
            }
        } else {
            // if the android version is below M
            @Suppress("DEPRECATION")
            val networkInfo =
                connectivityManager.activeNetworkInfo ?: return false
            @Suppress("DEPRECATION")
            return networkInfo.isConnected
        }
    }

    companion object {
        private const val REQUEST_CODE = 101
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