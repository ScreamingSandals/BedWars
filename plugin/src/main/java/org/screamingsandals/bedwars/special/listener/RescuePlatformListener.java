package org.screamingsandals.bedwars.special.listener;

import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.api.APIUtils;
import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.bedwars.api.game.GameStatus;
import org.screamingsandals.bedwars.api.special.SpecialItem;
import org.screamingsandals.bedwars.events.ApplyPropertyToBoughtItemEventImpl;
import org.screamingsandals.bedwars.player.BedWarsPlayer;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.bedwars.player.PlayerManager;
import org.screamingsandals.bedwars.special.RescuePlatform;
import org.screamingsandals.bedwars.utils.DelayFactory;
import org.screamingsandals.bedwars.utils.MiscUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.screamingsandals.lib.event.OnEvent;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.player.PlayerMapper;
import org.screamingsandals.lib.utils.annotations.Service;
import org.screamingsandals.lib.utils.annotations.methods.OnPostEnable;

import java.util.ArrayList;

@Service
public class RescuePlatformListener implements Listener {
    private static final String RESCUE_PLATFORM_PREFIX = "Module:RescuePlatform:";

    @OnPostEnable
    private void postEnable() {
        Main.getInstance().registerBedwarsListener(this); // TODO: get rid of platform events
    }

    @OnEvent
    public void onRescuePlatformRegistered(ApplyPropertyToBoughtItemEventImpl event) {
        if (event.getPropertyName().equalsIgnoreCase("rescueplatform")) {
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
                String unhidden = APIUtils.unhashFromInvisibleStringStartsWith(stack, RESCUE_PLATFORM_PREFIX);

                if (unhidden != null) {
                    if (!game.isDelayActive(player, RescuePlatform.class)) {
                        event.setCancelled(true);

                        boolean isBreakable = Boolean.parseBoolean(unhidden.split(":")[2]);
                        int delay = Integer.parseInt(unhidden.split(":")[3]);
                        int breakTime = Integer.parseInt(unhidden.split(":")[4]);
                        int distance = Integer.parseInt(unhidden.split(":")[5]);
                        var result = MiscUtils.getMaterialFromString(unhidden.split(":")[6], "GLASS");
                        Material material = result.as(Material.class);
                        short damage = (short) result.getDurability();

                        RescuePlatform rescuePlatform = new RescuePlatform(game, player,
                                game.getTeamOfPlayer(player), stack);

                        if (player.getLocation().getBlock().getRelative(BlockFace.DOWN)
                                .getType() != Material.AIR) {
                            MiscUtils.sendActionBarMessage(PlayerMapper.wrapPlayer(player), Message.of(LangKeys.SPECIALS_RESCUE_PLATFORM_NOT_IN_AIR).placeholder("time", delay));
                            return;
                        }

                        if (delay > 0) {
                            DelayFactory delayFactory = new DelayFactory(delay, rescuePlatform, player, game);
                            game.registerDelay(delayFactory);
                        }

                        rescuePlatform.createPlatform(isBreakable, breakTime, distance, material, damage);
                    } else {
                        event.setCancelled(true);

                        int delay = game.getActiveDelay(player, RescuePlatform.class).getRemainDelay();
                        MiscUtils.sendActionBarMessage(PlayerMapper.wrapPlayer(player), Message.of(LangKeys.SPECIALS_ITEM_DELAY).placeholder("time", delay));
                    }
                }
            }
        }
    }

    @EventHandler
    public void onFallDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        if (event.isCancelled() || !(entity instanceof Player)) {
            return;
        }

        Player player = ((Player) entity).getPlayer();
        if (!PlayerManager.getInstance().isPlayerInGame(player.getUniqueId())) {
            return;
        }

        BedWarsPlayer gPlayer = PlayerManager.getInstance().getPlayer(player.getUniqueId()).orElseThrow();
        Game game = gPlayer.getGame();
        if (gPlayer.isSpectator) {
            return;
        }

        RescuePlatform rescuePlatform = (RescuePlatform) game.getFirstActivedSpecialItemOfPlayer(player, RescuePlatform.class);
        if (rescuePlatform != null && event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            Block block = player.getLocation().getBlock().getRelative(BlockFace.DOWN);
            if (block != null) {
                if (block.getType() == rescuePlatform.getMaterial()) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (!PlayerManager.getInstance().isPlayerInGame(player.getUniqueId())) {
            return;
        }

        BedWarsPlayer gPlayer = PlayerManager.getInstance().getPlayer(player.getUniqueId()).orElseThrow();
        Game game = gPlayer.getGame();
        if (gPlayer.isSpectator) {
            return;
        }

        Block block = event.getBlock();
        for (RescuePlatform checkedPlatform : getCreatedPlatforms(game, player)) {
            if (checkedPlatform != null) {
                for (Block platformBlock : checkedPlatform.getPlatformBlocks()) {
                    if (platformBlock.equals(block) && !checkedPlatform.canBreak()) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    private ArrayList<RescuePlatform> getCreatedPlatforms(Game game, Player player) {
        ArrayList<RescuePlatform> createdPlatforms = new ArrayList<>();
        for (SpecialItem specialItem : game.getActivedSpecialItemsOfPlayer(player)) {
            if (specialItem instanceof RescuePlatform) {
                RescuePlatform platform = (RescuePlatform) specialItem;
                createdPlatforms.add(platform);
            }
        }
        return createdPlatforms;
    }

    private String applyProperty(ApplyPropertyToBoughtItemEventImpl event) {
        return RESCUE_PLATFORM_PREFIX
                + MiscUtils.getBooleanFromProperty(
                "is-breakable", "specials.rescue-platform.is-breakable", event) + ":"
                + MiscUtils.getIntFromProperty(
                "delay", "specials.rescue-platform.delay", event) + ":"
                + MiscUtils.getIntFromProperty(
                "break-time", "specials.rescue-platform.break-time", event) + ":"
                + MiscUtils.getIntFromProperty(
                "distance", "specials.rescue-platform.distance", event) + ":"
                + MiscUtils.getMaterialFromProperty(
                "material", "specials.rescue-platform.material", event);
    }
}
