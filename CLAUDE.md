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

## Fases de implementación (ver plan completo)
1. Setup multi-módulo + build-logic + libs.versions.toml
2. Core Network (Apollo + Retrofit)
3. Core Database (Room + DataStore)
4. Design System (AnimeTheme + componentes base)
5. Feature Profile
6. Feature Home
7. Feature Search
8. Feature Detail
9. Feature Player (MVI)
10. Android TV screens
11. Notificaciones WorkManager
12. Polish + caché TTL + ProGuard
