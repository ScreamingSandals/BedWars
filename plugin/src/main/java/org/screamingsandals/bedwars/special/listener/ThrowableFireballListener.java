package org.screamingsandals.bedwars.special.listener;

import org.bukkit.Material;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.api.APIUtils;
import org.screamingsandals.bedwars.api.events.BedwarsApplyPropertyToBoughtItem;
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
                String[] properties = unhash.split(":");
                double damage = Double.parseDouble(properties[2]);
                float explosion = (float) Double.parseDouble(properties[2]);

                Fireball fireball = player.launchProjectile(Fireball.class);
                fireball.setIsIncendiary(false);
                fireball.setYield(explosion);
                Main.registerGameEntity(fireball, Main.getPlayerGameProfile(player).getGame());

                event.setCancelled(true);

                if (stack.getAmount() > 1) {
                    stack.setAmount(stack.getAmount() - 1);
                } else {
                    try {
                        if (player.getInventory().getItemInOffHand().equals(stack)) {
                            player.getInventory().setItemInOffHand(new ItemStack(Material.AIR));
                        } else {
                            player.getInventory().remove(stack);
                        }
                    } catch (Throwable e) {
                        player.getInventory().remove(stack);
                    }
                }

                player.updateInventory();
            }
        }
    }

    private String applyProperty(BedwarsApplyPropertyToBoughtItem event) {
        return THROWABLE_FIREBALL_PREFIX
                + MiscUtils.getDoubleFromProperty(
                "explosion", "specials.throwable-fireball.explosion", event);
    }



}
