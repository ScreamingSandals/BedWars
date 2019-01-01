package misat11.bw.listener;

import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import misat11.bw.Main;

public class SignListener implements Listener {
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
	      if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
	    	  if (event.getClickedBlock().getState() instanceof Sign) {
	    		  Sign sign = (Sign) event.getClickedBlock().getState();
	    		  if (sign.getLine(0).equalsIgnoreCase("[BedWars]") || sign.getLine(0).equalsIgnoreCase("[BWGame]")) {
	    			  if (sign.getLine(1).equalsIgnoreCase("leave")) {
	    				  if (Main.isPlayerInGame(event.getPlayer())) {
	    					  Main.getPlayerGameProfile(event.getPlayer()).changeGame(null);
	    				  }
	    			  } else if (Main.isGameExists(sign.getLine(1))) {
	    				  Main.getGame(sign.getLine(1)).joinToGame(event.getPlayer());
	    			  }
	    		  }
	    	  }
	      }
	}
}
