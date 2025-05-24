package me.nasukhov.TukitaLearner.study

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface LearnedResourceRepository : JpaRepository<LearnedResource, Long> {
    @Query(
        value = """
    SELECT * FROM learned_resources
    WHERE group_id = :groupId
      AND resource_type = 'word'
    ORDER BY id DESC
    LIMIT 1
    
    """, nativeQuery = true
    )
    fun findLastLearnedWordId(groupId: String): Optional<LearnedResource>
}
