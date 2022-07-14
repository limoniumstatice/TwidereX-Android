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
package com.twidere.twiderex.db.sqldelight.transform

import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.enums.ComposeType
import com.twidere.twiderex.model.ui.UiDraft
import org.junit.Test
import java.util.UUID
import kotlin.test.assertEquals

internal class DraftTransformTest {

    @Test
    fun transform() {
        val uiDraft = UiDraft(
            draftId = UUID.randomUUID().toString(),
            content = "draft",
            media = listOf("media"),
            createdAt = System.currentTimeMillis(),
            composeType = ComposeType.New,
            statusKey = MicroBlogKey.valueOf("status"),
            excludedReplyUserIds = listOf("userId")
        )
        val draft = uiDraft.toDbDraft()
        assertEquals(uiDraft, draft.toUi())
    }
}
