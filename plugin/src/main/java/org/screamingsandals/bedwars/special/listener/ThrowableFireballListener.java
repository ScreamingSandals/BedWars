package org.screamingsandals.bedwars.special.listener;

import org.bukkit.Material;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.api.APIUtils;
import org.screamingsandals.bedwars.events.ApplyPropertyToBoughtItemEventImpl;
import org.screamingsandals.bedwars.player.PlayerManager;
import org.screamingsandals.bedwars.utils.MiscUtils;
import org.screamingsandals.lib.event.OnEvent;
import org.screamingsandals.lib.utils.annotations.Service;
import org.screamingsandals.lib.utils.annotations.methods.OnPostEnable;

@Service
public class ThrowableFireballListener implements Listener {

    public static final String THROWABLE_FIREBALL_PREFIX = "Module:ThrowableFireball:";

    @OnPostEnable
    private void postEnable() {
        Main.getInstance().registerBedwarsListener(this); // TODO: get rid of platform events
    }


    @OnEvent
    public void onThrowableFireballRegistered(ApplyPropertyToBoughtItemEventImpl event) {
        if (event.getPropertyName().equalsIgnoreCase("throwablefireball")) {
            var stack = event.getStack().as(ItemStack.class); // TODO: get rid of this transformation

            APIUtils.hashIntoInvisibleString(stack, applyProperty(event));
            event.setStack(stack);
        }
    }

    @EventHandler
    public void onFireballThrow(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (!PlayerManager.getInstance().isPlayerInGame(player.getUniqueId())) {
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
                Main.registerGameEntity(fireball, PlayerManager.getInstance().getGameOfPlayer(player.getUniqueId()).orElseThrow());

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

    private String applyProperty(ApplyPropertyToBoughtItemEventImpl event) {
        return THROWABLE_FIREBALL_PREFIX
                + MiscUtils.getDoubleFromProperty(
                "explosion", "specials.throwable-fireball.explosion", event);
    }



}
