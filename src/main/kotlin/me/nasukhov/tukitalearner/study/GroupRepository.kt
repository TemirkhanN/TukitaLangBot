package me.nasukhov.tukitalearner.study

import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface GroupRepository : JpaRepository<Group, String> {
    @Cacheable("studyGroup", key = "#id")
    override fun findById(id: String): Optional<Group>

    @CachePut("studyGroup", key = "#entity.id")
    override fun <S : Group?> save(entity: S & Any): S & Any
}
