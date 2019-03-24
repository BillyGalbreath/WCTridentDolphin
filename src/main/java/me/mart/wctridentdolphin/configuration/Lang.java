package me.mart.wctridentdolphin.configuration;

import me.mart.wctridentdolphin.WCTridentDolphin;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class Lang {
    public static String COMMAND_NO_PERMISSION;
    public static String PLAYER_COMMAND;
    public static String PLAYER_NOT_FOUND;

    public static String GIVE_TRIDENT;
    public static String RECEIVED_TRIDENT;

    public static String TRIDENT_ON_COOLDOWN;

    public static String DOLPHIN_EXPIRED;

    public static String VERSION;
    public static String RELOAD;

    private Lang() {
    }

    public static void reload() {
        String langFile = Config.LANGUAGE_FILE;
        JavaPlugin plugin = WCTridentDolphin.getInstance();
        File configFile = new File(plugin.getDataFolder(), langFile);
        plugin.saveResource(Config.LANGUAGE_FILE, false);
        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);

        COMMAND_NO_PERMISSION = config.getString("command-no-permission", "&4You do not have permission for this command!");
        PLAYER_COMMAND = config.getString("player-command", "&4Player only command!");
        PLAYER_NOT_FOUND = config.getString("player-not-found", "&4That player is not online!");

        GIVE_TRIDENT = config.getString("give-trident", "&bGiving a Dolphin Summoning Trident to &1{player}");
        RECEIVED_TRIDENT = config.getString("received-trident", "&bYou received a Dolphin Summoning Trident!");

        TRIDENT_ON_COOLDOWN = config.getString("trident-on-cooldown", "&1Cannot summon another trident for &c{cooldown} &1s");

        DOLPHIN_EXPIRED = config.getString("dolphin-expired", "&bYour dolphin has expired ...");

        VERSION = config.getString("version", "&d{plugin} v{version}");
        RELOAD = config.getString("reload", "&d{plugin} v{version} reloaded.");
    }

    public static void send(CommandSender recipient, String message) {
        if (message == null) {
            return; // do not send blank messages
        }
        message = ChatColor.translateAlternateColorCodes('&', message);
        if (ChatColor.stripColor(message).isEmpty()) {
            return; // do not send blank messages
        }

        for (String part : message.split("\n")) {
            recipient.sendMessage(part);
        }
    }
}
