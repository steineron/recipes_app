package com.startapp.recipes_app.pojo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Ingredients {

    @SerializedName("ingredients")
    @Expose
    var ingredients: List<Ingredient>? = null
}
