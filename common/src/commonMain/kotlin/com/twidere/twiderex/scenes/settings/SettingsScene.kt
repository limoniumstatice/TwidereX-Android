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
package com.twidere.twiderex.scenes.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.ListItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.twidere.twiderex.component.foundation.AppBar
import com.twidere.twiderex.component.foundation.AppBarNavigationButton
import com.twidere.twiderex.component.foundation.InAppNotificationScaffold
import com.twidere.twiderex.component.painterResource
import com.twidere.twiderex.component.stringResource
import com.twidere.twiderex.navigation.Root
import com.twidere.twiderex.ui.TwidereScene
import dev.icerock.moko.resources.FileResource
import dev.icerock.moko.resources.StringResource
import io.github.seiko.precompose.annotation.NavGraphDestination
import moe.tlaster.precompose.navigation.Navigator

data class SettingItem(
  val name: StringResource,
  val icon: FileResource,
  val route: String,
)

private val settings =
  mapOf(
    com.twidere.twiderex.MR.strings.scene_settings_section_header_account to listOf(
      SettingItem(
        com.twidere.twiderex.MR.strings.scene_settings_privacy_and_safety_title,
        com.twidere.twiderex.MR.files.ic_privacy_and_safety,
        route = Root.Settings.PrivacyAndSafety,
      )
    ),
    com.twidere.twiderex.MR.strings.scene_settings_section_header_general to listOf(
      SettingItem(
        com.twidere.twiderex.MR.strings.scene_settings_appearance_title,
        com.twidere.twiderex.MR.files.ic_shirt,
        route = Root.Settings.Appearance,
      ),
      SettingItem(
        com.twidere.twiderex.MR.strings.scene_settings_display_title,
        com.twidere.twiderex.MR.files.ic_template,
        route = Root.Settings.Display,
      ),
      SettingItem(
        com.twidere.twiderex.MR.strings.scene_settings_layout_title,
        com.twidere.twiderex.MR.files.ic_layout_sidebar,
        route = Root.Settings.Layout,
      ),
      SettingItem(
        com.twidere.twiderex.MR.strings.scene_settings_notification_title,
        com.twidere.twiderex.MR.files.ic_settings_notification,
        route = Root.Settings.Notification,
      ),
      SettingItem(
        com.twidere.twiderex.MR.strings.scene_settings_storage_title,
        com.twidere.twiderex.MR.files.ic_database,
        route = Root.Settings.Storage,
      ),
      SettingItem(
        com.twidere.twiderex.MR.strings.scene_settings_misc_title,
        com.twidere.twiderex.MR.files.ic_triangle_square_circle,
        route = Root.Settings.Misc,
      ),
    ),
    com.twidere.twiderex.MR.strings.scene_settings_section_header_about to listOf(
      SettingItem(
        com.twidere.twiderex.MR.strings.scene_settings_about_title,
        com.twidere.twiderex.MR.files.ic_info_circle,
        route = Root.Settings.About,
      ),
    )
  )

@NavGraphDestination(
  route = Root.Settings.Home,
)
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SettingsScene(
  navigator: Navigator,
) {
  TwidereScene {
    InAppNotificationScaffold(
      topBar = {
        AppBar(
          navigationIcon = {
            AppBarNavigationButton(
              onBack = {
                navigator.popBackStack()
              }
            )
          },
          title = {
            Text(text = stringResource(com.twidere.twiderex.MR.strings.scene_settings_title))
          }
        )
      }
    ) {
      LazyColumn(
        contentPadding = it
      ) {
        settings.forEach {
          item {
            ListItem(
              text = {
                ProvideTextStyle(value = MaterialTheme.typography.button) {
                  Text(text = stringResource(it.key))
                }
              },
            )
          }
          items(it.value) {
            ListItem(
              modifier = Modifier.clickable(
                onClick = {
                  if (it.route.isNotEmpty()) {
                    navigator.navigate(it.route)
                  }
                }
              ),
              icon = {
                Icon(
                  painter = painterResource(it.icon),
                  contentDescription = stringResource(it.name),
                  modifier = Modifier.size(24.dp),
                )
              },
              text = {
                Text(text = stringResource(it.name))
              },
            )
          }
        }
      }
    }
  }
}
