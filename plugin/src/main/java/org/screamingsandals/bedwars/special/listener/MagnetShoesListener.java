package org.screamingsandals.bedwars.special.listener;

import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.api.APIUtils;
import org.screamingsandals.bedwars.events.ApplyPropertyToBoughtItemEventImpl;
import org.screamingsandals.bedwars.player.PlayerManager;
import org.screamingsandals.bedwars.utils.MiscUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.screamingsandals.lib.event.OnEvent;
import org.screamingsandals.lib.utils.annotations.Service;
import org.screamingsandals.lib.utils.annotations.methods.OnPostEnable;

@Service
public class MagnetShoesListener implements Listener {
    // Class for special item is not needed in this case (so this special item is not registered in game)
    public static final String MAGNET_SHOES_PREFIX = "Module:MagnetShoes:";

    @OnPostEnable
    public void postEnable() {
        Main.getInstance().registerBedwarsListener(this); // TODO: get rid of platform events
    }

    @OnEvent
    public void onMagnetShoesRegistered(ApplyPropertyToBoughtItemEventImpl event) {
        if (event.getPropertyName().equalsIgnoreCase("magnetshoes")) {
            var stack = event.getStack().as(ItemStack.class); // TODO: get rid of this transformation
            int probability = MiscUtils.getIntFromProperty("probability", "magnet-shoes.probability", event);

            APIUtils.hashIntoInvisibleString(stack, MAGNET_SHOES_PREFIX + probability);
            event.setStack(stack);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.isCancelled() || !(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();
        if (PlayerManager.getInstance().isPlayerInGame(player.getUniqueId())) {
            ItemStack boots = player.getInventory().getBoots();
            if (boots != null) {
                String magnetShoes = APIUtils.unhashFromInvisibleStringStartsWith(boots, MAGNET_SHOES_PREFIX);
                if (magnetShoes != null) {
                    int probability = Integer.parseInt(magnetShoes.split(":")[2]);
                    int randInt = MiscUtils.randInt(0, 100);
                    if (randInt <= probability) {
                        event.setCancelled(true);
                        player.damage(event.getDamage());
                    }
                }
            }
        }
    }
}
