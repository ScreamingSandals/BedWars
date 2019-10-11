package misat11.bw.api.events;

import misat11.bw.api.game.Game;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author Bedwars Team
 */
public class BedwarsPostSpawnEffectEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private Game game;
    private Player player;
    private String effectsGroupName;

    /**
     * @param game
     * @param player
     * @param effectsGroupName
     */
    public BedwarsPostSpawnEffectEvent(Game game, Player player, String effectsGroupName) {
        this.game = game;
        this.player = player;
        this.effectsGroupName = effectsGroupName;
    }

    public static HandlerList getHandlerList() {
        return BedwarsPostSpawnEffectEvent.handlers;
    }

    /**
     * @return game
     */
    public Game getGame() {
        return this.game;
    }

    /**
     * @return player
     */
    public Player getPlayer() {
        return this.player;
    }

    /**
     * @return effects group
     */
    public String getEffectsGroupName() {
        return this.effectsGroupName;
    }

    @Override
    public HandlerList getHandlers() {
        return BedwarsPostSpawnEffectEvent.handlers;
    }

}
