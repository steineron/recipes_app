package com.startapp.recipes_app

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.startapp.recipes_app.dagger.DataModule
import com.startapp.recipes_app.dagger.BindingsModule
import com.startapp.recipes_app.presenter.Presenter
import com.startapp.recipes_app.view.RecipesView
import com.startapp.recipes_app.view.RecipesViewImpl
import com.startapp.recipes_app.view.ViewState
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides
import org.joda.time.DateTime
import java.lang.ref.WeakReference
import javax.inject.Scope

class MainActivity : AppCompatActivity() {

    private lateinit var presenter: Presenter
    private lateinit var viewState: ViewState

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        // build the MVP constelation
        DaggerMainActivity_MainComponent.builder()
            .bindActivity(WeakReference(this))
            .bindContext(WeakReference(applicationContext))
            .bindView(findViewById(R.id.main_view))
            .build().also {
                presenter = it.presenter()
                viewState = it.viewState()
            }

        viewState.selectedDate = DateTime.now()
    }


    override fun onResume() {
        super.onResume()
        presenter.start()
    }

    override fun onPause() {
        presenter.stop()
        super.onPause()
    }

    @Module
    object ViewModule {
        @Provides
        @JvmStatic
        fun provideView(view: View, interactions: RecipesView.Interactions): RecipesView =
            RecipesViewImpl(view, interactions)

    }

    @Scope
    annotation class MainActivityScope

    @Component(modules = [DataModule::class, BindingsModule::class, ViewModule::class])
    @MainActivityScope
    interface MainComponent {

        fun presenter(): Presenter

        fun viewState(): ViewState

        @Component.Builder
        interface Builder {

            @BindsInstance
            fun bindContext(activity: WeakReference<Context>): Builder

            @BindsInstance
            fun bindActivity(activity: WeakReference<AppCompatActivity>): Builder

            @BindsInstance
            fun bindView(view: View): Builder

            fun build(): MainComponent
        }
    }
}
