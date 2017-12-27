package com.example.dkalita.punkapi.activity

import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.dkalita.punkapi.R
import com.example.dkalita.punkapi.common.GlideApp
import com.example.dkalita.punkapi.common.notifyWhenNotNull
import com.example.dkalita.punkapi.databinding.ActivityBeerDetailsBinding
import com.example.dkalita.punkapi.inject.createComponent
import com.example.dkalita.punkapi.repository.BeerRepository
import kotlinx.android.synthetic.main.activity_beer_details.*
import javax.inject.Inject

class BeerDetailsActivity : AppCompatActivity() {

	companion object {

		private val KEY_BEER_ID = "KEY_BEER_ID"

		fun start(context: Context, beerId: Int) {
			val intent = Intent(context, BeerDetailsActivity::class.java).apply {
				putExtra(KEY_BEER_ID, beerId)
			}
			context.startActivity(intent)
		}
	}

	@Inject
	lateinit var beerRepository: BeerRepository

	private val beerId
		get() = intent.getIntExtra(KEY_BEER_ID, -1).also { check(it != -1) }

	private val beer by lazy { beerRepository.loadBeer(beerId) }

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		createComponent().inject(this)

		val binding = DataBindingUtil.setContentView<ActivityBeerDetailsBinding>(
				this, R.layout.activity_beer_details)

		setSupportActionBar(toolbarView)

		fabView.setOnClickListener {
			val beer = beer.value ?: return@setOnClickListener
			beerRepository.makeFavorite(beer, !beer.isFavorite)
		}

		beer.notifyWhenNotNull(this) { beer ->
			binding.beer = beer

			GlideApp.with(this)
					.load(beer.imageUrl)
					.diskCacheStrategy(DiskCacheStrategy.RESOURCE)
					.placeholder(R.drawable.ic_local_drink)
					.thumbnail()
					.into(imageView)
		}
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		if (item.itemId == android.R.id.home) {
			finish()
			return true
		}
		return super.onOptionsItemSelected(item)
	}
}
