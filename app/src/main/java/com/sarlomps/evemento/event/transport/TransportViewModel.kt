package com.sarlomps.evemento.event.transport

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.sarlomps.evemento.api.User


class TransportViewModel : ViewModel() {

    var selectedDriver: MutableLiveData<User> = MutableLiveData()
        private set
    var transport: MutableLiveData<TransportItem> = MutableLiveData()
        private set

    fun selectDriver(driver: User) {
        selectedDriver.value = driver
    }

    fun setTransport(selected: TransportItem?) {
        transport.value = selected
    }

}