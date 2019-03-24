package me.mart.wctridentdolphin;

import me.mart.wctridentdolphin.configuration.Config;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.HashMap;
import java.util.UUID;

public class Cooldown {
    private final HashMap<UUID, Long> cooldowns = new HashMap<>();
    private final Type type;

    public Cooldown(Type type) {
        this.type = type;
    }

    public boolean contains(Player player, boolean noCooldownPerm) {
        Long remaining = cooldowns.get(player.getUniqueId());
        if (remaining == null) {
            return false;
        }
        remaining -= System.currentTimeMillis() / 1000L;
        if (remaining <= 0) {
            cooldowns.remove(player.getUniqueId());
        }
        return remaining > 0 && noCooldownPerm;
    }

    public void start(Player player) {
        cooldowns.put(player.getUniqueId(), System.currentTimeMillis() / 1000L + type.getCooldown());
    }

    public String remaining(Player player) {
        long seconds = cooldowns.get(player.getUniqueId()) - System.currentTimeMillis() / 1000L;
        // https://stackoverflow.com/a/40487511/3530727
        return Duration.ofSeconds(seconds)
                .toString()
                .substring(2)
                .replaceAll("(\\d[HMS])(?!$)", "$1 ")
                .toLowerCase();
    }

    public enum Type {
        DOLPHIN,
        TRIDENT;

        public int getCooldown() {
            switch (this) {
                case DOLPHIN:
                    return Config.DOLPHIN_SPAWN_COOLDOWN;
                case TRIDENT:
                    return Config.TRIDENT_GET_COOLDOWN;
                default:
                    return 0;
            }
        }
    }
}
