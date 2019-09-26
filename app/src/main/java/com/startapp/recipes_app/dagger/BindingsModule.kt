package com.startapp.recipes_app.dagger

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.startapp.recipes_app.model.Model
import com.startapp.recipes_app.model.ModelImpl
import com.startapp.recipes_app.presenter.Presenter
import com.startapp.recipes_app.presenter.RecipesPresenter
import com.startapp.recipes_app.view.InteractionsImpl
import com.startapp.recipes_app.view.RecipesView
import com.startapp.recipes_app.view.ViewState
import dagger.Binds
import dagger.Module
import java.lang.ref.WeakReference

@Module
abstract class BindingsModule {

    @Binds
    abstract fun presenter(presenter: RecipesPresenter): Presenter

    @Binds
    abstract fun model(model: ModelImpl): Model

    @Binds
    abstract fun interactions(interactions: InteractionsImpl): RecipesView.Interactions
}