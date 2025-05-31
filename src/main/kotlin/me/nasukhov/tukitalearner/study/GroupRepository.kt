package me.nasukhov.tukitalearner.study

interface GroupRepository {
    fun findById(id: String): Group?

    fun save(group: Group): Group
}
