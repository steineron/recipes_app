package com.startapp.recipes_app.view

import com.startapp.recipes_app.patterns.BehaviourSubject
import org.joda.time.DateTime
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.properties.Delegates

interface ViewState {
    var selectedDate: DateTime
    val selectedDateUpdates: BehaviourSubject<DateTime>
}
@Singleton
class ViewStateImpl @Inject constructor() : ViewState {

    override var selectedDate:DateTime by Delegates.observable(DateTime.now()) { _, _:DateTime, newDate:DateTime ->
        selectedDateUpdates.emit(newDate)
    }

    override val selectedDateUpdates:BehaviourSubject<DateTime> = BehaviourSubject()
}