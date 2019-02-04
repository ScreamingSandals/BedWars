package misat11.bw.nms.v1_13_r2;

import net.minecraft.server.v1_13_R2.PacketPlayInClientCommand;
import net.minecraft.server.v1_13_R2.PacketPlayInClientCommand.EnumClientCommand;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class PerformRespawnRunnable extends BukkitRunnable {

  private Player player = null;

  public PerformRespawnRunnable(Player player) {
    this.player = player;
  }

  @Override
  public void run() {
    PacketPlayInClientCommand clientCommand =
        new PacketPlayInClientCommand(EnumClientCommand.PERFORM_RESPAWN);
    CraftPlayer cp = (CraftPlayer) player;

    cp.getHandle().playerConnection.a(clientCommand);
  }

}
