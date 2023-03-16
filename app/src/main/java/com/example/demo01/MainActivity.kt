package com.example.demo01

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    lateinit var tv: TextView
    lateinit var wifiManager: WifiManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tv = findViewById(R.id.tv)

        wifiManager = getSystemService(Context.WIFI_SERVICE) as WifiManager

        val intentFilter = IntentFilter()
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        registerReceiver(wifiScanReceiver, intentFilter)


        this.requestPermissions(
            arrayOf(
                "android.permission.CHANGE_WIFI_STATE",
                "android.permission.ACCESS_FINE_LOCATION"
            ), 200
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 200) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                val success = wifiManager.startScan()
                if (!success) {
                    scanFailure()
                }
                Log.i(TAG, "onRequestPermissionsResult: ok")
            } else {
                Log.i(TAG, "onRequestPermissionsResult: 无权限")
            }
        }
    }

    private val wifiScanReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val success = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false)
            if (success) {
                scanSuccess()
            } else {
                scanFailure()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun scanSuccess() {
        val result = wifiManager.scanResults
        Log.i(TAG, "scanSuccess: ${result.size}")
    }

    @SuppressLint("MissingPermission")
    private fun scanFailure() {
        val results = wifiManager.scanResults
        Log.i(TAG, "scanFailure: ${results.size}")
    }

}