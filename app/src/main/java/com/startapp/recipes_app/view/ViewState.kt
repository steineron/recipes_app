package com.startapp.recipes_app.view

import com.startapp.recipes_app.patterns.BehaviourSubject
import org.joda.time.DateTime
import javax.inject.Inject
import kotlin.properties.Delegates

class ViewState @Inject constructor() {

    var selectedDate:DateTime by Delegates.observable(DateTime.now()) {_, _:DateTime, newDate:DateTime ->
        selectedDateUpdates.emit(newDate)
    }

    val selectedDateUpdates:BehaviourSubject<DateTime> = BehaviourSubject()
}