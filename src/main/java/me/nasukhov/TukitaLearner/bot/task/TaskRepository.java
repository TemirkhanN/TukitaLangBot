package me.nasukhov.TukitaLearner.bot.task;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    @Query("""
    SELECT t
    FROM Task t
    JOIN t.channel c
    WHERE t.nextExecutionAt <= CURRENT_TIMESTAMP
      AND t.isActive = true
      AND c.isActive = true
    """)
    List<Task> getScheduledTasks(Pageable limit);

}
