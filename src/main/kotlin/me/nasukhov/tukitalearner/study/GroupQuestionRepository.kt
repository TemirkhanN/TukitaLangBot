package me.nasukhov.tukitalearner.study

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface GroupQuestionRepository : JpaRepository<GroupQuestion, UUID> {
    @Query(
        """
        SELECT gq FROM GroupQuestion gq
        JOIN FETCH gq.question
        JOIN FETCH gq.group
        LEFT JOIN FETCH gq.answers
        WHERE gq.id = :id
    """,
    )
    fun findByIdWithAnswers(
        @Param("id") id: UUID,
    ): Optional<GroupQuestion>
}
