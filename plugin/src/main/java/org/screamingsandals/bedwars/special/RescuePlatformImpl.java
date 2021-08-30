package org.screamingsandals.bedwars.special;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.screamingsandals.bedwars.api.special.RescuePlatform;
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
import org.screamingsandals.lib.utils.BlockFace;
import org.screamingsandals.lib.world.BlockHolder;

import java.util.ArrayList;
import java.util.List;

@Getter
@EqualsAndHashCode(callSuper = true)
public class RescuePlatformImpl extends SpecialItem implements RescuePlatform<GameImpl, BedWarsPlayer, CurrentTeam, Item, MaterialHolder, BlockHolder> {
    private final Item item;
    private List<BlockHolder> platformBlocks;
    private MaterialHolder material;
    private boolean breakable;
    private int breakingTime;
    private int livingTime;
    private TaskerTask task;

    public RescuePlatformImpl(GameImpl game, BedWarsPlayer player, CurrentTeam team, Item item) {
        super(game, player, team);
        this.item = item;
    }

    @Override
    public void runTask() {
        this.task = Tasker.build(() -> {
                    livingTime++;
                    int time = breakingTime - livingTime;

                    if (time < 6 && time > 0) {
                        MiscUtils.sendActionBarMessage(player, Message.of(LangKeys.SPECIALS_RESCUE_PLATFORM_DESTROY).placeholder("time", time));
                    }

                    if (livingTime == breakingTime) {
                        for (var block : List.copyOf(platformBlocks)) {
                            block.getLocation().getChunk().load(false);
                            block.setType(MaterialMapping.getAir());

                            removeBlockFromList(block);
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
        platformBlocks.add(block);
        game.getRegion().addBuiltDuringGame(block.getLocation());
    }

    private void removeBlockFromList(BlockHolder block) {
        platformBlocks.remove(block);
        game.getRegion().removeBlockBuiltDuringGame(block.getLocation());
    }

    public void createPlatform(boolean bre, int time, int dist, MaterialHolder bMat) {
        breakable = bre;
        breakingTime = time;
        material = bMat;
        platformBlocks = new ArrayList<>();

        var center = player.getLocation().clone();
        center.setY(center.getY() - dist);

        for (var blockFace : BlockFace.values()) {
            if (blockFace.equals(BlockFace.DOWN) || blockFace.equals(BlockFace.UP)) {
                continue;
            }

            var placedBlock = center.add(blockFace.getDirection()).getBlock();
            if (!placedBlock.getType().isAir()) {
                continue;
            }

            var coloredMatrerial = MaterialMapping.colorize(material, team.teamInfo.color.material1_13);
            placedBlock.setType(coloredMatrerial);
            addBlockToList(placedBlock);
        }

        if (breakingTime > 0) {
            game.registerSpecialItem(this);
            runTask();

            MiscUtils.sendActionBarMessage(player, Message.of(LangKeys.SPECIALS_RESCUE_PLATFORM_CREATED).placeholder("time", breakingTime));

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

            MiscUtils.sendActionBarMessage(player, Message.of(LangKeys.SPECIALS_RESCUE_PLATFORM_CREATED_UNBREAKABLE));
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
