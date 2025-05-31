package me.nasukhov.tukitalearner.event

import me.nasukhov.bot.event.JoinedChannel
import me.nasukhov.bot.event.LeftChannel
import me.nasukhov.bot.event.ReceivedInput
import me.nasukhov.tukitalearner.study.Group
import me.nasukhov.tukitalearner.study.GroupRepository
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class MaintainGroup(
    private val groupRepository: GroupRepository,
) {
    @EventListener
    fun onJoiningChannel(event: JoinedChannel) {
        val channel = event.channel

        val existingGroup = groupRepository.findById(channel.id)
        if (existingGroup != null) {
            if (!existingGroup.isActive) {
                existingGroup.activate()
            }

            return
        }

        groupRepository.save(Group(channel.id))
    }

    @EventListener
    fun onLeavingChannel(event: LeftChannel) {
        val group = groupRepository.findById(event.channel.id)
        if (group == null) {
            return
        }

        group.deactivate()
    }

    @EventListener
    fun onAnyInput(event: ReceivedInput) {
        val channelId = event.input.channel.id

        if (groupRepository.findById(channelId) == null) {
            groupRepository.save(Group(channelId))
        }
    }
}
