package com.startapp.recipes_app.model

import android.content.Context
import com.google.gson.Gson
import com.startapp.recipes_app.R
import com.startapp.recipes_app.common.Utils
import com.startapp.recipes_app.patterns.BehaviourSubject
import com.startapp.recipes_app.patterns.Observable
import com.startapp.recipes_app.pojo.Ingredients
import com.startapp.recipes_app.pojo.Recipes
import java.lang.ref.WeakReference
import javax.inject.Inject
import kotlin.properties.Delegates

/**
 * the model represents the stateful data layer of the app - from loading hte data and holding
 * the most updates values, to communicating changes via observables
 */
interface Model {

    // the stateful values of hte model - the current values for recipes and ingredients
    val recipes: Recipes?
    val ingredients: Ingredients?


    // the observable parts of the model
    val observables:Observables


    // model's observables defined:
    interface Observables {
        val recipes: Observable<Recipes?>
        val ingredients: Observable<Ingredients?>
    }

}

class ObservablesImpl :Model.Observables{
    override val recipes: BehaviourSubject<Recipes?> = BehaviourSubject()
    override val ingredients: BehaviourSubject<Ingredients?> = BehaviourSubject()
}

class ModelImpl @Inject constructor(private val context: WeakReference<Context>, private  val gson: Gson):Model{



    override val observables = ObservablesImpl()

    override var recipes: Recipes? by Delegates.observable(null){ _, _:Recipes?, new:Recipes? ->
        observables.recipes.emit(new)
    }

    override var ingredients: Ingredients? by Delegates.observable(null){ _, _:Ingredients?, new:Ingredients? ->
        observables.ingredients.emit(new)
    }

    // now that the implementation is defined - initialise it
    init {
        // load the 2 jsons that serve as data for this task

        context.get()?.resources?.let {
            ingredients = Utils.readObject(it.openRawResource(R.raw.ingredients),Ingredients::class.java ,gson)
            recipes = Utils.readObject(it.openRawResource(R.raw.recipes),Recipes::class.java ,gson)
        }
    }

}