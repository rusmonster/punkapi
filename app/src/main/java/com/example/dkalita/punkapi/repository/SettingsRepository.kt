package com.example.dkalita.punkapi.repository

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import com.example.dkalita.punkapi.datasource.BeerDatabase
import com.example.dkalita.punkapi.datasource.Setting
import java.util.concurrent.Executor
import javax.inject.Inject

class SettingsRepository @Inject constructor(
		database: BeerDatabase,
		executor: Executor
) : BaseRepository(executor) {

	private val KEY_SORTING_TYPE = 1

	private val settingDao = database.settingDao()

	fun saveSortingTypeId(sortingTypeId: Int) = doInBackground {
		val setting = Setting(KEY_SORTING_TYPE, sortingTypeId.toString())
		settingDao.save(setting)
	}

	fun loadSortingTypeId(defaultValue: Int): LiveData<Int> {
		val result = MediatorLiveData<Int>()
		result.value = defaultValue
		result.addSource(settingDao.loadByKey(KEY_SORTING_TYPE)) { setting ->
			result.value = setting?.value?.toInt() ?: defaultValue
		}
		return result
	}
}
