package me.nasukhov.TukitaLearner.dictionary;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DictionaryRepository extends JpaRepository<Word, Long> {
    @Query("SELECT w FROM Word w WHERE w.id > ?1 ORDER BY w.id ASC")
    List<Word> findWords(Long offsetId, Pageable pageable);
}
