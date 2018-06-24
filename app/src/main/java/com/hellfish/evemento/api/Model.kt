package com.hellfish.evemento.api

import com.hellfish.evemento.event.Event
import org.joda.time.DateTime

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
            DateTime.parse(entity.startDate),
            DateTime.parse(entity.endDate),
            entity.location,
            entity.user,
            emptyList(),
            emptyList(),
            emptyList(),
            emptyList(),
            emptyList())

    fun mapToEntity(event: Event): EventResponse = EventResponse(event.title,
            event.description,
            event.startDate.toString(),
            event.endDate.toString(),
            event.location,
            event.user)

}
