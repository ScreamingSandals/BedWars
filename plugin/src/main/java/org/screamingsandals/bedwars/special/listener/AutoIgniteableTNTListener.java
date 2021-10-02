package org.screamingsandals.bedwars.special.listener;

import org.bukkit.entity.Entity;
import org.bukkit.entity.TNTPrimed;
import org.screamingsandals.bedwars.utils.ItemUtils;
import org.screamingsandals.bedwars.events.ApplyPropertyToBoughtItemEventImpl;
import org.screamingsandals.bedwars.events.PlayerBuildBlockEventImpl;
import org.screamingsandals.bedwars.player.PlayerManagerImpl;
import org.screamingsandals.bedwars.utils.MiscUtils;
import org.screamingsandals.bedwars.special.AutoIgniteableTNTImpl;
import org.screamingsandals.lib.block.BlockTypeHolder;
import org.screamingsandals.lib.entity.EntityHuman;
import org.screamingsandals.lib.event.OnEvent;
import org.screamingsandals.lib.event.entity.SEntityDamageByEntityEvent;
import org.screamingsandals.lib.utils.annotations.Service;

@Service
public class AutoIgniteableTNTListener {
    private static final String AUTO_IGNITEABLE_TNT_PREFIX = "Module:AutoIgniteableTnt:";

    @OnEvent
    public void onAutoIgniteableTNTRegistered(ApplyPropertyToBoughtItemEventImpl event) {
        if (event.getPropertyName().equalsIgnoreCase("autoigniteabletnt")) {
            ItemUtils.saveData(event.getStack(), applyProperty(event));
        }
    }

    @OnEvent
    public void onPlace(PlayerBuildBlockEventImpl event) {
        var game = event.getGame();
        var block = event.getBlock();
        var stack = event.getItemInHand();
        var player = event.getPlayer();
        var unhidden = ItemUtils.getIfStartsWith(stack, AUTO_IGNITEABLE_TNT_PREFIX);
        if (unhidden != null) {
            block.setType(BlockTypeHolder.air());
            var location = block.getLocation().add(0.5, 0.5, 0.5);
            int explosionTime = Integer.parseInt(unhidden.split(":")[2]);
            boolean damagePlacer = Boolean.parseBoolean(unhidden.split(":")[3]);
            AutoIgniteableTNTImpl special = new AutoIgniteableTNTImpl(game, player, game.getPlayerTeam(player), explosionTime, damagePlacer);
            special.spawn(location);
        }
    }

    @OnEvent
    public void onDamage(SEntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof EntityHuman)) {
            return;
        }

        var player = ((EntityHuman) event.getEntity()).asPlayer();

        if (!PlayerManagerImpl.getInstance().isPlayerInGame(player)) {
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
