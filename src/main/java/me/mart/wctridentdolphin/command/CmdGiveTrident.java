package me.mart.wctridentdolphin.command;

import me.mart.wctridentdolphin.WCTridentDolphin;
import me.mart.wctridentdolphin.configuration.Config;
import me.mart.wctridentdolphin.configuration.Lang;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class CmdGiveTrident implements TabExecutor {
    private final WCTridentDolphin plugin;

    private final HashMap<UUID, Long> cooldowns = new HashMap<>();

    public CmdGiveTrident(WCTridentDolphin plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
        if (!sender.hasPermission("wctd.command.givetrident")) {
            Lang.send(sender, Lang.COMMAND_NO_PERMISSION);
            return true;
        }

        Player target;
        if (args.length < 1) {
            if (!(sender instanceof Player)) {
                Lang.send(sender, Lang.PLAYER_COMMAND);
                return true;
            }

            // give trident to self
            target = (Player) sender;
            giveTrident(target, true);
        } else {
            if (!sender.hasPermission("wctd.command.givetrident.others")) {
                Lang.send(sender, Lang.COMMAND_NO_PERMISSION);
                return true;
            }

            target = plugin.getServer().getPlayer(args[0]);
            if (target == null) {
                Lang.send(sender, Lang.PLAYER_NOT_FOUND);
                return true;
            }

            // give trident to target
            giveTrident(target, target == sender);

            // notify sender
            Lang.send(sender, Lang.GIVE_TRIDENT
                    .replace("{player}", target.getName()));
        }

        // notify receiver
        Lang.send(target, Lang.RECEIVED_TRIDENT);
        return true;
    }

    private void giveTrident(Player player, boolean checkCooldown) {
        boolean noCooldownPerm = !player.hasPermission("wctd.command.givetrident.nocooldown");
        if (checkCooldown && isOnCooldown(player, noCooldownPerm)) {
            Lang.send(player, Lang.TRIDENT_ON_COOLDOWN
                    .replace("{cooldown}", getRemaining(player)));
            return;
        }

        ItemStack trident = new ItemStack(Material.TRIDENT);
        ItemMeta meta = trident.getItemMeta();
        //noinspection ConstantConditions (meta is impossible to be null. thanks, md_5)
        meta.setLore(Collections.singletonList(Config.TRIDENT_LORE));
        Config.TRIDENT_ENCHANTS.forEach((enchantment, level) -> {
            if (level > 0) {
                meta.addEnchant(enchantment, level, true);
            }
        });
        trident.setItemMeta(meta);

        player.getInventory().addItem(trident);

        if (checkCooldown && noCooldownPerm) {
            setCooldown(player);
        }
    }

    private boolean isOnCooldown(Player player, boolean noCooldownPerm) {
        long remaining = cooldowns.get(player.getUniqueId());
        if (remaining <= 0) {
            cooldowns.remove(player.getUniqueId());
        }
        return remaining > 0 && noCooldownPerm;
    }

    private void setCooldown(Player player) {
        cooldowns.put(player.getUniqueId(), System.currentTimeMillis() / 1000L + Config.TRIDENT_GET_COOLDOWN);
    }

    private String getRemaining(Player player) {
        long seconds = cooldowns.get(player.getUniqueId()) - System.currentTimeMillis() / 1000L;
        // https://stackoverflow.com/a/40487511/3530727
        return Duration.ofSeconds(seconds)
                .toString()
                .substring(2)
                .replaceAll("(\\d[HMS])(?!$)", "$1 ")
                .toLowerCase();
    }
}
