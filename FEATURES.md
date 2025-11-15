# HellasElo

HellasElo is the lightweight rating backbone for the Hellas Pixelmon network. It keeps
track of competitive outcomes, computes Elo deltas with configurable factors, and exposes
staff-facing commands for maintaining public leaderboards.

## Features
- **Persistent Elo ratings** – `EloManager` keeps player ratings in
  `config/hellas/elo/elodata.json` and automatically loads/saves them as the server starts
  and when matches are recorded.
- **Manual match recording** – `/hellas elo addmatch <winner> <loser> <remaining>` lets
  moderators quickly register battle outcomes, including bonus points for remaining
  Pokémon.
- **Leaderboard visibility** – `/hellas elo table [page]` prints a paginated leaderboard
  that surfaces the highest ranked players directly in chat.
- **Configurable tuning** – `EloConfig` exposes a base K-factor and a multiplier for
  remaining Pokémon so balancing can be performed without code changes.
- **Metadata export** – `HellasEloInfoConfig` writes human-readable module information
  (version, dependencies, feature bullets) that other Hellas tooling can consume.

## Technical Overview
- `com.xsasakihaise.hellaselo.HellasElo` is the Forge `@Mod` entry point. It checks for the
  HellasControl core, instantiates `EloConfig`/`EloManager`, and hooks into
  `FMLServerStartingEvent` plus `RegisterCommandsEvent`.
- `com.xsasakihaise.hellaselo.EloManager` is a plain Java service that handles all rating
  math, serialisation, and username caching used for Brigadier suggestions.
- Commands under `com.xsasakihaise.hellaselo.commands` register the `/hellas elo` namespace
  and forward inputs to the manager.
- `HellasEloInfoConfig` (not currently invoked elsewhere) provides version/dependency data
  for other Hellas utilities.

## Extension Points
- **Adding new Elo-affecting logic** – use `EloManager.addMatch` as the canonical helper.
  Other mods or automated systems can call it whenever a result is known; see
  `EloAddMatchCommand` for the minimum wiring required.
- **New commands or integrations** – reference `EloTableCommand` to create additional
  Brigadier commands (e.g., stats lookups). Register them through the `RegisterCommandsEvent`
  in `HellasElo` to keep everything under `/hellas elo`.
- **Custom configuration knobs** – extend `EloConfig` with extra fields. They will be
  automatically persisted when `saveConfig()` runs, so ensure defaults are set before
  invoking `loadConfig()`.

## Dependencies & Environment
- Minecraft 1.16.5 with Forge 36.2.42 (see `build.gradle`).
- Java 8 toolchain as required by Forge 1.16.5.
- Depends on the HellasControl core mod (checked during construction).
- Designed to coexist with Pixelmon servers, but the Elo logic itself is Pixelmon-agnostic
  and can be triggered by any external automation or manual command.

## Notes for Future Migration
- Event wiring resides entirely inside `HellasElo`. Updating to a different Forge/NeoForge
  version will primarily affect the imports for `FMLServerStartingEvent` and
  `RegisterCommandsEvent`.
- Brigadier APIs changed subtly in newer Minecraft versions; review the command classes when
  updating beyond 1.16.5.
- JSON persistence currently uses Gson. If migrating away from Gson or altering file paths,
  pay particular attention to `EloManager.loadData()` and `EloConfig.loadConfig()` as other
  mods depend on those locations.
