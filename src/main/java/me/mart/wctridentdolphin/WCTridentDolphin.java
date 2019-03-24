package me.mart.wctridentdolphin;

import me.mart.wctridentdolphin.command.CmdGiveTrident;
import me.mart.wctridentdolphin.command.CmdWCTridentDolphin;
import me.mart.wctridentdolphin.configuration.Config;
import me.mart.wctridentdolphin.configuration.Lang;
import me.mart.wctridentdolphin.listener.AIListener;
import me.mart.wctridentdolphin.listener.PlayerJoinQuitListener;
import me.mart.wctridentdolphin.listener.TridentListener;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Dolphin;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class WCTridentDolphin extends JavaPlugin {
    private static WCTridentDolphin instance;

    public WCTridentDolphin() {
        instance = this;
    }

    public static WCTridentDolphin getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        Config.reload();
        Lang.reload();

        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new PlayerJoinQuitListener(), this);
        pm.registerEvents(new TridentListener(this), this);

        if (pm.isPluginEnabled("Ridables")) {
            pm.registerEvents(new AIListener(), this);
        } else {
            Logger.warn("Ridables plugin not found. Skipped registering AI listener!");
        }

        registerCommand("givetrident", new CmdGiveTrident(this));
        registerCommand("wctd", new CmdWCTridentDolphin(this));
    }

    @Override
    public void onDisable() {
        // remove all marked dolphins
        getServer().getWorlds().forEach(world ->
                world.getEntitiesByClass(Dolphin.class).stream()
                        .filter(dolphin -> dolphin.hasMetadata("MarkedDolphin"))
                        .forEach(Entity::remove)
        );
    }

    private void registerCommand(String command, CommandExecutor executor) {
        PluginCommand cmd = getCommand(command);
        if (cmd != null) {
            cmd.setExecutor(executor);
        }
    }
}
