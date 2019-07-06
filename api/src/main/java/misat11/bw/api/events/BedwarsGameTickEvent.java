package misat11.bw.api.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import misat11.bw.api.Game;
import misat11.bw.api.GameStatus;

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
	
	public Game getGame() {
		return this.game;
	}
	
	public int getCountdown() {
		return this.countdown;
	}
	
	public GameStatus getStatus() {
		return this.status;
	}

	public int getNextCountdown() {
		return nextCountdown;
	}

	public void setNextCountdown(int nextCountdown) {
		this.nextCountdown = nextCountdown;
	}

	public GameStatus getNextStatus() {
		return nextStatus;
	}

	public void setNextStatus(GameStatus nextStatus) {
		this.nextStatus = nextStatus;
	}

	public int getPreviousCountdown() {
		return previousCountdown;
	}

	public GameStatus getPreviousStatus() {
		return previousStatus;
	}
	
	public void preventContinuation(boolean prevent) {
		if (prevent) {
			this.nextCountdown = this.countdown;
			this.nextStatus = this.status;
		} else {
			this.nextCountdown = this.originalNextCountdown;
			this.nextStatus = this.originalNextStatus;
		}
	}
	
	public boolean isNextCountdownChanged() {
		return this.nextCountdown != this.originalNextCountdown;
	}
	
	public boolean isNextStatusChanged() {
		return this.nextStatus != this.originalNextStatus;
	}
	
	public boolean isNextTickChanged() {
		return isNextCountdownChanged() || isNextStatusChanged();
	}

}