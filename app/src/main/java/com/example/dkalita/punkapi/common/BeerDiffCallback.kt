package com.example.dkalita.punkapi.common

import android.support.v7.util.DiffUtil
import com.example.dkalita.punkapi.viewmodel.MainViewModel.BeerItemModel

class BeerDiffCallback(val oldBeers: List<BeerItemModel>, val newBeers: List<BeerItemModel>) : DiffUtil.Callback() {

	override fun getOldListSize() = oldBeers.size

	override fun getNewListSize() = newBeers.size

	override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
		return oldBeers[oldItemPosition].beerId == newBeers[newItemPosition].beerId
	}

	override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
		return oldBeers[oldItemPosition] == newBeers[newItemPosition]
	}
}
