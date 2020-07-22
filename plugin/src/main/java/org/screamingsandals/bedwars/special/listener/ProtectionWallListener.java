package org.screamingsandals.bedwars.special.listener;


import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.api.APIUtils;
import org.screamingsandals.bedwars.api.events.BedwarsPlayerBreakBlock;
import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.bedwars.api.game.GameStatus;
import org.screamingsandals.bedwars.api.events.BedwarsApplyPropertyToBoughtItem;
import org.screamingsandals.bedwars.api.special.SpecialItem;
import org.screamingsandals.bedwars.game.GamePlayer;
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
import org.screamingsandals.simpleinventories.utils.MaterialSearchEngine;

import java.util.ArrayList;

import static misat11.lib.lang.I18n.i18nonly;

public class ProtectionWallListener implements Listener {
    private static final String PROTECTION_WALL_PREFIX = "Module:ProtectionWall:";

    @EventHandler
    public void onProtectionWallRegistered(BedwarsApplyPropertyToBoughtItem event) {
        if (event.getPropertyName().equalsIgnoreCase("protectionwall")) {
            ItemStack stack = event.getStack();
            APIUtils.hashIntoInvisibleString(stack, applyProperty(event));
        }

    }

    @EventHandler
    public void onPlayerUseItem(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!Main.isPlayerInGame(player)) {
            return;
        }

        GamePlayer gPlayer = Main.getPlayerGameProfile(player);
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
                        MaterialSearchEngine.Result result = MiscUtils.getMaterialFromString(unhidden.split(":")[8], "CUT_SANDSTONE");
                        Material material = result.getMaterial();
                        short damage = result.getDamage();


                        ProtectionWall protectionWall = new ProtectionWall(game, event.getPlayer(),
                                game.getTeamOfPlayer(event.getPlayer()), stack);

                        if (event.getPlayer().getEyeLocation().getBlock().getType() != Material.AIR) {
                            MiscUtils.sendActionBarMessage(event.getPlayer(), i18nonly("specials_protection_wall_not_usable_here"));
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
                        MiscUtils.sendActionBarMessage(player, i18nonly("special_item_delay").replace("%time%", String.valueOf(delay)));
                    }
                }
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BedwarsPlayerBreakBlock event) {
        final Game game = event.getGame();
        final Block block = event.getBlock();

        for (ProtectionWall checkedWall : getCreatedWalls(game)) {
            if (checkedWall != null) {
                for (Block wallBlock : checkedWall.getWallBlocks()) {
                    if (wallBlock.equals(block) && !checkedWall.canBreak()) {
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

    private String applyProperty(BedwarsApplyPropertyToBoughtItem event) {
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
