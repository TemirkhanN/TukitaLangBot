package me.nasukhov.TukitaLearner.dictionary

import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface DictionaryRepository : JpaRepository<Word, Long> {
    @Query("SELECT w FROM Word w WHERE w.id > ?1 ORDER BY w.id ASC")
    fun findWords(offsetId: Long, pageable: Pageable): List<Word>
}
