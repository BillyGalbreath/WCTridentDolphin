package me.mart.wctridentdolphin.command;

import me.mart.wctridentdolphin.Cooldown;
import me.mart.wctridentdolphin.WCTridentDolphin;
import me.mart.wctridentdolphin.configuration.Config;
import me.mart.wctridentdolphin.configuration.Lang;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class CmdGiveTrident implements TabExecutor {
    private final WCTridentDolphin plugin;

    private Cooldown cooldowns = new Cooldown(Cooldown.Type.TRIDENT);

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
            if (giveTrident(target, true)) {
                return true;
            }
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
            if (giveTrident(target, target == sender)) {
                return true;
            }

            // notify sender
            Lang.send(sender, Lang.GIVE_TRIDENT
                    .replace("{player}", target.getName()));
        }

        // notify receiver
        Lang.send(target, Lang.RECEIVED_TRIDENT);
        return true;
    }

    private boolean giveTrident(Player player, boolean checkCooldown) {
        boolean noCooldownPerm = !player.hasPermission("wctd.command.givetrident.nocooldown");
        if (checkCooldown && cooldowns.contains(player, noCooldownPerm)) {
            Lang.send(player, Lang.TRIDENT_ON_COOLDOWN
                    .replace("{cooldown}", cooldowns.remaining(player)));
            return true; // short circuit command early
        }

        ItemStack trident = new ItemStack(Material.TRIDENT);
        ItemMeta meta = trident.getItemMeta();
        //noinspection ConstantConditions (meta is impossible to be null. thanks, md_5)
        meta.setLore(Config.TRIDENT_LORE);
        Config.TRIDENT_ENCHANTS.forEach((enchantment, level) -> {
            if (level > 0) {
                meta.addEnchant(enchantment, level, true);
            }
        });
        trident.setItemMeta(meta);

        if (!player.getInventory().addItem(trident).isEmpty()) {
            // could not fit in inventory, drop on ground
            Item item = player.getWorld().dropItem(player.getLocation(), trident);
            item.setOwner(player.getUniqueId());
            item.setPickupDelay(0);
        }

        if (checkCooldown && noCooldownPerm) {
            cooldowns.start(player);
        }
        return false; // do not short circuit command
    }
}
