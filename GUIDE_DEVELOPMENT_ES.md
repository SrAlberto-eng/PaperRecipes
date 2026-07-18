# Jetpack Compose: Guía Técnica de Referencia

Documento de referencia sobre conceptos, patrones y componentes para el desarrollo de interfaces en Android con **Jetpack Compose** y **Material 3**. Está pensado como material de consulta durante el desarrollo diario.

> **Principio fundamental:** en Jetpack Compose la interfaz de usuario no se modifica directamente; se **describe** en función de un estado. Cuando el estado cambia, Compose vuelve a ejecutar las funciones que dependen de él y actualiza la pantalla. Comprender este principio resuelve la mayoría de los problemas de comportamiento inesperado en la UI.

---

## Índice

1. [Introducción: ¿Qué es un Composable?](#1-introducción-qué-es-un-composable)
2. [Sistema de Diseño: Material 3 y Theming](#2-sistema-de-diseño-material-3-y-theming)
3. [El Modificador (Modifier)](#3-el-modificador-modifier)
4. [Layouts Básicos: Column, Row y Box](#4-layouts-básicos-column-row-y-box)
5. [Otros Tipos de Layout: ConstraintLayout, FlowRow y BoxWithConstraints](#5-otros-tipos-de-layout-constraintlayout-flowrow-y-boxwithconstraints)
6. [Listas y Cuadrículas Eficientes (Lazy Lists)](#6-listas-y-cuadrículas-eficientes-lazy-lists)
7. [Estado y Recomposición](#7-estado-y-recomposición)
8. [mutableStateOf y sus Variantes](#8-mutablestateof-y-sus-variantes)
9. [State Hoisting (Elevación de Estado)](#9-state-hoisting-elevación-de-estado)
10. [Eventos e Interacción](#10-eventos-e-interacción)
11. [Efectos Secundarios (Side Effects)](#11-efectos-secundarios-side-effects)
12. [StateFlow y SharedFlow](#12-stateflow-y-sharedflow)
13. [Carga de Imágenes desde la Red (Coil)](#13-carga-de-imágenes-desde-la-red-coil)
14. [Componentes Comunes de Material 3](#14-componentes-comunes-de-material-3)
15. [Navegación entre Pantallas](#15-navegación-entre-pantallas)
16. [Arquitectura: ViewModel y UI State](#16-arquitectura-viewmodel-y-ui-state)
17. [Previews](#17-previews)
18. [Buenas Prácticas](#18-buenas-prácticas)
19. [Errores Comunes del Trainee](#19-errores-comunes-del-trainee)
20. [Glosario de Términos](#20-glosario-de-términos)

---

## 1. Introducción: ¿Qué es un Composable?

Un **Composable** es una función de Kotlin anotada con `@Composable` que describe una porción de interfaz de usuario. A diferencia de una función tradicional, un composable no retorna una vista: al ejecutarse, **emite** la UI que describe dentro del árbol de composición.

```kotlin
@Composable
fun Saludo(nombre: String) {
    Text(text = "Hola, $nombre")
}
```

Convenciones y reglas:

- Los composables se **anidan**: un `Column` puede contener varios `Text`, que a su vez pueden estar dentro de un `Card`, y así sucesivamente.
- El nombre de la función se escribe en **PascalCase** (`PerfilUsuario`, no `perfilUsuario`), ya que conceptualmente representa un elemento de UI, no una acción.
- Los composables deben ser **idempotentes**: dado el mismo conjunto de parámetros, deben producir siempre la misma UI. Compose puede ejecutar un composable múltiples veces, o saltarse su ejecución, como parte de sus optimizaciones internas, por lo que no se debe depender de efectos colaterales (llamadas de red, escritura en base de datos) dentro del cuerpo de la función. Ese tipo de trabajo se maneja mediante los mecanismos descritos en [Efectos Secundarios](#11-efectos-secundarios-side-effects).

---

## 2. Sistema de Diseño: Material 3 y Theming

Para que la aplicación sea visualmente consistente, se deben usar los **tokens** definidos por el tema en lugar de valores fijos. Esto permite que el modo oscuro y los eventuales cambios de marca se apliquen automáticamente, sin modificar cada pantalla de forma individual.

### Colores: `MaterialTheme.colorScheme`

| Token | Uso |
| :--- | :--- |
| `.primary` | Color de marca y acciones principales (botones destacados). |
| `.onPrimary` | Texto o iconos ubicados sobre `.primary`. |
| `.secondary` / `.tertiary` | Acentos secundarios. |
| `.background` | Fondo general de la pantalla. |
| `.surface` | Fondo de tarjetas, hojas y menús. |
| `.onBackground` / `.onSurface` | Texto principal sobre fondo o superficie. |
| `.onSurfaceVariant` | Texto secundario. |
| `.outline` | Bordes y divisores. |
| `.primaryContainer` / `.onPrimaryContainer` | Contenedores de énfasis moderado (chips, etiquetas). |
| `.error` / `.onError` | Estados de error. |

Regla general: el color `onX` se aplica a elementos ubicados encima de un fondo pintado con `X`. Por ejemplo, si el fondo usa `.primary`, el texto sobre ese fondo debe usar `.onPrimary`. Esto garantiza el contraste adecuado tanto en modo claro como en modo oscuro.

```kotlin
Text(
    text = "Acción importante",
    color = MaterialTheme.colorScheme.primary
)
```

### Tipografía: `MaterialTheme.typography`

| Token | Uso típico |
| :--- | :--- |
| `.displayLarge` / `.displayMedium` / `.displaySmall` | Títulos de gran tamaño (pantallas destacadas). |
| `.headlineMedium` | Títulos de pantalla. |
| `.titleLarge` | Títulos de tarjeta o sección. |
| `.bodyLarge` / `.bodyMedium` | Cuerpo de texto. |
| `.labelMedium` / `.labelSmall` | Etiquetas, chips, texto de botones. |

```kotlin
Text("Configuración", style = MaterialTheme.typography.headlineMedium)
```

### Formas: `MaterialTheme.shapes`

| Token | Uso típico |
| :--- | :--- |
| `.small` | Botones, chips. |
| `.medium` | Tarjetas, contenedores. |
| `.large` | Diálogos, bottom sheets. |

```kotlin
Card(shape = MaterialTheme.shapes.medium) { /* contenido */ }
```

### Modo oscuro y elevación

El modo oscuro funciona correctamente solo si se usan tokens del tema; cualquier color fijo (por ejemplo, `Color.White`) es un punto que puede romperse visualmente al cambiar a modo oscuro. En Material 3, la elevación no se representa únicamente con sombra: en modo oscuro también aclara la superficie. Se recomienda usar `CardDefaults.cardElevation(2.dp)` en lugar de sombras manuales.

Evitar valores fijos:
```kotlin
color = Color(0xFFFF5722)
```

Preferir tokens del tema:
```kotlin
color = MaterialTheme.colorScheme.primary
```

---

## 3. El Modificador (Modifier)

`Modifier` configura tamaño, espaciado, fondo, comportamiento ante clics, entre otros aspectos de un composable. Los modificadores se encadenan, y **el orden en que se aplican afecta el resultado**, ya que cada modificador envuelve al anterior.

| Modificador | Caso de uso | Ejemplo |
| :--- | :--- | :--- |
| `.padding(dp)` | Espacio alrededor del elemento. | `.padding(16.dp)` |
| `.padding(horizontal=, vertical=)` | Padding por eje. | `.padding(horizontal = 16.dp)` |
| `.fillMaxWidth()` | Ocupa todo el ancho disponible. | `.fillMaxWidth()` |
| `.fillMaxHeight()` / `.fillMaxSize()` | Ocupa todo el alto, o toda la pantalla. | `.fillMaxSize()` |
| `.height(dp)` / `.width(dp)` | Tamaño fijo en un eje. | `.height(8.dp)` |
| `.size(dp)` | Tamaño fijo cuadrado (iconos, avatares). | `.size(48.dp)` |
| `.background(color, shape)` | Fondo de color, con forma opcional. | `.background(Color.Red, CircleShape)` |
| `.clip(shape)` | Recorta el contenido (por ejemplo, imágenes circulares). | `.clip(CircleShape)` |
| `.border(width, color, shape)` | Borde. | `.border(1.dp, Color.Gray, RoundedCornerShape(4.dp))` |
| `.clickable { }` | Hace pulsable cualquier elemento. | `.clickable { onClick() }` |
| `.weight(1f)` | Dentro de `Column`/`Row`, reparte el espacio sobrante. | `.weight(1f)` |
| `.aspectRatio(ratio)` | Mantiene una proporción de aspecto. | `.aspectRatio(16f / 9f)` |
| `.alpha(float)` | Transparencia (0f a 1f). | `.alpha(0.5f)` |
| `.wrapContentSize()` | El elemento ocupa solo el espacio que necesita. | `.wrapContentSize()` |

### El orden de los modificadores afecta el resultado

Los siguientes dos bloques usan los mismos modificadores, pero producen resultados distintos:

```kotlin
// A: padding antes de background -> el color no cubre el área del padding
Modifier
    .padding(16.dp)
    .background(Color.Red)

// B: padding después de background -> el color cubre toda el área, incluido el padding
Modifier
    .background(Color.Red)
    .padding(16.dp)
```

Un `padding` colocado antes de otro modificador reduce el área disponible para los modificadores que le siguen en la cadena. El mismo razonamiento aplica a `.clickable`: su posición determina si el área pulsable incluye o no ese padding.

### Combinaciones frecuentes

```kotlin
// Avatar circular recortado
Modifier
    .size(48.dp)
    .clip(CircleShape)

// Contenedor cuyas esquinas recortan también el contenido interno
Modifier
    .clip(MaterialTheme.shapes.medium)
    .background(MaterialTheme.colorScheme.surface)
```

---

## 4. Layouts Básicos: Column, Row y Box

Los tres contenedores fundamentales de Compose son:

- **`Column`**: apila sus elementos verticalmente. El eje principal es vertical.
- **`Row`**: apila sus elementos horizontalmente. El eje principal es horizontal.
- **`Box`**: superpone sus elementos en capas, uno sobre otro.

La distribución se controla con `Arrangement` (eje principal) y `Alignment` (eje cruzado):

```kotlin
Column(
    verticalArrangement = Arrangement.spacedBy(8.dp),
    horizontalAlignment = Alignment.CenterHorizontally
) {
    Text("Usuario")
    Text("usuario@correo.com")
}

Row(
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
) {
    Text("Total")
    Text("$120.00")
}
```

`Arrangement.spacedBy(8.dp)` distribuye espacio uniforme entre los elementos hijos, sin necesidad de insertar un `Spacer` entre cada uno.

### Ejemplo: elemento con icono y texto (Row + Column)

```kotlin
Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier.fillMaxWidth().padding(16.dp)
) {
    Icon(Icons.Default.Person, contentDescription = null)
    Spacer(Modifier.width(12.dp))
    Column {
        Text("Nombre del usuario", style = MaterialTheme.typography.titleMedium)
        Text("Administrador", style = MaterialTheme.typography.bodySmall)
    }
}
```

### Ejemplo: texto superpuesto a un fondo (Box)

```kotlin
Box(modifier = Modifier.fillMaxWidth().height(160.dp)) {
    // capa de fondo (imagen o color)
    Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.primary))
    // capa de texto, alineada abajo a la izquierda
    Text(
        "Bienvenido",
        color = Color.White,
        modifier = Modifier.align(Alignment.BottomStart).padding(16.dp)
    )
}
```

---

## 5. Otros Tipos de Layout: ConstraintLayout, FlowRow y BoxWithConstraints

`Column`, `Row` y `Box` cubren la mayoría de los casos de uso, pero existen escenarios donde conviene recurrir a otros layouts.

### ConstraintLayout

`ConstraintLayout` posiciona elementos mediante restricciones (constraints) relativas entre sí, de forma equivalente al `ConstraintLayout` del sistema de vistas basado en XML. Resulta útil cuando un diseño requiere posiciones complejas que serían difíciles de expresar anidando varios `Column`, `Row` y `Box`, ya que evita el anidamiento profundo y puede reducir el costo de medición en layouts complejos.

Dependencia:
```kotlin
implementation("androidx.constraintlayout:constraintlayout-compose:1.0.1")
```

```kotlin
@Composable
fun PerfilConstraint() {
    ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
        val (foto, nombre, cargo) = createRefs()

        Image(
            painter = painterResource(R.drawable.avatar),
            contentDescription = null,
            modifier = Modifier
                .size(64.dp)
                .constrainAs(foto) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                }
        )
        Text(
            "Ana Torres",
            modifier = Modifier.constrainAs(nombre) {
                top.linkTo(foto.top)
                start.linkTo(foto.end, margin = 12.dp)
            }
        )
        Text(
            "Ingeniera de software",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.constrainAs(cargo) {
                top.linkTo(nombre.bottom)
                start.linkTo(nombre.start)
            }
        )
    }
}
```

Para la mayoría de las pantallas de esta aplicación, `Column`, `Row` y `Box` son suficientes. `ConstraintLayout` se reserva para diseños con relaciones de posición complejas entre múltiples elementos.

### FlowRow y FlowColumn

`FlowRow` distribuye a sus hijos horizontalmente y, cuando no queda espacio disponible en la línea actual, continúa en una nueva línea, de forma similar al ajuste de línea de un texto. `FlowColumn` hace lo equivalente en el eje vertical. Ambos son útiles para listas de chips, etiquetas o tags de longitud variable.

```kotlin
FlowRow(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.spacedBy(8.dp),
    verticalArrangement = Arrangement.spacedBy(8.dp)
) {
    listaDeIngredientes.forEach { ingrediente ->
        AssistChip(onClick = {}, label = { Text(ingrediente) })
    }
}
```

A diferencia de `LazyRow`, `FlowRow` no recicla ni virtualiza sus elementos: todos se componen. Es adecuado para colecciones pequeñas (algunas decenas de elementos), no para listas largas.

### BoxWithConstraints

`BoxWithConstraints` expone las restricciones de tamaño disponibles (`maxWidth`, `maxHeight`) dentro de su contenido, lo que permite adaptar el layout según el espacio disponible; por ejemplo, para definir el número de columnas de una cuadrícula según el ancho de pantalla.

```kotlin
BoxWithConstraints {
    val columnas = if (maxWidth < 600.dp) 2 else 4
    LazyVerticalGrid(columns = GridCells.Fixed(columnas)) {
        items(recetas, key = { it.id }) { RecetaCard(it) }
    }
}
```

`BoxWithConstraints` tiene un costo de medición adicional respecto a un `Box` normal, por lo que se recomienda usarlo solo cuando el contenido realmente necesita adaptarse al espacio disponible.

---

## 6. Listas y Cuadrículas Eficientes (Lazy Lists)

Renderizar una lista larga con `Column` y `forEach` compone todos los elementos aunque no estén visibles en pantalla, lo que degrada el rendimiento. Los composables *lazy* (`LazyColumn`, `LazyRow`, `LazyVerticalGrid`, `LazyHorizontalGrid`) solo componen y miden los elementos visibles, más un margen cercano, de forma equivalente a un `RecyclerView`.

### LazyColumn

```kotlin
LazyColumn(
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp)
) {
    items(
        items = contactos,
        key = { contacto -> contacto.id }
    ) { contacto ->
        ContactoItem(contacto = contacto, onClick = { /* abrir detalle */ })
    }
}
```

- **`key`**: identificador único y estable de cada elemento, no su posición en la lista. Permite a Compose reconocer qué elementos cambiaron, se movieron o se eliminaron, evitando recomposiciones innecesarias y parpadeos visuales al reordenar o filtrar la lista.
- **`contentPadding`**: a diferencia de aplicar `Modifier.padding` directamente al `LazyColumn`, este parámetro añade espacio interno que respeta el desplazamiento (scroll); el contenido puede desplazarse hasta ese padding en lugar de quedar recortado por él.

### LazyRow y LazyVerticalGrid

```kotlin
LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
    items(categorias, key = { it.id }) { categoria ->
        CategoriaChip(categoria)
    }
}

LazyVerticalGrid(
    columns = GridCells.Fixed(2),
    contentPadding = PaddingValues(16.dp),
    horizontalArrangement = Arrangement.spacedBy(12.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp)
) {
    items(productos, key = { it.id }) { producto ->
        ProductoCard(producto)
    }
}
```

`GridCells.Fixed(n)` define un número fijo de columnas. `GridCells.Adaptive(minSize = 128.dp)` calcula automáticamente cuántas columnas caben según el ancho disponible, lo cual resulta útil cuando la aplicación debe soportar distintos tamaños de pantalla.

### itemsIndexed

Cuando se necesita la posición del elemento además del elemento en sí:

```kotlin
LazyColumn {
    itemsIndexed(pasos, key = { _, paso -> paso.id }) { index, paso ->
        Text("${index + 1}. ${paso.descripcion}")
    }
}
```

### Encabezados fijos: stickyHeader

`stickyHeader` mantiene un elemento visible en la parte superior mientras se desplaza el contenido que le sigue. Es útil para listas agrupadas, por ejemplo, ingredientes agrupados por categoría.

```kotlin
LazyColumn {
    ingredientesPorCategoria.forEach { (categoria, ingredientes) ->
        stickyHeader {
            Text(
                categoria,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(8.dp)
            )
        }
        items(ingredientes, key = { it.id }) { ingrediente ->
            IngredienteItem(ingrediente)
        }
    }
}
```

### Control del desplazamiento: LazyListState

`rememberLazyListState()` permite leer y controlar la posición de scroll de forma programática, por ejemplo, para volver al inicio de la lista tras agregar un elemento.

```kotlin
val listState = rememberLazyListState()
val scope = rememberCoroutineScope()

LazyColumn(state = listState) {
    items(recetas, key = { it.id }) { RecetaCard(it) }
}

Button(onClick = { scope.launch { listState.animateScrollToItem(0) } }) {
    Text("Ir al inicio")
}
```

### Recomendaciones adicionales

- Evitar anidar un `LazyColumn` dentro de otro `LazyColumn` con la misma orientación: Compose no puede medir correctamente listas de tamaño indefinido anidadas en el mismo eje. Sí es válido colocar un `LazyRow` dentro de un `item` de un `LazyColumn` (por ejemplo, un carrusel horizontal dentro de una pantalla que se desplaza verticalmente).
- Si la lista mezcla distintos tipos de elementos (encabezados, tarjetas, separadores), especificar `contentType` en `items` ayuda a Compose a reutilizar composiciones de forma más eficiente.
- `Modifier.animateItem()` anima automáticamente los cambios de posición de un elemento dentro de una lista lazy cuando se agregan, eliminan o reordenan elementos.

---

## 7. Estado y Recomposición

### Qué es el estado

El **estado** es cualquier dato que puede cambiar mientras la aplicación está en ejecución: el valor de un contador, el texto escrito en un campo de búsqueda, si un switch está activado, o una lista de tareas pendientes. Un valor que nunca cambia, como un título fijo o una constante, no constituye estado.

### Estado y UI

En Jetpack Compose, la interfaz no se actualiza modificando directamente los elementos ya dibujados, como ocurría al buscar una `TextView` y cambiar su texto en el sistema de vistas basado en XML. En su lugar, la UI se describe como una función del estado:

```
UI = f(estado)
```

Cuando el estado cambia, Compose vuelve a ejecutar las funciones composable que leen ese estado y actualiza únicamente las partes de la pantalla que dependen de él. A este proceso se le llama **recomposición**.

### remember y mutableStateOf

Estas dos piezas cumplen funciones distintas y complementarias:

| Elemento | Función | Consecuencia de omitirlo |
| :--- | :--- | :--- |
| `mutableStateOf(...)` | Crea un contenedor de estado observable. Al modificar su valor, notifica a Compose para que recomponga lo que depende de él. | El valor cambia, pero la UI no se entera: la pantalla no refleja el cambio. |
| `remember { ... }` | Conserva un valor entre recomposiciones, evitando que se reinicie cada vez que la función composable se vuelve a ejecutar. | El valor se reinicia en cada recomposición: el estado parece no conservarse. |

Por esta razón, ambos se usan juntos casi siempre: `remember { mutableStateOf(...) }` crea un valor observable que además persiste entre recomposiciones, aunque no sobrevive a la destrucción y recreación de la actividad, como ocurre al rotar la pantalla (ver más abajo).

```kotlin
@Composable
fun Contador() {
    var cuenta by remember { mutableStateOf(0) }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Button(onClick = { cuenta-- }) { Text("-") }
        Text("$cuenta", modifier = Modifier.padding(horizontal = 16.dp))
        Button(onClick = { cuenta++ }) { Text("+") }
    }
}
```

- El delegado `by` permite escribir `cuenta` directamente en lugar de `cuenta.value`.
- Sin `remember`, `cuenta` volvería a `0` en cada recomposición.
- Sin `mutableStateOf`, modificar `cuenta` no provocaría una recomposición.

### Persistencia ante cambios de configuración: rememberSaveable

`remember` no sobrevive a cambios de configuración, como la rotación de pantalla o el cambio de idioma, porque la actividad se destruye y se vuelve a crear. Cuando el dato debe conservarse en ese escenario, por ejemplo, texto ingresado por el usuario, se utiliza `rememberSaveable`:

```kotlin
var texto by rememberSaveable { mutableStateOf("") }
```

Regla práctica: si perder el valor al rotar la pantalla afectaría la experiencia del usuario, usar `rememberSaveable`; en caso contrario, `remember` es suficiente.

Las distintas variantes de `mutableStateOf` para otros tipos de datos se explican en la siguiente sección.

---

## 8. mutableStateOf y sus Variantes

`mutableStateOf` es la forma más común de crear estado observable en Compose, pero no es la única. Compose ofrece variantes especializadas para distintos tipos de datos, orientadas a mejorar la legibilidad y, en algunos casos, el rendimiento.

### mutableStateOf&lt;T&gt;

Contenedor de estado genérico para un único valor de cualquier tipo.

```kotlin
var nombre by remember { mutableStateOf("") }
var esFavorito by remember { mutableStateOf(false) }
var usuario by remember { mutableStateOf<Usuario?>(null) }
```

Cada vez que se asigna un nuevo valor, Compose programa una recomposición de las funciones que leen ese estado.

### Variantes primitivas: mutableIntStateOf, mutableLongStateOf, mutableFloatStateOf, mutableDoubleStateOf

`mutableStateOf<Int>()` almacena internamente el valor como un objeto (autoboxing), lo que tiene un costo de memoria y rendimiento ligeramente mayor. Para tipos primitivos de uso frecuente, Compose ofrece contenedores especializados que evitan el autoboxing:

```kotlin
var cantidad by remember { mutableIntStateOf(0) }
var precio by remember { mutableDoubleStateOf(0.0) }
```

Se recomienda usar estas variantes en lugar de `mutableStateOf<Int>()`, `mutableStateOf<Long>()`, `mutableStateOf<Float>()` o `mutableStateOf<Double>()` cuando el tipo del estado corresponde a uno de esos primitivos. Para el resto de los tipos (`String`, `Boolean`, clases de datos, tipos anulables), `mutableStateOf` genérico es la opción correcta.

### Colecciones observables: mutableStateListOf y mutableStateMapOf

Un `remember { mutableStateOf(listOf(...)) }` observa **la referencia** a la lista, no su contenido. Si la lista se modifica con `add` o `remove` sin reasignar la variable, Compose no detecta el cambio. Existen dos alternativas:

Reasignar una lista inmutable:
```kotlin
var tareas by remember { mutableStateOf(listOf<Tarea>()) }
tareas = tareas + nuevaTarea   // crea una nueva lista y reasigna la variable
```

Usar una lista observable:
```kotlin
val tareas = remember { mutableStateListOf<Tarea>() }
tareas.add(nuevaTarea)   // modifica en el lugar; Compose lo detecta igualmente
```

`mutableStateListOf` y `mutableStateMapOf` implementan `SnapshotStateList` y `SnapshotStateMap` respectivamente: son colecciones cuyas operaciones de modificación (`add`, `remove`, `put`, `clear`, entre otras) notifican directamente a Compose, sin necesidad de reasignar la variable.

```kotlin
val seleccionados = remember { mutableStateMapOf<String, Boolean>() }
seleccionados["ingrediente_1"] = true
```

En pantallas completas administradas por un `ViewModel`, la práctica recomendada es exponer listas inmutables dentro de un `UiState` (ver [Arquitectura](#16-arquitectura-viewmodel-y-ui-state)) en lugar de usar `mutableStateListOf` directamente en la UI. Estas variantes resultan más útiles para estado local, acotado a un composable específico.

### Estado derivado: derivedStateOf

Cuando un valor se calcula a partir de otro estado, no debe almacenarse como un estado independiente, ya que ambos podrían desincronizarse. En su lugar, se deriva:

```kotlin
val items = remember { mutableStateListOf<String>() }
val hayItems by remember { derivedStateOf { items.isNotEmpty() } }
```

`derivedStateOf` también evita recomposiciones innecesarias: si `items` cambia de tamaño pero el resultado de `isNotEmpty()` no cambia, las funciones que leen `hayItems` no se recomponen.

### snapshotFlow: de estado a Flow

`snapshotFlow` convierte lecturas de `State` en un `Flow`, lo que resulta útil para reaccionar a cambios de estado con los operadores de Kotlin Flow (`debounce`, `distinctUntilChanged`, `filter`, entre otros).

```kotlin
LaunchedEffect(listState) {
    snapshotFlow { listState.firstVisibleItemIndex }
        .distinctUntilChanged()
        .collect { indice -> /* reaccionar al cambio de posición */ }
}
```

### Resumen de variantes

| Función | Tipo de dato | Cuándo usarla |
| :--- | :--- | :--- |
| `mutableStateOf<T>()` | Cualquier tipo (String, Boolean, data class, tipos anulables) | Caso general. |
| `mutableIntStateOf()` | Int | Contadores, índices, cantidades. |
| `mutableLongStateOf()` | Long | Timestamps, identificadores numéricos grandes. |
| `mutableFloatStateOf()` | Float | Valores de animación, porcentajes. |
| `mutableDoubleStateOf()` | Double | Cálculos con decimales (precios, promedios). |
| `mutableStateListOf<T>()` | Lista mutable | Colecciones locales modificadas con `add`/`remove` sin reasignar la variable. |
| `mutableStateMapOf<K, V>()` | Mapa mutable | Selecciones o asociaciones clave-valor locales. |
| `derivedStateOf { }` | Calculado a partir de otro estado | Evitar guardar por separado valores que pueden calcularse. |

---

## 9. State Hoisting (Elevación de Estado)

**State hoisting** consiste en trasladar el estado de un composable hacia el composable que lo invoca, de modo que el composable hijo deja de ser dueño del dato: únicamente lo recibe como parámetro y notifica eventos hacia arriba.

El patrón se resume así: **el estado desciende, los eventos ascienden**. El dato baja como parámetro; el evento sube como una función lambda.

```kotlin
// Stateful: el estado vive dentro del composable
@Composable
fun CampoBusqueda() {
    var texto by remember { mutableStateOf("") }
    TextField(value = texto, onValueChange = { texto = it })
}

// Stateless: el estado vive en el padre; este composable solo muestra y notifica
@Composable
fun CampoBusqueda(
    valor: String,
    onValorCambia: (String) -> Unit
) {
    TextField(value = valor, onValueChange = onValorCambia)
}

// El padre es dueño del estado
@Composable
fun PantallaBusqueda() {
    var query by rememberSaveable { mutableStateOf("") }
    CampoBusqueda(valor = query, onValorCambia = { query = it })
}
```

Ventajas de un composable stateless:

- **Reutilizable** en distintas pantallas, ya que no impone dónde vive el estado.
- **Testeable y previsualizable**, porque basta con pasarle datos de ejemplo.
- **Predecible**, dado que no mantiene estado oculto.

Regla práctica: los composables deben ser stateless por defecto, y el estado debe residir lo más arriba posible en la jerarquía, idealmente en un `ViewModel` para pantallas completas (ver [Arquitectura](#16-arquitectura-viewmodel-y-ui-state)).

---

## 10. Eventos e Interacción

Los eventos viajan hacia arriba en forma de **lambdas**. El composable no decide qué hacer ante un evento; recibe una función y la invoca cuando corresponde.

```kotlin
@Composable
fun BotonFavorito(
    esFavorito: Boolean,
    onToggle: () -> Unit
) {
    IconButton(onClick = onToggle) {
        Icon(
            imageVector = if (esFavorito) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
            contentDescription = if (esFavorito) "Quitar de favoritos" else "Agregar a favoritos"
        )
    }
}
```

- `onClick: () -> Unit`: no recibe ni devuelve nada.
- `onValueChange: (String) -> Unit`: recibe el nuevo valor.
- `onItemClick: (Item) -> Unit`: recibe el elemento seleccionado.

Los parámetros de evento se nombran comenzando con `on` (`onClick`, `onSubmit`, `onDismiss`).

---

## 11. Efectos Secundarios (Side Effects)

Algunas acciones no consisten en dibujar UI: llamadas de red, mostrar un Snackbar, iniciar una animación o escribir en disco. Este tipo de trabajo no debe ejecutarse directamente en el cuerpo de un composable, ya que se repetiría en cada recomposición. Para ello existen los *effect handlers*.

| Effect | Cuándo usarlo |
| :--- | :--- |
| `LaunchedEffect(key)` | Ejecuta una corrutina cuando el composable entra en composición, o nuevamente cuando cambia `key`. Ejemplo: cargar datos al abrir una pantalla. |
| `rememberCoroutineScope()` | Provee un `CoroutineScope` para lanzar corrutinas en respuesta a un evento puntual, por ejemplo, un clic que muestra un Snackbar. |
| `DisposableEffect(key)` | Registra un recurso (listener, observer) y garantiza su limpieza cuando el composable sale de composición o cuando cambia `key`. |
| `rememberUpdatedState(x)` | Mantiene una referencia actualizada a un valor dentro de un efecto de larga duración, sin reiniciar dicho efecto. |
| `SideEffect { }` | Ejecuta código no observable por Compose después de cada recomposición exitosa; útil para sincronizar estado con librerías externas. |

```kotlin
@Composable
fun PantallaDetalle(id: String, viewModel: DetalleViewModel) {
    LaunchedEffect(id) {
        viewModel.cargarDetalle(id)
    }
    // resto de la UI
}
```

Para el uso cotidiano, `LaunchedEffect` (cargar datos al entrar a una pantalla) y `rememberCoroutineScope` (reaccionar a un evento puntual) cubren la mayoría de los casos. Los demás effect handlers se incorporan gradualmente conforme se presentan escenarios más específicos.

---

## 12. StateFlow y SharedFlow

En una arquitectura basada en `ViewModel`, el estado de la pantalla suele exponerse mediante un `Flow`. Kotlin Flow ofrece dos variantes especialmente relevantes para la UI: `StateFlow` y `SharedFlow`.

### Flow frío frente a flujo caliente

Un `Flow` estándar es **frío**: no produce valores hasta que alguien lo colecta, y cada colector dispara su propia ejecución desde el inicio. `StateFlow` y `SharedFlow` son **calientes**: existen y emiten valores de forma independiente a si hay o no colectores activos, y varios colectores comparten la misma emisión.

### StateFlow

`StateFlow` es un flujo caliente que siempre contiene un valor actual y lo expone mediante la propiedad `.value`. Es la herramienta estándar para exponer el estado de una pantalla desde un `ViewModel`.

```kotlin
class RecetasViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(RecetasUiState())
    val uiState: StateFlow<RecetasUiState> = _uiState.asStateFlow()

    fun cargarRecetas() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val resultado = repositorio.obtenerRecetas()
            _uiState.update { it.copy(recetas = resultado, isLoading = false) }
        }
    }
}
```

Convenciones de este patrón:

- El `MutableStateFlow` es **privado** (`_uiState`); solo el `ViewModel` puede modificarlo.
- Se expone una versión **de solo lectura** (`StateFlow`, mediante `.asStateFlow()`); la UI únicamente puede observarla, nunca modificarla directamente.
- `.update { }` aplica una transformación atómica sobre el valor actual, evitando condiciones de carrera si dos corrutinas intentan actualizar el estado al mismo tiempo.

### Consumir un StateFlow en Compose

```kotlin
@Composable
fun RecetasScreen(viewModel: RecetasViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    when {
        state.isLoading -> CircularProgressIndicator()
        state.error != null -> Text(state.error)
        else -> ListaRecetas(recetas = state.recetas)
    }
}
```

Existen dos funciones para convertir un `StateFlow` en un `State` observable por Compose:

| Función | Comportamiento |
| :--- | :--- |
| `collectAsState()` | Colecta el flujo mientras el composable esté en composición, sin considerar el ciclo de vida de la pantalla. |
| `collectAsStateWithLifecycle()` | Colecta el flujo únicamente cuando el ciclo de vida está al menos en estado `STARTED`; se detiene automáticamente cuando la aplicación pasa a segundo plano. |

`collectAsStateWithLifecycle()` es la opción recomendada en aplicaciones Android, ya que evita seguir colectando datos, y consumiendo recursos, cuando la pantalla no está visible. Requiere la dependencia `androidx.lifecycle:lifecycle-runtime-compose`.

### SharedFlow

`SharedFlow` es un flujo caliente de propósito más general, sin un valor "actual" garantizado. Se utiliza para modelar **eventos** que deben ocurrir una única vez, por ejemplo, navegar a otra pantalla o mostrar un Snackbar, a diferencia del estado persistente de pantalla, que corresponde a `StateFlow`.

```kotlin
class LoginViewModel : ViewModel() {
    private val _eventos = MutableSharedFlow<LoginEvento>()
    val eventos: SharedFlow<LoginEvento> = _eventos.asSharedFlow()

    fun onLoginExitoso() {
        viewModelScope.launch {
            _eventos.emit(LoginEvento.NavegarAInicio)
        }
    }
}

sealed interface LoginEvento {
    object NavegarAInicio : LoginEvento
}
```

```kotlin
@Composable
fun LoginScreen(viewModel: LoginViewModel, onNavegarAInicio: () -> Unit) {
    LaunchedEffect(Unit) {
        viewModel.eventos.collect { evento ->
            when (evento) {
                is LoginEvento.NavegarAInicio -> onNavegarAInicio()
            }
        }
    }
    // resto de la UI
}
```

### StateFlow frente a SharedFlow: cuándo usar cada uno

| Escenario | Herramienta |
| :--- | :--- |
| Estado que la pantalla debe reflejar en todo momento (lista de datos, indicador de carga, texto de un campo) | `StateFlow` |
| Evento que debe ocurrir una sola vez y no debe repetirse al recomponer o rotar la pantalla (navegación, Snackbar, Toast) | `SharedFlow` |

Usar `StateFlow` para eventos de un solo uso es un error frecuente: como siempre conserva el último valor, un evento de navegación podría volver a dispararse al recomponer la pantalla si no se maneja con cuidado. `SharedFlow`, configurado sin *replay* (su valor por defecto), no reproduce el último evento a nuevos colectores, lo que lo hace más apropiado para este caso.

### Otras operaciones útiles

- **`combine`**: combina múltiples flujos en uno solo. Útil cuando el estado de la UI depende de más de una fuente de datos.
- **`map`**: transforma cada valor emitido por un flujo.
- **`stateIn(scope, started, initialValue)`**: convierte un `Flow` genérico, por ejemplo, proveniente de una base de datos, en un `StateFlow`, ejecutándose dentro de un `CoroutineScope` (normalmente `viewModelScope`).

```kotlin
val uiState: StateFlow<RecetasUiState> = repositorio.observarRecetas()
    .map { recetas -> RecetasUiState(recetas = recetas) }
    .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = RecetasUiState(isLoading = true)
    )
```

---

## 13. Carga de Imágenes desde la Red (Coil)

`Image(painterResource(...))` se utiliza solo para recursos locales (drawables). Para fotos que provienen de una **URL**, se utiliza **Coil**, la librería estándar de carga de imágenes en Compose.

Dependencia (`build.gradle.kts`):
```kotlin
implementation("io.coil-kt:coil-compose:2.6.0")
```

Uso:
```kotlin
AsyncImage(
    model = "https://ejemplo.com/foto.jpg",
    contentDescription = "Foto de perfil",
    contentScale = ContentScale.Crop,
    modifier = Modifier
        .size(72.dp)
        .clip(CircleShape)
)
```

Coil administra la descarga, el caché y la carga asíncrona de la imagen. Para mostrar un placeholder mientras la imagen carga, se utiliza `SubcomposeAsyncImage`.

---

## 14. Componentes Comunes de Material 3

| Componente | Propósito | Snippet mínimo |
| :--- | :--- | :--- |
| `Text` | Mostrar texto. | `Text("Hola")` |
| `Button` | Acción principal. | `Button(onClick = {}) { Text("Guardar") }` |
| `OutlinedButton` / `TextButton` | Acciones secundarias. | `TextButton(onClick = {}) { Text("Cancelar") }` |
| `IconButton` | Botón compuesto solo por un icono. | `IconButton(onClick = {}) { Icon(...) }` |
| `TextField` / `OutlinedTextField` | Entrada de texto. | `OutlinedTextField(value, onValueChange = {})` |
| `Card` | Contenedor con superficie y elevación. | `Card { /* contenido */ }` |
| `Icon` | Iconos vectoriales. | `Icon(Icons.Default.Home, contentDescription = null)` |
| `Checkbox` / `Switch` | Selección booleana. | `Switch(checked, onCheckedChange = {})` |
| `CircularProgressIndicator` | Indicador de carga. | `CircularProgressIndicator()` |
| `HorizontalDivider` | Línea separadora. | `HorizontalDivider()` |
| `Spacer` | Espacio vacío. | `Spacer(Modifier.height(8.dp))` |

### Estructura de pantalla con Scaffold

`Scaffold` provee los espacios estándar de una pantalla: barra superior, barra inferior, botón de acción flotante y contenido.

```kotlin
Scaffold(
    topBar = {
        TopAppBar(title = { Text("Inicio") })
    },
    floatingActionButton = {
        FloatingActionButton(onClick = { /* nuevo */ }) {
            Icon(Icons.Default.Add, contentDescription = "Agregar")
        }
    }
) { innerPadding ->
    LazyColumn(modifier = Modifier.padding(innerPadding)) {
        // contenido
    }
}
```

**Importante:** el parámetro `innerPadding` no es opcional. Si no se aplica al contenido, este queda tapado por las barras superior o inferior.

---

## 15. Navegación entre Pantallas

Con **Navigation Compose** se define un `NavHost` con rutas (strings) y se navega mediante un `NavController`.

Dependencia:
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
                navController.navigate("detalle/$id")
            })
        }
        composable("detalle/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")
            DetalleScreen(id = id)
        }
    }
}
```

- `navController.navigate("ruta")`: navega hacia una pantalla.
- `navController.popBackStack()`: regresa a la pantalla anterior.
- Se recomienda pasar **identificadores** como argumento de navegación, no objetos completos.

---

## 16. Arquitectura: ViewModel y UI State

Cuando una pantalla crece en complejidad, la lógica y las llamadas de red no deben colocarse dentro del composable. La separación recomendada es la siguiente:

1. **ViewModel**: contiene la lógica de la pantalla y expone su estado. Sobrevive a cambios de configuración.
2. **UI State**: una `data class` que describe todo lo que la pantalla necesita mostrar.
3. **Composable**: únicamente observa el estado y lo dibuja.

```kotlin
// 1. Un solo objeto que describe la pantalla
data class TareasUiState(
    val tareas: List<Tarea> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

// 2. El ViewModel expone el estado como flujo de solo lectura
class TareasViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(TareasUiState())
    val uiState: StateFlow<TareasUiState> = _uiState.asStateFlow()

    fun cargarTareas() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            // obtener tareas...
            _uiState.update { it.copy(tareas = resultado, isLoading = false) }
        }
    }
}

// 3. La UI únicamente refleja el estado
@Composable
fun TareasScreen(viewModel: TareasViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    when {
        state.isLoading -> CircularProgressIndicator()
        state.error != null -> Text(state.error!!)
        else -> ListaTareas(tareas = state.tareas)
    }
}
```

Este patrón se apoya en `StateFlow`, descrito en detalle en [StateFlow y SharedFlow](#12-stateflow-y-sharedflow).

**Flujo de datos unidireccional (UDF):** el estado desciende del `ViewModel` hacia la UI; los eventos ascienden de la UI hacia el `ViewModel`, mediante la invocación de sus funciones. La UI nunca modifica el estado directamente; su responsabilidad se limita a reflejarlo.

---

## 17. Previews

No es necesario recompilar ni reinstalar la aplicación para ver un cambio de UI: `@Preview` permite visualizarlo directamente en el panel correspondiente de Android Studio.

```kotlin
@Preview(showBackground = true)
@Composable
private fun ContadorPreview() {
    MiAppTheme {
        Contador()
    }
}
```

Recomendaciones:

- `@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)` previsualiza el modo oscuro.
- `@Preview(name = "Pantalla pequeña", widthDp = 320)` previsualiza otros tamaños de pantalla.
- El valor del state hoisting se hace evidente aquí: un composable stateless con datos de ejemplo se previsualiza sin dependencias adicionales; uno que carga datos de la red, no.
- En los previews se deben usar **datos de ejemplo** definidos localmente, nunca llamadas reales a red o base de datos.

---

## 18. Buenas Prácticas

1. **Separación entre elementos**: usar `Spacer(Modifier.height(8.dp))` o `Arrangement.spacedBy(8.dp)` en lugar de combinaciones complejas de padding.
2. **Reutilización**: si un diseño se repite (un chip, una etiqueta), extraerlo a su propio composable.
3. **Imágenes**: usar `contentScale = ContentScale.Crop` para que las imágenes llenen su espacio sin deformarse.
4. **Stateless por defecto**: pasar datos por parámetro y elevar los eventos mediante lambdas (`onClick: () -> Unit`).
5. **Accesibilidad**: definir `contentDescription` en imágenes e iconos con significado; usar `null` cuando son puramente decorativos.
6. **Composables pequeños**: si una función supera aproximadamente 40 a 50 líneas, generalmente conviene dividirla.
7. **dp frente a sp**: usar `dp` para tamaños y espacios; usar `sp` para texto, ya que respeta el ajuste de fuente configurado por el usuario.
8. **Alcance de la lectura de estado**: leer el estado lo más cerca posible de donde se utiliza, para mantener acotado el alcance de la recomposición.
9. **Modifier como parámetro**: los composables reutilizables deben aceptar `modifier: Modifier = Modifier` y aplicarlo al elemento raíz.

---

## 19. Errores Comunes del Trainee

| Síntoma | Causa típica | Solución |
| :--- | :--- | :--- |
| El valor se reinicia solo | Falta `remember` | `var x by remember { mutableStateOf(...) }` |
| Se cambia el dato y la pantalla no reacciona | No es un `State` (es una variable normal) | Usar `mutableStateOf` |
| El valor se pierde al rotar la pantalla | `remember` no sobrevive a cambios de configuración | Usar `rememberSaveable` |
| El contenido queda tapado por la barra superior o inferior | No se aplicó `innerPadding` del `Scaffold` | `Modifier.padding(innerPadding)` |
| La lista larga se desplaza con lentitud | Uso de `Column` con `forEach` | Cambiar a `LazyColumn` con `items` |
| La lista parpadea al borrar o reordenar elementos | Falta `key` en `items` | Definir un `key` único y estable |
| El fondo no cubre todo el elemento | Orden incorrecto de modificadores | Aplicar `.background()` antes de `.padding()` |
| La pantalla se ve bien en modo claro pero no en oscuro | Uso de colores fijos | Usar `MaterialTheme.colorScheme` |
| La imagen se muestra deformada | Falta `contentScale` | Usar `ContentScale.Crop` |
| La llamada de red se repite sin control | Se colocó directamente en el cuerpo del composable | Encapsularla en `LaunchedEffect(key)` |
| Un evento de navegación se repite al rotar la pantalla | Se modeló el evento con `StateFlow` en lugar de `SharedFlow` | Usar `SharedFlow` para eventos de un solo uso |
| El composable no se puede reutilizar en otra pantalla | El composable es stateful | Aplicar state hoisting |

---

## 20. Glosario de Términos

- **Composable**: función marcada con `@Composable` que describe una porción de UI.
- **Recomposición**: proceso mediante el cual Compose vuelve a ejecutar un composable porque un estado del que depende cambió.
- **Estado (State)**: dato que puede cambiar en el tiempo y que, al hacerlo, dispara una recomposición. Se crea con `mutableStateOf` o alguna de sus variantes.
- **`remember`**: conserva un valor entre recomposiciones para que no se reinicie.
- **`rememberSaveable`**: equivalente a `remember`, pero además sobrevive a cambios de configuración (rotación, cambio de idioma).
- **`mutableStateOf`**: crea un contenedor de estado observable; al cambiar su valor, notifica a Compose.
- **`mutableIntStateOf` / `mutableLongStateOf` / `mutableFloatStateOf` / `mutableDoubleStateOf`**: variantes de `mutableStateOf` especializadas en tipos primitivos, que evitan el autoboxing.
- **`mutableStateListOf` / `mutableStateMapOf`**: colecciones observables cuyas operaciones de modificación notifican directamente a Compose.
- **`derivedStateOf`**: estado calculado a partir de otro estado.
- **`snapshotFlow`**: convierte lecturas de `State` en un `Flow`.
- **State Hoisting**: acción de trasladar el estado de un composable hacia su padre, dejando al hijo como stateless.
- **Stateless / Stateful**: sin estado propio, frente a con estado propio. Se prefiere el diseño stateless.
- **UDF (Flujo Unidireccional de Datos)**: patrón en el que el estado desciende y los eventos ascienden.
- **Modifier**: cadena de configuraciones (tamaño, padding, fondo, comportamiento ante clics) aplicada a un composable. El orden de aplicación es relevante.
- **`Scaffold`**: estructura base de una pantalla (barra superior, barra inferior, botón de acción flotante, contenido).
- **`innerPadding`**: espacio reservado por `Scaffold` para sus barras; debe aplicarse al contenido de la pantalla.
- **`LazyColumn` / `LazyRow` / `LazyVerticalGrid`**: listas y cuadrículas que solo componen los elementos visibles.
- **`key` (en listas)**: identificador único y estable de cada elemento; mejora el rendimiento y evita parpadeos visuales.
- **`stickyHeader`**: elemento de una lista lazy que permanece fijo en la parte superior mientras se desplaza el contenido siguiente.
- **`LazyListState`**: objeto que expone y permite controlar la posición de desplazamiento de una lista lazy.
- **`GridCells`**: configuración del número de columnas de una `LazyVerticalGrid` (`Fixed` o `Adaptive`).
- **`Arrangement`**: distribución de los elementos hijos en el eje principal de un `Column` o `Row`.
- **`Alignment`**: alineación de los elementos hijos en el eje cruzado.
- **`ConstraintLayout`**: layout que posiciona elementos mediante restricciones relativas entre sí.
- **`FlowRow` / `FlowColumn`**: layouts que distribuyen sus hijos en línea y continúan en una nueva línea cuando el espacio se agota.
- **`BoxWithConstraints`**: layout que expone el espacio disponible para adaptar su contenido.
- **Side Effect (efecto secundario)**: acción que no consiste en dibujar UI (red, disco, animación). Se ejecuta dentro de un effect handler.
- **`LaunchedEffect`**: ejecuta una corrutina al entrar el composable en composición, o al cambiar su `key`.
- **`rememberCoroutineScope`**: provee un alcance para lanzar corrutinas desde eventos puntuales, como un clic.
- **`ViewModel`**: clase que administra el estado y la lógica de una pantalla; sobrevive a cambios de configuración.
- **`Flow`**: secuencia asíncrona de valores de Kotlin Coroutines. Por defecto es fría: no emite hasta que se colecta.
- **`StateFlow`**: flujo caliente que siempre mantiene un valor actual; se utiliza para exponer estado de pantalla.
- **`SharedFlow`**: flujo caliente de propósito general, utilizado habitualmente para modelar eventos de un solo uso.
- **`collectAsStateWithLifecycle`**: convierte un `StateFlow` en un `State` que Compose puede observar, respetando el ciclo de vida de la pantalla.
- **UI State**: `data class` que describe todo lo que una pantalla necesita mostrar.
- **Token de diseño**: valor del tema (`colorScheme`, `typography`, `shapes`) utilizado en lugar de valores fijos.
- **`dp`**: unidad de tamaño y espacio independiente de la densidad de pantalla.
- **`sp`**: unidad para texto que respeta el ajuste de fuente configurado por el usuario.
- **Coil / `AsyncImage`**: librería y composable utilizados para cargar imágenes desde una URL.
- **`@Preview`**: muestra un composable en Android Studio sin necesidad de ejecutar la aplicación.

---

Ante cualquier duda durante el desarrollo, conviene partir de dos preguntas: primero, de qué estado depende lo que se quiere mostrar; segundo, quién debe ser el dueño de ese estado. La mayoría de los problemas de comportamiento en Compose se resuelven respondiendo estas dos preguntas.
