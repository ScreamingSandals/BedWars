package org.screamingsandals.bedwars.special.listener;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.api.APIUtils;
import org.screamingsandals.bedwars.api.BedwarsAPI;
import org.screamingsandals.bedwars.api.events.BedwarsApplyPropertyToBoughtItem;
import org.screamingsandals.bedwars.special.ThrowableFireball;
import org.screamingsandals.bedwars.utils.MiscUtils;

public class ThrowableFireballListener implements Listener {

    public static final String THROWABLE_FIREBALL_PREFIX = "Module:ThrowableFireball:";


    @EventHandler
    public void onThrowableFireballRegistered(BedwarsApplyPropertyToBoughtItem event) {
        if (event.getPropertyName().equalsIgnoreCase("throwablefireball")) {
            ItemStack stack = event.getStack();

            APIUtils.hashIntoInvisibleString(stack, applyProperty(event));
        }
    }

    @EventHandler
    public void onFireballThrow(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (!Main.isPlayerInGame(player)) {
            return;
        }

        if (event.getItem() != null) {
            ItemStack stack = event.getItem();
            String unhash = APIUtils.unhashFromInvisibleStringStartsWith(stack, THROWABLE_FIREBALL_PREFIX);
            if (unhash != null && (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR)) {
                event.setCancelled(true);
                String[] properties = unhash.split(":");
                float damage = (float) Double.parseDouble(properties[2]);
                boolean incendiary = Boolean.parseBoolean(properties[3]);
                boolean perfectVelocity = Boolean.parseBoolean(properties[4]);
                boolean damageThrower = Boolean.parseBoolean(properties[5]);

                ThrowableFireball special = new ThrowableFireball(
                        BedwarsAPI.getInstance().getGameOfPlayer(player),
                        player,
                        BedwarsAPI.getInstance().getFirstRunningGame().getTeamOfPlayer(player),
                        damage,
                        incendiary,
                        perfectVelocity,
                        damageThrower);
                special.run(player.getLocation());
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player))
            return;
        Player player = (Player) event.getEntity();
        if (!Main.isPlayerInGame(player))
            return;
        if (event.getDamager() instanceof Fireball) {
            if (event.getDamager().hasMetadata(player.getUniqueId().toString())
                    && event.getDamager().hasMetadata("fireball")) {
                event.setCancelled(true);
            }
        }
    }

    private String applyProperty(BedwarsApplyPropertyToBoughtItem event) {
        return THROWABLE_FIREBALL_PREFIX
                + MiscUtils.getDoubleFromProperty("damage", "specials.throwable-fireball.damage", event) + ":"
                + MiscUtils.getBooleanFromProperty("incendiary", "specials.throwable-fireball.incendiary", event) + ":"
                + MiscUtils.getBooleanFromProperty("perfect-velocity", "specials.throwable-fireball.perfect-velocity", event) + ":"
                + MiscUtils.getBooleanFromProperty("damage-thrower", "specials.throwable-fireball.damage-thrower", event);
    }

}
