# BigBangOptimizer

A server-side optimization mod for NeoForge 1.21.1 that provides a modular system for observability, lag prevention, and mitigation on modded Minecraft servers, with priority support for Cobblemon.

## Features

- **Modular Entity Cleanup** — Automatic and manual cleanup of items, Pokémon, and temporary entities
- **Performance Monitoring** — Real-time TPS, MSPT, and memory tracking with historical data
- **Smart Triggers** — Threshold-based or scheduled cleanup activation
- **Player Warnings** — Configurable pre-cleanup notifications via chat, title, or action bar
- **Protection System** — Shiny, legendary, player-owned, battling, and custom tag protections
- **Dry Run Mode** — Preview cleanup results without removing any entities
- **Audit Logging** — Full audit trail for all cleanup operations
- **Mod Integrations** — Cobblemon, FTB Chunks, and Spark support

## Requirements

| Dependency | Version | Required |
|------------|---------|----------|
| Minecraft | 1.21.1 | Yes |
| NeoForge | 21.1.233+ | Yes |
| Java | 21 | Yes |
| Cobblemon | 1.7.0+ | No |

## Installation

1. Download the compiled JAR from [Releases](https://github.com/pedro-dalben/BigBangOptimizer/releases)
2. Place it in your server's `mods/` directory
3. Restart the server
4. Edit `config/bigbangoptimizer-server.toml` as needed

## Building from Source

```bash
# Clone the repository
git clone https://github.com/pedro-dalben/BigBangOptimizer.git
cd BigBangOptimizer

# Build the mod
./gradlew build

# The JAR will be in build/libs/
```

### Build Commands

| Command | Description |
|---------|-------------|
| `./gradlew build` | Build the mod JAR |
| `./gradlew runClient` | Launch a development client |
| `./gradlew runServer` | Launch a development server (no GUI) |
| `./gradlew runData` | Run data generators |
| `./gradlew clean` | Clean build artifacts |
| `./gradlew publishToMavenLocal` | Publish to local Maven repository |

## Configuration

The configuration file is automatically generated at `config/bigbangoptimizer-server.toml` on first server start.

### General Settings

| Option | Default | Description |
|--------|---------|-------------|
| `enabled` | `true` | Enable/disable the mod |
| `log_level` | `INFO` | Logging level (TRACE, DEBUG, INFO, WARN, ERROR) |

### Monitoring Settings

| Option | Default | Description |
|--------|---------|-------------|
| `sample_interval_seconds` | `20` | Seconds between performance samples |
| `history_size` | `180` | Number of samples to keep in history |

### Cleanup Settings

| Option | Default | Description |
|--------|---------|-------------|
| `enabled` | `true` | Enable automatic cleanup |
| `interval_minutes` | `15` | Minutes between automatic cleanups |
| `cooldown_minutes` | `20` | Minutes to wait after a cleanup |
| `dry_run` | `false` | Preview mode — no entities removed |
| `max_execution_time_ms` | `150` | Max milliseconds per cleanup tick |
| `max_entities_scanned_per_tick` | `250` | Max entities to scan per tick |

### Warning Settings

| Option | Default | Description |
|--------|---------|-------------|
| `enabled` | `true` | Send warnings before cleanup |
| `seconds` | `60, 30, 10, 5` | Seconds before cleanup to warn |
| `chat` | `true` | Send warning in chat |
| `title` | `false` | Show warning as title |
| `actionbar` | `false` | Show warning on action bar |

### Item Cleanup Settings

| Option | Default | Description |
|--------|---------|-------------|
| `enabled` | `true` | Enable item cleanup |
| `minimum_global_count` | `500` | Min items globally before cleanup |
| `minimum_age_seconds` | `90` | Min age in seconds before removal |
| `max_removed_per_run` | `500` | Max items removed per cleanup |
| `protect_near_players_radius` | `24.0` | Protection radius around players |
| `whitelist` | `[]` | Item IDs to never remove |
| `blacklist` | `[]` | Item IDs to always remove |

### Pokémon Cleanup Settings

| Option | Default | Description |
|--------|---------|-------------|
| `enabled` | `true` | Enable Pokémon cleanup |
| `minimum_global_count` | `250` | Min Pokémon globally before cleanup |
| `minimum_age_seconds` | `120` | Min age in seconds before removal |
| `max_removed_per_run` | `100` | Max Pokémon removed per cleanup |
| `protect_near_players_radius` | `64.0` | Protection radius around players |
| `protect_shiny` | `true` | Protect shiny Pokémon |
| `protect_legendary` | `true` | Protect legendary Pokémon |
| `protect_mythical` | `true` | Protect mythical Pokémon |
| `protect_player_owned` | `true` | Protect player-owned Pokémon |
| `protect_battling` | `true` | Protect battling Pokémon |
| `protect_evolving` | `true` | Protect evolving Pokémon |
| `protect_pasture_tethered` | `true` | Protect pasture-tethered Pokémon |
| `protect_battle_clones` | `true` | Protect battle clones |
| `protect_mounted` | `true` | Protect mounted Pokémon |
| `protect_named` | `true` | Protect named Pokémon |
| `protect_npc_related` | `true` | Protect NPC-related Pokémon |
| `protect_bigbangoptimizer_tag` | `true` | Protect tagged entities |

### Loot Ball Cleanup Settings

| Option | Default | Description |
|--------|---------|-------------|
| `enabled` | `false` | Enable loot ball cleanup |
| `minimum_global_count` | `100` | Min loot balls globally before cleanup |
| `minimum_age_seconds` | `300` | Min age in seconds before removal |
| `max_removed_per_run` | `100` | Max loot balls removed per cleanup |

### Temporary Entity Cleanup Settings

| Option | Default | Description |
|--------|---------|-------------|
| `enabled` | `false` | Enable temporary entity cleanup |
| `entity_ids` | `["ars_nouveau:follow_proj"]` | Entity IDs to clean up |

### Trigger Settings

| Option | Default | Description |
|--------|---------|-------------|
| `mode` | `threshold_or_schedule` | Trigger mode (`threshold`, `schedule`, `threshold_or_schedule`) |
| `minimum_tps` | `15.0` | TPS threshold to trigger cleanup |
| `maximum_mspt` | `60.0` | MSPT threshold to trigger cleanup |
| `consecutive_bad_samples` | `3` | Bad samples needed before trigger |

## Commands

All commands require operator permission level 3 or higher.

### Main Commands

| Command | Description |
|---------|-------------|
| `/bbo status` | Show mod status and current configuration |
| `/bbo reload` | Reload configuration from file |
| `/bbo debug cobblemon` | Show Cobblemon integration debug info |

### Entity Statistics

| Command | Description |
|---------|-------------|
| `/bbo entities` | Show entity statistics summary |
| `/bbo entities top` | Show top entity types by count |
| `/bbo entities dimension <dim>` | Show entities in a specific dimension |

### Cleanup Commands

| Command | Description |
|---------|-------------|
| `/bbo clear preview items` | Preview item cleanup (no removal) |
| `/bbo clear preview pokemon` | Preview Pokémon cleanup (no removal) |
| `/bbo clear preview all` | Preview all cleanup (no removal) |
| `/bbo clear now items` | Clean up items immediately |
| `/bbo clear now pokemon` | Clean up Pokémon immediately |
| `/bbo clear now all` | Clean up everything immediately |
| `/bbo clear schedule items` | Schedule item cleanup |
| `/bbo clear schedule pokemon` | Schedule Pokémon cleanup |
| `/bbo clear cancel` | Cancel scheduled cleanup |

### Command Aliases

- `/bbo` — Primary alias
- `/bigbangoptimizer` — Full name alias
- `/bboptimizer` — Short alias

## Architecture

```
src/main/java/com/pedrodalben/bigbangoptimizer/
├── BigBangOptimizer.java           # Main mod class
├── config/
│   └── OptimizerConfig.java        # Server-side TOML configuration
├── core/
│   ├── OptimizerModule.java        # Module interface
│   ├── ModuleRegistry.java         # Module registration and lifecycle
│   ├── CleanupScheduler.java       # Cleanup scheduling engine
│   ├── CleanupPlan.java            # Cleanup preview/plan
│   ├── CleanupExecution.java       # Cleanup execution
│   ├── CleanupResult.java          # Cleanup result data
│   └── OptimizerPermissions.java   # Permission checks
├── monitoring/
│   ├── PerformanceSnapshot.java    # Performance data snapshot
│   ├── TpsMonitor.java             # TPS/MSPT monitoring
│   ├── MemoryMonitor.java          # Memory usage tracking
│   ├── EntityStatisticsCollector.java # Entity counting
│   └── DimensionStatistics.java    # Per-dimension stats
├── cleanup/
│   ├── EntityCleanupModule.java    # Base entity cleanup
│   ├── ItemCleanupModule.java      # Item entity cleanup
│   ├── PokemonCleanupModule.java   # Cobblemon Pokémon cleanup
│   ├── TemporaryEntityCleanupModule.java # Temporary entity cleanup
│   ├── CleanupRunner.java          # Cleanup executor
│   └── EntityCleanupAuditLogger.java # Audit logging
├── integrations/
│   ├── OptimizerIntegration.java   # Integration interface
│   ├── cobblemon/                  # Cobblemon integration
│   ├── ftbchunks/                  # FTB Chunks integration
│   └── spark/                      # Spark integration
├── commands/
│   ├── BigBangOptimizerCommand.java # Root command /bbo
│   ├── ClearCommand.java           # Cleanup commands
│   ├── StatusCommand.java          # Status command
│   ├── EntityStatsCommand.java     # Entity stats commands
│   └── ConfigCommand.java          # Config commands
└── util/
    ├── TimeFormatters.java         # Time formatting utilities
    ├── ComponentFactory.java       # Chat component factory
    └── SafeEntityRemoval.java      # Safe entity removal
```

### Cleanup Flow

```
IDLE → SCHEDULED → WARNING → EXECUTING → COOLDOWN → IDLE
```

1. **IDLE** — Waiting for schedule or trigger
2. **SCHEDULED** — Cleanup scheduled (manual or automatic)
3. **WARNING** — Sending pre-cleanup warnings to players
4. **EXECUTING** — Removing eligible entities in batches
5. **COOLDOWN** — Waiting before next cleanup is allowed

### Design Principles

- **Modularity** — Each feature is an independent module
- **Safety** — Never removes entities without protection checks
- **Observability** — All actions are auditable and logged
- **Performance** — Batch processing, no heavy per-tick scans
- **Compatibility** — Works without optional dependencies

## Safety Guarantees

- Never loads chunks to count or remove entities
- Never performs heavy per-tick scans
- Never removes entities without explicit eligibility policy
- All automatic actions are auditable in logs
- All automatic cleanups have configurable pre-warnings
- Works without Cobblemon (dependent modules are disabled)
- Never removes player-owned Pokémon
- Pokémon cleanup is conservative by default
- Does not implement aggressive changes (growth blocking, tick rate)
- Prioritizes stability, predictability, and low tick impact

## Protection Tag

Entities can be protected from cleanup by adding the `bigbangoptimizer:protected` tag.

## License

All Rights Reserved

## Author

pedrodalben
