package org.screamingsandals.lib.nms.network.inbound;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;

public abstract class AutoPacketInboundListener extends PacketInboundListener implements Listener {
	public AutoPacketInboundListener(Plugin plugin) {
		Bukkit.getPluginManager().registerEvents(this, plugin);
		Bukkit.getOnlinePlayers().forEach(player -> addPlayer(player));
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public final void onPlayerLogin(PlayerJoinEvent e) {
		addPlayer(e.getPlayer());
	}

	@EventHandler
	public final void onPluginDisable(PluginDisableEvent e) {
		Bukkit.getOnlinePlayers().forEach(player -> removePlayer(player));
	}
}
