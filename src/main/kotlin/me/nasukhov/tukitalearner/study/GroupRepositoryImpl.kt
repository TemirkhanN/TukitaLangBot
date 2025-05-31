package me.nasukhov.tukitalearner.study

import jakarta.persistence.EntityManager
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class GroupRepositoryImpl(
    private val em: EntityManager,
) : GroupRepository {
    @Cacheable("studyGroup", key = "#id")
    override fun findById(id: String): Group? = em.find(Group::class.java, id)

    @CachePut("studyGroup", key = "#group.id")
    @Transactional
    override fun save(group: Group): Group =
        if (em.contains(group)) {
            em.merge(group)
        } else {
            em.persist(group)
            group
        }
}
