package com.startapp.recipes_app.pojo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Recipe {

    @SerializedName("title")
    @Expose
    var title: String? = null

    @SerializedName("ingredients")
    @Expose
    var ingredients: List<String>? = null
}
