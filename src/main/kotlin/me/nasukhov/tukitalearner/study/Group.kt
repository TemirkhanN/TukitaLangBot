package me.nasukhov.tukitalearner.study

import jakarta.persistence.EntityManager

data class Group(
    val id: String,
) {
    fun preferences(db: EntityManager): Preferences = Preferences(this, db)
}
