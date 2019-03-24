package me.mart.wctridentdolphin.command;

import me.mart.wctridentdolphin.WCTridentDolphin;
import me.mart.wctridentdolphin.configuration.Config;
import me.mart.wctridentdolphin.configuration.Lang;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.Collections;
import java.util.List;

public class CmdWCTridentDolphin implements TabExecutor {
    private final WCTridentDolphin plugin;

    public CmdWCTridentDolphin(WCTridentDolphin plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1 && "reload".startsWith(args[0].toLowerCase())) {
            return Collections.singletonList("reload");
        }
        return null;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
        if (!sender.hasPermission("wctd.command.admin")) {
            Lang.send(sender, Lang.COMMAND_NO_PERMISSION);
            return true;
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            Config.reload();
            Lang.reload();

            Lang.send(sender, Lang.RELOAD
                    .replace("{plugin}", plugin.getName())
                    .replace("{version}", plugin.getDescription().getVersion()));
            return true;
        }

        Lang.send(sender, Lang.VERSION
                .replace("{version}", plugin.getDescription().getVersion())
                .replace("{plugin}", plugin.getName()));
        return true;
    }
}
