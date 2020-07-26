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

import static misat11.lib.lang.I18n.i18n;
import static misat11.lib.lang.I18n.i18nonly;

public class ProtectionWall extends SpecialItem implements org.screamingsandals.bedwars.api.special.ProtectionWall {
    private Game game;
    private Player player;
    private Team team;

    private int breakingTime;
    private int livingTime;
    private int width;
    private int height;
    private int distance;
    private boolean canBreak;

    private ItemStack item;
    private Material buildingMaterial;
    private List<Block> wallBlocks;

    public ProtectionWall(Game game, Player player, Team team, ItemStack item) {
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
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public int getDistance() {
        return distance;
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
    public void runTask() {
        new BukkitRunnable() {

            @Override
            public void run() {
                livingTime++;
                int time = breakingTime - livingTime;

                if (time < 6 && time > 0) {
                    MiscUtils.sendActionBarMessage(
                            player, i18nonly("specials_protection_wall_destroy").replace("%time%", Integer.toString(time)));
                }

                if (livingTime == breakingTime) {
                    for (Block block : wallBlocks) {
                        block.getChunk().load(false);
                        block.setType(Material.AIR);

                        game.getRegion().removeBlockBuiltDuringGame(block.getLocation());
                    }
                    game.unregisterSpecialItem(ProtectionWall.this);
                    this.cancel();
                }
            }
        }.runTaskTimer(Main.getInstance(), 20L, 20L);
    }

    @Override
    public List<Block> getWallBlocks() {
        return wallBlocks;
    }

    private void addBlockToList(Block block) {
        wallBlocks.add(block);
        game.getRegion().addBuiltDuringGame(block.getLocation());
    }

    public void createWall(boolean bre, int time, int wid, int hei, int dis, Material mat, short legacyData) {
        canBreak = bre;
        breakingTime = time;
        width = wid;
        height = hei;
        distance = dis;
        buildingMaterial = mat;
        wallBlocks = new ArrayList<>();

        if (width % 2 == 0) {
            player.sendMessage(i18n("The width of a protection block has to be odd! " + width + " is not an odd number."));
            width = width + 1;
            if (width % 2 == 0) {
                return;
            }
        }

        Location wallLocation = player.getLocation();
        wallLocation.add(wallLocation.getDirection().setY(0).normalize().multiply(distance));

        BlockFace face = MiscUtils.getCardinalDirection(player.getLocation());
        int widthStart = (int) Math.floor(((double) width) / 2.0);

        for (int w = widthStart * (-1); w < width - widthStart; w++) {
            for (int h = 0; h < height; h++) {
                Location wallBlock = wallLocation.clone();

                switch (face) {
                    case SOUTH:
                    case NORTH:
                    case SELF:
                        wallBlock.add(0, h, w);
                        break;
                    case WEST:
                    case EAST:
                        wallBlock.add(w, h, 0);
                        break;
                    case SOUTH_EAST:
                        wallBlock.add(w, h, w);
                        break;
                    case SOUTH_WEST:
                        wallBlock.add(w, h, w * (-1));
                        break;
                    case NORTH_EAST:
                        wallBlock.add(w * (-1), h, w);
                        break;
                    case NORTH_WEST:
                        wallBlock.add(w * (-1), h, w * (-1));
                        break;
                    default:
                        wallBlock = null;
                        break;
                }

                if (wallBlock == null) {
                    continue;
                }

                Block placedBlock = wallBlock.getBlock();
                if (!placedBlock.getType().equals(Material.AIR)) {
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
        }

        if (breakingTime > 0) {
            game.registerSpecialItem(this);
            runTask();

            MiscUtils.sendActionBarMessage(player, i18nonly("specials_protection_wall_created").replace("%time%", Integer.toString(breakingTime)));

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

            MiscUtils.sendActionBarMessage(player, i18nonly("specials_protection_wall_created_unbreakable"));
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
