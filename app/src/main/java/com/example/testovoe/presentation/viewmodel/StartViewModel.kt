package com.example.testovoe.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.testovoe.R
import com.example.testovoe.domain.entity.UrlEntity
import com.example.testovoe.domain.usecase.StartUseCase
import com.example.testovoe.presentation.common.SingleLiveDataEmpty
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.net.InetSocketAddress
import java.net.Socket
import java.net.URL

class StartViewModel(private val startUseCase: StartUseCase):BaseViewModel() {

    val urlState = MutableLiveData<UrlEntity>()
    private var urlSubjectDisposable: Disposable? = null
    private val url: UrlEntity? = null

    val showChromeTabs = SingleLiveDataEmpty()
    val showActivity = SingleLiveDataEmpty()





    init {
        updateUrl()
    }

    fun getUrlResponse(urlString: String) {
        urlState.value?.let {
                startUseCase
                    .urlSubject(it)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe{
                        if (isURLReachable(url)){
                            showChromeTabs.call()
                        } else {
                            showActivity
                        }
                    }
                    .also { disposables.add(it) }
            }
    }

    private fun updateUrl() {
        urlSubjectDisposable?.dispose()

        urlSubjectDisposable =
            url?.let {
                startUseCase
                    .urlSubject(it)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { urlState.value = it }
                    .also { disposables.add(it) }
            }
    }

    private fun isURLReachable(urlString: UrlEntity?): Boolean {
        val url = URL(urlString.toString())
        val socket = Socket()
        socket.soTimeout = 200
        socket.connect(InetSocketAddress(url.host, url.port), 200)
        val isConnect = socket.isConnected
        socket.close()
        return isConnect
    }

    private fun onInitUrl(urlString: UrlEntity) {
        startUseCase
            .insert(urlString)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe{
                showChromeTabs.call()
            }
            .also { disposables.add(it) }
    }


}