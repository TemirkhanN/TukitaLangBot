package me.nasukhov.tukitalearner.study

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface QuestionRepository : JpaRepository<Question, Long> {
    @Query(
        """
        SELECT q FROM Question q
        WHERE NOT EXISTS (
            SELECT 1 FROM GroupQuestion gq
            WHERE gq.question = q AND gq.channelId = :channelId
        )
        ORDER BY function('RANDOM')
        LIMIT 1
        """,
    )
    fun findUnseenRandom(
        @Param("channelId") channelId: String,
    ): Optional<Question>
}
