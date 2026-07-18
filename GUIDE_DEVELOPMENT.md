# 📘 Jetpack Compose — Documento de Referencia para Trainees

Documento de consulta de términos, conceptos y patrones para desarrollar UI en Android con **Jetpack Compose** y **Material 3**. Pensado para tenerlo abierto al lado mientras programas.

> **La idea que cambia todo (léela antes de seguir):** en Compose tú **no modificas** la pantalla, la **describes** según un dato (el *estado*). Cuando el dato cambia, Compose vuelve a dibujar lo que dependa de él. Si interiorizas esto, el 80% de los bugs raros desaparecen.

---

## 📑 Índice
1. [Conceptos base: ¿qué es un Composable?](#-1-conceptos-base-qué-es-un-composable)
2. [Sistema de diseño (Theming / Material 3)](#-2-sistema-de-diseño-theming--material-3)
3. [El Modificador (Modifier)](#-3-el-modificador-modifier)
4. [Layouts base (Column, Row, Box)](#-4-layouts-base-column-row-box)
5. [Listas eficientes (LazyColumn / LazyRow / Grid)](#-5-listas-eficientes-lazycolumn--lazyrow--grid)
6. [Estado y Recomposición (lo más importante)](#-6-estado-y-recomposición-lo-más-importante)
7. [State Hoisting (elevar el estado)](#-7-state-hoisting-elevar-el-estado)
8. [Eventos e interacción](#-8-eventos-e-interacción)
9. [Efectos secundarios (Side Effects)](#-9-efectos-secundarios-side-effects)
10. [Imágenes desde internet (Coil)](#-10-imágenes-desde-internet-coil)
11. [Componentes comunes de Material 3](#-11-componentes-comunes-de-material-3)
12. [Navegación entre pantallas](#-12-navegación-entre-pantallas)
13. [Arquitectura (ViewModel + UI State)](#-13-arquitectura-viewmodel--ui-state)
14. [Previews](#-14-previews)
15. [Consejos pro](#-15-consejos-pro)
16. [Errores comunes del trainee](#-16-errores-comunes-del-trainee)
17. [Glosario rápido de términos](#-17-glosario-rápido-de-términos)

---

<a name="1"></a>
## 🧩 1. Conceptos base: ¿qué es un Composable?

Un **Composable** es una función que describe un pedazo de interfaz. Se marca con la anotación `@Composable`.

```kotlin
@Composable
fun Saludo(nombre: String) {
    Text(text = "Hola, $nombre")
}
```

Reglas mentales:
- Un composable **no devuelve** una vista; **emite** UI al llamarse.
- Los composables se **anidan** unos dentro de otros (un `Column` que contiene `Text`s, etc.).
- Su nombre va en **PascalCase** (`PerfilUsuario`, no `perfilUsuario`).
- **Deben ser idempotentes**: llamarlos con los mismos datos debe producir siempre lo mismo. No metas lógica con efectos colaterales (guardar en BD, llamar a la red) directo en el cuerpo del composable; eso va en otro lado (ver [Side Effects](#-9-efectos-secundarios-side-effects)).

> **Analogía:** un composable es como una **receta de cocina** (la instrucción), no el platillo ya servido. Compose ejecuta la receta para producir lo que se ve.

---

<a name="2"></a>
## 🎨 2. Sistema de diseño (Theming / Material 3)

Para que la app sea consistente, usa los **tokens** del tema en lugar de valores fijos. Así el modo oscuro y los cambios de marca salen "gratis".

### Colores — `MaterialTheme.colorScheme`
| Token | Para qué sirve |
| :--- | :--- |
| `.primary` | Color de marca / acciones principales (botones importantes). |
| `.onPrimary` | Texto/iconos que van **encima** de `primary`. |
| `.secondary` / `.tertiary` | Acentos secundarios. |
| `.background` | Fondo de la pantalla. |
| `.surface` | Fondo de tarjetas, hojas, menús. |
| `.onBackground` / `.onSurface` | Texto principal sobre fondo/superficie. |
| `.onSurfaceVariant` | Texto secundario (gris suave). |
| `.outline` | Bordes y divisores. |
| `.primaryContainer` / `.onPrimaryContainer` | Contenedores tenues (chips, tags). |
| `.error` / `.onError` | Estados de error. |

**Regla de oro de los `on*`:** el color `onX` es el que va **encima** del color `X`. Si pintas un fondo `.primary`, el texto va en `.onPrimary`. Eso garantiza contraste correcto en claro y oscuro.

```kotlin
Text(
    text = "Acción importante",
    color = MaterialTheme.colorScheme.primary
)
```

### Tipografía — `MaterialTheme.typography`
| Token | Uso típico |
| :--- | :--- |
| `.displayLarge/Medium/Small` | Títulos enormes (pantallas hero). |
| `.headlineMedium` | Títulos de pantalla. |
| `.titleLarge` | Títulos de tarjeta. |
| `.bodyLarge` / `.bodyMedium` | Cuerpo de texto. |
| `.labelMedium` / `.labelSmall` | Etiquetas, tags, texto de botones. |

```kotlin
Text("Configuración", style = MaterialTheme.typography.headlineMedium)
```

### Formas — `MaterialTheme.shapes`
| Token | Uso típico |
| :--- | :--- |
| `.small` | Botones, chips. |
| `.medium` | Tarjetas, contenedores. |
| `.large` | Diálogos, bottom sheets. |

```kotlin
Card(shape = MaterialTheme.shapes.medium) { /* ... */ }
```

### Dark mode y elevación
- El **dark mode** funciona solo si usas tokens y **nunca** colores hardcodeados como `Color.White`. Cada color hardcodeado es un lugar que se rompe en oscuro.
- La **elevación** en Material 3 no es solo sombra: en oscuro tinta la superficie. Usa `CardDefaults.cardElevation(2.dp)` en vez de sombras manuales.

> ❌ `color = Color(0xFFFF5722)` &nbsp;&nbsp; ✅ `color = MaterialTheme.colorScheme.primary`

---

<a name="3"></a>
## 🛠 3. El Modificador (Modifier)

El `Modifier` configura tamaño, espaciado, fondo, clics, etc. Se encadena y **el orden importa**, porque cada modifier envuelve al anterior.

| Modificador | Caso de uso | Ejemplo |
| :--- | :--- | :--- |
| `.padding(dp)` | Espacio alrededor del elemento. | `.padding(16.dp)` |
| `.padding(horizontal=, vertical=)` | Padding por eje. | `.padding(horizontal = 16.dp)` |
| `.fillMaxWidth()` | Ocupa todo el ancho disponible. | `.fillMaxWidth()` |
| `.fillMaxHeight()` / `.fillMaxSize()` | Ocupa alto / toda la pantalla. | `.fillMaxSize()` |
| `.height(dp)` / `.width(dp)` | Tamaño fijo en un eje. | `.height(8.dp)` |
| `.size(dp)` | Cuadrado de tamaño fijo (iconos/fotos). | `.size(48.dp)` |
| `.background(color, shape)` | Fondo de color con forma opcional. | `.background(Color.Red, CircleShape)` |
| `.clip(shape)` | Recorta el contenido (fotos redondas). | `.clip(CircleShape)` |
| `.border(width, color, shape)` | Borde. | `.border(1.dp, Color.Gray, RoundedCornerShape(4.dp))` |
| `.clickable { }` | Hace pulsable cualquier cosa. | `.clickable { onClick() }` |
| `.weight(1f)` | (Solo en Column/Row) reparte el espacio sobrante. | `.weight(1f)` |
| `.aspectRatio(ratio)` | Mantiene proporción. | `.aspectRatio(16f / 9f)` |
| `.alpha(float)` | Transparencia (0f–1f). | `.alpha(0.5f)` |
| `.wrapContentSize()` | Que solo ocupe lo que necesita. | `.wrapContentSize()` |

### Por qué el orden cambia el resultado
Estos dos bloques se ven **distintos** aunque usen los mismos modifiers:

```kotlin
// A) padding ANTES de background -> el color NO cubre el padding
Modifier
    .padding(16.dp)
    .background(Color.Red)

// B) padding DESPUÉS de background -> el color SÍ cubre toda el área
Modifier
    .background(Color.Red)
    .padding(16.dp)
```

> **Truco mental:** un `.padding()` colocado antes "empuja hacia adentro" la zona donde se aplica lo que viene después (fondo, borde, clic). Mismo razonamiento con `.clickable`: el orden decide si el área pulsable incluye o no ese padding.

### Combos que vas a usar siempre
```kotlin
// Avatar circular recortado
Modifier
    .size(48.dp)
    .clip(CircleShape)

// Contenedor cuyas esquinas SÍ recortan el contenido interno
Modifier
    .clip(MaterialTheme.shapes.medium)
    .background(MaterialTheme.colorScheme.surface)
```

---

<a name="4"></a>
## 🏗 4. Layouts base (Column, Row, Box)

Los tres contenedores fundamentales:
- **`Column`** → apila vertical. Eje principal = vertical.
- **`Row`** → apila horizontal. Eje principal = horizontal.
- **`Box`** → apila en capas (Z), una cosa encima de otra.

Controlas la distribución con `Arrangement` (eje principal) y `Alignment` (eje cruzado):

```kotlin
Column(
    verticalArrangement = Arrangement.spacedBy(8.dp),  // separación uniforme entre hijos
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

> Tip: `Arrangement.spacedBy(8.dp)` te ahorra meter un `Spacer` entre cada hijo.

### Ejemplo: item con icono + textos (Row + Column)
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

### Ejemplo: texto encima de un color/imagen (Box)
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

<a name="5"></a>
## 📋 5. Listas eficientes (LazyColumn / LazyRow / Grid)

**No uses `Column` con un `forEach` para listas largas.** Eso renderiza TODOS los items aunque no se vean y mata el rendimiento. Usa `LazyColumn`: solo dibuja lo visible (como un RecyclerView, pero sin el dolor).

```kotlin
LazyColumn(
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp)
) {
    items(
        items = contactos,
        key = { contacto -> contacto.id }   // id único y estable
    ) { contacto ->
        ContactoItem(contacto = contacto, onClick = { /* abrir detalle */ })
    }
}
```

- **`key`**: dale un id único y estable (no la posición). Mejora rendimiento y evita parpadeos al reordenar/borrar.
- **`contentPadding`**: padding interno que respeta el scroll.
- **Horizontal**: `LazyRow`. **Cuadrícula**: `LazyVerticalGrid(columns = GridCells.Fixed(2))`.

```kotlin
LazyVerticalGrid(columns = GridCells.Fixed(2)) {
    items(productos, key = { it.id }) { producto ->
        ProductoCard(producto)
    }
}
```

---

<a name="6"></a>
## 🔄 6. Estado y Recomposición (lo más importante)

### 🧠 Explicado para dummies (empieza aquí)

**¿Qué es un "estado"?**
Es **un dato que puede cambiar mientras la app está abierta**. Ejemplos:
- El número en un contador (0, 1, 2...).
- El texto que el usuario escribe en un buscador.
- Si un switch está encendido o apagado.
- La lista de tareas pendientes.

Si un dato nunca cambia (un título fijo), **no es estado**; solo es estado lo que puede cambiar.

**La idea central (en una frase):**
> La pantalla es un **dibujo de tu estado**. Tú no mueves cosas en la pantalla: cambias el dato, y Compose vuelve a dibujar solito.

Se escribe así: **`UI = f(estado)`** → *"la interfaz es una función del estado"*. Dame el estado y te digo cómo se ve la pantalla.

**Analogía del pizarrón 🪧**

Imagina un pizarrón donde está escrito `contador = 0`. Al lado hay un dibujante (Compose) cuyo único trabajo es: *mirar el pizarrón y dibujar la pantalla tal como dice.*

1. El pizarrón dice `0` → el dibujante pinta "0".
2. El usuario aprieta **+** → tú **borras el pizarrón y escribes `1`**.
3. El dibujante ve que cambió → **borra su dibujo y lo rehace** mostrando "1".

Ese "rehacer el dibujo porque el dato cambió" se llama **recomposición**.

> ❌ Lo que **NO** haces (estilo XML viejo): buscar el `TextView` en pantalla y cambiarle el número a mano.
> ✅ Lo que **SÍ** haces en Compose: cambias el dato y dejas que Compose redibuje.

**¿Por qué necesito dos palabras raras (`remember` y `mutableStateOf`)?**

Son dos trabajos distintos:

| Pieza | Su trabajo (en cristiano) | Si te falta... |
| :--- | :--- | :--- |
| `mutableStateOf(...)` | El **pizarrón mágico**: cuando le cambias el valor, **le avisa al dibujante** para que redibuje. | Cambias el dato pero la pantalla **no se entera** (el botón "no hace nada"). |
| `remember { ... }` | La **memoria**: hace que el pizarrón **no se borre** cada vez que el dibujante recompone. | El dato **se reinicia** constantemente (parece que "no guarda nada"). |

Por eso casi siempre van juntos: `remember { mutableStateOf(...) }` = *"un pizarrón que avisa cuando cambia Y que no se borra solo"*.

### El código

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

- Usa `by` (delegación de propiedades) para escribir `cuenta` en vez de `cuenta.value`.
- Sin `remember`, `cuenta` vuelve a `0` en cada recomposición.
- Sin `mutableStateOf`, cambiar `cuenta` no redibuja nada.

### Sobrevivir a rotaciones de pantalla
`remember` se pierde al rotar el teléfono o cambiar de idioma. Para que el dato aguante esos cambios de configuración:

```kotlin
var texto by rememberSaveable { mutableStateOf("") }
```

Regla simple: si perder ese dato al rotar molestaría al usuario (lo que escribió, una selección), usa `rememberSaveable`.

### Estado derivado — `derivedStateOf`
Cuando un valor se **calcula** a partir de otro estado, no lo guardes aparte: derívalo. Así no se desincroniza.

```kotlin
val items = remember { mutableStateListOf<String>() }
val hayItems by remember { derivedStateOf { items.isNotEmpty() } }
```

### Tipos de estado para colecciones
- Un solo valor → `mutableStateOf(...)`.
- Una **lista mutable** observable → `mutableStateListOf(...)`.
- Un **mapa** observable → `mutableStateMapOf(...)`.

---

<a name="7"></a>
## ⬆️ 7. State Hoisting (elevar el estado)

### 🧠 Para dummies

**State hoisting** = sacar el estado de un composable y subirlo al que lo llama (el "padre"). El composable hijo deja de ser dueño del dato; solo lo **muestra** y **avisa** cuando algo pasa.

**Analogía del control remoto 📺**
La tele (el composable hijo) **no decide** qué canal poner. Solo **muestra** el canal que le digan y **avisa** "apretaron un botón". Quien guarda el canal actual y decide es el **control remoto en manos del papá** (el composable padre). Si cada tele guardara su propio canal por dentro, no podrías controlarla desde afuera ni saber en qué canal está.

**Patrón clave:**
> **State flows down, events flow up** → el **dato baja** como parámetro; el **evento sube** como una función (lambda `onAlgo`).

```kotlin
// ❌ Stateful: el dato vive adentro -> difícil de reutilizar, testear y previsualizar
@Composable
fun CampoBusqueda() {
    var texto by remember { mutableStateOf("") }
    TextField(value = texto, onValueChange = { texto = it })
}

// ✅ Stateless: el dato vive en el padre; este solo muestra y avisa
@Composable
fun CampoBusqueda(
    valor: String,                       // el dato BAJA
    onValorCambia: (String) -> Unit      // el evento SUBE
) {
    TextField(value = valor, onValueChange = onValorCambia)
}

// El padre es el dueño del estado:
@Composable
fun PantallaBusqueda() {
    var query by rememberSaveable { mutableStateOf("") }
    CampoBusqueda(valor = query, onValorCambia = { query = it })
}
```

**¿Por qué molestarse?** Un composable stateless es:
- **Reutilizable** (sirve en muchas pantallas).
- **Testeable y previsualizable** (le pasas datos falsos y ya).
- **Predecible** (no esconde estado por dentro).

Regla práctica: haz tus composables **stateless por defecto**; deja el estado lo más arriba posible (idealmente en un ViewModel para pantallas completas).

---

<a name="8"></a>
## 👆 8. Eventos e interacción

Los eventos viajan "hacia arriba" como **lambdas**. El composable no decide qué hacer; recibe una función y la invoca.

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

- `onClick: () -> Unit` → no recibe ni devuelve nada.
- `onValueChange: (String) -> Unit` → recibe el nuevo valor.
- `onItemClick: (Item) -> Unit` → recibe el item tocado.

> Nombra los parámetros de evento empezando con `on` (`onClick`, `onSubmit`, `onDismiss`).

---

<a name="9"></a>
## ⚡ 9. Efectos secundarios (Side Effects)

A veces necesitas hacer algo que **no es dibujar UI**: llamar a la red, mostrar un Snackbar, arrancar una animación, escribir en disco. Eso **no va suelto** en el cuerpo del composable (se ejecutaría en cada recomposición). Va dentro de un *effect handler*.

| Effect | Cuándo usarlo |
| :--- | :--- |
| `LaunchedEffect(key)` | Ejecutar código de corrutina cuando el composable entra a pantalla o cuando cambia `key`. Ej: cargar datos al abrir. |
| `rememberCoroutineScope()` | Lanzar corrutinas en respuesta a un evento (ej: al hacer clic mostrar un Snackbar). |
| `DisposableEffect(key)` | Registrar y **limpiar** algo (listeners, observers) cuando el composable sale de pantalla. |
| `rememberUpdatedState(x)` | Mantener un valor fresco dentro de un effect de larga vida sin reiniciarlo. |

```kotlin
@Composable
fun PantallaDetalle(id: String, viewModel: DetalleViewModel) {
    // Se ejecuta al entrar, y otra vez solo si 'id' cambia
    LaunchedEffect(id) {
        viewModel.cargarDetalle(id)
    }
    // ... UI ...
}
```

> Para trainee: por ahora te basta con `LaunchedEffect` (cargar al entrar) y `rememberCoroutineScope` (reaccionar a un clic). Los otros llegan con el tiempo.

---

<a name="10"></a>
## 🖼 10. Imágenes desde internet (Coil)

`Image(painterResource(...))` es solo para recursos locales (drawables). Para fotos que vienen de una **URL**, usa **Coil**, la librería estándar en Compose.

Dependencia (`build.gradle.kts`):
```kotlin
implementation("io.coil-kt:coil-compose:2.6.0")
```

Uso:
```kotlin
AsyncImage(
    model = "https://ejemplo.com/foto.jpg",
    contentDescription = "Foto de perfil",   // accesibilidad
    contentScale = ContentScale.Crop,         // llena sin deformar
    modifier = Modifier
        .size(72.dp)
        .clip(CircleShape)
)
```

Coil maneja descarga, caché y carga asíncrona. Si quieres placeholder mientras carga, usa `SubcomposeAsyncImage`.

---

<a name="11"></a>
## 🧱 11. Componentes comunes de Material 3

| Componente | Para qué | Snippet mínimo |
| :--- | :--- | :--- |
| `Text` | Mostrar texto. | `Text("Hola")` |
| `Button` | Acción principal. | `Button(onClick = {}) { Text("Guardar") }` |
| `OutlinedButton` / `TextButton` | Acciones secundarias. | `TextButton(onClick = {}) { Text("Cancelar") }` |
| `IconButton` | Botón solo con icono. | `IconButton(onClick = {}) { Icon(...) }` |
| `TextField` / `OutlinedTextField` | Entrada de texto. | `OutlinedTextField(value, onValueChange = {})` |
| `Card` | Contenedor con superficie/elevación. | `Card { /* contenido */ }` |
| `Icon` | Iconos vectoriales. | `Icon(Icons.Default.Home, contentDescription = null)` |
| `Checkbox` / `Switch` | Selección booleana. | `Switch(checked, onCheckedChange = {})` |
| `CircularProgressIndicator` | Cargando... | `CircularProgressIndicator()` |
| `Divider` (HorizontalDivider) | Línea separadora. | `HorizontalDivider()` |
| `Spacer` | Espacio vacío. | `Spacer(Modifier.height(8.dp))` |

### Estructura de pantalla con `Scaffold`
`Scaffold` da los "huecos" estándar: barra superior, inferior, FAB y contenido.

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
        // contenido...
    }
}
```

> ⚠️ **El `innerPadding` NO es opcional.** Si no lo aplicas, el contenido queda tapado por las barras.

---

<a name="12"></a>
## 🧭 12. Navegación entre pantallas

Con **Navigation Compose** defines un `NavHost` con rutas (strings) y navegas con un `NavController`.

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
                navController.navigate("detalle/$id")   // navegar con argumento
            })
        }
        composable("detalle/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")
            DetalleScreen(id = id)
        }
    }
}
```

- `navController.navigate("ruta")` → ir a una pantalla.
- `navController.popBackStack()` → regresar.
- Pasa **ids** como argumento, no objetos completos.

---

<a name="13"></a>
## 🏛 13. Arquitectura (ViewModel + UI State)

Cuando una pantalla crece, **no metas lógica ni llamadas de red dentro del composable**. Sepáralo:

1. **ViewModel** → contiene la lógica y expone el estado. Sobrevive a rotaciones.
2. **UI State** → una `data class` con *todo lo que la pantalla necesita mostrar*.
3. **Composable** → solo observa el estado y dibuja.

```kotlin
// 1. Un solo objeto que describe la pantalla
data class TareasUiState(
    val tareas: List<Tarea> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

// 2. El ViewModel expone el estado como flujo inmutable
class TareasViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(TareasUiState())
    val uiState: StateFlow<TareasUiState> = _uiState.asStateFlow()

    fun cargarTareas() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            // ...obtener tareas...
            _uiState.update { it.copy(tareas = resultado, isLoading = false) }
        }
    }
}

// 3. La UI solo refleja el estado
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

**Flujo de datos unidireccional (UDF):**
> El estado **baja** del ViewModel a la UI. Los eventos **suben** de la UI al ViewModel (llamando sus funciones). La UI nunca cambia el estado directamente.

No necesitas dominar esto el día 1, pero sí grabarte: **la UI no decide la lógica; solo refleja un estado**.

---

<a name="14"></a>
## 👁 14. Previews

No recompiles ni reinstales para ver un cambio de UI: usa `@Preview` y míralo en el panel de Android Studio.

```kotlin
@Preview(showBackground = true)
@Composable
private fun ContadorPreview() {
    MiAppTheme {          // ¡envuelve SIEMPRE en tu tema!
        Contador()
    }
}
```

Trucos:
- `@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)` → previsualiza el modo oscuro.
- `@Preview(name = "Pantalla pequeña", widthDp = 320)` → otros tamaños.
- Por esto importa el **state hoisting**: un composable stateless con datos de ejemplo se previsualiza solo; uno que carga de la red, no.
- En previews usa **datos falsos** hardcodeados, nunca llamadas reales.

---

<a name="15"></a>
## 💡 15. Consejos pro
1. **Separación**: usa `Spacer(Modifier.height(8.dp))` o `Arrangement.spacedBy(8.dp)` en lugar de paddings complicados.
2. **Reusabilidad**: si un diseño se repite (un chip, una etiqueta), extráelo a su propio `@Composable`.
3. **Imágenes**: `contentScale = ContentScale.Crop` para que llenen su espacio sin deformarse.
4. **Stateless por defecto**: pasa datos por parámetro y sube eventos con lambdas (`onClick: () -> Unit`).
5. **Accesibilidad**: pon `contentDescription` en imágenes/iconos con significado; `null` si son decorativos.
6. **Composables pequeños**: si una función pasa de ~40–50 líneas, casi siempre conviene partirla.
7. **dp vs sp**: `dp` para tamaños/espacios; `sp` para texto (respeta el zoom de fuente del usuario).
8. **No leas estado fuera del composable correcto**: lee el estado lo más cerca posible de donde se usa, para que la recomposición sea pequeña.
9. **`Modifier` como parámetro**: tus composables reutilizables deben aceptar `modifier: Modifier = Modifier` y aplicarlo al elemento raíz.

---

<a name="16"></a>
## ⚠️ 16. Errores comunes del trainee

| Síntoma | Causa típica | Solución |
| :--- | :--- | :--- |
| "El valor se reinicia solo" | Falta `remember` | `var x by remember { mutableStateOf(...) }` |
| "Cambio el dato y la pantalla no reacciona" | No es `State` (es una var normal) | Usa `mutableStateOf` |
| "Se pierde al rotar la pantalla" | `remember` no sobrevive a config changes | `rememberSaveable` |
| "El contenido lo tapa la barra superior/inferior" | No aplicaste `innerPadding` del Scaffold | `Modifier.padding(innerPadding)` |
| "La lista larga va lentísima" | `Column` + `forEach` | Cambia a `LazyColumn` con `items` |
| "La lista parpadea al borrar/reordenar" | Falta `key` en `items` | Dale un `key` único y estable |
| "El fondo no cubre todo el elemento" | Orden de modifiers | `.background()` antes de `.padding()` |
| "Se ve bien en claro y feo en oscuro" | Colores hardcodeados | Usa `MaterialTheme.colorScheme` |
| "La foto sale estirada" | Falta `contentScale` | `ContentScale.Crop` |
| "La llamada a la red se repite sin parar" | La pusiste suelta en el composable | Métela en `LaunchedEffect(key)` |
| "El composable no se reutiliza" | Está stateful | Aplica state hoisting |

---

<a name="17"></a>
## 📖 17. Glosario rápido de términos

- **Composable**: función marcada con `@Composable` que describe un pedazo de UI.
- **Recomposición**: cuando Compose vuelve a ejecutar un composable porque un estado del que depende cambió.
- **Estado (State)**: dato que puede cambiar en el tiempo y dispara recomposición. Se crea con `mutableStateOf`.
- **`remember`**: guarda un valor entre recomposiciones para que no se reinicie.
- **`rememberSaveable`**: como `remember`, pero también sobrevive a cambios de configuración (rotar, cambiar idioma).
- **`mutableStateOf`**: crea un contenedor de estado observable; al cambiar su valor, avisa a Compose.
- **`derivedStateOf`**: estado calculado a partir de otro estado.
- **State Hoisting**: sacar el estado de un composable y subirlo al padre; el hijo queda stateless.
- **Stateless / Stateful**: sin estado propio / con estado propio. Prefiere stateless.
- **UDF (Flujo Unidireccional de Datos)**: el estado baja, los eventos suben.
- **Modifier**: cadena de configuraciones (tamaño, padding, fondo, clic...). El orden importa.
- **`Scaffold`**: estructura base de pantalla (topBar, bottomBar, FAB, contenido).
- **`innerPadding`**: espacio que el `Scaffold` reserva para las barras; debes aplicarlo al contenido.
- **`LazyColumn` / `LazyRow`**: listas que solo dibujan lo visible (eficientes para listas largas).
- **`key` (en listas)**: identificador único y estable de cada item; mejora rendimiento.
- **`Arrangement`**: cómo se distribuyen los hijos en el **eje principal** de un Column/Row.
- **`Alignment`**: cómo se alinean los hijos en el **eje cruzado**.
- **Side Effect (efecto secundario)**: acción que no es dibujar UI (red, disco, animación). Va en effect handlers.
- **`LaunchedEffect`**: corre una corrutina al entrar el composable o al cambiar su `key`.
- **`rememberCoroutineScope`**: alcance para lanzar corrutinas desde eventos (clics).
- **`ViewModel`**: clase que guarda y maneja el estado/lógica de una pantalla; sobrevive a rotaciones.
- **`StateFlow`**: flujo observable de estado que el ViewModel expone a la UI.
- **`collectAsStateWithLifecycle`**: convierte un `StateFlow` en `State` que Compose puede observar.
- **UI State**: `data class` que describe todo lo que una pantalla necesita mostrar.
- **Token de diseño**: valor del tema (`colorScheme`, `typography`, `shapes`) que usas en vez de valores fijos.
- **`dp`**: unidad de tamaño/espacio independiente de densidad.
- **`sp`**: unidad para texto, que respeta el ajuste de fuente del usuario.
- **Coil / `AsyncImage`**: librería y composable para cargar imágenes desde URL.
- **`@Preview`**: muestra un composable en Android Studio sin correr la app.

---

> **Cierre:** si te atoras, ráscale a dos preguntas: (1) *¿de qué estado depende esto que quiero mostrar?* y (2) *¿quién es el dueño de ese estado?* Casi todo en Compose se resuelve respondiéndolas.