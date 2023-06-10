/*
 * Copyright (C) 2023 ScreamingSandals
 *
 * This file is part of Screaming BedWars.
 *
 * Screaming BedWars is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Screaming BedWars is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Screaming BedWars. If not, see <https://www.gnu.org/licenses/>.
 */

package org.screamingsandals.bedwars.lib.signmanager;

import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.screamingsandals.bedwars.commands.BaseCommand;
import org.screamingsandals.bedwars.lib.nms.utils.Version;

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
                        if (Version.isVersion(1, 20)) {
                            Sign sign = (Sign) event.getBlock().getState();
                            sign.setEditable(false);
                            sign.update(true, false);
                        }
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
