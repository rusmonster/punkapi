package com.example.dkalita.punkapi.inject

import com.example.dkalita.punkapi.viewmodel.MainViewModel
import dagger.Subcomponent

@Subcomponent
interface MainActivityComponent {

	fun getMainViewModel(): MainViewModel
}
