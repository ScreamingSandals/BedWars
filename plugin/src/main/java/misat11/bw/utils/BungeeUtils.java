package misat11.bw.utils;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import misat11.bw.Main;
import org.bukkit.entity.Player;

public class BungeeUtils {

    public static void movePlayer(Player player) {
        String server = Main.getConfigurator().config.getString("bungee.server");
        ByteArrayDataOutput out = ByteStreams.newDataOutput();

        out.writeUTF("Connect");
        out.writeUTF(server);

        player.sendPluginMessage(Main.getInstance(), "BungeeCord", out.toByteArray());

    }
}