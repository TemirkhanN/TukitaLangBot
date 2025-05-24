package me.nasukhov.tukitalearner.bot.io

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ChannelRepository : JpaRepository<Channel, String> {
    override fun findById(id: String): Optional<Channel>
}
