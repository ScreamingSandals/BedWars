package org.screamingsandals.bedwars.special.listener;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.inventory.ItemStack;
import org.screamingsandals.bedwars.api.APIUtils;
import org.screamingsandals.bedwars.events.ApplyPropertyToBoughtItemEventImpl;
import org.screamingsandals.bedwars.events.PlayerBuildBlockEventImpl;
import org.screamingsandals.bedwars.player.PlayerManager;
import org.screamingsandals.bedwars.utils.MiscUtils;
import org.screamingsandals.bedwars.special.AutoIgniteableTNTImpl;
import org.screamingsandals.lib.entity.EntityHuman;
import org.screamingsandals.lib.event.OnEvent;
import org.screamingsandals.lib.event.entity.SEntityDamageByEntityEvent;
import org.screamingsandals.lib.material.MaterialMapping;
import org.screamingsandals.lib.utils.annotations.Service;

@Service
public class AutoIgniteableTNTListener {
    private static final String AUTO_IGNITEABLE_TNT_PREFIX = "Module:AutoIgniteableTnt:";

    @OnEvent
    public void onAutoIgniteableTNTRegistered(ApplyPropertyToBoughtItemEventImpl event) {
        if (event.getPropertyName().equalsIgnoreCase("autoigniteabletnt")) {
            var stack = event.getStack().as(ItemStack.class); // TODO: get rid of this transformation
            APIUtils.hashIntoInvisibleString(stack, applyProperty(event));
            event.setStack(stack);
        }
    }

    @OnEvent
    public void onPlace(PlayerBuildBlockEventImpl event) {
        var game = event.getGame();
        var block = event.getBlock();
        var stack = event.getItemInHand();
        var player = event.getPlayer();
        var unhidden = APIUtils.unhashFromInvisibleStringStartsWith(stack.as(ItemStack.class), AUTO_IGNITEABLE_TNT_PREFIX); // TODO: get rid of this transformation
        if (unhidden != null) {
            block.setType(MaterialMapping.getAir());
            var location = block.getLocation().add(0.5, 0.5, 0.5);
            int explosionTime = Integer.parseInt(unhidden.split(":")[2]);
            boolean damagePlacer = Boolean.parseBoolean(unhidden.split(":")[3]);
            AutoIgniteableTNTImpl special = new AutoIgniteableTNTImpl(game, player, game.getPlayerTeam(player), explosionTime, damagePlacer);
            special.spawn(location.as(Location.class));
        }
    }

    @OnEvent
    public void onDamage(SEntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof EntityHuman)) {
            return;
        }

        var player = ((EntityHuman) event.getEntity()).asPlayer();

        if (!PlayerManager.getInstance().isPlayerInGame(player)) {
            return;
        }

        var bukkitDamager = event.getDamager().as(Entity.class);
        if (bukkitDamager instanceof TNTPrimed) {
            var tnt = (TNTPrimed) bukkitDamager;
            if (tnt.hasMetadata(player.getUuid().toString()) && tnt.hasMetadata("autoignited")) {
                event.setCancelled(true);
            }
        }
    }

    private String applyProperty(ApplyPropertyToBoughtItemEventImpl event) {
        return AUTO_IGNITEABLE_TNT_PREFIX
                + MiscUtils.getIntFromProperty("explosion-time", "specials.auto-igniteable-tnt.explosion-time", event)
                + ":" + MiscUtils.getBooleanFromProperty("damage-placer", "specials.auto-igniteable-tnt.damage-placer",
                        event);
    }
}
