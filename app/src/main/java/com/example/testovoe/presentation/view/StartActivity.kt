package com.example.testovoe.presentation.view

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities.TRANSPORT_CELLULAR
import android.net.NetworkCapabilities.TRANSPORT_ETHERNET
import android.net.NetworkCapabilities.TRANSPORT_WIFI
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import com.example.testovoe.R
import com.example.testovoe.databinding.ActivityStartBinding
import com.example.testovoe.presentation.viewmodel.StartViewModel
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ConfigUpdate
import com.google.firebase.remoteconfig.ConfigUpdateListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.onesignal.OneSignal
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.net.HttpURLConnection
import java.net.InetSocketAddress
import java.net.Socket
import java.net.URL
import java.security.Security
import java.util.concurrent.Executors


class StartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStartBinding
    private val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig
    val viewModel: StartViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStartBinding.inflate(layoutInflater)
        val configSettings = remoteConfigSettings { minimumFetchIntervalInSeconds = 3600 }
        remoteConfig.setConfigSettingsAsync(configSettings)
        setContentView(binding.root)
        val url = remoteConfig.getString("url")
        saveUrl(url)

        remoteConfig.addOnConfigUpdateListener(object : ConfigUpdateListener {
            override fun onUpdate(configUpdate: ConfigUpdate) {
                Log.d(TAG, "Updated keys: " + configUpdate.updatedKeys)
                remoteConfig.activate()
            }
            override fun onError(error: FirebaseRemoteConfigException) {
                Log.w(TAG, "Config update error with code: " + error.code, error)
            }
        })

        fun isURLReachable() {
            CoroutineScope(Dispatchers.IO).launch {
                val url = URL(getUrl(url))
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.responseCode
                val t = if (connection.responseCode == HttpURLConnection.HTTP_OK) 200 else 404
                CoroutineScope(Dispatchers.Main).launch {
                    if (t == 200) {
                        if (isInternetAvailable()){
                            val customIntent = CustomTabsIntent.Builder()
                            openCustomTab(
                                this@StartActivity,
                                customIntent.build(),
                                Uri.parse(url.toString())
                            )
                            finish()
                        } else {
                            startActivity(
                                Intent(
                                    this@StartActivity,
                                    MainActivity::class.java
                                )
                            )
                            finish()
                        }
                    } else {
                        startActivity(
                            Intent(
                                this@StartActivity,
                                MainActivity::class.java
                            )
                        )
                        finish()
                    }
                }
            }
        }


        remoteConfig.fetchAndActivate().addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                val updated = task.result
                Log.d(TAG, "Config params updated: $updated")
                val url: String = remoteConfig.getString("url")
                val flag: Boolean = remoteConfig.getBoolean("flag")
                saveUrl(url)


                if (flag) {
                    if (url.isEmpty()) {
                        if (isInternetAvailable()){
                            val customIntent = CustomTabsIntent.Builder()
                            openCustomTab(
                                this@StartActivity,
                                customIntent.build(),
                                Uri.parse("https://ya.ru/")
                            )
                            finish()
                        } else {
                            startActivity(
                                Intent(
                                    this@StartActivity,
                                    MainActivity::class.java
                                )
                            )
                            finish()
                        }
                    } else {
                        isURLReachable()
                    }
                } else {
                    startActivity(
                        Intent(
                            this@StartActivity,
                            MainActivity::class.java
                        )
                    )
                    finish()
                }
            } else {
                startActivity(
                    Intent(
                        this@StartActivity,
                        MainActivity::class.java
                    )
                )
                finish()
            }


        }
    }


    private fun saveUrl(url: String) {
        val sharedPreference = getSharedPreferences("application", Context.MODE_PRIVATE)
        val editor = sharedPreference.edit()
        editor.putString("url", url)
        editor.apply()
    }

    private fun getUrl(url: String): String? {
        val sharedPreference = getSharedPreferences("application", Context.MODE_PRIVATE)
        return sharedPreference.getString("url", url)
    }

    private fun isSharedSaved(): Boolean {
        val sharedPref = getSharedPreferences("application", Context.MODE_PRIVATE)
        return sharedPref.contains("url")
    }

    private fun openCustomTab(activity: Activity, customTabsIntent: CustomTabsIntent, uri: Uri) {
        val packageName = "com.android.chrome"
        if (packageName != null) {
            customTabsIntent.intent.setPackage(packageName)

            customTabsIntent.launchUrl(activity, uri)
        } else {
            activity.startActivity(Intent(Intent.ACTION_VIEW, uri))
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun isInternetAvailable(): Boolean {
        val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                if (capabilities.hasTransport(TRANSPORT_CELLULAR)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                    return true
                } else if (capabilities.hasTransport(TRANSPORT_WIFI)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                    return true
                } else if (capabilities.hasTransport(TRANSPORT_ETHERNET)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                    return true
                }
            }
        }
        return false
    }
}