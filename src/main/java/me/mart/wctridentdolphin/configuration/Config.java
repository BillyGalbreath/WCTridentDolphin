package me.mart.wctridentdolphin.configuration;

import me.mart.wctridentdolphin.WCTridentDolphin;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class Config {
    public static boolean COLOR_LOGS;
    public static boolean DEBUG_MODE;
    public static String LANGUAGE_FILE;

    public static int DOLPHIN_LIVE_TIME;
    public static int DOLPHIN_SPAWN_COOLDOWN;
    public static boolean DOLPHIN_SPAWN_ON_SNEAK_ONLY;
    public static boolean DOLPHIN_INVULNERABLE;
    public static boolean DOLPHIN_HAS_AI;

    public static int TRIDENT_GET_COOLDOWN;
    public static String TRIDENT_LORE;

    public static boolean SPAWN_FLYING_TRIDENT;
    public static int FLYING_TRIDENT_SPEED;

    public static boolean DESTROY_TRIDENT_AFTER_USE;
    public static int TRIDENT_DAMAGE_ON_USE;
    public static boolean TRIDENT_CAN_BE_PICKED_UP;

    public static Map<Enchantment, Integer> TRIDENT_ENCHANTS = new HashMap<>();

    private Config() {
    }

    public static void reload() {
        JavaPlugin plugin = WCTridentDolphin.getInstance();
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        FileConfiguration config = plugin.getConfig();

        COLOR_LOGS = config.getBoolean("color-logs", true);
        DEBUG_MODE = config.getBoolean("debug-mode", false);
        LANGUAGE_FILE = config.getString("language-file", "lang-en.yml");

        DOLPHIN_LIVE_TIME = config.getInt("dolphin-live-time", 60);
        DOLPHIN_SPAWN_COOLDOWN = config.getInt("dolphin-spawn-cooldown", 120);
        DOLPHIN_SPAWN_ON_SNEAK_ONLY = config.getBoolean("dolphin-spawn-on-sneak-only", true);
        DOLPHIN_INVULNERABLE = config.getBoolean("dolphin-invulnerable", false);
        DOLPHIN_HAS_AI = config.getBoolean("dolphin-has-ai", true);

        TRIDENT_GET_COOLDOWN = config.getInt("trident-get-cooldown", 120);
        //noinspection ConstantConditions (getString() with a default cannot be null. thanks, md_5)
        TRIDENT_LORE = ChatColor.translateAlternateColorCodes('&', config.getString("trident-lore", "Summon Dolphin"));

        SPAWN_FLYING_TRIDENT = config.getBoolean("spawn-flying-trident", true);
        FLYING_TRIDENT_SPEED = config.getInt("flying-trident-speed", 2);

        DESTROY_TRIDENT_AFTER_USE = config.getBoolean("destroy-trident-after-use", false);
        TRIDENT_DAMAGE_ON_USE = config.getInt("trident-damage-on-use", 1);
        TRIDENT_CAN_BE_PICKED_UP = config.getBoolean("trident-can-be-picked-up", true);

        TRIDENT_ENCHANTS.clear();
        ConfigurationSection enchants = config.getConfigurationSection("trident.enchants");
        if (enchants != null) {
            for (String key : enchants.getKeys(false)) {
                //noinspection deprecation
                Enchantment enchantment = Enchantment.getByName(key);
                if (enchantment != null) {
                    TRIDENT_ENCHANTS.put(enchantment, config.getInt("trident.enchants." + key, 1));
                }
            }
        }
    }
}
