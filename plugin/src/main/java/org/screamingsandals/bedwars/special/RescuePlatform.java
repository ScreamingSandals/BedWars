package org.screamingsandals.bedwars.special;

import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.bedwars.api.Team;
import org.screamingsandals.bedwars.game.TeamColor;
import org.screamingsandals.bedwars.utils.MiscUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

import static misat11.lib.lang.I18n.i18nonly;

public class RescuePlatform extends SpecialItem implements org.screamingsandals.bedwars.api.special.RescuePlatform {
    private Game game;
    private Player player;
    private Team team;
    private List<Block> platformBlocks;

    private Material buildingMaterial;
    private ItemStack item;

    private boolean canBreak;
    private int breakingTime;
    private int livingTime;

    public RescuePlatform(Game game, Player player, Team team, ItemStack item) {
        super(game, player, team);
        this.game = game;
        this.player = player;
        this.team = team;
        this.item = item;
    }

    @Override
    public int getBreakingTime() {
        return breakingTime;
    }

    @Override
    public boolean canBreak() {
        return canBreak;
    }

    @Override
    public Material getMaterial() {
        return buildingMaterial;
    }

    @Override
    public ItemStack getStack() {
        return item;
    }

    @Override
    public void runTask() {
        new BukkitRunnable() {

            @Override
            public void run() {
                livingTime++;
                int time = breakingTime - livingTime;

                if (time < 6 && time > 0) {
                    MiscUtils.sendActionBarMessage(
                            player, i18nonly("specials_rescue_platform_destroy").replace("%time%", Integer.toString(time)));
                }

                if (livingTime == breakingTime) {
                    for (Block block : platformBlocks) {
                        block.getChunk().load(false);
                        block.setType(Material.AIR);

                        removeBlockFromList(block);
                        game.getRegion().removeBlockBuiltDuringGame(block.getLocation());

                    }
                    game.unregisterSpecialItem(RescuePlatform.this);
                    this.cancel();
                }
            }
        }.runTaskTimer(Main.getInstance(), 20L, 20L);
    }

    @Override
    public List<Block> getPlatformBlocks() {
        return platformBlocks;
    }

    private void addBlockToList(Block block) {
        platformBlocks.add(block);
        game.getRegion().addBuiltDuringGame(block.getLocation());
    }

    private void removeBlockFromList(Block block) {
        game.getRegion().removeBlockBuiltDuringGame(block.getLocation());
    }

    public void createPlatform(boolean bre, int time, int dist, Material bMat, short legacyData) {
        canBreak = bre;
        breakingTime = time;
        buildingMaterial = bMat;
        platformBlocks = new ArrayList<>();

        Location center = player.getLocation().clone();
        center.setY(center.getY() - dist);

        for (BlockFace blockFace : BlockFace.values()) {
            if (blockFace.equals(BlockFace.DOWN) || blockFace.equals(BlockFace.UP)) {
                continue;
            }

            Block placedBlock = center.getBlock().getRelative(blockFace);
            if (placedBlock.getType() != Material.AIR) {
                continue;
            }

            ItemStack coloredStack = Main.applyColor(
                    TeamColor.fromApiColor(team.getColor()), new ItemStack(buildingMaterial, 1, legacyData));
            if (Main.isLegacy()) {
                placedBlock.setType(coloredStack.getType());
                try {
                    // The method is no longer in API, but in legacy versions exists
                    Block.class.getMethod("setData", byte.class).invoke(placedBlock, (byte) coloredStack.getDurability());
                } catch (Exception e) {
                }
            } else {
                placedBlock.setType(coloredStack.getType());
            }
            addBlockToList(placedBlock);
        }

        if (breakingTime > 0) {
            game.registerSpecialItem(this);
            runTask();

            MiscUtils.sendActionBarMessage(player, i18nonly("specials_rescue_platform_created").replace("%time%", Integer.toString(breakingTime)));

            if (item.getAmount() > 1) {
                item.setAmount(item.getAmount() - 1);
            } else {
                try {
                    if (player.getInventory().getItemInOffHand().equals(item)) {
                        player.getInventory().setItemInOffHand(new ItemStack(Material.AIR));
                    } else {
                        player.getInventory().remove(item);
                    }
                } catch (Throwable e) {
                    player.getInventory().remove(item);
                }
            }
            player.updateInventory();
        } else {
            game.registerSpecialItem(this);

            MiscUtils.sendActionBarMessage(player, i18nonly("specials_rescue_platform_created_unbreakable"));
            if (item.getAmount() > 1) {
                item.setAmount(item.getAmount() - 1);
            } else {
                try {
                    if (player.getInventory().getItemInOffHand().equals(item)) {
                        player.getInventory().setItemInOffHand(new ItemStack(Material.AIR));
                    } else {
                        player.getInventory().remove(item);
                    }
                } catch (Throwable e) {
                    player.getInventory().remove(item);
                }
            }
            player.updateInventory();
        }
    }
}
