package com.example.dkalita.punkapi.repository

import java.util.concurrent.Executor

abstract class BaseRepository(
		private val executor: Executor
) {

	protected fun doInBackground(block: () -> Unit) = executor.execute(block)
}
