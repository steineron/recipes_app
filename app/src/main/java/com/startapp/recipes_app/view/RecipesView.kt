package com.startapp.recipes_app.view

import android.app.DatePickerDialog
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.startapp.recipes_app.pojo.Recipe
import kotlinx.android.synthetic.main.activity_main.view.*
import org.joda.time.DateTime
import java.lang.ref.WeakReference
import javax.inject.Inject


/**
 * a wrapper around the view, to be used by the presenter
 */
interface RecipesView {

    // set the text of the selected date that matches the list or recipes
    fun setDateText(date: String?)

    // each item in the recipes list is:
    data class ViewModel(val name: String, val ingredients: String, val recipe: Recipe)

    // the current list of recipes
    var recipes: List<ViewModel>

    // the interaction with the recipes list is the click, and the date-picker
    interface Interactions {

        fun onSelectDate()

        fun onRecipeClicked(recipe: Recipe)
    }

}


/**
 * the implementation wraps a recycler view (list) and attaches an adapter to it to render the recipes
 */

class RecipesViewImpl @Inject constructor(
    private val view: View,
    private val interactions: RecipesView.Interactions
) : RecipesView {

    override fun setDateText(date: String?) {
        view.date_picker.text = date
    }

    private var recipesListAdapter = RecipesListAdapter(interactions)

    init {
        with(view.recipes_list) {
            layoutManager = LinearLayoutManager(view.context)
            adapter = recipesListAdapter
        }

        view.date_picker.setOnClickListener {
            interactions.onSelectDate()
        }

    }

    override var recipes: List<RecipesView.ViewModel> = emptyList()
        set(value) {
            // just assigns the value to he adapter - it will take care of the rest
            recipesListAdapter.submitList(value)
        }
}


/**
 * handle the various interactions with the view
 */
class InteractionsImpl @Inject constructor(
    private val activity: WeakReference<AppCompatActivity>,
    private val viewState: ViewState
) : RecipesView.Interactions {
    override fun onSelectDate() {
        activity.get()?.let {

            val currDate = DateTime(viewState.selectedDate)

            DatePickerDialog(
                it,
                DatePickerDialog.OnDateSetListener { _, y, m, d ->

                    viewState.selectedDate = DateTime(y, m, d, 0, 0, 0)

                },
                currDate.year,
                currDate.monthOfYear,
                currDate.dayOfMonth
            ).also { dialog ->

                dialog.show()

            }
        }
    }

    override fun onRecipeClicked(recipe: Recipe) {
        activity.get()?.let {
            Toast.makeText(it, "hmm... ${recipe.title}.... YUMMY!", Toast.LENGTH_LONG).show()
        }
    }

}