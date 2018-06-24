package com.hellfish.evemento.api

import com.hellfish.evemento.event.Event

data class EventResponse(val title: String,
                         val description: String,
                         val startDate: String,
                         val endDate: String,
                         val location: String,
                         val user: String)

data class PushEventResponse(val name:String)

class EventMapper {

    fun mapToDomain(eventId: String, entity: EventResponse): Event = Event(eventId,
            entity.title,
            entity.description,
            entity.startDate,
            entity.endDate,
            entity.location,
            entity.user,
            emptyList(),
            emptyList(),
            emptyList(),
            emptyList(),
            emptyList())

    fun mapToEntity(event: Event): EventResponse = EventResponse(event.title,
            event.description,
            event.startDate,
            event.endDate,
            event.location,
            event.user)

}
