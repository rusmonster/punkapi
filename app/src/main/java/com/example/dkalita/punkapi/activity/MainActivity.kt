package com.example.dkalita.punkapi.activity

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.util.DiffUtil
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import com.example.dkalita.punkapi.R
import com.example.dkalita.punkapi.common.BeerDiffCallback
import com.example.dkalita.punkapi.common.lazyViewModel
import com.example.dkalita.punkapi.common.notifyWhenNotNull
import com.example.dkalita.punkapi.common.notifyWhenTrue
import com.example.dkalita.punkapi.databinding.BeerItemBinding
import com.example.dkalita.punkapi.inject.createComponent
import com.example.dkalita.punkapi.viewmodel.MainViewModel.BeerItemModel
import com.example.dkalita.punkapi.viewmodel.MainViewModel.SortingType
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

	private val viewModel by lazyViewModel { createComponent().getMainViewModel() }

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)

		swipeToRefreshView.setColorSchemeResources(R.color.colorAccent)
		swipeToRefreshView.setOnRefreshListener { viewModel.refreshBeers() }

		beerRecyclerView.layoutManager = LinearLayoutManager(this)
		beerRecyclerView.adapter = Adapter()

		navigationView.selectedItemId =
				if (viewModel.showFavoritesOnly) R.id.menu_favorites else R.id.menu_home

		navigationView.setOnNavigationItemSelectedListener { menuItem ->
			when (menuItem.itemId) {
				R.id.menu_home -> viewModel.showFavoritesOnly = false

				R.id.menu_favorites -> viewModel.showFavoritesOnly = true
			}
			return@setOnNavigationItemSelectedListener true
		}

		viewModel.onBeerSelectedListener = { beer ->
			BeerDetailsActivity.start(this, beer.id)
		}

		viewModel.isFetching.notifyWhenNotNull(this) { isFetching ->
			if (!isFetching) {
				swipeToRefreshView.isRefreshing = false
			}
		}

		viewModel.networkError.notifyWhenTrue(this) {
			viewModel.resetNetworkError()
			showError()
		}
	}

	override fun onCreateOptionsMenu(menu: Menu?): Boolean {
		menuInflater.inflate(R.menu.main_menu, menu)
		return super.onCreateOptionsMenu(menu)
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		when (item.itemId) {
			R.id.menu_sort -> {
				showSortDialog()
				return true
			}

			R.id.menu_refresh -> {
				viewModel.refreshBeers()
				return true
			}
		}

		return super.onOptionsItemSelected(item)
	}

	private fun showSortDialog() {
		val items = SortingType.values()
		val titles = items.map { getString(it.titleId) }.toTypedArray()
		val checked = items.indexOf(viewModel.sortingType)

		AlertDialog.Builder(this)
				.setTitle(R.string.sort_dialog_title)
				.setSingleChoiceItems(titles, checked) { dialog, index: Int ->
					dialog.dismiss()
					viewModel.sortingType = items[index]
				}
				.create()
				.show()
	}

	private fun showError() {
		Snackbar
				.make(beerRecyclerView, R.string.error_fetch_beers, Snackbar.LENGTH_LONG)
				.setAction(R.string.action_retry) { viewModel.refreshBeers() }
				.show();
	}

	private inner class Adapter : RecyclerView.Adapter<BeerViewHolder>() {

		private var items: List<BeerItemModel> = emptyList()

		init {
			viewModel.beerItems.notifyWhenNotNull(this@MainActivity) { newItems ->
				val oldItems = items
				items = newItems

				DiffUtil.calculateDiff(BeerDiffCallback(oldItems, newItems))
						.dispatchUpdatesTo(this)
			}
		}

		override fun getItemCount() = items.size

		override fun onBindViewHolder(holder: BeerViewHolder, position: Int) {
			holder.binding.beerItem = items[position]
		}

		override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BeerViewHolder {
			val binding = BeerItemBinding.inflate(layoutInflater, parent, false)
			binding.viewModel = viewModel
			return BeerViewHolder(binding)
		}
	}

	private class BeerViewHolder(val binding: BeerItemBinding) : RecyclerView.ViewHolder(binding.root)
}
