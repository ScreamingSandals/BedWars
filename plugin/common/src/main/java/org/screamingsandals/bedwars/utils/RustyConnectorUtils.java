package org.screamingsandals.bedwars.utils;

import group.aelysium.rustyconnector.toolkit.RustyConnector;
import group.aelysium.rustyconnector.toolkit.core.packet.Packet;
import group.aelysium.rustyconnector.toolkit.core.packet.PacketIdentification;
import group.aelysium.rustyconnector.toolkit.mc_loader.central.IMCLoaderFlame;
import group.aelysium.rustyconnector.toolkit.mc_loader.central.IMCLoaderTinder;
import lombok.experimental.UtilityClass;
import org.screamingsandals.bedwars.BedWarsPlugin;
import org.screamingsandals.bedwars.config.MainConfig;
import org.screamingsandals.bedwars.lib.debug.Debug;
import org.screamingsandals.lib.Server;
import org.screamingsandals.lib.player.Player;
import org.screamingsandals.lib.spectator.Color;
import org.screamingsandals.lib.spectator.Component;

import java.util.Optional;

@UtilityClass
public class RustyConnectorUtils {
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
		Packet message = flame.services().packetBuilder().newBuilder()
				.identification(PacketIdentification.from("RC", "SP"))
				.sendingToProxy()
				.parameter("f", familyName)
				.parameter("p", player.getUniqueId().toString())
				.build();

		flame.services().magicLink().connection().orElseThrow().publish(message);
		Debug.info("Player " + player.getName() + " has been moved to hub server.");
	}

	public static IMCLoaderFlame<?> getFlame() {
		return flame;
	}
}
