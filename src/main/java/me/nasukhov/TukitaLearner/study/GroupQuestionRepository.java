package me.nasukhov.TukitaLearner.study;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface GroupQuestionRepository extends JpaRepository<GroupQuestion, UUID> {
    @Query("SELECT gq FROM GroupQuestion gq LEFT JOIN FETCH gq.answers WHERE gq.id = :id")
    Optional<GroupQuestion> findByIdWithAnswers(@Param("id") UUID id);
}
