package misat11.bw.nms.v1_8_r1;

import net.minecraft.server.v1_8_R1.ChatSerializer;
import net.minecraft.server.v1_8_R1.EnumTitleAction;
import net.minecraft.server.v1_8_R1.IChatBaseComponent;
import net.minecraft.server.v1_8_R1.PacketPlayOutTitle;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class Title {

  public static void showSubTitle(Player player, String subTitle, double fadeIn, double stay,
      double fadeOut) {
    IChatBaseComponent subTitleComponent = ChatSerializer.a("{\"text\": \"" + subTitle + "\"}");

    PacketPlayOutTitle subTitlePacket =
        new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, subTitleComponent);
    PacketPlayOutTitle timesPacket =
        new PacketPlayOutTitle(EnumTitleAction.TIMES, null, (int) Math.round(fadeIn * 20.0),
            (int) Math.round(stay * 20.0), (int) Math.round(fadeOut * 20.0));

    ((CraftPlayer) player).getHandle().playerConnection.sendPacket(timesPacket);
    ((CraftPlayer) player).getHandle().playerConnection.sendPacket(subTitlePacket);
  }

  public static void showTitle(Player player, String title, double fadeIn, double stay,
      double fadeOut) {
    IChatBaseComponent titleComponent = ChatSerializer.a("{\"text\": \"" + title + "\"}");

    PacketPlayOutTitle titlePacket = new PacketPlayOutTitle(EnumTitleAction.TITLE, titleComponent);
    PacketPlayOutTitle timesPacket = new PacketPlayOutTitle(EnumTitleAction.TIMES, null,
        (int) Math.round(fadeIn * 20), (int) Math.round(stay * 20), (int) Math.round(fadeOut * 20));

    ((CraftPlayer) player).getHandle().playerConnection.sendPacket(timesPacket);
    ((CraftPlayer) player).getHandle().playerConnection.sendPacket(titlePacket);
  }

}
