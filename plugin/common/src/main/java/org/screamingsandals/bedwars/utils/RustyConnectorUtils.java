package org.screamingsandals.bedwars.utils;

import group.aelysium.rustyconnector.toolkit.RustyConnector;
import group.aelysium.rustyconnector.toolkit.core.packet.Packet;
import group.aelysium.rustyconnector.toolkit.core.packet.PacketIdentification;
import group.aelysium.rustyconnector.toolkit.core.packet.PacketParameter;
import group.aelysium.rustyconnector.toolkit.mc_loader.central.IMCLoaderFlame;
import group.aelysium.rustyconnector.toolkit.mc_loader.central.IMCLoaderTinder;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;
import org.screamingsandals.bedwars.config.MainConfig;
import org.screamingsandals.bedwars.game.remote.Constants;
import org.screamingsandals.bedwars.lib.debug.Debug;
import org.screamingsandals.lib.Server;
import org.screamingsandals.lib.player.Player;
import org.screamingsandals.lib.spectator.Color;
import org.screamingsandals.lib.spectator.Component;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

@UtilityClass
public class RustyConnectorUtils {
	public static final @NotNull String PACKET_TO_SERVER = "BEDWARS_TO_SERVER";
	public static final @NotNull String PACKET_BROADCAST= "BEDWARS_BROADCAST";

	private static IMCLoaderFlame<?> flame;

	public static void init() {
		Optional<IMCLoaderTinder> tinderHolder = RustyConnector.Toolkit.mcLoader();
		if (tinderHolder.isPresent()) {
			IMCLoaderTinder tinder = tinderHolder.get();
			tinder.onStart(flame -> {
				RustyConnectorUtils.flame = flame;
				Server.getConsoleSender().sendMessage(Component.text("Rusty Connector connection found!", Color.GREEN));
			});
		} else {
			Server.getConsoleSender().sendMessage(Component.text("Rusty Connector connection not found!", Color.RED));
		}
	}

	public static void sendToHub(Player player) {
		var familyName = MainConfig.getInstance().node("bungee", "rustyConnector", "family").getString("hub");
		flame.send(player.getUniqueId(), familyName);
		Debug.info("Player " + player.getName() + " has been moved to hub server.");
	}

	public static IMCLoaderFlame<?> getFlame() {
		return flame;
	}

	public static void packetToServer(String server, byte[] data) {
		Packet packet = flame.services().packetBuilder().newBuilder()
				.identification(PacketIdentification.from(Constants.MESSAGING_CHANNEL, PACKET_TO_SERVER))
				.sendingToAnotherMCLoader(UUID.fromString(server))
				.parameter("bytes", new PacketParameter(new String(data, StandardCharsets.UTF_8))).build();

		flame.services().magicLink().connection().orElseThrow().publish(packet);
	}

	public static void packetBroadcast(byte[] data) {
		Packet packet = flame.services().packetBuilder().newBuilder()
				.identification(PacketIdentification.from(Constants.MESSAGING_CHANNEL, PACKET_BROADCAST))
				.sendingToProxy() //TODO: figure out how broadcasting actually works.
				.parameter("bytes", new PacketParameter(new String(data, StandardCharsets.UTF_8))).build();

		flame.services().magicLink().connection().orElseThrow().publish(packet);
	}
}
