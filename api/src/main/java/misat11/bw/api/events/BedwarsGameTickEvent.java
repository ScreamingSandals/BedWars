package misat11.bw.api.events;

import misat11.bw.api.game.Game;
import misat11.bw.api.game.GameStatus;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author Bedwars Team
 */
public class BedwarsGameTickEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private Game game;
    private int previousCountdown;
    private GameStatus previousStatus;
    private int countdown;
    private GameStatus status;
    private int originalNextCountdown;
    private GameStatus originalNextStatus;
    private int nextCountdown;
    private GameStatus nextStatus;

    /**
     * @param game
     * @param previousCountdown
     * @param previousStatus
     * @param countdown
     * @param status
     * @param nextCountdown
     * @param nextStatus
     */
    public BedwarsGameTickEvent(Game game, int previousCountdown, GameStatus previousStatus, int countdown, GameStatus status, int nextCountdown, GameStatus nextStatus) {
        this.game = game;
        this.previousCountdown = previousCountdown;
        this.previousStatus = previousStatus;
        this.countdown = countdown;
        this.status = status;
        this.nextCountdown = this.originalNextCountdown = nextCountdown;
        this.nextStatus = this.originalNextStatus = nextStatus;
    }

    public static HandlerList getHandlerList() {
        return BedwarsGameTickEvent.handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return BedwarsGameTickEvent.handlers;
    }

    /**
     * @return
     */
    public Game getGame() {
        return this.game;
    }

    /**
     * @return
     */
    public int getCountdown() {
        return this.countdown;
    }

    /**
     * @return
     */
    public GameStatus getStatus() {
        return this.status;
    }

    /**
     * @return
     */
    public int getNextCountdown() {
        return nextCountdown;
    }

    /**
     * @param nextCountdown
     */
    public void setNextCountdown(int nextCountdown) {
        this.nextCountdown = nextCountdown;
    }

    /**
     * @return
     */
    public GameStatus getNextStatus() {
        return nextStatus;
    }

    /**
     * @param nextStatus
     */
    public void setNextStatus(GameStatus nextStatus) {
        this.nextStatus = nextStatus;
    }

    /**
     * @return
     */
    public int getPreviousCountdown() {
        return previousCountdown;
    }

    /**
     * @return
     */
    public GameStatus getPreviousStatus() {
        return previousStatus;
    }

    /**
     * @param prevent
     */
    public void preventContinuation(boolean prevent) {
        if (prevent) {
            this.nextCountdown = this.countdown;
            this.nextStatus = this.status;
        } else {
            this.nextCountdown = this.originalNextCountdown;
            this.nextStatus = this.originalNextStatus;
        }
    }

    /**
     * @return
     */
    public boolean isNextCountdownChanged() {
        return this.nextCountdown != this.originalNextCountdown;
    }

    /**
     * @return
     */
    public boolean isNextStatusChanged() {
        return this.nextStatus != this.originalNextStatus;
    }

    /**
     * @return
     */
    public boolean isNextTickChanged() {
        return isNextCountdownChanged() || isNextStatusChanged();
    }

}