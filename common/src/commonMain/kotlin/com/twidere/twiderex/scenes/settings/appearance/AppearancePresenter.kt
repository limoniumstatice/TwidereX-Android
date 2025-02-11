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
package com.twidere.twiderex.scenes.settings.appearance

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.twidere.twiderex.di.ext.get
import com.twidere.twiderex.preferences.PreferencesHolder
import com.twidere.twiderex.preferences.model.AppearancePreferences
import kotlinx.coroutines.flow.Flow

@Composable
fun AppearancePresenter(
  event: Flow<AppearanceEvent>,
  preferencesHolder: PreferencesHolder = get(),
): AppearanceState {
  var showPrimaryColorDialog by remember { mutableStateOf(false) }
  val preferences by preferencesHolder.appearancePreferences.data.collectAsState(
    AppearancePreferences()
  )

  LaunchedEffect(Unit) {
    event.collect { event ->
      when (event) {
        AppearanceEvent.HidePrimaryColorDialog -> showPrimaryColorDialog = false
        AppearanceEvent.ShowPrimaryColorDialog -> showPrimaryColorDialog = true
        is AppearanceEvent.SelectPrimaryColor -> preferencesHolder.appearancePreferences.updateData {
          it.copy(primaryColorIndex = event.color)
        }
        is AppearanceEvent.SetHideAppBarWhenScrolling -> preferencesHolder.appearancePreferences.updateData {
          it.copy(hideAppBarWhenScroll = event.hide)
        }
        is AppearanceEvent.SetHideFabWhenScrolling -> preferencesHolder.appearancePreferences.updateData {
          it.copy(hideFabWhenScroll = event.hide)
        }
        is AppearanceEvent.SetHideTabBarWhenScrolling -> preferencesHolder.appearancePreferences.updateData {
          it.copy(hideTabBarWhenScroll = event.hide)
        }
        is AppearanceEvent.SetIsDarkModePureBlack -> preferencesHolder.appearancePreferences.updateData {
          it.copy(isDarkModePureBlack = event.isDarkModePureBlack)
        }
        is AppearanceEvent.SetTabPosition -> preferencesHolder.appearancePreferences.updateData {
          it.copy(tabPosition = event.position)
        }
        is AppearanceEvent.SetTheme -> preferencesHolder.appearancePreferences.updateData {
          it.copy(theme = event.theme)
        }
      }
    }
  }

  return AppearanceState(
    showPrimaryColorDialog = showPrimaryColorDialog,
    appearance = preferences,
  )
}

data class AppearanceState(
  val showPrimaryColorDialog: Boolean,
  val appearance: AppearancePreferences,
)

interface AppearanceEvent {
  object ShowPrimaryColorDialog : AppearanceEvent
  object HidePrimaryColorDialog : AppearanceEvent
  data class SelectPrimaryColor(val color: Int) : AppearanceEvent
  data class SetTabPosition(val position: AppearancePreferences.TabPosition) : AppearanceEvent
  data class SetTheme(val theme: AppearancePreferences.Theme) : AppearanceEvent
  data class SetHideTabBarWhenScrolling(val hide: Boolean) : AppearanceEvent
  data class SetHideFabWhenScrolling(val hide: Boolean) : AppearanceEvent
  data class SetHideAppBarWhenScrolling(val hide: Boolean) : AppearanceEvent
  data class SetIsDarkModePureBlack(val isDarkModePureBlack: Boolean) : AppearanceEvent
}
