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
package com.twidere.twiderex.model.ui

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.LayoutDirection
import com.twidere.twiderex.component.status.ResolvedLink
import com.twidere.twiderex.extensions.humanizedCount
import com.twidere.twiderex.extensions.humanizedTimestamp
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.enums.MastodonNotificationType
import com.twidere.twiderex.model.enums.MastodonVisibility
import com.twidere.twiderex.model.enums.PlatformType
import com.twidere.twiderex.model.enums.TwitterReplySettings
import com.twidere.twiderex.model.ui.mastodon.MastodonMention
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList
import moe.tlaster.twitter.parser.Token
import moe.tlaster.twitter.parser.TwitterParser
import java.text.Bidi

sealed interface UiTimeline {
  val key: String
  val contentType: String
}

@Immutable
data class UiGap(
  val maxId: String,
  val sinceId: String,
  val loading: Boolean,
) : UiTimeline {
  override val contentType: String = "gap"
  override val key = "$maxId-$sinceId"
}

private val parser = TwitterParser()

sealed interface UiStatusTimeline : UiTimeline {
  val statusKey: MicroBlogKey
  val metrics: UiStatusMetrics
  val humanizedTime: String
  val user: UiUser
  val platformType: PlatformType
  val source: String
  val parsedContent: ImmutableList<Token>
  val geo: UiGeo?
  val contentDirection: LayoutDirection
  val url: ImmutableList<UiUrlEntity>
  val menu: UiStatusTimelineMenu
}

data class UiStatusTimelineMenu(
  val retweetOpened: Boolean,
  val moreOpened: Boolean,
)

fun UiStatusTimeline.resolveLink(
  href: String,
): ResolvedLink {
  val entity = url.firstOrNull { it.url == href }
  return when {
    entity != null -> {
      ResolvedLink(
        expanded = entity.expandedUrl,
        display = entity.displayUrl,
      )
    }
    else -> {
      ResolvedLink(expanded = null)
    }
  }
}

sealed interface UiStatusWithExtra : UiTimeline {
  val status: UiStatusTimeline
}

@Immutable
data class UiTwitterStatus(
  override val statusKey: MicroBlogKey,
  // non html content
  val content: String,
  override val metrics: UiStatusMetrics,
  val timestamp: Long,
  override val user: UiUser,
  override val platformType: PlatformType,
  override val source: String,
  override val geo: UiGeo?,
  val replySettings: TwitterReplySettings,
  override val url: ImmutableList<UiUrlEntity>,
  override val menu: UiStatusTimelineMenu,
) : UiTimeline, UiStatusTimeline {
  override val key = statusKey.toString()
  override val humanizedTime = timestamp.humanizedTimestamp()
  override val contentType = "twitter-status"
  override val parsedContent: ImmutableList<Token> = parser.parse(content).toPersistentList()
  override val contentDirection = if (Bidi(content, Bidi.DIRECTION_DEFAULT_LEFT_TO_RIGHT).baseIsLeftToRight()) {
    LayoutDirection.Ltr
  } else {
    LayoutDirection.Rtl
  }
}

@Immutable
data class UiMastodonStatus(
  override val statusKey: MicroBlogKey,
  // non html content
  val content: String,
  override val metrics: UiStatusMetrics,
  val timestamp: Long,
  override val user: UiUser,
  override val platformType: PlatformType,
  override val source: String,
  override val geo: UiGeo?,
  val expanded: Boolean,
  val spoilerText: String? = null,
  val notificationType: MastodonNotificationType?,
  val emoji: ImmutableList<UiEmoji>,
  val visibility: MastodonVisibility,
  val mentions: ImmutableList<MastodonMention>?,
  override val url: ImmutableList<UiUrlEntity>,
  override val menu: UiStatusTimelineMenu,
) : UiTimeline, UiStatusTimeline {
  override val key = statusKey.toString()
  override val humanizedTime = timestamp.humanizedTimestamp()
  override val contentType = "mastodon-status"
  override val parsedContent: ImmutableList<Token> = parser.parse(content).toPersistentList()
  val parsedSpoilerText: ImmutableList<Token> = parser.parse(spoilerText ?: "").toPersistentList()
  override val contentDirection = if (Bidi(content, Bidi.DIRECTION_DEFAULT_LEFT_TO_RIGHT).baseIsLeftToRight()) {
    LayoutDirection.Ltr
  } else {
    LayoutDirection.Rtl
  }
}

@Immutable
data class UiStatusMetrics(
  val retweetCount: Long,
  val likeCount: Long,
  val replyCount: Long,
  val quoteCount: Long,
  val liked: Boolean,
  val retweeted: Boolean,
) {
  val humanizedRetweetCount = retweetCount.humanizedCount()
  val humanizedLikeCount = likeCount.humanizedCount()
  val humanizedReplyCount = replyCount.humanizedCount()
  val humanizedQuoteCount = quoteCount.humanizedCount()
  val hasRetweetCount = retweetCount > 0
  val hasLikeCount = likeCount > 0
  val hasReplyCount = replyCount > 0
  val hasQuoteCount = quoteCount > 0
}

@Immutable
data class UiStatusWithPoll(
  override val status: UiStatusTimeline,
  val poll: UiPoll,
) : UiStatusWithExtra {
  override val key = status.key
  override val contentType: String = "${status.contentType}-poll"
}

@Immutable
data class UiStatusWithMedia(
  override val status: UiStatusTimeline,
  val media: ImmutableList<UiMedia>,
) : UiStatusWithExtra {
  override val key = status.key
  override val contentType: String = "${status.contentType}-media"
}

@Immutable
data class UiStatusWithCard(
  override val status: UiStatusTimeline,
  val card: UiCard,
) : UiStatusWithExtra {
  override val key = status.key
  override val contentType: String = "${status.contentType}-card"
}

@Immutable
data class UiRetweetStatus(
  val status: UiStatusWithExtra,
  val retweet: UiStatusWithExtra,
) : UiTimeline {
  override val key = status.key
  override val contentType: String = "retweet-status-${retweet.contentType}"
}

@Immutable
data class UiStatusWithQuote(
  val status: UiStatusWithExtra,
  val quote: UiStatusWithExtra,
) : UiTimeline {
  override val key = status.key
  override val contentType: String = "status-quote-${quote.contentType}"
}

@Immutable
data class UiStatusWithRetweetAndQuote(
  val status: UiStatusWithExtra,
  val retweet: UiStatusWithExtra,
  val quote: UiStatusWithExtra,
) : UiTimeline {
  override val key = status.key
  override val contentType: String = "status-retweet-quote-${quote.contentType}"
}

@Immutable
data class UiFollow(
  val user: UiUser,
) : UiTimeline {
  override val key = user.userKey.copy(id = user.userKey.id + "-follow").toString()
  override val contentType: String = "follow"
}

@Immutable
data class UiFollowRequest(
  val user: UiUser,
) : UiTimeline {
  override val key = user.userKey.copy(id = user.userKey.id + "-follow-request").toString()
  override val contentType: String = "follow-request"
}
