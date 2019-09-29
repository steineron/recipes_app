package com.startapp.recipes_app.presenter

import com.startapp.recipes_app.common.Utils
import com.startapp.recipes_app.dagger.DataModule
import com.startapp.recipes_app.model.Model
import com.startapp.recipes_app.patterns.BehaviourSubject
import com.startapp.recipes_app.pojo.DateTimeJsonAdapter
import com.startapp.recipes_app.pojo.Ingredients
import com.startapp.recipes_app.pojo.Recipes
import com.startapp.recipes_app.utils.TestUtils
import com.startapp.recipes_app.view.RecipesView
import com.startapp.recipes_app.view.ViewState
import io.mockk.*
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.SpyK
import junit.framework.Assert.assertEquals
import org.joda.time.DateTime
import org.junit.After
import org.junit.Before
import org.junit.Test

class RecipesPresenterTest {

    class Mocks {

        @MockK
        lateinit var viewState: ViewState

        @MockK
        lateinit var model: Model

        @MockK
        lateinit var observables: Model.Observables

        @MockK
        lateinit var view: RecipesView

        @SpyK
        var date: BehaviourSubject<DateTime> = BehaviourSubject()

        @SpyK
        var recipes: BehaviourSubject<Recipes?> = BehaviourSubject()

        @SpyK
        var ingredients: BehaviourSubject<Ingredients?> = BehaviourSubject()

        init {
            MockKAnnotations.init(this)

            every { model.observables } returns observables
            every { observables.recipes } returns recipes
            every { observables.ingredients } returns ingredients

            every { view.setDateText(any()) } just runs
            every { view setProperty "recipes" value any<List<RecipesView.ViewModel>>() } just runs

            every { viewState.selectedDateUpdates } returns date
        }
    }


    lateinit var mocked: Mocks

    lateinit var presenter: Presenter

    lateinit var ingredients: Ingredients

    lateinit var recipes: Recipes

    @Before
    fun setUp() {

        // mocks
        mocked = Mocks()

        TestUtils.convertAllSchedulersToTest()

        // SUT
        presenter = RecipesPresenter(mocked.model, mocked.viewState, mocked.view)


        // data:
        val gson = DataModule.provideGson(
            DateTimeJsonAdapter()
        )
        ingredients = Utils.readObject("raw/ingredients.json", Ingredients::class.java, gson)!!
        recipes = Utils.readObject("raw/recipes.json", Recipes::class.java, gson)!!

    }

    @After
    fun tearDown() {
        TestUtils.resetSchedulers()
    }

    @Test
    fun that_presetner_observes_the_model_and_the_view_state() {
        // setup

        // assert that the model isn't observing those prior to being started
        verify(exactly = 0) { mocked.recipes.add(any()) }
        verify(exactly = 0) { mocked.ingredients.add(any()) }
        verify(exactly = 0) { mocked.date.add(any()) }

        verify(exactly = 0) { mocked.recipes.remove(any()) }
        verify(exactly = 0) { mocked.ingredients.remove(any()) }
        verify(exactly = 0) { mocked.date.remove(any()) }

        // execute
        presenter.start()
        TestUtils.testScheduler.triggerActions()

        // verify this results in teh presenter observing the model and the view state
        verify(exactly = 1) { mocked.recipes.add(any()) }
        verify(exactly = 1) { mocked.ingredients.add(any()) }
        verify(exactly = 1) { mocked.date.add(any()) }

        // but still not removed...
        verify(exactly = 0) { mocked.recipes.remove(any()) }
        verify(exactly = 0) { mocked.ingredients.remove(any()) }
        verify(exactly = 0) { mocked.date.remove(any()) }


        // now stop
        presenter.stop()

        // verify this results in the presenter disregarding the model and the view state
        verify(exactly = 1) { mocked.recipes.add(any()) }
        verify(exactly = 1) { mocked.ingredients.add(any()) }
        verify(exactly = 1) { mocked.date.add(any()) }

        // and now removed...
        verify(exactly = 1) { mocked.recipes.remove(any()) }
        verify(exactly = 1) { mocked.ingredients.remove(any()) }
        verify(exactly = 1) { mocked.date.remove(any()) }

    }

    @Test
    fun that_presenter_does_not_set_values_on_the_view_if_it_only_has_recipes() {

        presenter.start()

        TestUtils.testScheduler.triggerActions()

        verify(exactly = 0) { mocked.view setProperty "recipes" value any<List<RecipesView.ViewModel>>() }

        every { mocked.model.recipes } returns recipes
        mocked.recipes.emit(recipes)

        TestUtils.testScheduler.triggerActions()

        verify(exactly = 1) { mocked.view setProperty "recipes" value emptyList<RecipesView.ViewModel>() }

    }


    @Test
    fun that_presenter_does_not_set_values_on_the_view_if_it_only_has_ingredients() {

        presenter.start()

        TestUtils.testScheduler.triggerActions()
        verify(exactly = 0) { mocked.view setProperty "recipes" value any<List<RecipesView.ViewModel>>() }

        every { mocked.model.ingredients } returns ingredients
        mocked.ingredients.emit(ingredients)

        TestUtils.testScheduler.triggerActions()
        verify(exactly = 1) { mocked.view setProperty "recipes" value emptyList<RecipesView.ViewModel>() }


    }

    @Test
    fun that_presenter_does_not_set_values_on_the_view_if_it_only_has_date() {

        presenter.start()

        TestUtils.testScheduler.triggerActions()
        verify(exactly = 0) { mocked.view setProperty "recipes" value any<List<RecipesView.ViewModel>>() }

        val sept12 = DateTime(2019, 9, 12, 0, 0, 0)

        every { mocked.viewState.selectedDate } returns sept12
        mocked.date.emit(sept12)

        TestUtils.testScheduler.triggerActions()
        verify(exactly = 1) { mocked.view setProperty "recipes" value emptyList<RecipesView.ViewModel>() }
        verify(exactly = 1) { mocked.view.setDateText("12 - Sep - 2019") }


    }

    @Test
    fun that_presenter_does_not_set_values_on_the_view_if_it_only_has_ingredients_and_recipes_but_no_date() {

        presenter.start()

        TestUtils.testScheduler.triggerActions()
        verify(exactly = 0) { mocked.view setProperty "recipes" value any<List<RecipesView.ViewModel>>() }

        every { mocked.model.recipes } returns recipes
        every { mocked.model.ingredients } returns ingredients

        mocked.ingredients.emit(ingredients)
        mocked.recipes.emit(recipes)

        TestUtils.testScheduler.triggerActions()
        verify(exactly = 1) { mocked.view setProperty "recipes" value emptyList<RecipesView.ViewModel>() }

    }

    @Test
    fun that_presenter_sets_values_on_the_view_if_it_has_valid_recipes_ingredients_and_date() {


        // setup to capture the recipes view models
        val viewModels = mutableListOf<List<RecipesView.ViewModel>>()
        every { mocked.view setProperty "recipes" value capture(viewModels) } just runs

        presenter.start()

        TestUtils.testScheduler.triggerActions()
        verify(exactly = 0) { mocked.view setProperty "recipes" value any<List<RecipesView.ViewModel>>() }

        every { mocked.model.ingredients } returns ingredients
        every { mocked.model.recipes } returns recipes

        mocked.ingredients.emit(ingredients)
        mocked.recipes.emit(recipes)

        val sept12 = DateTime(2019, 9, 12, 0, 0, 0)
        every { mocked.viewState.selectedDate } returns sept12
        mocked.date.emit(sept12)

        TestUtils.testScheduler.triggerActions()
        verify(exactly = 1) { mocked.view.setDateText("12 - Sep - 2019") }
        verify(exactly = 1) { mocked.view setProperty "recipes" value capture(viewModels) }

        assertEquals(viewModels.first().size, 2)

    }

    @Test
    fun that_presenter_updatess_values_on_the_view_when_date_changes() {


        val sept05 = DateTime(2019, 9, 5, 0, 0, 0)
        val sept12 = DateTime(2019, 9, 12, 0, 0, 0)
        val sept30 = DateTime(2019, 9, 30, 0, 0, 0)

        // setup to capture the recipes view models
        val viewModels = slot<List<RecipesView.ViewModel>>()
        every { mocked.view setProperty "recipes" value capture(viewModels) } just runs

        presenter.start()

        TestUtils.testScheduler.triggerActions()
        verify(exactly = 0) { mocked.view setProperty "recipes" value any<List<RecipesView.ViewModel>>() }

        every { mocked.model.ingredients } returns ingredients
        every { mocked.model.recipes } returns recipes

        mocked.ingredients.emit(ingredients)
        mocked.recipes.emit(recipes)

        every { mocked.viewState.selectedDate } returns sept12
        mocked.date.emit(sept12)

        TestUtils.testScheduler.triggerActions()
        verify(exactly = 1) { mocked.view.setDateText("12 - Sep - 2019") }
        // first call
        verify(exactly = 1) { mocked.view setProperty "recipes" value capture(viewModels) }

        assertEquals(viewModels.captured.size, 2)

        // change teh date 5.9.2019
        every { mocked.viewState.selectedDate } returns sept05
        mocked.date.emit(sept05)

        TestUtils.testScheduler.triggerActions()

        verify(exactly = 1) { mocked.view.setDateText("05 - Sep - 2019") }
        // second call
        verify(exactly = 2) { mocked.view setProperty "recipes" value capture(viewModels) }

        assertEquals(viewModels.captured.size, 3) // one more recipe is valid now

        // change teh date 30.9.2019
        every { mocked.viewState.selectedDate } returns sept30
        mocked.date.emit(sept30)

        TestUtils.testScheduler.triggerActions()

        verify(exactly = 1) { mocked.view.setDateText("30 - Sep - 2019") }
        // 3rd call
        verify(exactly = 3) { mocked.view setProperty "recipes" value capture(viewModels) }

        assertEquals(viewModels.captured.size, 0) // all ingredients expired

    }
}