package me.mart.wctridentdolphin.listener;

import me.mart.wctridentdolphin.configuration.Config;
import net.pl3x.bukkit.ridables.event.RidableDismountEvent;
import net.pl3x.bukkit.ridables.event.RidableMountEvent;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class AIListener implements Listener {
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMountDolphin(RidableMountEvent event) {
        if (Config.DOLPHIN_HAS_AI) {
            return; // ai is enabled
        }
        if (event.getEntityType() != EntityType.DOLPHIN) {
            return; // not mounting a dolphin
        }
        LivingEntity dolphin = (LivingEntity) event.getEntity();
        if (!dolphin.hasAI()) {
            return; // already has ai removed
        }
        dolphin.setAI(false);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDismountDolphin(RidableDismountEvent event) {
        if (Config.DOLPHIN_HAS_AI) {
            return; // ai is enabled
        }
        if (event.getEntityType() != EntityType.DOLPHIN) {
            return; // not dismounting a dolphin
        }
        LivingEntity dolphin = (LivingEntity) event.getEntity();
        if (dolphin.hasAI()) {
            return; // already has ai
        }
        ((LivingEntity) event.getEntity()).setAI(true);
    }
}
