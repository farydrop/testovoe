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
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import com.example.testovoe.R
import com.example.testovoe.presentation.viewmodel.StartViewModel
import com.example.testovoe.databinding.ActivityStartBinding
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.InetSocketAddress
import java.net.Socket
import java.net.URL
import java.util.concurrent.Executors
import org.koin.androidx.viewmodel.ext.android.viewModel


class StartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStartBinding
    private val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig
    val viewModel: StartViewModel by viewModel()
    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
    }
    private val monoThreadDispatcher = Executors.newFixedThreadPool(1).asCoroutineDispatcher()

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStartBinding.inflate(layoutInflater)

        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(R.xml.url_default_value)
        //getValueFromFireBaseRemoteConfig()

        setContentView(binding.root)

        val url = remoteConfig.getString("url")
        binding.tv.text = url

        viewModel.showActivity.observe(this){
            startActivity(
                Intent(
                    this@StartActivity,
                    MainActivity::class.java
                )
            )
            finish()
        }

        viewModel.showChromeTabs.observe(this){
            val customIntent = CustomTabsIntent.Builder()
            //customIntent.setToolbarColor(ContextCompat.getColor(this@StartActivity, R.color.holo_red_dark))
            openCustomTab(
                this@StartActivity,
                customIntent.build(),
                Uri.parse(url)
            )
        }

        /*remoteConfig.addOnConfigUpdateListener(object : ConfigUpdateListener {
            override fun onUpdate(configUpdate: ConfigUpdate) {
                Log.d(TAG, "Updated keys: " + configUpdate.updatedKeys)
                remoteConfig.activate()
            }

            override fun onError(error: FirebaseRemoteConfigException) {
                Log.w(TAG, "Config update error with code: " + error.code, error)
            }
        })*/


        remoteConfig.fetchAndActivate().addOnCompleteListener(this) { task ->
            /*val url: String = remoteConfig.getString("url")
            val flag: Boolean = remoteConfig.getBoolean("flag")*/
            if (task.isSuccessful) {
                val updated = task.result
                Log.d(TAG, "Config params updated: $updated")
                Toast.makeText(
                    this,
                    "Fetch and activate succeeded",
                    Toast.LENGTH_SHORT,
                ).show()
                val url: String = remoteConfig.getString("url")
                val flag: Boolean = remoteConfig.getBoolean("flag")
                saveUrl(url)

                if (flag) {
                    if (url.isEmpty()) {
                        val customIntent = CustomTabsIntent.Builder()
                        openCustomTab(
                            this@StartActivity,
                            customIntent.build(),
                            Uri.parse("https://ya.ru/")
                        )
                    } else {
                        viewModel.getUrlResponse(url)
                        /*GlobalScope.launch {
                            //Log.d("MyTag","$u")
                            val u = isURLReachable()
                            withContext(monoThreadDispatcher) {
                                if (u) {
                                    val customIntent = CustomTabsIntent.Builder()
                                    //customIntent.setToolbarColor(ContextCompat.getColor(this@StartActivity, R.color.holo_red_dark))
                                    openCustomTab(
                                        this@StartActivity,
                                        customIntent.build(),
                                        Uri.parse(url)
                                    )
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
                        }*/
                    }
                } else {
                    Toast.makeText(
                        this,
                        "Fetch failed",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }


        }
    }

    private fun isURLReachable(): Boolean {
        val url = URL(remoteConfig.getString("url"))
        val socket = Socket()
        socket.soTimeout = 200
        socket.connect(InetSocketAddress(url.host, url.port), 200)
        val isConnect = socket.isConnected
        socket.close()
        return isConnect
    }

    /*private fun getValueFromFireBaseRemoteConfig() {
        remoteConfig.fetchAndActivate()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val updated = task.result
                    Log.d(TAG, "Config params updated: $updated")
                    Toast.makeText(
                        this,
                        "Fetch and activate succeeded",
                        Toast.LENGTH_SHORT,
                    ).show()
                } else {
                    Toast.makeText(
                        this,
                        "Fetch failed",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
    }*/

    suspend fun makeRequest(urlString: String) {
        val url = URL(urlString)
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"

        val responseCode = connection.responseCode
        if (responseCode == HttpURLConnection.HTTP_OK) {
            val customIntent = CustomTabsIntent.Builder()
            //customIntent.setToolbarColor(ContextCompat.getColor(this@StartActivity, R.color.holo_red_dark))
            openCustomTab(
                this@StartActivity,
                customIntent.build(),
                Uri.parse(url.toString())
            )
            //return responseCode
        } else {
            startActivity(
                Intent(
                    this@StartActivity,
                    MainActivity::class.java
                )
            )
            finish()
            //throw Exception("Request failed with response code $responseCode")
        }
    }


    private fun saveUrl(url: String) {
        //val urlText = config.getString("url")
        val sharedPreference = getSharedPreferences("application", Context.MODE_PRIVATE)
        val editor = sharedPreference.edit()
        editor.putString("url", url)
        editor.apply()
    }

    private fun getUrl(url: String): String? {
        //val urlText = config.getString("url")
        val sharedPreference = getSharedPreferences("application", Context.MODE_PRIVATE)
        return sharedPreference.getString("url", url)
    }

    private fun isSharedSaved(): Boolean {
        val sharedPref = getSharedPreferences("application", Context.MODE_PRIVATE)
        return sharedPref.contains("url")
    }

    private fun openCustomTab(activity: Activity, customTabsIntent: CustomTabsIntent, uri: Uri) {
        // package name is the default package
        // for our custom chrome tab
        val packageName = "com.android.chrome"
        if (packageName != null) {

            // we are checking if the package name is not null
            // if package name is not null then we are calling
            // that custom chrome tab with intent by passing its
            // package name.
            customTabsIntent.intent.setPackage(packageName)

            // in that custom tab intent we are passing
            // our url which we have to browse.
            customTabsIntent.launchUrl(activity, uri)
        } else {
            // if the custom tabs fails to load then we are simply
            // redirecting our user to users device default browser.
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