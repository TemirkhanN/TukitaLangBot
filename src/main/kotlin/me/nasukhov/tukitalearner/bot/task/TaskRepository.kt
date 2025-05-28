package me.nasukhov.tukitalearner.bot.task

import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface TaskRepository : JpaRepository<Task, Long> {
    @Query(
        """
    SELECT t
    FROM Task t
    WHERE t.nextExecutionAt <= CURRENT_TIMESTAMP
      AND t.isActive = true
    """,
    )
    fun getScheduledTasks(limit: Pageable): List<Task>
}
