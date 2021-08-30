package org.screamingsandals.bedwars.special.listener;

import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.screamingsandals.bedwars.BedWarsPlugin;
import org.screamingsandals.bedwars.api.APIUtils;
import org.screamingsandals.bedwars.entities.EntitiesManagerImpl;
import org.screamingsandals.bedwars.events.ApplyPropertyToBoughtItemEventImpl;
import org.screamingsandals.bedwars.player.PlayerManagerImpl;
import org.screamingsandals.bedwars.utils.MiscUtils;
import org.screamingsandals.lib.event.OnEvent;
import org.screamingsandals.lib.event.player.SPlayerInteractEvent;
import org.screamingsandals.lib.material.builder.ItemFactory;
import org.screamingsandals.lib.utils.annotations.Service;

@Service
public class ThrowableFireballListener {

    public static final String THROWABLE_FIREBALL_PREFIX = "Module:ThrowableFireball:";

    @OnEvent
    public void onThrowableFireballRegistered(ApplyPropertyToBoughtItemEventImpl event) {
        if (event.getPropertyName().equalsIgnoreCase("throwablefireball")) {
            var stack = event.getStack().as(ItemStack.class); // TODO: get rid of this transformation

            APIUtils.hashIntoInvisibleString(stack, applyProperty(event));
            event.setStack(stack);
        }
    }

    @OnEvent
    public void onFireballThrow(SPlayerInteractEvent event) {
        var player = event.getPlayer();

        if (!PlayerManagerImpl.getInstance().isPlayerInGame(player)) {
            return;
        }

        if (event.getItem() != null) {
            var stack = event.getItem();
            var unhash = APIUtils.unhashFromInvisibleStringStartsWith(stack.as(ItemStack.class), THROWABLE_FIREBALL_PREFIX);
            if (unhash != null && (event.getAction() == SPlayerInteractEvent.Action.RIGHT_CLICK_BLOCK || event.getAction() == SPlayerInteractEvent.Action.RIGHT_CLICK_AIR)) {
                var properties = unhash.split(":");
                var explosion = (float) Double.parseDouble(properties[2]);

                var fireball = player.as(Player.class).launchProjectile(Fireball.class);
                fireball.setIsIncendiary(false);
                fireball.setYield(explosion);
                EntitiesManagerImpl.getInstance().addEntityToGame(fireball, PlayerManagerImpl.getInstance().getGameOfPlayer(player).orElseThrow());

                event.setCancelled(true);

                stack.setAmount(1);
                try {
                    if (player.getPlayerInventory().getItemInOffHand().equals(stack)) {
                        player.getPlayerInventory().setItemInOffHand(ItemFactory.getAir());
                    } else {
                        player.getPlayerInventory().removeItem(stack);
                    }
                } catch (Throwable e) {
                    player.getPlayerInventory().removeItem(stack);
                }

                player.as(Player.class).updateInventory();
            }
        }
    }

    private String applyProperty(ApplyPropertyToBoughtItemEventImpl event) {
        return THROWABLE_FIREBALL_PREFIX
                + MiscUtils.getDoubleFromProperty(
                "explosion", "specials.throwable-fireball.explosion", event);
    }



}
