package me.nasukhov.TukitaLearner.study

import jakarta.persistence.EntityManager

data class Group(val id: String) {
    fun preferences(db: EntityManager): Preferences {
        return Preferences(this, db)
    }
}
