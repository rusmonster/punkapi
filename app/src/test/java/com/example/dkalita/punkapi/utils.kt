package com.example.dkalita.punkapi

import android.arch.core.executor.TaskExecutor
import org.powermock.api.mockito.PowerMockito

fun<T: Any?> whenCall(methodCall: T) = PowerMockito.`when`(methodCall)

class ImmediateExecutor : TaskExecutor() {

	override fun isMainThread() = true

	override fun executeOnDiskIO(runnable: Runnable?) {
		runnable?.run()
	}

	override fun postToMainThread(runnable: Runnable?) {
		runnable?.run()
	}
}
