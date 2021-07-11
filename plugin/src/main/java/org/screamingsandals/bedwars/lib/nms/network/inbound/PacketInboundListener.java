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
