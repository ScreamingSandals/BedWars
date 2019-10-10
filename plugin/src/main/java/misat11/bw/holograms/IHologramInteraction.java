package misat11.bw.holograms;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public interface IHologramInteraction {

    void addHologramLocation(Location eyeLocation);

    ArrayList<Location> getHologramLocations();

    String getType();

    void loadHolograms();

    void onHologramTouch(Player player, Location holoLocation);

    void unloadAllHolograms(Player player);

    void unloadHolograms();

    void updateHolograms(Player p);

    void updateHolograms(Player player, long l);

    void updateHolograms();

}
