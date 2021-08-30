package org.screamingsandals.bedwars.special;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.screamingsandals.bedwars.api.special.ProtectionWall;
import org.screamingsandals.bedwars.game.CurrentTeam;
import org.screamingsandals.bedwars.game.GameImpl;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.bedwars.player.BedWarsPlayer;
import org.screamingsandals.bedwars.utils.MiscUtils;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.material.Item;
import org.screamingsandals.lib.material.MaterialHolder;
import org.screamingsandals.lib.material.MaterialMapping;
import org.screamingsandals.lib.material.builder.ItemFactory;
import org.screamingsandals.lib.tasker.Tasker;
import org.screamingsandals.lib.tasker.TaskerTime;
import org.screamingsandals.lib.tasker.task.TaskerTask;
import org.screamingsandals.lib.world.BlockHolder;

import java.util.ArrayList;
import java.util.List;

@Getter
@EqualsAndHashCode(callSuper = true)
public class ProtectionWallImpl extends SpecialItem implements ProtectionWall<GameImpl, BedWarsPlayer, CurrentTeam, MaterialHolder, BlockHolder> {
    private int breakingTime;
    private int livingTime;
    private int width;
    private int height;
    private int distance;
    private boolean breakable;

    private final Item item;
    private MaterialHolder material;
    private List<BlockHolder> wallBlocks;
    private TaskerTask task;

    public ProtectionWallImpl(GameImpl game, BedWarsPlayer player, CurrentTeam team, Item item) {
        super(game, player, team);
        this.item = item;
    }

    @Override
    public void runTask() {
        this.task = Tasker.build(() -> {
                    livingTime++;
                    int time = breakingTime - livingTime;

                    if (time < 6 && time > 0) {
                        MiscUtils.sendActionBarMessage(player, Message.of(LangKeys.SPECIALS_PROTECTION_WALL_DESTROY).placeholder("time", time));
                    }

                    if (livingTime == breakingTime) {
                        for (var block : wallBlocks) {
                            block.getLocation().getChunk().load(false);
                            block.setType(MaterialMapping.getAir());

                            game.getRegion().removeBlockBuiltDuringGame(block.getLocation());
                        }
                        game.unregisterSpecialItem(this);
                        this.task.cancel();
                    }
                })
                .delay(20, TaskerTime.TICKS)
                .repeat(20, TaskerTime.TICKS)
                .start();
    }

    private void addBlockToList(BlockHolder block) {
        wallBlocks.add(block);
        game.getRegion().addBuiltDuringGame(block.getLocation());
    }

    public void createWall(boolean bre, int time, int wid, int hei, int dis, MaterialHolder mat) {
        breakable = bre;
        breakingTime = time;
        width = wid;
        height = hei;
        distance = dis;
        material = mat;
        wallBlocks = new ArrayList<>();

        if (width % 2 == 0) {
            player.sendMessage("The width of a protection block has to be odd! " + width + " is not an odd number.");
            width = width + 1;
            if (width % 2 == 0) {
                return;
            }
        }

        var wallLocation = player.getLocation();
        wallLocation = wallLocation.add(wallLocation.getFacingDirection().setY(0).normalize().multiply(distance));

        var face = MiscUtils.getCardinalDirection(player.getLocation());
        var widthStart = (int) Math.floor(((double) width) / 2.0);

        for (int w = widthStart * (-1); w < width - widthStart; w++) {
            for (int h = 0; h < height; h++) {
                var wallBlock = wallLocation.clone();

                switch (face) {
                    case SOUTH:
                    case NORTH:
                    case SELF:
                        wallBlock = wallBlock.add(0, h, w);
                        break;
                    case WEST:
                    case EAST:
                        wallBlock = wallBlock.add(w, h, 0);
                        break;
                    case SOUTH_EAST:
                        wallBlock = wallBlock.add(w, h, w);
                        break;
                    case SOUTH_WEST:
                        wallBlock = wallBlock.add(w, h, w * (-1));
                        break;
                    case NORTH_EAST:
                        wallBlock = wallBlock.add(w * (-1), h, w);
                        break;
                    case NORTH_WEST:
                        wallBlock = wallBlock.add(w * (-1), h, w * (-1));
                        break;
                    default:
                        wallBlock = null;
                        break;
                }

                if (wallBlock == null) {
                    continue;
                }

                var placedBlock = wallBlock.getBlock();
                if (!placedBlock.getType().isAir()) {
                    continue;
                }

                var coloredMaterial = MaterialMapping.colorize(material, team.teamInfo.color.material1_13);
                placedBlock.setType(coloredMaterial);
                addBlockToList(placedBlock);
            }
        }

        if (breakingTime > 0) {
            game.registerSpecialItem(this);
            runTask();

            MiscUtils.sendActionBarMessage(player, Message.of(LangKeys.SPECIALS_PROTECTION_WALL_CREATED).placeholder("time", breakingTime));

            item.setAmount(1);
            try {
                if (player.getPlayerInventory().getItemInOffHand().equals(item)) {
                    player.getPlayerInventory().setItemInOffHand(ItemFactory.getAir());
                } else {
                    player.getPlayerInventory().removeItem(item);
                }
            } catch (Throwable e) {
                player.getPlayerInventory().removeItem(item);
            }
            player.as(Player.class).updateInventory();
        } else {
            game.registerSpecialItem(this);

            MiscUtils.sendActionBarMessage(player, Message.of(LangKeys.SPECIALS_PROTECTION_WALL_CREATED_UNBREAKABLE));
            item.setAmount(1);
            try {
                if (player.getPlayerInventory().getItemInOffHand().equals(item)) {
                    player.getPlayerInventory().setItemInOffHand(ItemFactory.getAir());
                } else {
                    player.getPlayerInventory().removeItem(item);
                }
            } catch (Throwable e) {
                player.getPlayerInventory().removeItem(item);
            }
            player.as(Player.class).updateInventory();
        }
    }
}
