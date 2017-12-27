package com.example.dkalita.punkapi.inject

import android.app.Activity
import com.example.dkalita.punkapi.activity.BeerDetailsActivity
import com.example.dkalita.punkapi.activity.MainActivity
import com.example.dkalita.punkapi.PunkApiApplication

private val Activity.applicationComponent get() = (application as PunkApiApplication).component

fun MainActivity.createComponent() = applicationComponent.mainActivityComponent()

fun BeerDetailsActivity.createComponent() = applicationComponent.beerDetailsActivityComponent()
