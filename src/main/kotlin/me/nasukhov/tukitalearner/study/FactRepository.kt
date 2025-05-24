package me.nasukhov.tukitalearner.study

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface FactRepository : JpaRepository<Fact, Long> {
    @Query(
        """
        SELECT f FROM Fact f
        WHERE NOT EXISTS (
            SELECT 1 FROM LearnedResource lr
            WHERE lr.groupId = :groupId AND lr.resourceId = f.id AND lr.resourceType = 'FACT'
        )
        ORDER BY function('RANDOM')
        LIMIT 1
        
        """,
    )
    fun findUnlearnedFact(
        @Param("groupId") groupId: String,
    ): Optional<Fact>
}
