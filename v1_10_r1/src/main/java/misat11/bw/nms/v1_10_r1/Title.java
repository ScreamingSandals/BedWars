package misat11.bw.nms.v1_10_r1;

import net.minecraft.server.v1_10_R1.IChatBaseComponent;
import net.minecraft.server.v1_10_R1.PacketPlayOutTitle;
import net.minecraft.server.v1_10_R1.PacketPlayOutTitle.EnumTitleAction;
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class Title {

  public static void showSubTitle(Player player, String subTitle, double fadeIn, double stay,
      double fadeOut) {
    IChatBaseComponent subTitleComponent =
        IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + subTitle + "\"}");

    PacketPlayOutTitle subTitlePacket =
        new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, subTitleComponent);
    PacketPlayOutTitle timesPacket =
        new PacketPlayOutTitle(EnumTitleAction.TIMES, null, (int) Math.round(fadeIn),
            (int) Math.round(stay), (int) Math.round(fadeOut));

    ((CraftPlayer) player).getHandle().playerConnection.sendPacket(timesPacket);
    ((CraftPlayer) player).getHandle().playerConnection.sendPacket(subTitlePacket);
  }

  public static void showTitle(Player player, String title, double fadeIn, double stay,
      double fadeOut) {
    IChatBaseComponent titleComponent =
        IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + title + "\"}");

    PacketPlayOutTitle titlePacket = new PacketPlayOutTitle(EnumTitleAction.TITLE, titleComponent);
    PacketPlayOutTitle timesPacket = new PacketPlayOutTitle(EnumTitleAction.TIMES, null,
        (int) Math.round(fadeIn), (int) Math.round(stay), (int) Math.round(fadeOut));

    ((CraftPlayer) player).getHandle().playerConnection.sendPacket(timesPacket);
    ((CraftPlayer) player).getHandle().playerConnection.sendPacket(titlePacket);
  }

}
