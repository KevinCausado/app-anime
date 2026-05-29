# CLAUDE.md — App Anime (Android)

## Descripción del proyecto
App Android de streaming de anime estilo Crunchyroll. Soporta Phone y Android TV desde el mismo codebase. Sin backend propio — todo es local + APIs públicas gratuitas.

## Repo
https://github.com/KevinCausado/app-anime.git

## Stack
- **Lenguaje:** Kotlin
- **UI:** Jetpack Compose + Material 3 (phone) / androidx.tv (TV)
- **Arquitectura:** Clean Architecture + MVVM (general) / MVI (feature player)
- **DI:** Hilt
- **Player:** Media3 ExoPlayer — HLS via Consumet API
- **Metadata:** AniList GraphQL via Apollo Kotlin
- **Streaming:** Consumet API self-hosted (HiAnime primario, AnimeKai fallback)
- **DB local:** Room
- **Preferencias:** DataStore
- **Imágenes:** Coil 3
- **Paginación:** Paging 3
- **Background:** WorkManager (notificaciones nuevos episodios)

## Estructura de módulos
```
app/          → entry point phone
app-tv/       → entry point Android TV (LEANBACK_LAUNCHER)
core/model    → data classes puras
core/network  → Retrofit (Consumet) + Apollo (AniList)
core/database → Room: entidades, DAOs, AnimeDatabase
core/datastore→ DataStore: perfil activo
core/ui       → componentes Compose phone
core/ui-tv    → componentes androidx.tv
feature/home
feature/search
feature/detail
feature/player
feature/profile
build-logic/  → convention plugins Gradle
```

## APIs
- **AniList:** `https://graphql.anilist.co` — sin auth, 90 req/min
- **Consumet:** URL propia en `local.properties` como `CONSUMET_BASE_URL`
  - Proveedor primario: HiAnime (`/anime/hianime/...`)
  - Fallback: AnimeKai (`/anime/animekai/...`)

## Diseño
- Dark theme: fondo `#0B0C0F`, superficie `#1A1B1E`
- Acento naranja: `#F47521` (Crunchyroll orange)
- Hero banner en Home con HorizontalPager (phone) / Carousel (TV)
- Filas horizontales por sección (Trending, This Season, Continuar viendo, Por género)

## Base de datos Room
Tablas: `ProfileEntity`, `AnimeEntity` (caché AniList, TTL 24h), `EpisodeEntity`,
`WatchHistoryEntity`, `FavoriteEntity`, `WatchlistEntity`, `NotificationTrackEntity`.
El perfil activo se guarda en DataStore (`activeProfileId: Long`).

## Convenciones
- Queries GraphQL en `core/network/src/main/graphql/com/kevindev/animeapp/`
- `local.properties` para secrets (CONSUMET_BASE_URL) — no commitear
- minSdk 26 (Android 8.0), targetSdk último estable
- Módulos TV reusan todos los ViewModels/Repositories de phone — solo difieren los Composables

## AGP 9+ Built-in Kotlin

AGP 9.0+ tiene **built-in Kotlin** habilitado por defecto. Si aplicas `kotlin-android` explícitamente, explota con:
```
Cannot add extension with name 'kotlin', as there is an extension already registered with that name.
```

### Solución actual (bypass temporal hasta que KSP/Apollo migren)
En `gradle.properties`:
```properties
android.builtInKotlin=false
android.newDsl=false
```

Esto deshabilita built-in Kotlin y el nuevo DSL de AGP 9, permitiendo usar `kotlin-android` como antes.

### Migración futura
Cuando KSP y Apollo sean 100% compatibles con built-in Kotlin, eliminar:
- `android.builtInKotlin=false` y `android.newDsl=false` de `gradle.properties`
- `alias(libs.plugins.kotlin.android)` de todos los `build.gradle.kts` y del root
- La línea `kotlin-android` de `gradle/libs.versions.toml`

## Versiones clave (actualizado May 2026)
| Componente | Versión |
|---|---|
| Gradle | 9.2.1 |
| AGP | 9.0.1 |
| Kotlin | 2.3.21 |
| KSP | 2.3.9 |

## Estado actual (Mayo 2026)

| Fase | Estado | Módulos |
|---|---|---|
| 1. Setup | ✅ Completo | multi-módulo + version catalog + plugins |
| 2. Core Network | ✅ Completo | Apollo (5 queries), Retrofit (HiAnime + AnimeKai), DTOs, mappers, DI |
| 3. Core Database + DataStore | ✅ Completo | Room (7 entidades, 7 DAOs), ProfileDataStore |
| 4. Design System | ✅ Completo | AnimeTheme, AnimeCard, EpisodeCard, SectionHeader, SectionRow, Shimmer, ErrorState |
| 5. Feature Profile | ✅ Completo | ProfileViewModel, ProfileSelectionScreen, ProfileEditScreen, ProfileNavigation, AnimeNavHost actualizado |
| 6. Feature Home | ✅ Completo | HomeViewModel (trending+seasonal+continueWatching), HeroBanner (HorizontalPager), HomeScreen, ContinueWatchingCard, HomeNavigation |
| 7. Feature Search | ✅ Completo | SearchViewModel (debounce 300ms), SearchPagingSource (Apollo Paging3), AnimeSearchBar, FilterSheet (género/formato/estado), SearchScreen grid 3 cols |
| 8. Feature Detail | ✅ Completo | DetailViewModel (Apollo+Consumet+favoritos/watchlist), DetailHeader (banner+desc expandible+acciones), EpisodeList, RelatedAnimeRow, DetailScreen |
| 9. Feature Player (MVI) | ✅ Completo | PlayerIntent/State/Effect, ExoPlayerManager (HLS+subtítulos), PlayerViewModel (fallback HiAnime→AnimeKai, guardado progreso 10s), PlayerScreen (controles custom, skip intro/outro, seekbar) |
| 10. Android TV | ✅ Completo | TvHomeScreen (Carousel), TvSearchScreen (grid 5 cols), TvDetailScreen, TvPlayerScreen (reutiliza PlayerScreen), TvSearchBar, TvNavHost con rutas completas |
| 11. Notificaciones | ❌ Pendiente | No existe |

---

## Instrucciones por fase pendiente

### Fase 5 — Feature Profile

Módulo: `feature/profile/`

**Lo que debe crear:**
- `ProfileScreen.kt` — Lista de perfiles, crear/editar, seleccionar activo
- Contenido: avatar, nombre, botón "Agregar perfil", swipe to delete
- Fuente de datos: `ProfileDataStore` (activeProfileId) + `ProfileDao`

**Estructura esperada:**
```
feature/profile/src/main/kotlin/com/kevindev/animeapp/feature/profile/
├── ProfileScreen.kt
├── ProfileViewModel.kt
└── ProfileNavigation.kt   (ruta "profile")
```

**Patrón:** MVVM. ViewModel con Hilt (`@HiltViewModel`). StateFlow con `ProfileUiState`.

---

### Fase 6 — Feature Home

Módulo: `feature/home/`

**Lo que debe crear:**
- `HomeScreen.kt` — Pantalla principal con hero banner + secciones horizontales
- Hero: `HorizontalPager` con poster grande, título, score, botón "Ver detalle"
- Secciones: Trending, This Season, Popular, Continue Watching (si hay WatchHistory)
- Cada sección es una `AnimeSectionRow` de core/ui

**Estructura esperada:**
```
feature/home/src/main/kotlin/com/kevindev/animeapp/feature/home/
├── HomeScreen.kt
├── HomeViewModel.kt
├── components/
│   └── HeroBanner.kt
└── HomeNavigation.kt   (ruta "home", startDestination)
```

**Flujo de datos:**
```
HomeViewModel → llama AniListRepository (que usa ApolloClient)
             → expone StateFlow<HomeUiState>
             → HomeScreen.collectAsState()
```

**Estados UI:** Loading, Success(hero, sections), Error.

---

### Fase 7 — Feature Search

Módulo: `feature/search/`

**Lo que debe crear:**
- `SearchScreen.kt` — Barra de búsqueda + resultados en grid
- Búsqueda local (debounce 300ms, mínimo 3 caracteres) vs popular initial
- Grid 2 columnas usando `AnimeCard`

**Estructura esperada:**
```
feature/search/src/main/kotlin/com/kevindev/animeapp/feature/search/
├── SearchScreen.kt
├── SearchViewModel.kt
├── components/
│   └── SearchBar.kt
└── SearchNavigation.kt   (ruta "search")
```

**APIs:** Consumet HiAnime `search` (por texto) + AniList `SearchAnime` (por género/año/status/format)
Filtros: género, año, estado (pantalla de filtros modal/bottom sheet)

---

### Fase 8 — Feature Detail

Módulo: `feature/detail/`

**Lo que debe crear:**
- `DetailScreen.kt` — Banner, info, episodios, relaciones, recomendaciones
- Banner con backdrop, título, score, formato, estado, descripción expandible
- Lista de episodios con `EpisodeCard` (scroll infinito)
- Botón "Reproducir" → navega a Player
- Botón favorito + watchlist (usar FavoriteDao/WatchlistDao)

**Estructura esperada:**
```
feature/detail/src/main/kotlin/com/kevindev/animeapp/feature/detail/
├── DetailScreen.kt
├── DetailViewModel.kt
├── components/
│   ├── DetailHeader.kt
│   ├── EpisodeList.kt
│   └── RelatedAnimeRow.kt
└── DetailNavigation.kt   (ruta "detail/{animeId}")
```

**APIs:** AniList `AnimeDetail` + Consumet `getInfo` (para episodios streaming)

---

### Fase 9 — Feature Player (MVI)

Módulo: `feature/player/`

**Arquitectura:** MVI (no MVVM como las demás)

**Lo que debe crear:**
- `PlayerScreen.kt` — ExoPlayer fullscreen con overlay de controles
- Controles: play/pause, seek, skip intro/outro, velocidad, subtítulos
- `PlayerViewModel.kt` — Gestión de estado MVI con `PlayerIntent`/`PlayerState`/`PlayerEffect`
- Integración con Media3 ExoPlayer + HLS desde Consumet

**Estructura esperada:**
```
feature/player/src/main/kotlin/com/kevindev/animeapp/feature/player/
├── PlayerScreen.kt
├── PlayerViewModel.kt
├── mvi/
│   ├── PlayerIntent.kt
│   ├── PlayerState.kt
│   └── PlayerEffect.kt
├── media/
│   └── ExoPlayerManager.kt
└── PlayerNavigation.kt   (ruta "player/{episodeId}")
```

**Estados MVI:** `Idle`, `Loading`, `Playing`, `Paused`, `Buffering`, `Error`
**Intents:** `Play`, `Pause`, `SeekTo`, `SkipIntro`, `ChangeSpeed`, `SelectSubtitle`, `Back`

---

### Fase 10 — Android TV

Módulo: `app-tv/`

**Componentes TV** ya existen en `core/ui-tv`:
- `TvAnimeCard.kt`
- `TvSectionRow.kt`

**Lo que debe crear:**
- Reusar los ViewModels de los features phone (mismos repositorios, misma lógica)
- Componer pantallas TV con `androidx.tv` components:
  - `TvHomeScreen.kt` — Carousel banner + filas con `TvSectionRow`
  - `TvSearchScreen.kt` — Search con teclado virtual TV
  - `TvDetailScreen.kt` — Detail adaptado a TV (navegación con D-pad)
  - `TvPlayerScreen.kt` — Player con controles TV

**Estructura esperada:**
```
app-tv/src/main/kotlin/com/kevindev/animeapp/tv/
├── TvActivity.kt                 (ya existe)
├── TvApp.kt                      (ya existe)
├── TvNavHost.kt                  (ya existe, agregar rutas)
├── screens/
│   ├── TvHomeScreen.kt
│   ├── TvSearchScreen.kt
│   ├── TvDetailScreen.kt
│   └── TvPlayerScreen.kt
└── components/
    └── TvSearchBar.kt
```

**Importante:** NO crear nuevos ViewModels — reusar los de `feature/*`.
Ejemplo: `val viewModel: HomeViewModel = hiltViewModel()`

---

### Fase 11 — Notificaciones WorkManager

Módulo: a crear en `core/notification/` o dentro de `app/`

**Lo que debe crear:**
- `NewEpisodeWorker.kt` — Worker periódico que revisa episodios nuevos de animes trackeados
- `NotificationHelper.kt` — Crear canales, mostrar notificaciones
- `NotificationModule.kt` — Hilt module para Worker

**Estructura esperada:**
```
core/notification/src/main/kotlin/com/kevindev/animeapp/core/notification/
├── di/NotificationModule.kt
├── NewEpisodeWorker.kt
└── NotificationHelper.kt
```

**Worker:** PeriodicWorkRequest cada 6h, consulta Consumet `getEpisodes` para animes en `NotificationTrackEntity`, compara con `lastCheckedEpisode`.

---

## Reglas generales para todas las fases

1. **No duplicar ViewModels entre phone y TV** — Los TV screens importan `hiltViewModel()` de feature/ modules
2. **Navegación** — Cada feature expone su ruta como `const val` en `*Navigation.kt`, el NavHost central en `AnimeNavHost.kt` las registra
3. **Estados** — Cada Screen maneja: `Loading`, `Success(data)`, `Error(message, onRetry)`
4. **Paginación** — Usar Paging 3 para listas largas (episodios, resultados de búsqueda)
5. **Local first** — Cachear metadata de AniList en Room (AnimeEntity con TTL 24h), consumir de DB primero
6. **Sin comentarios** en código a menos que el WHY sea no obvio
7. **Sin features no pedidas** — No agregar refactors, abstracciones ni features extras
