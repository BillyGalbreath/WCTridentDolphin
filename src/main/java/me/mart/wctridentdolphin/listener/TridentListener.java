package me.mart.wctridentdolphin.listener;

import me.mart.wctridentdolphin.Cooldown;
import me.mart.wctridentdolphin.WCTridentDolphin;
import me.mart.wctridentdolphin.configuration.Config;
import me.mart.wctridentdolphin.configuration.Lang;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Dolphin;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Trident;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.LazyMetadataValue;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.UUID;

public class TridentListener implements Listener {
    private final WCTridentDolphin plugin;

    private Cooldown cooldowns = new Cooldown(Cooldown.Type.DOLPHIN);

    public TridentListener(WCTridentDolphin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return; // not right clicking
        }

        ItemStack handItem = event.getItem();
        if (handItem == null) {
            return; // no item in hand
        }

        if (handItem.getType() != Material.TRIDENT) {
            return; // not holding a trident
        }

        if (!isTrident(handItem)) {
            return; // not a marked trident
        }

        Player player = event.getPlayer();
        if (!player.hasPermission("wctd.summon.dolphin")) {
            return; // no permission to use
        }

        if (Config.DOLPHIN_SPAWN_ON_SNEAK_ONLY && !player.isSneaking()) {
            return; // player must be sneaking, but is not
        }

        boolean noCooldownPerm = !player.hasPermission("wctd.summon.dolphin.nocooldown");
        if (cooldowns.contains(player, noCooldownPerm)) {
            Lang.send(player, Lang.DOLPHIN_ON_COOLDOWN
                    .replace("{cooldown}", cooldowns.remaining(player)));
            return; // on cooldown
        }

        if (Config.DESTROY_TRIDENT_AFTER_USE && !player.hasPermission("wctd.trident.keep")) {
            player.getInventory().remove(handItem);
        } else if (player.getGameMode() != GameMode.CREATIVE) {
            Damageable meta = (Damageable) handItem.getItemMeta();
            //noinspection ConstantConditions (meta is impossible to be null. thanks, md_5)
            meta.setDamage(meta.getDamage() + Config.TRIDENT_DAMAGE_ON_USE);
            handItem.setItemMeta((ItemMeta) meta);
        }

        Location eye = player.getEyeLocation();
        Vector eyeDir = eye.getDirection();
        Dolphin dolphin = player.getWorld().spawn(eye.add(eyeDir.multiply(2)), Dolphin.class);
        dolphin.setRemainingAir(dolphin.getMaximumAir());
        dolphin.setRemoveWhenFarAway(true);
        dolphin.setInvulnerable(true); // make dolphin invincible for 20 ticks
        if (!Config.DOLPHIN_INVULNERABLE) {
            plugin.getServer().getScheduler().runTaskLater(plugin,
                    () -> dolphin.setInvulnerable(false), 20);
        }
        dolphin.setAI(Config.DOLPHIN_HAS_AI);
        dolphin.setCustomName(player.getName() + "'s Dolphin");
        dolphin.setCustomNameVisible(true);
        dolphin.setVelocity(eyeDir.multiply(0.5D));
        if (!dolphin.hasMetadata("MarkedDolphin")) {
            dolphin.setMetadata("MarkedDolphin", new LazyMetadataValue(plugin, Object::new));
        }

        scheduleDeath(dolphin.getUniqueId(), player.getUniqueId());

        if (noCooldownPerm) {
            cooldowns.start(player);
        }

        Lang.send(player, Lang.DOLPHIN_SUMMONED);

        if (Config.SPAWN_FLYING_TRIDENT) {
            Trident trident = player.getWorld().spawn(player.getLocation(), Trident.class);
            trident.setVelocity(eyeDir.multiply(Config.FLYING_TRIDENT_SPEED));
            trident.setPickupStatus(Config.TRIDENT_CAN_BE_PICKED_UP ? Arrow.PickupStatus.ALLOWED : Arrow.PickupStatus.CREATIVE_ONLY);
            trident.setShooter(player);
        }
    }

    private void scheduleDeath(UUID dolphin, UUID owner) {
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            Entity entity = plugin.getServer().getEntity(dolphin);
            if (entity != null && !entity.isDead()) {
                entity.remove();
            }
            Player player = plugin.getServer().getPlayer(owner);
            if (player != null && player.isOnline()) {
                Lang.send(player, Lang.DOLPHIN_EXPIRED);
            }
        }, 20 * Config.DOLPHIN_LIVE_TIME);
    }

    private boolean isTrident(ItemStack trident) {
        if (trident == null) {
            return false;
        }
        if (trident.getType() != Material.TRIDENT) {
            return false;
        }
        if (!trident.hasItemMeta()) {
            return false;
        }
        ItemMeta meta = trident.getItemMeta();
        //noinspection ConstantConditions (meta is impossible to be null. thanks, md_5)
        if (!meta.hasLore()) {
            return false;
        }
        List<String> lore = meta.getLore();
        return lore != null && lore.equals(Config.TRIDENT_LORE);
    }
}
