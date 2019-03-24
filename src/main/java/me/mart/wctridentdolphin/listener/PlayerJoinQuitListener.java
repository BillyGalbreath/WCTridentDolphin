package me.mart.wctridentdolphin.listener;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerJoinQuitListener implements Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        // when joining riding a marked dolphin, remove it
        dealWithPotentialRidingPlayer(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        // when leaving riding a marked dolphin, remove it
        dealWithPotentialRidingPlayer(event.getPlayer());
    }

    private void dealWithPotentialRidingPlayer(Player player) {
        Entity vehicle = player.getVehicle();
        if (vehicle == null) {
            return; // not riding anything
        }
        if (vehicle.getType() != EntityType.DOLPHIN) {
            return; // not riding dolphin
        }
        if (!vehicle.hasMetadata("MarkedDolphin")) {
            return; // not a marked dolphin
        }

        // remove the dolphin
        player.leaveVehicle();
        vehicle.remove();
    }
}
