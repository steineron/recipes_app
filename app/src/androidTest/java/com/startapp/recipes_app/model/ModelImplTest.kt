package com.startapp.recipes_app.model

import android.content.Context
import androidx.test.runner.AndroidJUnit4
import com.startapp.recipes_app.Observations
import com.startapp.recipes_app.dagger.DataModule
import com.startapp.recipes_app.pojo.Ingredients
import com.startapp.recipes_app.pojo.Recipes
import dagger.BindsInstance
import dagger.Component
import org.junit.Before

import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import java.lang.ref.WeakReference

@RunWith(AndroidJUnit4::class)
class ModelImplTest {
    lateinit var context: Context

    lateinit var model: Model
    @Before
    fun setUp() {
        context = androidx.test.core.app.ApplicationProvider.getApplicationContext()

        model = DaggerModelImplTest_TestComponent.builder().context(WeakReference(context)).build()
            .model()
    }


    @Test
    fun that_model_initialises_with_recipes_and_ingredients() {
        with(model) {
            assertNotNull(recipes)
            assertNotNull(ingredients)
        }
    }


    @Test
    fun that_once_the_model_initialises_with_recipes_they_are_observed() {
        Observations<Recipes?>().let {

            // before observing
            assertTrue(it.observations.isEmpty())

            with(model.observables) {

                // observe it and verify that it observed the model's recipes
                recipes.add(it)
                assertTrue(it.observations.size==1)
                assertTrue(it.observations.first()!! === model.recipes)
            }
        }
    }

    @Test
    fun that_once_the_model_initialises_with_ingredients_they_are_observed() {
        Observations<Ingredients?>().let {

            // before observing
            assertTrue(it.observations.isEmpty())

            with(model.observables) {

                // observe it and verify that it observed the model's ingredients
                ingredients.add(it)
                assertTrue(it.observations.size==1)
                assertTrue(it.observations.first()!! === model.ingredients)
            }
        }
    }


    @Component(modules = [DataModule::class])
    interface TestComponent {

        fun model(): ModelImpl

        @Component.Builder
        interface Builder {

            @BindsInstance
            fun context(context: WeakReference<Context>): Builder

            fun build(): TestComponent

        }
    }
}