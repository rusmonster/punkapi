package com.example.dkalita.punkapi

import android.app.Application
import com.example.dkalita.punkapi.inject.ApplicationModule
import com.example.dkalita.punkapi.inject.DaggerApplicationComponent

class PunkApiApplication : Application() {

	val component by lazy {
		DaggerApplicationComponent.builder()
				.applicationModule(ApplicationModule(this))
				.build()
	}
}