package org.screamingsandals.bedwars.special.listener;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.api.APIUtils;
import org.screamingsandals.bedwars.api.events.BedwarsApplyPropertyToBoughtItem;
import org.screamingsandals.bedwars.utils.MiscUtils;
import org.screamingsandals.bedwars.api.events.BedwarsPlayerBuildBlock;
import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.bedwars.special.AutoIgniteableTNT;

public class AutoIgniteableTNTListener implements Listener {
    private static final String AUTO_IGNITEABLE_TNT_PREFIX = "Module:AutoIgniteableTnt:";

    @EventHandler
    public void onAutoIgniteableTNTRegistered(BedwarsApplyPropertyToBoughtItem event) {
        if (event.getPropertyName().equalsIgnoreCase("autoigniteabletnt")) {
            ItemStack stack = event.getStack();
            APIUtils.hashIntoInvisibleString(stack, applyProperty(event));
        }
    }

    @EventHandler
    public void onPlace(BedwarsPlayerBuildBlock event) {
        Game game = event.getGame();
        Block block = event.getBlock();
        ItemStack stack = event.getItemInHand();
        Player player = event.getPlayer();
        String unhidden = APIUtils.unhashFromInvisibleStringStartsWith(stack, AUTO_IGNITEABLE_TNT_PREFIX);
        if (unhidden != null) {
            block.setType(Material.AIR);
            Location location = block.getLocation().add(0.5, 0.5, 0.5);
            int explosionTime = Integer.parseInt(unhidden.split(":")[2]);
            boolean damagePlacer = Boolean.parseBoolean(unhidden.split(":")[3]);
            AutoIgniteableTNT special = new AutoIgniteableTNT(game, player, game.getTeamOfPlayer(player), explosionTime,
                    damagePlacer);
            special.spawn(location);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player))
            return;
        Player player = (Player) event.getEntity();
        if (!Main.isPlayerInGame(player))
            return;
        if (event.getDamager() instanceof TNTPrimed) {
            TNTPrimed tnt = (TNTPrimed) event.getDamager();
            if (tnt.hasMetadata(player.getUniqueId().toString()) && tnt.hasMetadata("autoignited")) {
                event.setCancelled(true);
            }
        }
    }

    private String applyProperty(BedwarsApplyPropertyToBoughtItem event) {
        return AUTO_IGNITEABLE_TNT_PREFIX
                + MiscUtils.getIntFromProperty("explosion-time", "specials.auto-igniteable-tnt.explosion-time", event)
                + ":" + MiscUtils.getBooleanFromProperty("damage-placer", "specials.auto-igniteable-tnt.damage-placer",
                        event);
    }
}
