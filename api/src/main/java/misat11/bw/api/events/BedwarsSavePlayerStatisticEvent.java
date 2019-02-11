package misat11.bw.api.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import misat11.bw.api.statistics.PlayerStatistic;

public class BedwarsSavePlayerStatisticEvent extends Event implements Cancellable {

	  private static final HandlerList handlers = new HandlerList();
	  private boolean cancelled = false;
	  private PlayerStatistic playerStatistic = null;

	  public BedwarsSavePlayerStatisticEvent(PlayerStatistic playerStatistic) {
	    this.playerStatistic = playerStatistic;
	  }

	  public static HandlerList getHandlerList() {
	    return BedwarsSavePlayerStatisticEvent.handlers;
	  }

	  @Override
	  public HandlerList getHandlers() {
	    return BedwarsSavePlayerStatisticEvent.handlers;
	  }

	  public PlayerStatistic getPlayerStatistic() {
	    return this.playerStatistic;
	  }

	  @Override
	  public boolean isCancelled() {
	    return this.cancelled;
	  }

	  @Override
	  public void setCancelled(boolean cancel) {
	    this.cancelled = cancel;
	  }

	}