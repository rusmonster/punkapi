package com.example.dkalita.punkapi.viewmodel

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LifecycleRegistry
import android.arch.lifecycle.ViewModel

abstract class LifecycleViewModel : ViewModel(), LifecycleOwner {

	private val lifecycleRegistry = LifecycleRegistry(this).apply {
		markState(Lifecycle.State.STARTED)
	}

	override fun getLifecycle() = lifecycleRegistry

	override fun onCleared() {
		super.onCleared()
		lifecycleRegistry.markState(Lifecycle.State.DESTROYED);
	}
}
