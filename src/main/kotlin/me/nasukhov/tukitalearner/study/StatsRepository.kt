package me.nasukhov.tukitalearner.study

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface StatsRepository : JpaRepository<Answer, Long> {
    @Query(
        """
        SELECT new me.nasukhov.tukitalearner.study.StudentStats(
            a.userId,
            SUM(CASE WHEN a.isCorrect = true THEN 1 ELSE 0 END),
            COUNT(a)
        )
        FROM Answer a
        JOIN GroupQuestion gq
        WHERE gq.group = :groupId
        GROUP BY a.userId
        ORDER BY COUNT(a) DESC
        """,
    )
    fun getStatsForGroup(
        @Param("groupId") groupId: String,
    ): List<StudentStats>
}
