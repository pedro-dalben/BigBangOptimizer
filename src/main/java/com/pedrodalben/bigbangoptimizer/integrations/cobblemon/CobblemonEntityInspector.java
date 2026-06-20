package com.pedrodalben.bigbangoptimizer.integrations.cobblemon;

import com.pedrodalben.bigbangoptimizer.BigBangOptimizer;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.MinecraftServer;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.ArrayList;
import java.util.List;

public class CobblemonEntityInspector {
    private static final String POKEMON_ENTITY_CLASS = "com.cobblemon.mod.common.entity.pokemon.PokemonEntity";

    private boolean pokemonAvailable = false;

    private boolean hasGetPokemon = false;
    private boolean hasGetBattle = false;
    private boolean hasIsEvolving = false;
    private boolean hasGetTethering = false;
    private boolean hasIsBattleClone = false;
    private boolean hasIsLegendary = false;
    private boolean hasIsMythical = false;
    private boolean hasIsPlayerOwned = false;
    private boolean hasIsNPCOwned = false;
    private boolean hasIsWild = false;

    public CobblemonEntityInspector() {
        verifyAvailableChecks();
    }

    private void verifyAvailableChecks() {
        try {
            Class<?> clazz = Class.forName(POKEMON_ENTITY_CLASS);
            pokemonAvailable = true;

            hasGetPokemon = hasMethod(clazz, "getPokemon");
            hasGetBattle = hasMethod(clazz, "getBattle");
            hasIsEvolving = hasMethod(clazz, "isEvolving");
            hasGetTethering = hasMethod(clazz, "getTethering");

            Class<?> pokemonClass = Class.forName("com.cobblemon.mod.common.pokemon.Pokemon");
            hasIsPlayerOwned = hasMethod(pokemonClass, "isPlayerOwned");
            hasIsNPCOwned = hasMethod(pokemonClass, "isNPCOwned");
            hasIsWild = hasMethod(pokemonClass, "isWild");
            hasIsLegendary = hasMethod(pokemonClass, "isLegendary");
            hasIsMythical = hasMethod(pokemonClass, "isMythical");
            hasIsBattleClone = hasMethod(pokemonClass, "isBattleClone");

        } catch (ClassNotFoundException e) {
            BigBangOptimizer.LOGGER.warn("[BigBangOptimizer] Cobblemon classes not found: {}", e.getMessage());
        }
    }

    private boolean hasMethod(Class<?> clazz, String name) {
        for (java.lang.reflect.Method m : clazz.getMethods()) {
            if (m.getName().equals(name) && m.getParameterCount() == 0) {
                return true;
            }
        }
        return false;
    }

    private Object invokeSafe(Object obj, String methodName) {
        try {
            java.lang.reflect.Method m = obj.getClass().getMethod(methodName);
            return m.invoke(obj);
        } catch (Exception e) {
            return null;
        }
    }

    public boolean isPokemon(Entity entity) {
        if (!pokemonAvailable) return false;
        return entity instanceof PokemonEntity;
    }

    public boolean isPlayerOwned(Entity entity) {
        if (!(entity instanceof PokemonEntity pokemonEntity)) return true;
        if (!hasGetPokemon || !hasIsPlayerOwned) return true;
        try {
            Object pokemon = pokemonEntity.getPokemon();
            if (pokemon == null) return true;
            java.lang.reflect.Method m = pokemon.getClass().getMethod("isPlayerOwned");
            return (Boolean) m.invoke(pokemon);
        } catch (Exception e) {
            return true;
        }
    }

    public boolean isInBattle(Entity entity) {
        if (!(entity instanceof PokemonEntity pokemonEntity)) return true;
        if (!hasGetBattle) return true;
        try {
            return pokemonEntity.getBattle() != null;
        } catch (Exception e) {
            return true;
        }
    }

    public boolean isEvolving(Entity entity) {
        if (!(entity instanceof PokemonEntity pokemonEntity)) return true;
        if (!hasIsEvolving) return true;
        try {
            return pokemonEntity.isEvolving();
        } catch (Exception e) {
            return true;
        }
    }

    public boolean isShiny(Entity entity) {
        if (!(entity instanceof PokemonEntity pokemonEntity)) return true;
        if (!hasGetPokemon) return true;
        try {
            Object pokemon = pokemonEntity.getPokemon();
            if (pokemon == null) return true;
            java.lang.reflect.Method m = pokemon.getClass().getMethod("getShiny");
            return (Boolean) m.invoke(pokemon);
        } catch (Exception e) {
            return true;
        }
    }

    public boolean isPastureTethered(Entity entity) {
        if (!(entity instanceof PokemonEntity pokemonEntity)) return true;
        if (!hasGetTethering) return true;
        try {
            return pokemonEntity.getTethering() != null;
        } catch (Exception e) {
            return true;
        }
    }

    public boolean isBattleClone(Entity entity) {
        if (!(entity instanceof PokemonEntity pokemonEntity)) return true;
        if (!hasGetPokemon || !hasIsBattleClone) return true;
        try {
            Object pokemon = pokemonEntity.getPokemon();
            if (pokemon == null) return true;
            java.lang.reflect.Method m = pokemon.getClass().getMethod("isBattleClone");
            return (Boolean) m.invoke(pokemon);
        } catch (Exception e) {
            return true;
        }
    }

    public boolean isMountedOrHasPassengers(Entity entity) {
        try {
            return !entity.getPassengers().isEmpty();
        } catch (Exception e) {
            return true;
        }
    }

    public boolean isRecentlySpawned(Entity entity, int minimumAgeTicks) {
        return entity.tickCount < minimumAgeTicks;
    }

    public boolean isNearAnyPlayer(Entity entity, double radius) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) return true;
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (player.distanceToSqr(entity) <= radius * radius) {
                return true;
            }
        }
        return false;
    }

    public boolean isNpcRelated(Entity entity) {
        if (!(entity instanceof PokemonEntity pokemonEntity)) return true;
        if (!hasGetPokemon || !hasIsNPCOwned) return true;
        try {
            Object pokemon = pokemonEntity.getPokemon();
            if (pokemon == null) return true;
            java.lang.reflect.Method m = pokemon.getClass().getMethod("isNPCOwned");
            return (Boolean) m.invoke(pokemon);
        } catch (Exception e) {
            return true;
        }
    }

    public boolean isLegendaryOrMythical(Entity entity) {
        if (!(entity instanceof PokemonEntity pokemonEntity)) return true;
        if (!hasGetPokemon) return true;
        try {
            Object pokemon = pokemonEntity.getPokemon();
            if (pokemon == null) return true;
            boolean legendary = false;
            boolean mythical = false;
            if (hasIsLegendary) {
                java.lang.reflect.Method m = pokemon.getClass().getMethod("isLegendary");
                legendary = (Boolean) m.invoke(pokemon);
            }
            if (hasIsMythical) {
                java.lang.reflect.Method m = pokemon.getClass().getMethod("isMythical");
                mythical = (Boolean) m.invoke(pokemon);
            }
            return legendary || mythical;
        } catch (Exception e) {
            return true;
        }
    }

    public String getAvailableChecks() {
        List<String> checks = new ArrayList<>();
        if (pokemonAvailable) checks.add("pokemon_entity");
        if (hasGetPokemon && hasIsPlayerOwned) checks.add("player_owned");
        if (hasGetBattle) checks.add("in_battle");
        if (hasIsEvolving) checks.add("evolving");
        if (hasGetPokemon) checks.add("shiny");
        if (hasGetTethering) checks.add("pasture_tethered");
        if (hasGetPokemon && hasIsBattleClone) checks.add("battle_clone");
        checks.add("mounted");
        checks.add("near_player");
        if (hasGetPokemon && hasIsNPCOwned) checks.add("npc_related");
        if (hasGetPokemon && hasIsLegendary) checks.add("legendary_mythical");
        return String.join(", ", checks);
    }
}
