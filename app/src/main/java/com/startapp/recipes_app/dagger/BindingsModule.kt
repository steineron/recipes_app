package com.startapp.recipes_app.dagger

import com.startapp.recipes_app.model.Model
import com.startapp.recipes_app.model.ModelImpl
import com.startapp.recipes_app.presenter.Presenter
import com.startapp.recipes_app.presenter.RecipesPresenter
import com.startapp.recipes_app.view.InteractionsImpl
import com.startapp.recipes_app.view.RecipesView
import com.startapp.recipes_app.view.ViewState
import com.startapp.recipes_app.view.ViewStateImpl
import dagger.Binds
import dagger.Module

@Module
abstract class BindingsModule {

    @Binds
    abstract fun presenter(presenter: RecipesPresenter): Presenter

    @Binds
    abstract fun viewState(vs: ViewStateImpl): ViewState

    @Binds
    abstract fun model(model: ModelImpl): Model

    @Binds
    abstract fun interactions(interactions: InteractionsImpl): RecipesView.Interactions
}