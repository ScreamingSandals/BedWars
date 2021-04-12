package org.screamingsandals.bedwars.special.listener;


import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.api.APIUtils;
import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.bedwars.api.game.GameStatus;
import org.screamingsandals.bedwars.api.special.SpecialItem;
import org.screamingsandals.bedwars.events.ApplyPropertyToBoughtItemEventImpl;
import org.screamingsandals.bedwars.events.PlayerBreakBlockEventImpl;
import org.screamingsandals.bedwars.player.BedWarsPlayer;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.bedwars.player.PlayerManager;
import org.screamingsandals.bedwars.special.ProtectionWall;
import org.screamingsandals.bedwars.utils.DelayFactory;
import org.screamingsandals.bedwars.utils.MiscUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.screamingsandals.lib.event.OnEvent;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.player.PlayerMapper;
import org.screamingsandals.lib.utils.annotations.Service;
import org.screamingsandals.lib.utils.annotations.methods.OnPostEnable;

import java.util.ArrayList;

@Service
public class ProtectionWallListener implements Listener {
    private static final String PROTECTION_WALL_PREFIX = "Module:ProtectionWall:";

    @OnPostEnable
    private void postEnable() {
        Main.getInstance().registerBedwarsListener(this); // TODO: get rid of platform events
    }

    @OnEvent
    public void onProtectionWallRegistered(ApplyPropertyToBoughtItemEventImpl event) {
        if (event.getPropertyName().equalsIgnoreCase("protectionwall")) {
            var stack = event.getStack().as(ItemStack.class); // TODO: get rid of this transformation
            APIUtils.hashIntoInvisibleString(stack, applyProperty(event));
            event.setStack(stack);
        }

    }

    @EventHandler
    public void onPlayerUseItem(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!PlayerManager.getInstance().isPlayerInGame(player.getUniqueId())) {
            return;
        }

        BedWarsPlayer gPlayer = PlayerManager.getInstance().getPlayer(player.getUniqueId()).orElseThrow();
        Game game = gPlayer.getGame();

        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (game.getStatus() == GameStatus.RUNNING && !gPlayer.isSpectator && event.getItem() != null) {
                ItemStack stack = event.getItem();
                String unhidden = APIUtils.unhashFromInvisibleStringStartsWith(stack, PROTECTION_WALL_PREFIX);

                if (unhidden != null) {
                    if (!game.isDelayActive(player, ProtectionWall.class)) {
                        event.setCancelled(true);

                        boolean isBreakable = Boolean.parseBoolean(unhidden.split(":")[2]);
                        int delay = Integer.parseInt(unhidden.split(":")[3]);
                        int breakTime = Integer.parseInt(unhidden.split(":")[4]);
                        int width = Integer.parseInt(unhidden.split(":")[5]);
                        int height = Integer.parseInt(unhidden.split(":")[6]);
                        int distance = Integer.parseInt(unhidden.split(":")[7]);
                        var result = MiscUtils.getMaterialFromString(unhidden.split(":")[8], "CUT_SANDSTONE");
                        Material material = result.as(Material.class);
                        short damage = (short) result.getDurability();


                        ProtectionWall protectionWall = new ProtectionWall(game, event.getPlayer(),
                                game.getTeamOfPlayer(event.getPlayer()), stack);

                        if (event.getPlayer().getEyeLocation().getBlock().getType() != Material.AIR) {
                            MiscUtils.sendActionBarMessage(PlayerMapper.wrapPlayer(player), Message.of(LangKeys.SPECIALS_PROTECTION_WALL_NOT_USABLE_HERE));
                            return;
                        }

                        if (delay > 0) {
                            DelayFactory delayFactory = new DelayFactory(delay, protectionWall, player, game);
                            game.registerDelay(delayFactory);
                        }

                        protectionWall.createWall(isBreakable, breakTime, width, height, distance, material, damage);
                    } else {
                        event.setCancelled(true);

                        int delay = game.getActiveDelay(player, ProtectionWall.class).getRemainDelay();
                        MiscUtils.sendActionBarMessage(PlayerMapper.wrapPlayer(player), Message.of(LangKeys.SPECIALS_ITEM_DELAY).placeholder("time", delay));
                    }
                }
            }
        }
    }

    @OnEvent
    public void onBlockBreak(PlayerBreakBlockEventImpl event) {
        final var game = event.getGame();
        final var block = event.getBlock();

        for (ProtectionWall checkedWall : getCreatedWalls(game)) {
            if (checkedWall != null) {
                for (Block wallBlock : checkedWall.getWallBlocks()) {
                    if (wallBlock.equals(block.as(Block.class)) && !checkedWall.canBreak()) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    private ArrayList<ProtectionWall> getCreatedWalls(Game game) {
        ArrayList<ProtectionWall> createdWalls = new ArrayList<>();
        for (SpecialItem specialItem : game.getActivedSpecialItems(ProtectionWall.class)) {
            if (specialItem instanceof ProtectionWall) {
                ProtectionWall wall = (ProtectionWall) specialItem;
                createdWalls.add(wall);
            }
        }
        return createdWalls;
    }

    private String applyProperty(ApplyPropertyToBoughtItemEventImpl event) {
        return PROTECTION_WALL_PREFIX
                + MiscUtils.getBooleanFromProperty(
                "is-breakable", "specials.protection-wall.is-breakable", event) + ":"
                + MiscUtils.getIntFromProperty(
                "delay", "specials.protection-wall.delay", event) + ":"
                + MiscUtils.getIntFromProperty(
                "break-time", "specials.protection-wall.break-time", event) + ":"
                + MiscUtils.getIntFromProperty(
                "width", "specials.protection-wall.width", event) + ":"
                + MiscUtils.getIntFromProperty(
                "height", "specials.protection-wall.height", event) + ":"
                + MiscUtils.getIntFromProperty(
                "distance", "specials.protection-wall.distance", event) + ":"
                + MiscUtils.getMaterialFromProperty(
                "material", "specials.protection-wall.material", event);
    }

}
