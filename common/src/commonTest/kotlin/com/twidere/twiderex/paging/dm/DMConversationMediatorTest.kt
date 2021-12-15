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
package com.twidere.twiderex.paging.dm

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingConfig
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.twidere.twiderex.mock.db.MockCacheDatabase
import com.twidere.twiderex.mock.service.MockDirectMessageService
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.ui.UiDMConversationWithLatestMessage
import com.twidere.twiderex.paging.mediator.dm.DMConversationMediator
import kotlinx.coroutines.runBlocking
import kotlin.test.Test

internal class DMConversationMediatorTest {
    private val accountKey = MicroBlogKey.twitter("123")

    private val mockDataBase = MockCacheDatabase()

    private var mockService = MockDirectMessageService(accountKey)

    @OptIn(ExperimentalPagingApi::class)
    @Test
    fun refresh_LoadReturnsSuccessResultWhenSuccess() = runBlocking {
        val mediator = DMConversationMediator(mockDataBase, accountKey = accountKey) { mockService.getDirectMessages(it, 50) }
        val pagingState = PagingState<Int, UiDMConversationWithLatestMessage>(emptyList(), config = PagingConfig(20), anchorPosition = 0, leadingPlaceholderCount = 0)
        val result = mediator.load(LoadType.REFRESH, pagingState)
        assert(result is RemoteMediator.MediatorResult.Success)
        assert(!(result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
    }

    @OptIn(ExperimentalPagingApi::class)
    @Test
    fun refresh_LoadReturnsErrorResultWhenErrorOccurs() = runBlocking {
        mockService.errorMsg = "Throw test failure"
        val mediator = DMConversationMediator(mockDataBase, accountKey = accountKey) {
            mockService.getDirectMessages(it, 50)
        }
        val pagingState = PagingState<Int, UiDMConversationWithLatestMessage>(emptyList(), config = PagingConfig(20), anchorPosition = 0, leadingPlaceholderCount = 0)
        val result = mediator.load(LoadType.REFRESH, pagingState)
        assert(result is RemoteMediator.MediatorResult.Error)
    }
}
