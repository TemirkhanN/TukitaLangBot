package me.nasukhov.tukitalearner.dictionary

import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface DictionaryRepository : JpaRepository<DictionaryEntry, Long> {
    @Query("SELECT w FROM DictionaryEntry w WHERE w.id > ?1 ORDER BY w.id ASC")
    fun findWords(
        offsetId: Long,
        pageable: Pageable,
    ): List<DictionaryEntry>
}
