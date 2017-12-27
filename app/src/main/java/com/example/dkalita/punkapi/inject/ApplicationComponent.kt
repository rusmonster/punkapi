package com.example.dkalita.punkapi.inject

import android.app.Application
import android.arch.persistence.room.Room
import android.content.Context
import com.example.dkalita.punkapi.datasource.BeerDatabase
import dagger.Component
import dagger.Module
import dagger.Provides
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import javax.inject.Singleton

@Singleton
@Component(modules = [ApplicationModule::class])
interface ApplicationComponent {

	fun mainActivityComponent(): MainActivityComponent

	fun beerDetailsActivityComponent(): BeerDetailsActivityComponent
}

@Module
class ApplicationModule(val application: Application) {

	@Provides
	@Singleton
	@ApplicationContext
	fun provideContext(): Context = application

	@Provides
	@Singleton
	fun provideExecutor(): Executor = Executors.newSingleThreadExecutor()

	@Provides
	@Singleton
	fun provideDatabase(@ApplicationContext context: Context): BeerDatabase {
		return Room.databaseBuilder(context, BeerDatabase::class.java, "beer-db").build()
	}
}
