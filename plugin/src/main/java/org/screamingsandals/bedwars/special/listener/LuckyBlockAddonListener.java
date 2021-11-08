package org.screamingsandals.bedwars.special.listener;

import org.screamingsandals.bedwars.events.ApplyPropertyToBoughtItemEventImpl;
import org.screamingsandals.bedwars.events.PlayerBreakBlockEventImpl;
import org.screamingsandals.bedwars.events.PlayerBuildBlockEventImpl;
import org.screamingsandals.bedwars.special.LuckyBlockImpl;
import org.screamingsandals.bedwars.utils.ItemUtils;
import org.screamingsandals.lib.event.OnEvent;
import org.screamingsandals.lib.utils.annotations.Service;

import java.util.List;
import java.util.Map;

@Service
public class LuckyBlockAddonListener {
    private static final String LUCKY_BLOCK_PREFIX = "Module:LuckyBlock:";

    @OnEvent
    @SuppressWarnings("unchecked")
    public void onLuckyBlockRegistered(ApplyPropertyToBoughtItemEventImpl event) {
        if (event.getPropertyName().equalsIgnoreCase("luckyblock")) {
            var lucky = new LuckyBlockImpl(event.getGame(), event.getPlayer(),
                    event.getGame().getPlayerTeam(event.getPlayer()),
                    (List<Map<String, Object>>) event.getProperty("data"));

            ItemUtils.saveData(event.getStack(), LUCKY_BLOCK_PREFIX + System.identityHashCode(lucky));
        }
    }

    @OnEvent
    public void onLuckyBlockBuild(PlayerBuildBlockEventImpl event) {
        if (event.isCancelled()) {
            return;
        }

        var luckyItem = event.getItemInHand();
        var unhidden = ItemUtils.getIfStartsWith(luckyItem, LUCKY_BLOCK_PREFIX);
        if (unhidden != null) {
            var propertiesSplit = unhidden.split(":");
            var classID = Integer.parseInt(propertiesSplit[2]);

            for (var luckyBlock : event.getGame().getActiveSpecialItems(LuckyBlockImpl.class)) {
                if (System.identityHashCode(luckyBlock) == classID) {
                    luckyBlock.place(event.getBlock().getLocation());
                    return;
                }
            }
        }
    }

    @OnEvent
    public void onLuckyBlockBreak(PlayerBreakBlockEventImpl event) {
        if (event.isCancelled()) {
            return;
        }

        for (var luckyBlock : event.getGame().getActiveSpecialItems(LuckyBlockImpl.class)) {
            if (luckyBlock.isPlaced()) {
                if (event.getBlock().getLocation().equals(luckyBlock.getBlockLocation())) {
                    event.setDrops(false);
                    luckyBlock.process(event.getPlayer());
                    return;
                }
            }
        }
    }
}
