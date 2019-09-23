package com.startapp.recipes_app.pojo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Recipes {

    @SerializedName("recipes")
    @Expose
    var recipes: List<Recipe>? = null
}
