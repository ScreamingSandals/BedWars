package org.screamingsandals.bedwars.special;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.screamingsandals.bedwars.api.special.ArrowBlocker;
import org.screamingsandals.bedwars.game.CurrentTeam;
import org.screamingsandals.bedwars.game.GameImpl;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.bedwars.player.BedWarsPlayer;
import org.screamingsandals.bedwars.utils.MiscUtils;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.material.Item;
import org.screamingsandals.lib.material.builder.ItemFactory;
import org.screamingsandals.lib.tasker.Tasker;
import org.screamingsandals.lib.tasker.TaskerTime;
import org.screamingsandals.lib.tasker.task.TaskerTask;

@Getter
@EqualsAndHashCode(callSuper = true)
public class ArrowBlockerImpl extends SpecialItem implements ArrowBlocker<GameImpl, BedWarsPlayer, CurrentTeam> {
    private final int protectionTime;
    private int usedTime;
    private boolean isActivated;
    private Item item;
    private TaskerTask task;

    public ArrowBlockerImpl(GameImpl game, BedWarsPlayer player, CurrentTeam team, Item item, int protectionTime) {
        super(game, player, team);
        this.item = item;
        this.protectionTime = protectionTime;
    }

    @Override
    public void runTask() {
        this.task = Tasker.build(() -> {
                    usedTime++;
                    if (usedTime == protectionTime) {
                        isActivated = false;
                        MiscUtils.sendActionBarMessage(player, Message.of(LangKeys.SPECIALS_ARROW_BLOCKER_ENDED));

                        game.unregisterSpecialItem(ArrowBlockerImpl.this);
                        this.task.cancel();
                    }
                })
                .repeat(20, TaskerTime.TICKS)
                .start();
    }

    public void activate() {
        if (protectionTime > 0) {
            game.registerSpecialItem(this);
            runTask();

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

            MiscUtils.sendActionBarMessage(player, Message.of(LangKeys.SPECIALS_ARROW_BLOCKER_STARTED).placeholder("time", protectionTime));
        }
    }
}
