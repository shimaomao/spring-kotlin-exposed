package com.example.api.tweeter

import com.example.api.tweeter.db.TweetStatus
import com.example.api.tweeter.db.TweetsRecord
import java.time.Instant
import java.util.*

data class CreateTweetRequest(val message: String, val comment: String?)
data class UpdateTweetRequest(val message: String, val comment: String?)

data class TweetDto(
        val id: UUID, val createdAt: Instant, val modifiedAt: Instant, val deletedAt: Instant,
        val message: String, val comment: String?, val status: TweetStatus
)

fun TweetsRecord.toTweetsDto() = TweetDto(
        id = id, createdAt = createdAt, modifiedAt = modifiedAt, deletedAt = deletedAt,
        message = message, comment = comment, status = status
)

fun CreateTweetRequest.toRecord(id: UUID, now: Instant): TweetsRecord =
        TweetsRecord(
                id = id, version = 0, createdAt = now, modifiedAt = now, deletedAt = Instant.EPOCH,
                message = message, comment = comment, status = TweetStatus.DRAFT
        )
