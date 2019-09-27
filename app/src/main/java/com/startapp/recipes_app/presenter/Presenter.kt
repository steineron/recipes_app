package com.startapp.recipes_app.presenter

import com.startapp.recipes_app.model.Model
import com.startapp.recipes_app.patterns.Observable
import com.startapp.recipes_app.patterns.Observer
import com.startapp.recipes_app.patterns.disregard
import com.startapp.recipes_app.patterns.observe
import com.startapp.recipes_app.pojo.Ingredient
import com.startapp.recipes_app.pojo.Ingredients
import com.startapp.recipes_app.pojo.Recipes
import com.startapp.recipes_app.view.RecipesView
import com.startapp.recipes_app.view.ViewState
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormatterBuilder
import java.util.*
import javax.inject.Inject

interface Presenter {
    // start/stop presentation - correlates with Android resume/pause
    fun start()

    fun stop()
}

/**
 * presentation logic - observe changes from the model, build the "correct" data for the view and set it to display it
 */
class RecipesPresenter @Inject constructor(
    private val model: Model,
    private val viewState: ViewState,
    private val view: RecipesView
) : Presenter {

    private val disposables = CompositeDisposable()

    // this is the stream that generates view-models on a worker thread, emits them (list) on the main thread
    private val viewModelsFactory = Single.fromCallable {
        createViewModels()
    }
        .subscribeOn(Schedulers.single())
        .observeOn(AndroidSchedulers.mainThread())

    // update the view after building everything off the main thread
    private fun updateViewAsync() {
        with(disposables) {
            clear()
            add(
                viewModelsFactory
                    .subscribe({
                        view.recipes = it
                    }, {
                        view.recipes = emptyList()
                    })
            )
        }
    }

    /**
     * filter only the "valid" ingredients
     */
    private val filterValidIngredients: (Ingredient) -> Boolean = { ingredient ->
        ingredient.title != null &&
                ingredient.useBy != null &&
                ingredient.bestBefore != null &&
                (ingredient.useBy!!.isAfter(ingredient.bestBefore) || ingredient.useBy == ingredient.bestBefore)

    }

    /**
     *  consider the model's values so far to create data for the view
     */
    private fun createViewModels(): List<RecipesView.ViewModel> {
        val ingredients = model.ingredients?.ingredients
        val recipes = model.recipes?.recipes
        if (ingredients.isNullOrEmpty() || recipes.isNullOrEmpty()) {
            return emptyList()// no valid data to deal with atm
        }

        ingredients
            .filter(filterValidIngredients)
            .also { validIngredients ->

                val timeToEat =
                    DateTime(viewState.selectedDate) // stabilise "now" by capturing it first

                // create 2 sets of ingredients (by name):
                // bestBeforeFuture - a set of "good" ingredients
                // bestBeforePast - a set of "about to expire"

                val bestBeforePast = validIngredients
                    .filter { its: Ingredient ->
                        (its.bestBefore == timeToEat || its.bestBefore!!.isBefore(timeToEat)) &&
                                its.useBy!!.isAfter(timeToEat) // still not expired but due it's best before....
                    }
                    .map { it.title }
                    .toSet()

                val bestBeforeFuture = validIngredients
                    .filter { its: Ingredient ->
                        its.bestBefore!!.isAfter(timeToEat) // implying the use-by is event further than that
                    }
                    .map { it.title }
                    .toSet()


                val viewModels = LinkedList<RecipesView.ViewModel>()
                recipes.forEach { recipe ->

                    recipe.ingredients?.let { itsIngredients ->
                        // count the number of items that are past their "best-before"
                        var pastBestBeforeIngredients = 0

                        // count the number of items that their "best-before" is in the future
                        var futureBestBeforeIngredients = 0

                        for (ingredientName in itsIngredients) {

                            // each ingredient can be:
                            // 1. before the best-before date
                            // 2. after the best-before date
                            // 3 expired or not in the list (fridge)

                            if (ingredientName in bestBeforeFuture) {
                                futureBestBeforeIngredients++

                            } else if (ingredientName in bestBeforePast) {
                                pastBestBeforeIngredients++

                            } else {
                                break // ingredient not found in any of the valid ingredients i.e. expired or doesn't exist
                            }
                        }

                        if (pastBestBeforeIngredients + futureBestBeforeIngredients == itsIngredients.size) {
                            // all ingredients are valid - decide where to position this recipe
                            val viewModel = RecipesView.ViewModel(
                                recipe.title!!,
                                itsIngredients.joinToString(", "),
                                recipe
                            )

                            if (pastBestBeforeIngredients > 0) {
                                viewModels.addLast(viewModel)
                            } else {
                                viewModels.addFirst(viewModel)
                            }
                        }
                    }

                }
                return viewModels
            }
    }


    /**
     * observer for changes in ingredients - updates the view async
     */
    private val ingredientsHelper = object : Observer<Ingredients?> {
        override fun observed(
            value: Ingredients?,
            oldValue: Ingredients?,
            observable: Observable<Ingredients?>
        ) {
            updateViewAsync()
        }
    }

    private val selectedDateHelper = object : Observer<DateTime> {
        var dateFormatter = DateTimeFormatterBuilder()
            .appendDayOfMonth(2)
            .appendLiteral(" - ")
            .appendMonthOfYearShortText()
            .appendLiteral(" - ")
            .appendYear(4, 4)
            .toFormatter()

        override fun observed(
            value: DateTime?,
            oldValue: DateTime?,
            observable: Observable<DateTime>
        ) {
            view.setDateText(dateFormatter.print(value))
            updateViewAsync()
        }
    }

    /**
     * observer for changes in recipes - updates the view async
     */
    private val recipesHelper = object : Observer<Recipes?> {
        override fun observed(
            value: Recipes?,
            oldValue: Recipes?,
            observable: Observable<Recipes?>
        ) {
            updateViewAsync()
        }
    }


    override fun start() {
        // start observing the model and the view-state, respond to data and value changes
        ingredientsHelper.observe(model.observables.ingredients)
        recipesHelper.observe(model.observables.recipes)
        selectedDateHelper.observe(viewState.selectedDateUpdates)
    }

    override fun stop() {
        // disregard the model and the view-state
        ingredientsHelper.disregard(model.observables.ingredients)
        recipesHelper.disregard(model.observables.recipes)
        selectedDateHelper.disregard(viewState.selectedDateUpdates)
    }

}