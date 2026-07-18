# JustCounter repository guide

Use this file as the project-specific source of truth when planning, implementing, or reviewing changes in this repository. Prefer the established local patterns below over generic Android conventions unless the task explicitly requires an architectural change.

## Product and module overview

- JustCounter is a small offline-first Android click-counter app.
- The repository currently contains one application module: `:app`.
- Package root: `com.atarusov.justcounter`.
- UI: Kotlin + Jetpack Compose + Material 3, edge-to-edge, light/dark themes.
- Persistence: Room for counters/categories and Preferences DataStore for dismissed hints.
- DI: Hilt with KSP.
- Async/state: coroutines, `Flow`, `StateFlow`, and `SharedFlow`.
- Navigation: Navigation Compose with type-safe `@Serializable` routes.
- Minimum/target/compile SDK and dependency versions live in `app/build.gradle.kts` and `gradle/libs.versions.toml`; inspect those files instead of copying versions from this guide.
- Supported string resources currently have default English plus Russian (`values-ru`) and Spanish (`values-es`) translations.

## Repository map

```text
app/src/main/java/com/atarusov/justcounter/
├── App.kt, MainActivity.kt
├── common/                 # Shared Room entities, database, converters
│   └── di/                 # App-wide database provision
├── navigation/             # Typed routes and the single NavHost
├── features/
│   ├── counter_list_screen/
│   ├── counter_full_screen/
│   ├── edit_dialog/
│   └── category_drawer/
├── shared_features/hints/  # DataStore-backed cross-feature hint state/UI
└── ui/theme/               # Theme, typography, colors, dimensions
```

Each main feature is organized by responsibility:

- `_di/`: Hilt bindings/providers local to the feature.
- `data/`: DAO, repository interface/implementation, and query result models.
- `presentation/`: route-level composable, ViewModel, callback holders, components.
- `presentation/mvi/`: `Actor`, `Reducer`, optional `Bootstrapper`, `OneTimeEventHandler`.
- `presentation/mvi/entities/`: `Action`, `InternalAction`, `State`, `OneTimeEvent`.

This is a pragmatic, feature-first, Clean-inspired architecture. There is no separate domain layer or use-case layer. Do not introduce one for an isolated change unless the task calls for a broader refactor.

## MVI contract

Follow the existing unidirectional flow for behavior added to an MVI-backed feature:

```text
Composable -> Action -> ViewModel -> Actor -> InternalAction
                                      ├-> Reducer -> StateFlow<State> -> Composable
                                      └-> OneTimeEventHandler -> SharedFlow<OneTimeEvent>

Room/DataStore Flow -> Bootstrapper -> InternalAction -> Reducer -> StateFlow<State>
```

- `Action` represents a user/UI intent.
- `Actor` performs validation, side effects, and repository calls, returning a `Flow<InternalAction>`.
- `InternalAction` is the exhaustive internal result vocabulary. It may affect state, trigger a one-time event, or both.
- `Reducer` is synchronous and side-effect free. It returns a copied immutable state.
- `OneTimeEventHandler` maps only ephemeral effects such as navigation, focus changes, scrolling, or toasts.
- `Bootstrapper` observes repository flows and converts persisted updates into `InternalAction`s.
- ViewModels expose read-only `StateFlow`/`SharedFlow`; mutable flows stay private. Work is launched in `viewModelScope`.
- Route-level composables collect state with `collectAsStateWithLifecycle()` and collect events inside `LaunchedEffect(Unit)`.
- Keep all `when` expressions over sealed MVI types exhaustive. When adding a new internal action, deliberately update both the reducer and one-time-event handler.
- Existing actors sometimes optimistically emit a state update before the database call. Preserve that ordering when modifying related behavior unless there is a concrete consistency reason to change it.

## UI and Compose conventions

- Keep route-level composables responsible for obtaining the Hilt ViewModel, collecting flows, handling one-time effects, and connecting navigation callbacks.
- Put renderable UI in a stateless/state-driven composable that receives `State` plus `onAction` or a small callback holder. This makes previews possible without Hilt.
- Reusable visual pieces belong in `presentation/components`.
- Use `Modifier` parameters on reusable composables and keep them near the end of the parameter list.
- Use `stringResource`; do not hardcode user-visible text or accessibility descriptions.
- Add every new translatable string to English, Russian, and Spanish resources in the same change. Mark truly invariant strings `translatable="false"`.
- All tappable icons need a meaningful `contentDescription`, unless they are intentionally decorative.
- Reuse `MaterialTheme`, `JustCounterTheme`, `Dimensions`, typography, semantic colors, and `CounterColorProvider`. Avoid scattering new raw spacing, radius, color, or typography values when an existing token fits.
- Maintain previews for meaningful UI states. Preview helpers live on state/entities (`getPreviewState`, `getPreviewCounter`, `getPreviewCategory`) and previews are wrapped in `JustCounterTheme`.
- Lists with reorder behavior use stable keys (`Counter.id`, category identity) and the `sh.calvin.reorderable` integration.
- UI-only transient state may use `remember`; persistent/business state belongs in the MVI state or persistence layer.

## Persistence rules and invariants

Room entities are shared in `common`:

- `Counter`: string UUID primary key, bounded value, color, 1-3 step values in current UI, nullable `categoryId`, and explicit `position`.
- `Category`: auto-generated integer ID and explicit `position`.
- Deleting a category cascades deletion of its counters through the Room foreign key.
- Counter step lists and colors are stored using `Converters` and kotlinx serialization.

Ordering is a database invariant, not just UI state:

- `position` is contiguous and zero-based within a category; uncategorized counters form their own nullable-category group.
- Category positions are contiguous and zero-based globally.
- New entities must be passed with `UNDEFINED_POSITION`; transactional DAO helpers compute the real position.
- Deletes must shift subsequent positions in the same group.
- Swaps use a temporary `-1` position inside a Room transaction.
- Counter operations involving order must include `categoryId`, including the `null` uncategorized case.
- Editing a counter must preserve its stored `position` and `categoryId`; `EditCounterDao.setCounter` restores both before updating.
- Multi-query mutations that protect these invariants belong in `@Transaction` DAO methods, not in composables or ViewModels.

When changing the Room schema:

- Increment the database version.
- Add a valid auto/manual migration.
- Keep exported schemas under `app/schemas` updated and committed.
- Verify upgrade behavior from every supported prior schema, not only a clean install.

Use DataStore for small preference-like state. The existing hints repository exposes a single state flow and focused mutation methods; follow that shape for related preferences.

## DI, repositories, and navigation

- Hilt starts at `@HiltAndroidApp App`; `MainActivity` is an `@AndroidEntryPoint`; ViewModels use `@HiltViewModel` constructor injection.
- Provide DAOs from the singleton `AppDatabase` in feature `_di` modules.
- Bind repository interfaces to implementations with `@Binds`, or use `@Provides` when construction needs Android context/configuration.
- Repository interfaces form the boundary used by actors/bootstrappers. UI code must not call DAOs directly.
- Keep database/query models close to the owning feature unless they are truly shared entities.
- Define destinations as `@Serializable` objects/data classes in `navigation/NavGraph.kt` and use typed `composable<T>`, `dialog<T>`, `navigate(route)`, and `SavedStateHandle.toRoute<T>()`.
- Navigation is emitted as a one-time event and executed by the route-level composable/NavHost callback.
- Current edit-dialog navigation passes a snapshot of editable counter fields in the route. Preserve its ID so persistence can restore category and position.

## Kotlin style observed in this codebase

- Kotlin official style is enabled (`kotlin.code.style=official`); use 4 spaces and trailing commas in multiline declarations/calls where surrounding code does.
- Names are descriptive and role-based: `*Screen`, `*ViewModel`, `*Repository`, `*RepositoryImpl`, `*Dao`, `*Callbacks`.
- Compose functions and types use PascalCase; properties/functions use camelCase; constants use `UPPER_SNAKE_CASE`.
- Prefer immutable `data class` state and `copy`; local mutable lists are acceptable inside reducers/DAO algorithms when returned as immutable state.
- Prefer expression bodies for short mappings/delegations and block bodies for multi-step logic.
- Use constructor injection by default and keep injected dependencies private unless the surrounding feature already exposes them for a reason.
- Keep feature package names consistent with the existing snake_case convention.
- Avoid opportunistic formatting or architecture cleanup outside the requested scope. There are small inconsistencies in legacy formatting; match the nearest well-structured code and use official Kotlin formatting for new code.

## Validation and delivery checklist

The existing test files are template placeholders, so do not treat test coverage as sufficient. Add focused unit tests when changing reducers, validation, ordering, mapping, or other pure/business logic; add instrumentation/Compose tests for important Android/UI behavior when practical.

Run the smallest relevant checks first, then broader checks for cross-cutting changes:

```bash
./gradlew testDebugUnitTest
./gradlew lintDebug
./gradlew assembleDebug
```

For release/shrinker-sensitive work, also use the existing minified debug-like build:

```bash
./gradlew assembleReleaseDebug
```

Before finishing a change, verify as applicable:

- MVI sealed types are handled exhaustively.
- State changes are reducer-only and side effects remain in actors/repositories.
- Room ordering/category invariants and migrations are intact.
- New strings exist in English, Russian, and Spanish.
- Accessibility descriptions and Compose previews are updated.
- Navigation arguments remain serializable and stable.
- Relevant Gradle checks pass; report any pre-existing or environment-dependent failure explicitly.
- No secrets or machine-local files (`local.properties`, credentials, generated build output) are added to version control.

## Known project-specific cautions

- `Counter.MIN_VALUE`/`MAX_VALUE` are enforced when incrementing/decrementing; preserve clamping.
- An empty counter title is allowed after warning, and invalid/blank numeric input is normalized by the edit actor. Inspect the existing validation before changing UX semantics.
- A selected category can be `null`, meaning uncategorized counters; never equate `null` with “all counters.”
- The counter-list bootstrapper creates a default counter for an empty selected category. Account for this behavior in empty-state changes and tests.
- Firebase Analytics is currently configured as a dependency/plugin on the active development branch; inspect the current branch before assuming concrete analytics events already exist.
- Build variants include `debug`, minified `releaseDebug`, and `release`; release signing is intentionally not defined in the repository.
