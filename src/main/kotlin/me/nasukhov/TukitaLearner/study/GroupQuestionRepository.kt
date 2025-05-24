package me.nasukhov.TukitaLearner.study

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface GroupQuestionRepository : JpaRepository<GroupQuestion, UUID> {
    @Query("SELECT gq FROM GroupQuestion gq LEFT JOIN FETCH gq.answers WHERE gq.id = :id")
    fun findByIdWithAnswers(@Param("id") id: UUID): Optional<GroupQuestion>
}
