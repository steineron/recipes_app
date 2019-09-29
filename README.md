# Recipes App

Display a list of recipes that can be cooked on a given day, considering the ingredients, the recipes and the selected date.
The displayed list considers and respects the best-before and used-by dates of the ingredients.

# Design Principles

Implementation is SOLID and testable.

Dependencies are injected (using DAGGER).

Architecture is MVP w/ Supervising Controller variant.

The Controller (Activity) is the only entity that instantiates/injects, and has 0 external dependencies.


# MVP Architecture

## The Model

The `Model` represents the stateful, autonomous data layer.
Generally speaking, the model's API consists of 3 categories of elements:
* Values - stateful properties representing the current up-to-date values this model exposes. Values can be read/write-only or both.
* Observables - the means for clients of this model to learn about changes to its values.
* Transactionals - transactions that can be executed by this model (e.g. "add", "remove"). Transaction may change the values and trigger observations.


## The View

The `View` is a wrapper around (encapsulates) a native Android view, and it exposes only a high-level limited set of API. In this example, setting the text of the date-button and the list of recipes.
This encapsulation prevents other runtime components from directly accessing the native view, which guards against undesired modifications and simplifies testings (by adhering to SOLID's "depend on abstractions" principle)

The `View` defines two additional abstractions: 
1. `ViewModel` - the view's model - what kind of data the view accepts.
2. `Interactions` - the view's interactions - what kind of user interactions are supported by this view.

## The Presenter

The `Presenter` is responsible for the business logic of the app: responding to changes in teh data and presenting it according to the application's business rules, i.e which recipes are displayed, in which order etc.

The `Presenter` is able to understand the model's data and the view's data and connect the two. It does so by observing the model and, in accord with the presentation logic, build the right data for the view.

## The View's State

As Some values are not part of the model (i.e. not data), and originate from the user's interactions with the view (or preferences, or session history etc.), they are managed by the `ViewState`.

The view state is designed to expose values (properties) and observables, but nothing more. The `Presenter` observes and responds to change in the view state's values.

## The Interactions

To maintain SOLID, clean architecture the implementation for user's interactions (whose interface is defined by the `View`) is in a separate class. `Interactions` would change values on `ViewState`, modify the `Model` (when applicable) and generally speaking would open dialogs, start activities/fragments etc.


# SOLID

SOLID principles are the lighthouse driving implementation in the app. All the classes are:
* Single Responsibility classes.
* Open/Close-d (since all classes in this app are closed, i.e. final).
* Liskov-Substitutable - despite the small class-hierarchy, this can be seen in the tests where classes are substituted by mocks.
* Interface Segregation - interfaces are scoped to the smallest units that match their responsibility
* Dependency Inversion - all implementations depend on abstractions (interfaces of other classes) and decouple for specific implementations (e.g. Observable pattern is implemented directly in the code and not borrowed from Java/ RxJava)


# Data Flow

Data is flowing from the `Model` which attempts loading it as soon as it is created. When successfully loaded the data is readable via the matching properties (val) on the model, and is also emitted via observables (`BehaviourSubject` in this implementations).

Another meaningful value is the selected date, which similarly to the data from the model, flows via an observable and exposed via a property (va).

The `Presenter` observes the `Model` and the `ViewState` responds to changes in their values. Since the observables are implemented by `BehaviorSubject`s the `Presenter` will receive the most up-to-date values even if emitted before it started observing.

Once the `Presenter` observes a change in either value - it will attempt to create the data for the view (view-models) and set it on the `View`. The `View `in turn will display it.

# Interactions Flow

User interactions - selecting a date and clicking on a recipe from the list - are captured by the `View` and brokered to the implementation of `Interactions`. 

When the user clicks on the date button - interactions open a date-picker dialog and apply the selected date to the `ViewState` (which in turn results in observation by the `Presenter`)

When user clicks on a recipes - the implementation of `Interactions` simply toasts a text on the screen.
