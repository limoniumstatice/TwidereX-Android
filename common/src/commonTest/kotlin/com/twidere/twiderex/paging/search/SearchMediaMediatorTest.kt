/*
 *  Twidere X
 *
 *  Copyright (C) TwidereProject and Contributors
 * 
 *  This file is part of Twidere X.
 * 
 *  Twidere X is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  Twidere X is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with Twidere X. If not, see <http://www.gnu.org/licenses/>.
 */
package com.twidere.twiderex.paging.search

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingConfig
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.twidere.twiderex.mock.db.MockCacheDatabase
import com.twidere.twiderex.mock.paging.collectDataForTest
import com.twidere.twiderex.mock.service.MockSearchService
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.paging.PagingTimeLineWithStatus
import com.twidere.twiderex.paging.mediator.search.SearchMediaMediator
import kotlinx.coroutines.runBlocking
import kotlin.test.Test

internal class SearchMediaMediatorTest {
  @OptIn(ExperimentalPagingApi::class)
  @Test
  fun refresh_saveToDatabaseWhenSuccess() = runBlocking {
    val mockDataBase = MockCacheDatabase()
    val accountKey = MicroBlogKey.twitter("test")
    val mediator = SearchMediaMediator(
      query = "test",
      database = mockDataBase,
      accountKey = accountKey,
      service = MockSearchService()
    )
    val pagingState = PagingState<Int, PagingTimeLineWithStatus>(emptyList(), config = PagingConfig(20), anchorPosition = 0, leadingPlaceholderCount = 0)
    val result = mediator.load(LoadType.REFRESH, pagingState)
    // when mediator get data from service, it store to database\
    assert(mockDataBase.pagingTimelineDao().getPagingSource(pagingKey = mediator.pagingKey, accountKey = accountKey).collectDataForTest().isNotEmpty())
    assert(result is RemoteMediator.MediatorResult.Success)
    assert(!(result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
  }

  @OptIn(ExperimentalPagingApi::class)
  @Test
  fun refresh_LoadReturnsErrorResultWhenErrorOccurs() = runBlocking {
    val accountKey = MicroBlogKey.twitter("test")
    val mediator = SearchMediaMediator(
      query = "test",
      database = MockCacheDatabase(),
      accountKey = accountKey,
      service = MockSearchService().apply {
        errorMsg = "throw test errors"
      }
    )
    val pagingState = PagingState<Int, PagingTimeLineWithStatus>(emptyList(), config = PagingConfig(20), anchorPosition = 0, leadingPlaceholderCount = 0)
    val result = mediator.load(LoadType.REFRESH, pagingState)
    assert(result is RemoteMediator.MediatorResult.Error)
  }
}
