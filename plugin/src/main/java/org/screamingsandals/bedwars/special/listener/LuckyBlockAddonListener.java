package org.screamingsandals.bedwars.special.listener;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.screamingsandals.bedwars.api.APIUtils;
import org.screamingsandals.bedwars.api.special.SpecialItem;
import org.screamingsandals.bedwars.events.ApplyPropertyToBoughtItemEventImpl;
import org.screamingsandals.bedwars.events.PlayerBreakBlockEventImpl;
import org.screamingsandals.bedwars.events.PlayerBuildBlockEventImpl;
import org.screamingsandals.bedwars.special.LuckyBlock;
import org.bukkit.inventory.ItemStack;
import org.screamingsandals.lib.event.OnEvent;
import org.screamingsandals.lib.utils.annotations.Service;

import java.util.List;
import java.util.Map;

@Service
public class LuckyBlockAddonListener {

    public static final String LUCKY_BLOCK_PREFIX = "Module:LuckyBlock:";

    @OnEvent
    public void onLuckyBlockRegistered(ApplyPropertyToBoughtItemEventImpl event) {
        if (event.getPropertyName().equalsIgnoreCase("luckyblock")) {
            var stack = event.getStack().as(ItemStack.class); // TODO: get rid of this transformation

            LuckyBlock lucky = new LuckyBlock(event.getGame(), event.getPlayer().as(Player.class),
                    event.getGame().getPlayerTeam(event.getPlayer()),
                    (List<Map<String, Object>>) event.getProperty("data"));

            int id = System.identityHashCode(lucky);

            String luckyBlockString = LUCKY_BLOCK_PREFIX + id;

            APIUtils.hashIntoInvisibleString(stack, luckyBlockString);
            event.setStack(stack);
        }
    }

    @OnEvent
    public void onLuckyBlockBuild(PlayerBuildBlockEventImpl event) {
        if (event.isCancelled()) {
            return;
        }

        var luckyItem = event.getItemInHand();
        var invisible = APIUtils.unhashFromInvisibleStringStartsWith(luckyItem.as(ItemStack.class), LUCKY_BLOCK_PREFIX);
        if (invisible != null) {
            String[] splitted = invisible.split(":");
            int classID = Integer.parseInt(splitted[2]);

            for (SpecialItem special : event.getGame().getActivedSpecialItems(LuckyBlock.class)) {
                LuckyBlock luckyBlock = (LuckyBlock) special;
                if (System.identityHashCode(luckyBlock) == classID) {
                    luckyBlock.place(event.getBlock().getLocation().as(Location.class));
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
        for (SpecialItem special : event.getGame().getActivedSpecialItems(LuckyBlock.class)) {
            LuckyBlock luckyBlock = (LuckyBlock) special;
            if (luckyBlock.isPlaced()) {
                if (event.getBlock().getLocation().as(Location.class).equals(luckyBlock.getBlockLocation())) {
                    event.setDrops(false);
                    luckyBlock.process(event.getPlayer().as(Player.class));
                    return;
                }
            }
        }
    }

}
