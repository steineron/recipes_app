package com.startapp.recipes_app.pojo

import com.google.gson.Gson
import com.startapp.recipes_app.common.Utils
import com.startapp.recipes_app.dagger.DataModule
import dagger.Component
import org.joda.time.DateTime
import org.junit.Before

import org.junit.Assert.*
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.InputStreamReader

/**
 * test the deserialisation of ingredients and recipes from JSON
 */
class SerialisationTest {


    @Component(modules = [DataModule::class])
    interface TestComponent {
        fun gson(): Gson
    }

    lateinit var gson: Gson

    @Before
    fun setUp() {
        gson = DaggerSerialisationTest_TestComponent.builder().build().gson()
    }


    // ingredients
    @Test
    fun that_ingredients_deserialisation_reads_the_correct_values_from_JSON() {

        val ingredients = Utils.readObject("raw/ingredients.json", Ingredients::class.java, gson)!!

        assertEquals(ingredients.ingredients!!.size, 16)

    }

    @Test
    fun that_ingredient_is_deserialised_correctly() {
        // setup
        val json = "{\n" +
                "      \"title\": \"Ham\",\n" +
                "      \"best-before\": \"2019-09-25\",\n" +
                "      \"use-by\": \"2019-09-27\"\n" +
                "    }"

        // execute
        val ingredient =
            Utils.readObjectFromStream(
                InputStreamReader(json.byteInputStream()),
                Ingredient::class.java,
                gson
            )!!

        // verify
        with(ingredient) {
            assertEquals(title, "Ham")
            assertEquals(bestBefore, DateTime(2019, 9, 25, 0, 0, 0))
            assertEquals(useBy, DateTime(2019, 9, 27, 0, 0, 0))
        }
    }

    @Test
    fun that_ingredient_is_deserialised_correctly_despite_missing_title() {
        // setup
        var json = "{\n" +
                "      \"best-before\": \"2019-09-25\",\n" +
                "      \"use-by\": \"2019-09-27\"\n" +
                "    }"

        // execute
        val ingredient =
            Utils.readObjectFromStream(
                InputStreamReader(json.byteInputStream()),
                Ingredient::class.java,
                gson
            )!!

        // verify
        with(ingredient) {
            assertNull(title)
            assertEquals(bestBefore, DateTime(2019, 9, 25, 0, 0, 0))
            assertEquals(useBy, DateTime(2019, 9, 27, 0, 0, 0))
        }
    }


    @Test
    fun that_ingredient_is_deserialised_correctly_despite_missing_best_before_date() {
        // setup
        val json = "{\n" +
                "      \"title\": \"Ham\",\n" +
                "      \"use-by\": \"2019-09-27\"\n" +
                "    }"

        // execute
        val ingredient =
            Utils.readObjectFromStream(
                InputStreamReader(json.byteInputStream()),
                Ingredient::class.java,
                gson
            )!!

        // verify
        with(ingredient) {
            assertEquals(title, "Ham")
            assertNull(bestBefore)
            assertEquals(useBy, DateTime(2019, 9, 27, 0, 0, 0))
        }
    }


    @Test
    fun that_ingredient_is_deserialised_correctly_despite_missing_used_b_date() {
        // setup
        val json = "{\n" +
                "      \"title\": \"Ham\",\n" +
                "      \"best-before\": \"2019-09-25\"\n" +
                "    }"

        // execute
        val ingredient =
            Utils.readObjectFromStream(
                InputStreamReader(json.byteInputStream()),
                Ingredient::class.java,
                gson
            )!!

        // verify
        with(ingredient) {
            assertEquals(title, "Ham")
            assertEquals(bestBefore, DateTime(2019, 9, 25, 0, 0, 0))
            assertNull(useBy)
        }
    }

    // recipes
    @Test
    fun that_recipes_deserialisation_reads_the_correct_values_from_JSON() {

        val recipes = Utils.readObject("raw/recipes.json", Recipes::class.java, gson)!!

        assertEquals(recipes.recipes!!.size, 4)

    }

    @Test
    fun that_recipe_is_deserialised_correctly() {
        // setup
        val json = "{\n" +
                "      \"title\": \"Ham and Cheese Toastie\",\n" +
                "      \"ingredients\": [\n" +
                "        \"Ham\",\n" +
                "        \"Cheese\",\n" +
                "        \"Bread\",\n" +
                "        \"Butter\"\n" +
                "      ]\n" +
                "    }"

        // execute
        val recipe =
            Utils.readObjectFromStream(
                InputStreamReader(json.byteInputStream()),
                Recipe::class.java,
                gson
            )!!

        // verify
        with(recipe) {
            assertEquals(title, "Ham and Cheese Toastie")
            assertEquals(ingredients, listOf("Ham", "Cheese","Bread","Butter") )
        }
    }

    @Test
    fun that_recipe_is_deserialised_correctly_despite_missing_title() {
        // setup
        var json = "{\n" +
                "      \"ingredients\": [\n" +
                "        \"Ham\",\n" +
                "        \"Cheese\",\n" +
                "        \"Bread\",\n" +
                "        \"Butter\"\n" +
                "      ]\n" +
                "    }"

        // execute
        val recipe =
            Utils.readObjectFromStream(
                InputStreamReader(json.byteInputStream()),
                Recipe::class.java,
                gson
            )!!

        // verify
        with(recipe) {
            assertNull(title)
            assertEquals(ingredients, listOf("Ham", "Cheese","Bread","Butter") )
        }
    }


    @Test
    fun that_recipe_is_deserialised_correctly_despite_empty_ingredients() {
        // setup
        var json = "{\n" +
                "      \"title\": \"Ham and Cheese Toastie\",\n" +
                "      \"ingredients\": []\n" +
                "    }"

        // execute
        val recipe =
            Utils.readObjectFromStream(
                InputStreamReader(json.byteInputStream()),
                Recipe::class.java,
                gson
            )!!

        // verify
        with(recipe) {
            assertEquals(title, "Ham and Cheese Toastie")
            assertTrue(ingredients!!.isEmpty())
        }
    }
    @Test
    fun that_recipe_is_deserialised_correctly_despite_missing_ingredients() {
        // setup
        var json = "{\n" +
                "      \"title\": \"Ham and Cheese Toastie\"\n" +
                "    }"

        // execute
        val recipe =
            Utils.readObjectFromStream(
                InputStreamReader(json.byteInputStream()),
                Recipe::class.java,
                gson
            )!!

        // verify
        with(recipe) {
            assertEquals(title, "Ham and Cheese Toastie")
            assertNull(ingredients)
        }
    }


}