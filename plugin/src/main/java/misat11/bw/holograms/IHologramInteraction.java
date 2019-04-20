package misat11.bw.holograms;

import java.util.ArrayList;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface IHologramInteraction {

  public void addHologramLocation(Location eyeLocation);

  public ArrayList<Location> getHologramLocations();

  public String getType();

  public void loadHolograms();

  public void onHologramTouch(Player player, Location holoLocation);

  public void unloadAllHolograms(Player player);

  public void unloadHolograms();

  public void updateHolograms(Player p);

  public void updateHolograms(Player player, long l);

  public void updateHolograms();

}
