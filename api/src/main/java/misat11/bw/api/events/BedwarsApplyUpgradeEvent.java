package misat11.bw.api.events;

import misat11.bw.api.Game;
import misat11.bw.api.ItemSpawner;
import misat11.bw.api.Team;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author Bedwars Team
 */
@Deprecated
public class BedwarsApplyUpgradeEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private Game game;
    private Team team;
    private Player player;
    private ItemSpawner spawner;
    private double newLevel;
    private boolean cancel = false;

    /**
     * @param game
     * @param player
     * @param team
     * @param spawner
     * @param newLevel
     */
    public BedwarsApplyUpgradeEvent(Game game, Player player, Team team, ItemSpawner spawner, double newLevel) {
        this.game = game;
        this.team = team;
        this.player = player;
        this.spawner = spawner;
        this.newLevel = newLevel;
    }

    public static HandlerList getHandlerList() {
        return BedwarsApplyUpgradeEvent.handlers;
    }

    /**
     * @return
     */
    public Game getGame() {
        return this.game;
    }

    @Override
    public HandlerList getHandlers() {
        return BedwarsApplyUpgradeEvent.handlers;
    }

    /**
     * @return
     */
    public double getNewLevel() {
        return newLevel;
    }

    /**
     * @param newLevel
     */
    public void setNewLevel(int newLevel) {
        this.newLevel = newLevel;
    }

    /**
     * @return
     */
    public Team getTeam() {
        return team;
    }

    /**
     * @return
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * @return
     */
    public ItemSpawner getSpawner() {
        return spawner;
    }

    @Override
    public boolean isCancelled() {
        return cancel;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }


}
