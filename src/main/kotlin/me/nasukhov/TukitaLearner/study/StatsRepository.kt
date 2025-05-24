package me.nasukhov.TukitaLearner.study

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface StatsRepository : JpaRepository<Answer, Long> {
    @Query(
        """
        SELECT new me.nasukhov.TukitaLearner.study.StudentStats(
            a.userId,
            SUM(CASE WHEN a.isCorrect = true THEN 1 ELSE 0 END),
            COUNT(a)
        )
        FROM Answer a
        WHERE a.channelId = :channelId
        GROUP BY a.userId
        ORDER BY COUNT(a) DESC
        
        """
    )
    fun getStatsForGroup(@Param("channelId") channelId: String): List<StudentStats>
}
