package com.example.dkalita.punkapi.inject

import com.example.dkalita.punkapi.activity.BeerDetailsActivity
import dagger.Subcomponent

@Subcomponent
interface BeerDetailsActivityComponent {

	fun inject(activity: BeerDetailsActivity)
}
