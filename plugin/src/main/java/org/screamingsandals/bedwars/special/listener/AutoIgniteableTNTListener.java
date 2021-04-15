package org.screamingsandals.bedwars.special.listener;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.api.APIUtils;
import org.screamingsandals.bedwars.events.ApplyPropertyToBoughtItemEventImpl;
import org.screamingsandals.bedwars.events.PlayerBuildBlockEventImpl;
import org.screamingsandals.bedwars.player.PlayerManager;
import org.screamingsandals.bedwars.utils.MiscUtils;
import org.screamingsandals.bedwars.special.AutoIgniteableTNT;
import org.screamingsandals.lib.event.OnEvent;
import org.screamingsandals.lib.material.MaterialMapping;
import org.screamingsandals.lib.material.builder.ItemFactory;
import org.screamingsandals.lib.utils.annotations.Service;
import org.screamingsandals.lib.utils.annotations.methods.OnPostEnable;
import org.screamingsandals.lib.world.BlockMapper;

@Service(dependsOn = {
        MaterialMapping.class,
        BlockMapper.class,
        ItemFactory.class
})
public class AutoIgniteableTNTListener implements Listener {
    private static final String AUTO_IGNITEABLE_TNT_PREFIX = "Module:AutoIgniteableTnt:";

    @OnPostEnable
    public void postEnable() {
        Main.getInstance().registerBedwarsListener(this); // TODO: get rid of platform events
    }

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
            AutoIgniteableTNT special = new AutoIgniteableTNT(game, player.as(Player.class), game.getPlayerTeam(player), explosionTime,
                    damagePlacer);
            special.spawn(location.as(Location.class));
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();
        if (!PlayerManager.getInstance().isPlayerInGame(player.getUniqueId())) {
            return;
        }

        if (event.getDamager() instanceof TNTPrimed) {
            TNTPrimed tnt = (TNTPrimed) event.getDamager();
            if (tnt.hasMetadata(player.getUniqueId().toString()) && tnt.hasMetadata("autoignited")) {
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
