package org.screamingsandals.lib.signmanager;

import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.screamingsandals.bedwars.commands.BaseCommand;

import java.util.List;

public class SignListener implements Listener {

    public final List<String> SIGN_PREFIX;
    
    private SignOwner owner;
    private SignManager manager;
	
	public SignListener(SignOwner owner, SignManager manager) {
		this.owner = owner;
		this.manager = manager;
		SIGN_PREFIX = owner.getSignPrefixes();
	}

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (event.getClickedBlock().getState() instanceof Sign) {
                if (manager.isSignRegistered(event.getClickedBlock().getLocation())) {
                    SignBlock sign = manager.getSign(event.getClickedBlock().getLocation());
                    owner.onClick(event.getPlayer(), sign);
                }
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (event.getBlock().getState() instanceof Sign) {
            Location loc = event.getBlock().getLocation();
            if (manager.isSignRegistered(loc)) {
                if (BaseCommand.hasPermission(event.getPlayer(), owner.getSignCreationPermissions(), false)) {
                    manager.unregisterSign(loc);
                } else {
                    event.getPlayer().sendMessage(owner.returnTranslate("sign_can_not_been_destroyed"));
                    event.setCancelled(true);
                }
            }
        }

    }

    @EventHandler
    public void onChangeSign(SignChangeEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (event.getBlock().getState() instanceof Sign) {
            Location loc = event.getBlock().getLocation();
            if (SIGN_PREFIX.contains(event.getLine(0).toLowerCase())) {
                if (BaseCommand.hasPermission(event.getPlayer(), owner.getSignCreationPermissions(), false)) {
                    if (manager.registerSign(loc, event.getLine(1))) {
                        event.getPlayer().sendMessage(owner.returnTranslate("sign_successfully_created"));
                    } else {
                        event.getPlayer().sendMessage(owner.returnTranslate("sign_can_not_been_created"));
                        event.setCancelled(true);
                        event.getBlock().breakNaturally();
                    }
                }
            }
        }
    }
}
