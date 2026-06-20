# Changelog - BigBangOptimizer

## [1.0.0.2] - 2026-06-20

Technical audit corrections and robustness enhancements for production deployment.

### Fixed
* **Blocker (NPE in Configuration)**: Fixed `OptimizerConfig.INSTANCE` not being initialized by setting `INSTANCE = this;` in its constructor, resolving global `NullPointerException` crashes.
* **Blocker (StackOverflowError in Commands)**: Resolved infinite recursion in `ClearCommand.getPlayer(CommandSourceStack)` by invoking the correct API method `source.getPlayer()`.
* **Critical (Dry-Run Preview Crash)**: Fixed `UnsupportedOperationException` when generating preview/dry-runs by refactoring the entities list aggregation to avoid modifications to the unmodifiable list returned by `CleanupExecution.getRemoved()`.
* **Critical (NoClassDefFoundError on Startup)**: Completely decoupled Cobblemon dependency imports from `PokemonCleanupModule` class signatures, moving all optional imports to `CobblemonEntityInspector` which is only loaded at runtime if Cobblemon is present. The mod can now boot successfully on vanilla/non-Cobblemon servers.
* **High (Warning Infinite Loop)**: Corrected tick mathematics in `CleanupScheduler` warning states. Scheduler warning states are now evaluated using absolute target ticks (`cleanupTargetTick`), preventing warning loop lockups and ensuring cleanups run.
* **High (Console NPE on Permission Check)**: Fixed `NullPointerException` when running administrative commands from the server console by adding null checks to `OptimizerPermissions` to safely treat the console as an admin.
* **Medium (Dimension Statistics in Audit Logs)**: Wired `result.addDimensionStat` inside `CleanupRunner` so that removed entity counts are logged by dimension in JSON audit logs.
* **Medium (Incorrect stats in /bbo entities dimension)**: Fixed the dimension stats command to filter entities and count them only in the requested dimension, instead of displaying global stats. Prints available dimensions to operators on invalid inputs.

### Added
* **High (Performance Triggers)**: Fully implemented the threshold triggers logic (`schedule`, `threshold`, `threshold_or_schedule`) in `CleanupScheduler`. Automatic cleanups are now dynamically scheduled when both average performance (TPS/MSPT) is degraded and candidate entity counts exceed global limits across consecutive samples.
* **Medium (Cleanup Cancellation)**: Implemented `cancelActive()` logic in `CleanupScheduler` and hooked it to `/bbo clear cancel` to support cancelling scheduled cleanups and active warning phases.
* **Changelog and Configurations**: Created `CHANGELOG.md` and `config/bigbangoptimizer-server-example.toml` with detailed comments for administrators.

### Changed
* **TpsMonitor Sample Correction**: Fixed sample interval calculation in `TpsMonitor` by converting `sample_interval_seconds` to tick-based sample intervals (seconds * 20).
* **Older Pokemon Priority**: Refactored `PokemonCleanupModule.java` to sort candidates by age (`tickCount` descending) *before* applying the `maxRemovals` limit, ensuring the oldest wild Pokémon are prioritized for cleanup.
