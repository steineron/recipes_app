package com.startapp.recipes_app.pojo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Ingredient {

    @SerializedName("title")
    @Expose
    var title: String? = null

    @SerializedName("best-before")
    @Expose
    var bestBefore: String? = null

    @SerializedName("use-by")
    @Expose
    var useBy: String? = null
}
