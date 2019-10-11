package misat11.bw.special;

import misat11.bw.Main;
import misat11.bw.api.Game;
import misat11.bw.api.Team;
import misat11.bw.utils.MiscUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import static misat11.lib.lang.I18n.i18nonly;

public class ArrowBlocker extends SpecialItem implements misat11.bw.api.special.ArrowBlocker {
    private Game game;
    private Player player;

    private int protectionTime;
    private int usedTime;
    private boolean isActivated;
    private ItemStack item;

    public ArrowBlocker(Game game, Player player, Team team, ItemStack item, int protectionTime) {
        super(game, player, team);
        this.game = game;
        this.player = player;
        this.item = item;
        this.protectionTime = protectionTime;
    }

    @Override
    public int getProtectionTime() {
        return protectionTime;
    }

    @Override
    public int getUsedTime() {
        return usedTime;
    }

    @Override
    public boolean isActivated() {
        return isActivated;
    }

    @Override
    public void runTask() {
        new BukkitRunnable() {

            @Override
            public void run() {
                usedTime++;
                if (usedTime == protectionTime) {
                    isActivated = false;
                    MiscUtils.sendActionBarMessage(player, i18nonly("specials_arrow_blocker_ended"));

                    game.unregisterSpecialItem(ArrowBlocker.this);
                    this.cancel();
                }
            }
        }.runTaskTimer(Main.getInstance(), 0L, 20L);
    }

    public void activate() {
        if (protectionTime > 0) {
            game.registerSpecialItem(this);
            runTask();

            item.setAmount(item.getAmount() - 1);
            player.updateInventory();

            MiscUtils.sendActionBarMessage(player, i18nonly("specials_arrow_blocker_started").replace("%time%", Integer.toString(protectionTime)));
        }
    }
}
