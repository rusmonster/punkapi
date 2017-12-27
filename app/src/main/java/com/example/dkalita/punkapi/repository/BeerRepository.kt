package com.example.dkalita.punkapi.repository

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.example.dkalita.punkapi.datasource.Beer
import com.example.dkalita.punkapi.datasource.BeerDatabase
import com.example.dkalita.punkapi.datasource.PunkWebService
import com.example.dkalita.punkapi.datasource.PunkWebService.PunkItem
import java.util.concurrent.Executor
import javax.inject.Inject

class BeerRepository @Inject constructor(
		private val webService: PunkWebService,
		database: BeerDatabase,
		executor: Executor
) : BaseRepository(executor) {

	private val beerDao = database.beerDao()

	fun loadBeer(beerId: Int): LiveData<Beer> {
		fetchBeer(beerId)
		return beerDao.loadBeerById(beerId)
	}

	fun loadBeers(): LiveData<List<Beer>> {
		fetchBeers()
		return beerDao.load()
	}

	fun fetchBeer(beerId: Int): LiveData<FetchResource> {
		val result = MutableLiveData<FetchResource>().apply {
			value = FetchResource(isFetching = true)
		}

		doInBackground {
			val beer = fetch(result) { webService.fetchItem(beerId) }
					?.toBeer()
					?: return@doInBackground

			beerDao.insertSaveFavorite(beer)
		}

		return result
	}

	fun fetchBeers(): LiveData<FetchResource> {
		val result = MutableLiveData<FetchResource>().apply {
			value = FetchResource(isFetching = true)
		}

		doInBackground {
			val beers = fetch(result) { webService.fetchAll() }
					?.map { it.toBeer() }
					?: return@doInBackground

			beerDao.replaceBeers(beers)
		}

		return result
	}

	fun makeFavorite(beer: Beer, isFavorite: Boolean) = doInBackground {
		beerDao.updateFavorite(beer.id, isFavorite)
	}

	private inline fun <T> fetch(fetchResource: MutableLiveData<FetchResource>, block: () -> T): T? {
		var fetchValue = fetchResource.value!!
		try {
			return block()
		} catch (e: Exception) {
			fetchValue = fetchValue.copy(fetchError = true)
			fetchResource.postValue(fetchValue)
			return null
		} finally {
			fetchValue = fetchValue.copy(isFetching = false)
			fetchResource.postValue(fetchValue)
		}
	}

	private fun PunkItem.toBeer() = Beer(
			id = id,
			name = name,
			imageUrl = image_url,
			abv = abv,
			ebc = ebc,
			ibu = ibu,
			brewersTips = brewers_tips,
			contributedBy = contributed_by,
			isFavorite = false
	)
}
