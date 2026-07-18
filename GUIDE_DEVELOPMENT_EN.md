# Jetpack Compose: Technical Reference Guide

Reference document covering concepts, patterns, and components for building Android UI with **Jetpack Compose** and **Material 3**. It is intended as consultation material to keep at hand during day-to-day development.

> **Core principle:** in Jetpack Compose, the user interface is not modified directly; it is **described** as a function of state. When the state changes, Compose re-executes the functions that depend on it and updates the screen accordingly. Internalizing this principle resolves most cases of unexpected UI behavior.

---

## Table of Contents

1. [Introduction: What Is a Composable?](#1-introduction-what-is-a-composable)
2. [Design System: Material 3 and Theming](#2-design-system-material-3-and-theming)
3. [The Modifier](#3-the-modifier)
4. [Basic Layouts: Column, Row, and Box](#4-basic-layouts-column-row-and-box)
5. [Other Layout Types: ConstraintLayout, FlowRow, and BoxWithConstraints](#5-other-layout-types-constraintlayout-flowrow-and-boxwithconstraints)
6. [Efficient Lists and Grids (Lazy Lists)](#6-efficient-lists-and-grids-lazy-lists)
7. [State and Recomposition](#7-state-and-recomposition)
8. [mutableStateOf and Its Variants](#8-mutablestateof-and-its-variants)
9. [State Hoisting](#9-state-hoisting)
10. [Events and Interaction](#10-events-and-interaction)
11. [Side Effects](#11-side-effects)
12. [StateFlow and SharedFlow](#12-stateflow-and-sharedflow)
13. [Loading Images from the Network (Coil)](#13-loading-images-from-the-network-coil)
14. [Common Material 3 Components](#14-common-material-3-components)
15. [Navigation Between Screens](#15-navigation-between-screens)
16. [Architecture: ViewModel and UI State](#16-architecture-viewmodel-and-ui-state)
17. [Previews](#17-previews)
18. [Best Practices](#18-best-practices)
19. [Common Trainee Mistakes](#19-common-trainee-mistakes)
20. [Glossary of Terms](#20-glossary-of-terms)

---

## 1. Introduction: What Is a Composable?

A **Composable** is a Kotlin function annotated with `@Composable` that describes a piece of user interface. Unlike a regular function, a composable does not return a view: when executed, it **emits** the UI it describes into the composition tree.

```kotlin
@Composable
fun Greeting(name: String) {
    Text(text = "Hello, $name")
}
```

Conventions and rules:

- Composables are **nested**: a `Column` may contain several `Text` elements, which in turn may live inside a `Card`, and so on.
- Function names use **PascalCase** (`UserProfile`, not `userProfile`), since they conceptually represent a UI element rather than an action.
- Composables must be **idempotent**: given the same set of parameters, they must always produce the same UI. Compose may execute a composable multiple times, or skip its execution, as part of its internal optimizations, so side effects (network calls, database writes) must not be placed directly in the body of the function. That kind of work is handled through the mechanisms described in [Side Effects](#11-side-effects).

---

## 2. Design System: Material 3 and Theming

For the application to remain visually consistent, use the **tokens** defined by the theme instead of fixed values. This allows dark mode and any future branding changes to apply automatically, without modifying each screen individually.

### Colors: `MaterialTheme.colorScheme`

| Token | Purpose |
| :--- | :--- |
| `.primary` | Brand color and primary actions (emphasized buttons). |
| `.onPrimary` | Text or icons placed on top of `.primary`. |
| `.secondary` / `.tertiary` | Secondary accents. |
| `.background` | General screen background. |
| `.surface` | Background for cards, sheets, and menus. |
| `.onBackground` / `.onSurface` | Primary text over background or surface. |
| `.onSurfaceVariant` | Secondary text. |
| `.outline` | Borders and dividers. |
| `.primaryContainer` / `.onPrimaryContainer` | Containers with moderate emphasis (chips, tags). |
| `.error` / `.onError` | Error states. |

General rule: the `onX` color is applied to elements placed on top of a background painted with `X`. For example, if the background uses `.primary`, the text on top of it should use `.onPrimary`. This guarantees proper contrast in both light and dark mode.

```kotlin
Text(
    text = "Important action",
    color = MaterialTheme.colorScheme.primary
)
```

### Typography: `MaterialTheme.typography`

| Token | Typical use |
| :--- | :--- |
| `.displayLarge` / `.displayMedium` / `.displaySmall` | Very large titles (hero screens). |
| `.headlineMedium` | Screen titles. |
| `.titleLarge` | Card or section titles. |
| `.bodyLarge` / `.bodyMedium` | Body text. |
| `.labelMedium` / `.labelSmall` | Labels, chips, button text. |

```kotlin
Text("Settings", style = MaterialTheme.typography.headlineMedium)
```

### Shapes: `MaterialTheme.shapes`

| Token | Typical use |
| :--- | :--- |
| `.small` | Buttons, chips. |
| `.medium` | Cards, containers. |
| `.large` | Dialogs, bottom sheets. |

```kotlin
Card(shape = MaterialTheme.shapes.medium) { /* content */ }
```

### Dark mode and elevation

Dark mode works correctly only when theme tokens are used; any fixed color (for example, `Color.White`) is a point that can break visually when switching to dark mode. In Material 3, elevation is not represented solely by shadow: in dark mode it also lightens the surface. Use `CardDefaults.cardElevation(2.dp)` instead of manual shadows.

Avoid fixed values:
```kotlin
color = Color(0xFFFF5722)
```

Prefer theme tokens:
```kotlin
color = MaterialTheme.colorScheme.primary
```

---

## 3. The Modifier

`Modifier` configures size, spacing, background, click behavior, and other aspects of a composable. Modifiers are chained, and **the order in which they are applied affects the result**, since each modifier wraps the previous one.

| Modifier | Use case | Example |
| :--- | :--- | :--- |
| `.padding(dp)` | Space around the element. | `.padding(16.dp)` |
| `.padding(horizontal=, vertical=)` | Padding per axis. | `.padding(horizontal = 16.dp)` |
| `.fillMaxWidth()` | Takes up all available width. | `.fillMaxWidth()` |
| `.fillMaxHeight()` / `.fillMaxSize()` | Takes up all height, or the whole screen. | `.fillMaxSize()` |
| `.height(dp)` / `.width(dp)` | Fixed size on one axis. | `.height(8.dp)` |
| `.size(dp)` | Fixed square size (icons, avatars). | `.size(48.dp)` |
| `.background(color, shape)` | Colored background, with an optional shape. | `.background(Color.Red, CircleShape)` |
| `.clip(shape)` | Clips the content (for example, circular images). | `.clip(CircleShape)` |
| `.border(width, color, shape)` | Border. | `.border(1.dp, Color.Gray, RoundedCornerShape(4.dp))` |
| `.clickable { }` | Makes any element clickable. | `.clickable { onClick() }` |
| `.weight(1f)` | Inside `Column`/`Row`, distributes remaining space. | `.weight(1f)` |
| `.aspectRatio(ratio)` | Maintains an aspect ratio. | `.aspectRatio(16f / 9f)` |
| `.alpha(float)` | Transparency (0f to 1f). | `.alpha(0.5f)` |
| `.wrapContentSize()` | The element only takes up the space it needs. | `.wrapContentSize()` |

### Modifier order affects the result

The following two blocks use the same modifiers but produce different results:

```kotlin
// A: padding before background -> the color does NOT cover the padding area
Modifier
    .padding(16.dp)
    .background(Color.Red)

// B: padding after background -> the color covers the whole area, including the padding
Modifier
    .background(Color.Red)
    .padding(16.dp)
```

A `padding` placed before another modifier reduces the area available to the modifiers that follow it in the chain. The same reasoning applies to `.clickable`: its position determines whether the clickable area includes that padding or not.

### Common combinations

```kotlin
// Clipped circular avatar
Modifier
    .size(48.dp)
    .clip(CircleShape)

// Container whose corners also clip its inner content
Modifier
    .clip(MaterialTheme.shapes.medium)
    .background(MaterialTheme.colorScheme.surface)
```

---

## 4. Basic Layouts: Column, Row, and Box

Compose has three fundamental containers:

- **`Column`**: stacks its elements vertically. The main axis is vertical.
- **`Row`**: stacks its elements horizontally. The main axis is horizontal.
- **`Box`**: overlays its elements in layers, one on top of another.

Distribution is controlled with `Arrangement` (main axis) and `Alignment` (cross axis):

```kotlin
Column(
    verticalArrangement = Arrangement.spacedBy(8.dp),
    horizontalAlignment = Alignment.CenterHorizontally
) {
    Text("User")
    Text("user@email.com")
}

Row(
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
) {
    Text("Total")
    Text("$120.00")
}
```

`Arrangement.spacedBy(8.dp)` distributes uniform spacing between child elements, without the need to insert a `Spacer` between each one.

### Example: item with icon and text (Row + Column)

```kotlin
Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier.fillMaxWidth().padding(16.dp)
) {
    Icon(Icons.Default.Person, contentDescription = null)
    Spacer(Modifier.width(12.dp))
    Column {
        Text("User name", style = MaterialTheme.typography.titleMedium)
        Text("Administrator", style = MaterialTheme.typography.bodySmall)
    }
}
```

### Example: text overlaid on a background (Box)

```kotlin
Box(modifier = Modifier.fillMaxWidth().height(160.dp)) {
    // background layer (image or color)
    Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.primary))
    // text layer, aligned to the bottom start
    Text(
        "Welcome",
        color = Color.White,
        modifier = Modifier.align(Alignment.BottomStart).padding(16.dp)
    )
}
```

---

## 5. Other Layout Types: ConstraintLayout, FlowRow, and BoxWithConstraints

`Column`, `Row`, and `Box` cover most use cases, but there are scenarios where other layouts are a better fit.

### ConstraintLayout

`ConstraintLayout` positions elements using constraints relative to one another, equivalent to the XML-based `ConstraintLayout`. It is useful when a design requires complex positioning that would be difficult to express by nesting several `Column`, `Row`, and `Box` elements, since it avoids deep nesting and can reduce measurement cost in complex layouts.

Dependency:
```kotlin
implementation("androidx.constraintlayout:constraintlayout-compose:1.0.1")
```

```kotlin
@Composable
fun ProfileConstraint() {
    ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
        val (photo, name, role) = createRefs()

        Image(
            painter = painterResource(R.drawable.avatar),
            contentDescription = null,
            modifier = Modifier
                .size(64.dp)
                .constrainAs(photo) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                }
        )
        Text(
            "Anna Cooper",
            modifier = Modifier.constrainAs(name) {
                top.linkTo(photo.top)
                start.linkTo(photo.end, margin = 12.dp)
            }
        )
        Text(
            "Software Engineer",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.constrainAs(role) {
                top.linkTo(name.bottom)
                start.linkTo(name.start)
            }
        )
    }
}
```

For most screens in this application, `Column`, `Row`, and `Box` are sufficient. `ConstraintLayout` is reserved for layouts with complex positioning relationships between multiple elements.

### FlowRow and FlowColumn

`FlowRow` lays out its children horizontally and, when there is no more space available on the current line, continues on a new line, similar to how text wraps. `FlowColumn` does the equivalent on the vertical axis. Both are useful for lists of chips, tags, or labels of variable length.

```kotlin
FlowRow(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.spacedBy(8.dp),
    verticalArrangement = Arrangement.spacedBy(8.dp)
) {
    ingredientList.forEach { ingredient ->
        AssistChip(onClick = {}, label = { Text(ingredient) })
    }
}
```

Unlike `LazyRow`, `FlowRow` does not recycle or virtualize its elements: all of them are composed. It is suitable for small collections (a few dozen elements), not for long lists.

### BoxWithConstraints

`BoxWithConstraints` exposes the available size constraints (`maxWidth`, `maxHeight`) within its content, which allows the layout to adapt based on the available space, for example, to define the number of columns in a grid based on screen width.

```kotlin
BoxWithConstraints {
    val columns = if (maxWidth < 600.dp) 2 else 4
    LazyVerticalGrid(columns = GridCells.Fixed(columns)) {
        items(recipes, key = { it.id }) { RecipeCard(it) }
    }
}
```

`BoxWithConstraints` carries an additional measurement cost compared to a regular `Box`, so it should be used only when the content genuinely needs to adapt to the available space.

---

## 6. Efficient Lists and Grids (Lazy Lists)

Rendering a long list with `Column` and `forEach` composes every element even if it is not visible on screen, which degrades performance. The *lazy* composables (`LazyColumn`, `LazyRow`, `LazyVerticalGrid`, `LazyHorizontalGrid`) only compose and measure the visible elements, plus a nearby buffer, equivalent to a `RecyclerView`.

### LazyColumn

```kotlin
LazyColumn(
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp)
) {
    items(
        items = contacts,
        key = { contact -> contact.id }
    ) { contact ->
        ContactItem(contact = contact, onClick = { /* open detail */ })
    }
}
```

- **`key`**: a unique, stable identifier for each element, not its position in the list. It lets Compose recognize which elements changed, moved, or were removed, avoiding unnecessary recompositions and visual flickering when reordering or filtering the list.
- **`contentPadding`**: unlike applying `Modifier.padding` directly to the `LazyColumn`, this parameter adds internal spacing that respects scrolling; content can scroll all the way to that padding instead of being clipped by it.

### LazyRow and LazyVerticalGrid

```kotlin
LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
    items(categories, key = { it.id }) { category ->
        CategoryChip(category)
    }
}

LazyVerticalGrid(
    columns = GridCells.Fixed(2),
    contentPadding = PaddingValues(16.dp),
    horizontalArrangement = Arrangement.spacedBy(12.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp)
) {
    items(products, key = { it.id }) { product ->
        ProductCard(product)
    }
}
```

`GridCells.Fixed(n)` defines a fixed number of columns. `GridCells.Adaptive(minSize = 128.dp)` automatically calculates how many columns fit based on the available width, which is useful when the application must support different screen sizes.

### itemsIndexed

When the position of the element is needed in addition to the element itself:

```kotlin
LazyColumn {
    itemsIndexed(steps, key = { _, step -> step.id }) { index, step ->
        Text("${index + 1}. ${step.description}")
    }
}
```

### Fixed headers: stickyHeader

`stickyHeader` keeps an element visible at the top while the content that follows it scrolls. It is useful for grouped lists, for example, ingredients grouped by category.

```kotlin
LazyColumn {
    ingredientsByCategory.forEach { (category, ingredients) ->
        stickyHeader {
            Text(
                category,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(8.dp)
            )
        }
        items(ingredients, key = { it.id }) { ingredient ->
            IngredientItem(ingredient)
        }
    }
}
```

### Controlling scroll: LazyListState

`rememberLazyListState()` allows reading and controlling the scroll position programmatically, for example, to return to the top of the list after adding an element.

```kotlin
val listState = rememberLazyListState()
val scope = rememberCoroutineScope()

LazyColumn(state = listState) {
    items(recipes, key = { it.id }) { RecipeCard(it) }
}

Button(onClick = { scope.launch { listState.animateScrollToItem(0) } }) {
    Text("Go to top")
}
```

### Additional recommendations

- Avoid nesting a `LazyColumn` inside another `LazyColumn` with the same orientation: Compose cannot correctly measure lists of unbounded size nested on the same axis. It is valid to place a `LazyRow` inside an `item` of a `LazyColumn` (for example, a horizontal carousel within a screen that scrolls vertically).
- If the list mixes different types of elements (headers, cards, dividers), specifying `contentType` in `items` helps Compose reuse compositions more efficiently.
- `Modifier.animateItem()` automatically animates position changes of an element within a lazy list when items are added, removed, or reordered.

---

## 7. State and Recomposition

### What state is

**State** is any data that can change while the application is running: the value of a counter, the text typed into a search field, whether a switch is on, or a list of pending tasks. A value that never changes, such as a fixed title or a constant, is not state.

### State and UI

In Jetpack Compose, the interface is not updated by directly modifying elements that have already been drawn, as used to happen when finding a `TextView` and changing its text in the XML-based view system. Instead, the UI is described as a function of state:

```
UI = f(state)
```

When the state changes, Compose re-executes the composable functions that read that state and updates only the parts of the screen that depend on it. This process is called **recomposition**.

### remember and mutableStateOf

These two pieces serve distinct, complementary purposes:

| Element | Function | Consequence of omitting it |
| :--- | :--- | :--- |
| `mutableStateOf(...)` | Creates an observable state container. When its value is modified, it notifies Compose to recompose whatever depends on it. | The value changes, but the UI never finds out: the screen does not reflect the change. |
| `remember { ... }` | Preserves a value across recompositions, preventing it from resetting every time the composable function runs again. | The value resets on every recomposition: the state appears not to persist. |

For this reason, the two are almost always used together: `remember { mutableStateOf(...) }` creates an observable value that also persists across recompositions, although it does not survive the destruction and recreation of the activity, as happens when the screen rotates (see below).

```kotlin
@Composable
fun Counter() {
    var count by remember { mutableStateOf(0) }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Button(onClick = { count-- }) { Text("-") }
        Text("$count", modifier = Modifier.padding(horizontal = 16.dp))
        Button(onClick = { count++ }) { Text("+") }
    }
}
```

- The `by` delegate allows writing `count` directly instead of `count.value`.
- Without `remember`, `count` would reset to `0` on every recomposition.
- Without `mutableStateOf`, modifying `count` would not trigger a recomposition.

### Surviving configuration changes: rememberSaveable

`remember` does not survive configuration changes, such as screen rotation or a language change, because the activity is destroyed and recreated. When the data must be preserved in that scenario, for example, text entered by the user, `rememberSaveable` is used:

```kotlin
var text by rememberSaveable { mutableStateOf("") }
```

Practical rule: if losing the value on rotation would negatively affect the user experience, use `rememberSaveable`; otherwise, `remember` is sufficient.

The different variants of `mutableStateOf` for other data types are covered in the next section.

---

## 8. mutableStateOf and Its Variants

`mutableStateOf` is the most common way to create observable state in Compose, but it is not the only one. Compose offers specialized variants for different data types, aimed at improving readability and, in some cases, performance.

### mutableStateOf&lt;T&gt;

A generic state container for a single value of any type.

```kotlin
var name by remember { mutableStateOf("") }
var isFavorite by remember { mutableStateOf(false) }
var user by remember { mutableStateOf<User?>(null) }
```

Every time a new value is assigned, Compose schedules a recomposition of the functions that read that state.

### Primitive variants: mutableIntStateOf, mutableLongStateOf, mutableFloatStateOf, mutableDoubleStateOf

`mutableStateOf<Int>()` stores the value internally as an object (autoboxing), which carries a slightly higher memory and performance cost. For frequently used primitive types, Compose offers specialized containers that avoid autoboxing:

```kotlin
var quantity by remember { mutableIntStateOf(0) }
var price by remember { mutableDoubleStateOf(0.0) }
```

These variants are recommended over `mutableStateOf<Int>()`, `mutableStateOf<Long>()`, `mutableStateOf<Float>()`, or `mutableStateOf<Double>()` whenever the state's type is one of those primitives. For every other type (`String`, `Boolean`, data classes, nullable types), the generic `mutableStateOf` is the correct choice.

### Observable collections: mutableStateListOf and mutableStateMapOf

A `remember { mutableStateOf(listOf(...)) }` observes **the reference** to the list, not its contents. If the list is modified with `add` or `remove` without reassigning the variable, Compose does not detect the change. There are two alternatives:

Reassign an immutable list:
```kotlin
var tasks by remember { mutableStateOf(listOf<Task>()) }
tasks = tasks + newTask   // creates a new list and reassigns the variable
```

Use an observable list:
```kotlin
val tasks = remember { mutableStateListOf<Task>() }
tasks.add(newTask)   // modifies in place; Compose detects it either way
```

`mutableStateListOf` and `mutableStateMapOf` implement `SnapshotStateList` and `SnapshotStateMap` respectively: they are collections whose mutating operations (`add`, `remove`, `put`, `clear`, among others) notify Compose directly, without needing to reassign the variable.

```kotlin
val selected = remember { mutableStateMapOf<String, Boolean>() }
selected["ingredient_1"] = true
```

In full screens managed by a `ViewModel`, the recommended practice is to expose immutable lists inside a `UiState` (see [Architecture](#16-architecture-viewmodel-and-ui-state)) rather than using `mutableStateListOf` directly in the UI. These variants are more useful for local state, scoped to a specific composable.

### Derived state: derivedStateOf

When a value is calculated from another piece of state, it should not be stored as an independent state, since the two could fall out of sync. Instead, it is derived:

```kotlin
val items = remember { mutableStateListOf<String>() }
val hasItems by remember { derivedStateOf { items.isNotEmpty() } }
```

`derivedStateOf` also avoids unnecessary recompositions: if `items` changes size but the result of `isNotEmpty()` does not change, the functions reading `hasItems` do not recompose.

### snapshotFlow: from state to Flow

`snapshotFlow` converts reads of `State` into a `Flow`, which is useful for reacting to state changes using Kotlin Flow operators (`debounce`, `distinctUntilChanged`, `filter`, among others).

```kotlin
LaunchedEffect(listState) {
    snapshotFlow { listState.firstVisibleItemIndex }
        .distinctUntilChanged()
        .collect { index -> /* react to the position change */ }
}
```

### Summary of variants

| Function | Data type | When to use it |
| :--- | :--- | :--- |
| `mutableStateOf<T>()` | Any type (String, Boolean, data class, nullable types) | General case. |
| `mutableIntStateOf()` | Int | Counters, indices, quantities. |
| `mutableLongStateOf()` | Long | Timestamps, large numeric identifiers. |
| `mutableFloatStateOf()` | Float | Animation values, percentages. |
| `mutableDoubleStateOf()` | Double | Calculations with decimals (prices, averages). |
| `mutableStateListOf<T>()` | Mutable list | Local collections modified with `add`/`remove` without reassigning the variable. |
| `mutableStateMapOf<K, V>()` | Mutable map | Local selections or key-value associations. |
| `derivedStateOf { }` | Computed from other state | Avoid storing separately values that can be computed. |

---

## 9. State Hoisting

**State hoisting** consists of moving a composable's state up to the composable that calls it, so that the child composable stops owning the data: it only receives it as a parameter and notifies events upward.

The pattern can be summarized as: **state flows down, events flow up**. The data comes down as a parameter; the event goes up as a lambda function.

```kotlin
// Stateful: the state lives inside the composable
@Composable
fun SearchField() {
    var text by remember { mutableStateOf("") }
    TextField(value = text, onValueChange = { text = it })
}

// Stateless: the state lives in the parent; this composable only displays and notifies
@Composable
fun SearchField(
    value: String,
    onValueChange: (String) -> Unit
) {
    TextField(value = value, onValueChange = onValueChange)
}

// The parent owns the state
@Composable
fun SearchScreen() {
    var query by rememberSaveable { mutableStateOf("") }
    SearchField(value = query, onValueChange = { query = it })
}
```

Advantages of a stateless composable:

- **Reusable** across different screens, since it does not dictate where the state lives.
- **Testable and previewable**, because it only needs sample data.
- **Predictable**, since it does not hold hidden state.

Practical rule: composables should be stateless by default, and state should live as high up in the hierarchy as possible, ideally in a `ViewModel` for full screens (see [Architecture](#16-architecture-viewmodel-and-ui-state)).

---

## 10. Events and Interaction

Events travel upward as **lambdas**. The composable does not decide what to do in response to an event; it receives a function and invokes it when appropriate.

```kotlin
@Composable
fun FavoriteButton(
    isFavorite: Boolean,
    onToggle: () -> Unit
) {
    IconButton(onClick = onToggle) {
        Icon(
            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
            contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites"
        )
    }
}
```

- `onClick: () -> Unit`: takes no parameters and returns nothing.
- `onValueChange: (String) -> Unit`: receives the new value.
- `onItemClick: (Item) -> Unit`: receives the selected item.

Event parameters are named starting with `on` (`onClick`, `onSubmit`, `onDismiss`).

---

## 11. Side Effects

Some actions are not about drawing UI: network calls, showing a Snackbar, starting an animation, or writing to disk. This kind of work must not run directly in the body of a composable, since it would repeat on every recomposition. Effect handlers exist for this purpose.

| Effect | When to use it |
| :--- | :--- |
| `LaunchedEffect(key)` | Runs a coroutine when the composable enters composition, or again whenever `key` changes. Example: loading data when a screen opens. |
| `rememberCoroutineScope()` | Provides a `CoroutineScope` to launch coroutines in response to a one-off event, for example, a click that shows a Snackbar. |
| `DisposableEffect(key)` | Registers a resource (listener, observer) and guarantees its cleanup when the composable leaves composition or when `key` changes. |
| `rememberUpdatedState(x)` | Keeps an up-to-date reference to a value inside a long-lived effect, without restarting that effect. |
| `SideEffect { }` | Runs code that Compose cannot observe after every successful recomposition; useful for synchronizing state with external libraries. |

```kotlin
@Composable
fun DetailScreen(id: String, viewModel: DetailViewModel) {
    LaunchedEffect(id) {
        viewModel.loadDetail(id)
    }
    // rest of the UI
}
```

For everyday use, `LaunchedEffect` (loading data when entering a screen) and `rememberCoroutineScope` (reacting to a one-off event) cover most cases. The other effect handlers are introduced gradually as more specific scenarios arise.

---

## 12. StateFlow and SharedFlow

In an architecture based on `ViewModel`, the screen's state is usually exposed through a `Flow`. Kotlin Flow offers two variants that are especially relevant for UI: `StateFlow` and `SharedFlow`.

### Cold flow versus hot flow

A standard `Flow` is **cold**: it produces no values until someone collects it, and each collector triggers its own execution from the start. `StateFlow` and `SharedFlow` are **hot**: they exist and emit values independently of whether there are active collectors, and multiple collectors share the same emission.

### StateFlow

`StateFlow` is a hot flow that always holds a current value, exposed through the `.value` property. It is the standard tool for exposing a screen's state from a `ViewModel`.

```kotlin
class RecipesViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(RecipesUiState())
    val uiState: StateFlow<RecipesUiState> = _uiState.asStateFlow()

    fun loadRecipes() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = repository.getRecipes()
            _uiState.update { it.copy(recipes = result, isLoading = false) }
        }
    }
}
```

Conventions of this pattern:

- The `MutableStateFlow` is **private** (`_uiState`); only the `ViewModel` can modify it.
- A **read-only** version is exposed (`StateFlow`, via `.asStateFlow()`); the UI can only observe it, never modify it directly.
- `.update { }` applies an atomic transformation to the current value, avoiding race conditions if two coroutines try to update the state at the same time.

### Consuming a StateFlow in Compose

```kotlin
@Composable
fun RecipesScreen(viewModel: RecipesViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    when {
        state.isLoading -> CircularProgressIndicator()
        state.error != null -> Text(state.error)
        else -> RecipeList(recipes = state.recipes)
    }
}
```

There are two functions for converting a `StateFlow` into a `State` observable by Compose:

| Function | Behavior |
| :--- | :--- |
| `collectAsState()` | Collects the flow while the composable is in composition, without regard to the screen's lifecycle. |
| `collectAsStateWithLifecycle()` | Collects the flow only while the lifecycle is at least in the `STARTED` state; it automatically stops when the application goes to the background. |

`collectAsStateWithLifecycle()` is the recommended option in Android applications, since it avoids continuing to collect data, and consuming resources, when the screen is not visible. It requires the `androidx.lifecycle:lifecycle-runtime-compose` dependency.

### SharedFlow

`SharedFlow` is a more general-purpose hot flow, with no guaranteed "current" value. It is used to model **events** that must happen exactly once, for example, navigating to another screen or showing a Snackbar, as opposed to persistent screen state, which corresponds to `StateFlow`.

```kotlin
class LoginViewModel : ViewModel() {
    private val _events = MutableSharedFlow<LoginEvent>()
    val events: SharedFlow<LoginEvent> = _events.asSharedFlow()

    fun onLoginSuccess() {
        viewModelScope.launch {
            _events.emit(LoginEvent.NavigateToHome)
        }
    }
}

sealed interface LoginEvent {
    object NavigateToHome : LoginEvent
}
```

```kotlin
@Composable
fun LoginScreen(viewModel: LoginViewModel, onNavigateToHome: () -> Unit) {
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is LoginEvent.NavigateToHome -> onNavigateToHome()
            }
        }
    }
    // rest of the UI
}
```

### StateFlow versus SharedFlow: when to use each

| Scenario | Tool |
| :--- | :--- |
| State the screen must reflect at all times (list of data, loading indicator, text of a field) | `StateFlow` |
| Event that must occur exactly once and must not repeat on recomposition or screen rotation (navigation, Snackbar, Toast) | `SharedFlow` |

Using `StateFlow` for one-off events is a common mistake: since it always retains the last value, a navigation event could fire again on recomposition if not handled carefully. `SharedFlow`, configured without *replay* (its default), does not replay the last event to new collectors, which makes it more appropriate for this case.

### Other useful operators

- **`combine`**: combines multiple flows into one. Useful when the UI state depends on more than one data source.
- **`map`**: transforms each value emitted by a flow.
- **`stateIn(scope, started, initialValue)`**: converts a generic `Flow`, for example, one coming from a database, into a `StateFlow`, running within a `CoroutineScope` (typically `viewModelScope`).

```kotlin
val uiState: StateFlow<RecipesUiState> = repository.observeRecipes()
    .map { recipes -> RecipesUiState(recipes = recipes) }
    .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = RecipesUiState(isLoading = true)
    )
```

---

## 13. Loading Images from the Network (Coil)

`Image(painterResource(...))` is used only for local resources (drawables). For photos that come from a **URL**, use **Coil**, the standard image-loading library for Compose.

Dependency (`build.gradle.kts`):
```kotlin
implementation("io.coil-kt:coil-compose:2.6.0")
```

Usage:
```kotlin
AsyncImage(
    model = "https://example.com/photo.jpg",
    contentDescription = "Profile photo",
    contentScale = ContentScale.Crop,
    modifier = Modifier
        .size(72.dp)
        .clip(CircleShape)
)
```

Coil handles downloading, caching, and asynchronous loading of the image. To show a placeholder while the image loads, use `SubcomposeAsyncImage`.

---

## 14. Common Material 3 Components

| Component | Purpose | Minimal snippet |
| :--- | :--- | :--- |
| `Text` | Display text. | `Text("Hello")` |
| `Button` | Primary action. | `Button(onClick = {}) { Text("Save") }` |
| `OutlinedButton` / `TextButton` | Secondary actions. | `TextButton(onClick = {}) { Text("Cancel") }` |
| `IconButton` | Button composed only of an icon. | `IconButton(onClick = {}) { Icon(...) }` |
| `TextField` / `OutlinedTextField` | Text input. | `OutlinedTextField(value, onValueChange = {})` |
| `Card` | Container with surface and elevation. | `Card { /* content */ }` |
| `Icon` | Vector icons. | `Icon(Icons.Default.Home, contentDescription = null)` |
| `Checkbox` / `Switch` | Boolean selection. | `Switch(checked, onCheckedChange = {})` |
| `CircularProgressIndicator` | Loading indicator. | `CircularProgressIndicator()` |
| `HorizontalDivider` | Separator line. | `HorizontalDivider()` |
| `Spacer` | Empty space. | `Spacer(Modifier.height(8.dp))` |

### Screen structure with Scaffold

`Scaffold` provides the standard slots of a screen: top bar, bottom bar, floating action button, and content.

```kotlin
Scaffold(
    topBar = {
        TopAppBar(title = { Text("Home") })
    },
    floatingActionButton = {
        FloatingActionButton(onClick = { /* new item */ }) {
            Icon(Icons.Default.Add, contentDescription = "Add")
        }
    }
) { innerPadding ->
    LazyColumn(modifier = Modifier.padding(innerPadding)) {
        // content
    }
}
```

**Important:** the `innerPadding` parameter is not optional. If it is not applied to the content, the content will be covered by the top or bottom bar.

---

## 15. Navigation Between Screens

**Navigation Compose** defines a `NavHost` with routes (strings) and navigates using a `NavController`.

Dependency:
```kotlin
implementation("androidx.navigation:navigation-compose:2.7.7")
```

```kotlin
@Composable
fun AppNav() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(onItemClick = { id ->
                navController.navigate("detail/$id")
            })
        }
        composable("detail/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")
            DetailScreen(id = id)
        }
    }
}
```

- `navController.navigate("route")`: navigates to a screen.
- `navController.popBackStack()`: returns to the previous screen.
- It is recommended to pass **identifiers** as navigation arguments, not full objects.

---

## 16. Architecture: ViewModel and UI State

As a screen grows in complexity, logic and network calls should not be placed inside the composable. The recommended separation is as follows:

1. **ViewModel**: holds the screen's logic and exposes its state. Survives configuration changes.
2. **UI State**: a `data class` that describes everything the screen needs to display.
3. **Composable**: only observes the state and draws it.

```kotlin
// 1. A single object that describes the screen
data class TasksUiState(
    val tasks: List<Task> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

// 2. The ViewModel exposes the state as a read-only flow
class TasksViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(TasksUiState())
    val uiState: StateFlow<TasksUiState> = _uiState.asStateFlow()

    fun loadTasks() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            // fetch tasks...
            _uiState.update { it.copy(tasks = result, isLoading = false) }
        }
    }
}

// 3. The UI only reflects the state
@Composable
fun TasksScreen(viewModel: TasksViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    when {
        state.isLoading -> CircularProgressIndicator()
        state.error != null -> Text(state.error!!)
        else -> TaskList(tasks = state.tasks)
    }
}
```

This pattern relies on `StateFlow`, covered in detail in [StateFlow and SharedFlow](#12-stateflow-and-sharedflow).

**Unidirectional Data Flow (UDF):** state flows down from the `ViewModel` to the UI; events flow up from the UI to the `ViewModel` by invoking its functions. The UI never modifies state directly; its responsibility is limited to reflecting it.

---

## 17. Previews

There is no need to rebuild or reinstall the application to see a UI change: `@Preview` allows viewing it directly in the corresponding panel of Android Studio.

```kotlin
@Preview(showBackground = true)
@Composable
private fun CounterPreview() {
    MyAppTheme {
        Counter()
    }
}
```

Recommendations:

- `@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)` previews dark mode.
- `@Preview(name = "Small screen", widthDp = 320)` previews other screen sizes.
- This is where the value of state hoisting becomes evident: a stateless composable with sample data can be previewed with no additional dependencies; one that loads data from the network cannot.
- Previews should use **sample data** defined locally, never real calls to a network or database.

---

## 18. Best Practices

1. **Spacing between elements**: use `Spacer(Modifier.height(8.dp))` or `Arrangement.spacedBy(8.dp)` instead of complex padding combinations.
2. **Reusability**: if a design repeats (a chip, a label), extract it into its own composable.
3. **Images**: use `contentScale = ContentScale.Crop` so images fill their space without distortion.
4. **Stateless by default**: pass data as parameters and hoist events through lambdas (`onClick: () -> Unit`).
5. **Accessibility**: set `contentDescription` on meaningful images and icons; use `null` when they are purely decorative.
6. **Small composables**: if a function exceeds roughly 40 to 50 lines, it generally should be split.
7. **dp versus sp**: use `dp` for sizes and spacing; use `sp` for text, since it respects the user's font scale setting.
8. **Scope of state reads**: read state as close as possible to where it is used, to keep the scope of recomposition small.
9. **Modifier as a parameter**: reusable composables should accept `modifier: Modifier = Modifier` and apply it to the root element.

---

## 19. Common Trainee Mistakes

| Symptom | Typical cause | Solution |
| :--- | :--- | :--- |
| The value resets on its own | Missing `remember` | `var x by remember { mutableStateOf(...) }` |
| The data is changed but the screen does not react | It is not a `State` (it is a regular variable) | Use `mutableStateOf` |
| The value is lost on screen rotation | `remember` does not survive configuration changes | Use `rememberSaveable` |
| Content is covered by the top or bottom bar | `innerPadding` from `Scaffold` was not applied | `Modifier.padding(innerPadding)` |
| A long list scrolls sluggishly | Using `Column` with `forEach` | Switch to `LazyColumn` with `items` |
| The list flickers when deleting or reordering items | Missing `key` in `items` | Define a unique, stable `key` |
| The background does not cover the whole element | Incorrect modifier order | Apply `.background()` before `.padding()` |
| The screen looks fine in light mode but not in dark mode | Use of fixed colors | Use `MaterialTheme.colorScheme` |
| The image renders stretched | Missing `contentScale` | Use `ContentScale.Crop` |
| The network call repeats uncontrollably | It was placed directly in the composable body | Wrap it in `LaunchedEffect(key)` |
| A navigation event repeats after screen rotation | The event was modeled with `StateFlow` instead of `SharedFlow` | Use `SharedFlow` for one-off events |
| The composable cannot be reused on another screen | The composable is stateful | Apply state hoisting |

---

## 20. Glossary of Terms

- **Composable**: a function annotated with `@Composable` that describes a piece of UI.
- **Recomposition**: the process by which Compose re-executes a composable because a piece of state it depends on has changed.
- **State**: data that can change over time and, when it does, triggers a recomposition. Created with `mutableStateOf` or one of its variants.
- **`remember`**: preserves a value across recompositions so it does not reset.
- **`rememberSaveable`**: equivalent to `remember`, but also survives configuration changes (rotation, language change).
- **`mutableStateOf`**: creates an observable state container; when its value changes, it notifies Compose.
- **`mutableIntStateOf` / `mutableLongStateOf` / `mutableFloatStateOf` / `mutableDoubleStateOf`**: variants of `mutableStateOf` specialized for primitive types, which avoid autoboxing.
- **`mutableStateListOf` / `mutableStateMapOf`**: observable collections whose mutating operations notify Compose directly.
- **`derivedStateOf`**: state computed from other state.
- **`snapshotFlow`**: converts reads of `State` into a `Flow`.
- **State Hoisting**: the act of moving a composable's state up to its parent, leaving the child stateless.
- **Stateless / Stateful**: without its own state, versus with its own state. Stateless design is preferred.
- **UDF (Unidirectional Data Flow)**: pattern in which state flows down and events flow up.
- **Modifier**: a chain of configurations (size, padding, background, click behavior) applied to a composable. The order of application matters.
- **`Scaffold`**: the base structure of a screen (top bar, bottom bar, floating action button, content).
- **`innerPadding`**: the space reserved by `Scaffold` for its bars; it must be applied to the screen's content.
- **`LazyColumn` / `LazyRow` / `LazyVerticalGrid`**: lists and grids that only compose visible elements.
- **`key` (in lists)**: a unique, stable identifier for each element; improves performance and prevents visual flickering.
- **`stickyHeader`**: an element of a lazy list that stays fixed at the top while the following content scrolls.
- **`LazyListState`**: an object that exposes and allows controlling the scroll position of a lazy list.
- **`GridCells`**: configuration for the number of columns in a `LazyVerticalGrid` (`Fixed` or `Adaptive`).
- **`Arrangement`**: distribution of child elements along the main axis of a `Column` or `Row`.
- **`Alignment`**: alignment of child elements along the cross axis.
- **`ConstraintLayout`**: a layout that positions elements using constraints relative to one another.
- **`FlowRow` / `FlowColumn`**: layouts that arrange their children in a line and continue on a new line once the space runs out.
- **`BoxWithConstraints`**: a layout that exposes the available space so its content can adapt.
- **Side Effect**: an action that does not consist of drawing UI (network, disk, animation). It runs inside an effect handler.
- **`LaunchedEffect`**: runs a coroutine when the composable enters composition, or when its `key` changes.
- **`rememberCoroutineScope`**: provides a scope to launch coroutines from one-off events, such as a click.
- **`ViewModel`**: a class that manages a screen's state and logic; survives configuration changes.
- **`Flow`**: an asynchronous sequence of values from Kotlin Coroutines. Cold by default: it does not emit until it is collected.
- **`StateFlow`**: a hot flow that always holds a current value; used to expose screen state.
- **`SharedFlow`**: a general-purpose hot flow, commonly used to model one-off events.
- **`collectAsStateWithLifecycle`**: converts a `StateFlow` into a `State` that Compose can observe, respecting the screen's lifecycle.
- **UI State**: a `data class` that describes everything a screen needs to display.
- **Design token**: a theme value (`colorScheme`, `typography`, `shapes`) used instead of fixed values.
- **`dp`**: a unit of size and spacing independent of screen density.
- **`sp`**: a unit for text that respects the user's font scale setting.
- **Coil / `AsyncImage`**: the library and composable used to load images from a URL.
- **`@Preview`**: displays a composable in Android Studio without running the application.

---

Whenever in doubt during development, it helps to start with two questions: first, what state does what I want to display depend on; second, who should own that state. Most behavior issues in Compose are resolved by answering these two questions.
