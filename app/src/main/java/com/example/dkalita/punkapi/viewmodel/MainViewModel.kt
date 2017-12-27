package com.example.dkalita.punkapi.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.content.Context
import com.example.dkalita.punkapi.R
import com.example.dkalita.punkapi.common.notifyWhenNotNull
import com.example.dkalita.punkapi.datasource.Beer
import com.example.dkalita.punkapi.inject.ApplicationContext
import com.example.dkalita.punkapi.repository.BeerRepository
import com.example.dkalita.punkapi.repository.SettingsRepository
import com.example.dkalita.punkapi.viewmodel.MainViewModel.SortingType.ABV
import com.example.dkalita.punkapi.viewmodel.MainViewModel.SortingType.EBC
import com.example.dkalita.punkapi.viewmodel.MainViewModel.SortingType.IBU
import java.util.concurrent.Executor
import javax.inject.Inject
import kotlin.properties.Delegates

class MainViewModel @Inject constructor(
		@ApplicationContext
		private val context: Context,
		private val executor: Executor,
		private val settingRepository: SettingsRepository,
		private val beerRepository: BeerRepository
) : LifecycleViewModel() {

	val beerItems = MutableLiveData<List<BeerItemModel>>()

	val isFetching = MutableLiveData<Boolean>()

	val networkError = MutableLiveData<Boolean>()

	var showFavoritesOnly by Delegates.observable(false) { _, _, _ -> updateBeerItems() }

	var sortingType: SortingType
		get() = SortingType[sortingTypeSetting.value!!]
		set(value) = settingRepository.saveSortingTypeId(value.id)

	var onBeerSelectedListener: (Beer) -> Unit = {}

	private val sortingTypeSetting = settingRepository.loadSortingTypeId(SortingType.DEFAULT.id)

	private val beers = beerRepository.loadBeers()

	init {
		sortingTypeSetting.notifyWhenNotNull(this) { updateBeerItems() }
		beers.notifyWhenNotNull(this) { updateBeerItems() }
		refreshBeers()
	}

	fun refreshBeers() {
		if (isFetching.value == true) return

		beerRepository.fetchBeers().notifyWhenNotNull(this) { fetchResource ->
			isFetching.value = fetchResource.isFetching
			networkError.value = fetchResource.fetchError
		}
	}

	fun resetNetworkError() {
		networkError.value = false
	}

	fun onItemFavoriteClick(beerItem: BeerItemModel, isChecked: Boolean) {
		val beer = findBeer(beerItem) ?: return
		beerRepository.makeFavorite(beer, isChecked)
	}

	fun onBeerSelected(beerItem: BeerItemModel) {
		findBeer(beerItem)?.let { onBeerSelectedListener(it) }
	}

	private fun findBeer(beerItem: BeerItemModel): Beer? {
		return beers.value?.find { it.id == beerItem.beerId }
	}

	private fun updateBeerItems() {
		val isFavoritesOnly = showFavoritesOnly

		executor.execute {
			val updatedBeerItems = beers.value
					?.filter { !isFavoritesOnly || it.isFavorite }
					?.sortedWith(sortingType.comparator)
					?.map { it.toBeerItemModel(sortingType) }

			beerItems.postValue(updatedBeerItems)
		}
	}

	private fun Beer.toBeerItemModel(sorting: SortingType): BeerItemModel {
		val secondRowLabel = context.getString(sorting.titleId)
		val secondRowValue = when (sorting) {
			ABV -> abv
			IBU -> ibu
			EBC -> ebc
		}

		return BeerItemModel(
				beerId = id,
				firstRow = name,
				secondRow = "$secondRowLabel: $secondRowValue",
				isFavorite = isFavorite
		)
	}

	data class BeerItemModel(
			val beerId: Int,
			val firstRow: String,
			val secondRow: String,
			val isFavorite: Boolean
	)

	enum class SortingType(val id: Int, val titleId: Int, val comparator: Comparator<Beer>) {
		ABV(0, R.string.sorting_type_abv, compareBy({ it.abv }, { it.name })),
		IBU(1, R.string.sorting_type_ibu, compareBy({ it.ibu }, { it.name })),
		EBC(2, R.string.sorting_type_ebc, compareBy({ it.ebc }, { it.name }));

		companion object {

			val DEFAULT = ABV

			private val map = values().associateBy { it.id }

			operator fun get(id: Int) = map[id] ?: error("Unknown SortingType id: $id")
		}
	}
}
