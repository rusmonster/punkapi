package com.example.dkalita.punkapi.datasource

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Database
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.PrimaryKey
import android.arch.persistence.room.Query
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.Transaction
import android.arch.persistence.room.Update

@Entity
data class Beer(
		@PrimaryKey
		val id: Int,
		val name: String,
		val imageUrl: String,
		val abv: Float,
		val ibu: Float,
		val ebc: Float,
		val brewersTips: String,
		val contributedBy: String,
		val isFavorite: Boolean
)

@Entity
data class Setting(
		@PrimaryKey
		val key: Int,
		val value: String
)

@Dao
abstract class BeerDao {
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	abstract fun insert(beer: Beer)

	@Update
	abstract fun updateBeer(beer: Beer)

	@Query("SELECT * FROM beer")
	abstract fun load(): LiveData<List<Beer>>

	@Query("SELECT * FROM beer WHERE id = :beerId")
	abstract fun loadBeerById(beerId: Int): LiveData<Beer>

	@Query("SELECT * FROM beer WHERE id = :beerId")
	abstract fun getBeerById(beerId: Int): Beer?

	@Query("DELETE FROM beer WHERE id NOT IN (:beerIds)")
	abstract fun keepOnly(beerIds: List<Int>)

	@Transaction
	open fun updateFavorite(beerId: Int, isFavorite: Boolean) {
		val beer = getBeerById(beerId) ?: return
		if (beer.isFavorite == isFavorite) return
		updateBeer(beer.copy(isFavorite = isFavorite))
	}

	@Transaction
	open fun insertSaveFavorite(beer: Beer) {
		val isFavorite = getBeerById(beer.id)?.isFavorite ?: false
		insert(beer.copy(isFavorite = isFavorite))
	}

	@Transaction
	open fun replaceBeers(beers: List<Beer>) {
		keepOnly(beers.map { it.id })
		beers.forEach { insertSaveFavorite(it) }
	}
}

@Dao
interface SettingDao {
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	fun save(setting: Setting)

	@Query("SELECT * FROM setting WHERE key = :key")
	fun loadByKey(key: Int): LiveData<Setting>
}

@Database(entities = [Beer::class, Setting::class], version = 1, exportSchema = false)
abstract class BeerDatabase : RoomDatabase() {

	abstract fun beerDao(): BeerDao

	abstract fun settingDao(): SettingDao
}
