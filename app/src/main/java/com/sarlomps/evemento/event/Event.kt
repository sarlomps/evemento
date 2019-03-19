package com.sarlomps.evemento.event

import com.sarlomps.evemento.event.transport.Location
import org.joda.time.DateTime

data class Event(var eventId:String,
                 var title: String,
                 val imageUrl: String,
                 var description: String,
                 var startDate: DateTime,
                 var endDate: DateTime,
                 var location: Location,
                 var user: String )
