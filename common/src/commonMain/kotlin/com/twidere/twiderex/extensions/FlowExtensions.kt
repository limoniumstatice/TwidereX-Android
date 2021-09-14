/*
 *  Twidere X
 *
 *  Copyright (C) 2020-2021 Tlaster <tlaster@outlook.com>
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
package com.twidere.twiderex.extensions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collect
import moe.tlaster.precompose.lifecycle.Lifecycle
import moe.tlaster.precompose.lifecycle.repeatOnLifecycle
import moe.tlaster.precompose.ui.LocalLifecycleOwner

@Composable
fun <T> Flow<T>.observeAsState(initial: T): State<T> {
    val lifecycleOwner = LocalLifecycleOwner.current
    return remember(this, lifecycleOwner) {
        flowWithLifecycle(lifecycleOwner.lifecycle)
    }.collectAsState(initial = initial)
}

@OptIn(ExperimentalCoroutinesApi::class)
fun <T> Flow<T>.flowWithLifecycle(
    lifecycle: Lifecycle,
): Flow<T> = callbackFlow {
    lifecycle.repeatOnLifecycle {
        this@flowWithLifecycle.collect {
            send(it)
        }
    }
    close()
}
