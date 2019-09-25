package com.startapp.recipes_app.view

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.startapp.recipes_app.pojo.Recipe
import kotlinx.android.synthetic.main.activity_main.view.*
import javax.inject.Inject


/**
 * a wrapper around the view, to be used by the presenter
 */
interface RecipesView {

    // each item in the recipes list is:
    data class ViewModel(val name: String, val ingredients: String, val expiration: String)

    // the current list of recipes
    var recipes: List<ViewModel>

    // the only interaction with the recipes list is the click
    interface Interactions {
        fun onRecipeClicked(recipe: Recipe)
    }

}


// the implementation wraps a recycler view (list) and attaches an adapter to it to render the recipes

class RecipesViewImpl @Inject constructor(
    private val view: View,
    interactions: RecipesView.Interactions
) : RecipesView {

    private var recipesListAdapter = RecipesListAdapter(interactions)

    init {
        with(view.recipes_list){
            layoutManager = LinearLayoutManager(view.context)
            adapter = recipesListAdapter
        }

    }
    override var recipes: List<RecipesView.ViewModel> = emptyList()
        set(value) {
            // just assigns the value to he adapter - it will take care of the rest
            recipesListAdapter.submitList(value)
        }

}