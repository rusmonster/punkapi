package com.example.dkalita.punkapi

import android.arch.core.executor.ArchTaskExecutor
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import com.example.dkalita.punkapi.datasource.Beer
import com.example.dkalita.punkapi.repository.BeerRepository
import com.example.dkalita.punkapi.repository.FetchResource
import com.example.dkalita.punkapi.repository.SettingsRepository
import com.example.dkalita.punkapi.viewmodel.MainViewModel
import com.example.dkalita.punkapi.viewmodel.MainViewModel.BeerItemModel
import com.example.dkalita.punkapi.viewmodel.MainViewModel.SortingType
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.any
import org.mockito.Mockito.anyInt
import org.mockito.Mockito.inOrder
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyZeroInteractions
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import java.util.concurrent.Executor
import kotlin.properties.Delegates
import kotlin.test.assertEquals

@RunWith(PowerMockRunner::class)
@PrepareForTest(
		SettingsRepository::class,
		BeerRepository::class
)
class MainViewModelTest {

	var mainViewModel by Delegates.notNull<MainViewModel>()

	@Mock
	lateinit var context: Context

	@Mock
	lateinit var executor: Executor

	@Mock
	lateinit var settingsRepository: SettingsRepository

	@Mock
	lateinit var beerRepository: BeerRepository

	@Mock
	lateinit var beerSelectedListener: Function1<Beer, Unit>

	private val testBeers = createTestData()

	private val beersLiveData = MutableLiveData<List<Beer>>()

	private val fetchResourceLiveData = MutableLiveData<FetchResource>()

	private val sortingSettingLiveData = MutableLiveData<Int>()

	@Before
	fun setUp() {
		ArchTaskExecutor.getInstance().setDelegate(ImmediateExecutor())

		sortingSettingLiveData.value = MainViewModel.SortingType.DEFAULT.id
		whenCall(settingsRepository.loadSortingTypeId(anyInt())).thenReturn(sortingSettingLiveData)

		beersLiveData.value = testBeers
		whenCall(beerRepository.loadBeers()).thenReturn(beersLiveData)
		whenCall(beerRepository.fetchBeers()).thenReturn(fetchResourceLiveData)

		whenCall(executor.execute(any())).then { (it.arguments[0] as Runnable).run() }

		mainViewModel = MainViewModel(context, executor, settingsRepository, beerRepository)
		mainViewModel.onBeerSelectedListener = beerSelectedListener
	}

	@Test
	fun onBearSelected() {
		val beerItem99 = BeerItemModel(99, "", "", false)
		mainViewModel.onBeerSelected(beerItem99)
		verifyZeroInteractions(beerSelectedListener)

		val beerItem0 = BeerItemModel(0, "", "", false)
		mainViewModel.onBeerSelected(beerItem0)
		verify(beerSelectedListener).invoke(testBeers[0])
	}

	@Test
	fun getSortingType() {
		sortingSettingLiveData.value = SortingType.ABV.id
		var expectedIds = testBeers.sortedBy { it.abv }.map { it.id }
		var viewItemsIds = mainViewModel.beerItems.value!!.map { it.beerId }
		assertEquals(expectedIds, viewItemsIds)

		sortingSettingLiveData.value = SortingType.EBC.id
		expectedIds = testBeers.sortedBy { it.ebc }.map { it.id }
		viewItemsIds = mainViewModel.beerItems.value!!.map { it.beerId }
		assertEquals(expectedIds, viewItemsIds)

		sortingSettingLiveData.value = SortingType.IBU.id
		expectedIds = testBeers.sortedBy { it.ibu }.map { it.id }
		viewItemsIds = mainViewModel.beerItems.value!!.map { it.beerId }
		assertEquals(expectedIds, viewItemsIds)
	}

	@Test
	fun setSortingType() {
		val callOrder = inOrder(settingsRepository)

		mainViewModel.sortingType = SortingType.EBC
		callOrder.verify(settingsRepository).saveSortingTypeId(SortingType.EBC.id)

		mainViewModel.sortingType = SortingType.ABV
		callOrder.verify(settingsRepository).saveSortingTypeId(SortingType.ABV.id)

		mainViewModel.sortingType = SortingType.IBU
		callOrder.verify(settingsRepository).saveSortingTypeId(SortingType.IBU.id)

		callOrder.verifyNoMoreInteractions()
	}
}

fun createTestData() = listOf(
		Beer(
				id = 0,
				name = "name0",
				imageUrl = "imageUrl0",
				abv = 0f,
				ebc = 2f,
				ibu = 1f,
				brewersTips = "brewersTips0",
				contributedBy = "contributedBy0",
				isFavorite = false
		),
		Beer(
				id = 1,
				name = "name1",
				imageUrl = "imageUrl1",
				abv = 1f,
				ebc = 0f,
				ibu = 2f,
				brewersTips = "brewersTips1",
				contributedBy = "contributedBy1",
				isFavorite = false
		),
		Beer(
				id = 2,
				name = "name2",
				imageUrl = "imageUrl2",
				abv = 2f,
				ebc = 1f,
				ibu = 0f,
				brewersTips = "brewersTips2",
				contributedBy = "contributedBy2",
				isFavorite = false
		)
)
