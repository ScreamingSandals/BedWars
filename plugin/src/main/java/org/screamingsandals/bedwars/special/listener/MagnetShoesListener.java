package org.screamingsandals.bedwars.special.listener;

import org.screamingsandals.bedwars.utils.ItemUtils;
import org.screamingsandals.bedwars.events.ApplyPropertyToBoughtItemEventImpl;
import org.screamingsandals.bedwars.player.PlayerManagerImpl;
import org.screamingsandals.bedwars.utils.MiscUtils;
import org.screamingsandals.lib.event.OnEvent;
import org.screamingsandals.lib.event.entity.SEntityDamageEvent;
import org.screamingsandals.lib.player.PlayerWrapper;
import org.screamingsandals.lib.utils.annotations.Service;

@Service
public class MagnetShoesListener {
    // Class for special item is not needed in this case (so this special item is not registered in game)
    private static final String MAGNET_SHOES_PREFIX = "Module:MagnetShoes:";

    @OnEvent
    public void onMagnetShoesRegistered(ApplyPropertyToBoughtItemEventImpl event) {
        if (event.getPropertyName().equalsIgnoreCase("magnetshoes")) {
            int probability = MiscUtils.getIntFromProperty("probability", "magnet-shoes.probability", event);

            ItemUtils.saveData(event.getStack(), MAGNET_SHOES_PREFIX + probability);
        }
    }

    @OnEvent
    public void onDamage(SEntityDamageEvent event) {
        if (event.isCancelled() || !(event.getEntity() instanceof PlayerWrapper)) {
            return;
        }

        var player = (PlayerWrapper) event.getEntity();
        if (PlayerManagerImpl.getInstance().isPlayerInGame(player)) {
            var boots = player.getPlayerInventory().getBoots();
            if (boots != null) {
                String magnetShoes = ItemUtils.getIfStartsWith(boots, MAGNET_SHOES_PREFIX);
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
