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

package org.screamingsandals.bedwars.lib.nms.network.inbound;

import org.bukkit.entity.Player;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import org.screamingsandals.bedwars.lib.nms.accessors.NetworkManagerAccessor;
import org.screamingsandals.bedwars.lib.nms.accessors.PlayerConnectionAccessor;

import static org.screamingsandals.bedwars.lib.nms.utils.ClassStorage.*;

public abstract class PacketInboundListener{

	private static int ID = 0;

	private final String channelName = PacketInboundListener.class.getCanonicalName() + "-" + ID++;
	
	public void addPlayer(Player player) {
		try {
			Channel ch = getChannel(player);
			if (ch.pipeline().get(channelName) == null) {
				ChannelDuplexHandler handler = new ChannelDuplexHandler() {
					@Override
					public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
						try {
							msg = handle(player, msg);
						} catch (Throwable t) {
						}
						if (msg != null) {
							super.channelRead(ctx, msg);
						}
					}
				};
				ch.pipeline().addBefore("packet_handler", channelName, handler);
			}
		} catch (Throwable t) {
		}
	}

	public void removePlayer(Player player) {
		try {
			Channel ch = getChannel(player);
			if (ch.pipeline().get(channelName) != null) {
				ch.pipeline().remove(channelName);
			}
		} catch (Throwable t) {
		}
	}
	
	private Channel getChannel(Player player) {
		try {
			Object manager = getField(getPlayerConnection(player), PlayerConnectionAccessor.getFieldNetworkManager());
			Channel channel = (Channel) getField(manager, NetworkManagerAccessor.getFieldChannel());
			return channel;
		} catch (Throwable t) {
		}
		return null;
	}
	
	protected abstract Object handle(Player p, Object packet) throws Throwable;
}
