package com.rmd.media.ml.tf.myapplication

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.provider.Settings
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

            //
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

            if (checkForInternet(this)) {
                Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show()
                showConnectionInfos(this)
            } else {
                Toast.makeText(this, "Disconnected", Toast.LENGTH_SHORT).show()
                binding.showConnectionInfosTv.text = ""
            }
        }
        binding.showSystemInfosBtn.setOnClickListener {
            getHardwareInfos()
        }
    }

    @SuppressLint("HardwareIds")
    private fun getHardwareInfos() {

        if (SDK_INT >= Build.VERSION_CODES.M) {
            systemInfos = "Brand: ${Build.BRAND} \n" +
                    "DeviceID: ${
                        Settings.Secure.getString(
                            contentResolver,
                            Settings.Secure.ANDROID_ID
                        )
                    } \n" +
                    "Model: ${Build.MODEL} \n" +
                    "ID: ${Build.ID} \n" +
                    "SDK: $SDK_INT \n" +
                    "Manufacture: ${Build.MANUFACTURER} \n" +
                    "Brand: ${Build.BRAND} \n" +
                    "User: ${Build.USER} \n" +
                    "Type: ${Build.TYPE} \n" +
                    "Base: ${Build.VERSION_CODES.BASE} \n" +
                    "Incremental: ${Build.VERSION.INCREMENTAL} \n" +
                    "Board: ${Build.BOARD} \n" +
                    "Host: ${Build.HOST} \n" +
                    "FingerPrint: ${Build.FINGERPRINT} \n" +
                    "Version Code: ${Build.VERSION.RELEASE}"

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
        if (SDK_INT >= Build.VERSION_CODES.M) {

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
        if (SDK_INT >= Build.VERSION_CODES.M) {

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