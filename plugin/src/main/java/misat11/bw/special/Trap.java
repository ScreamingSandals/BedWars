package misat11.bw.special;

import misat11.bw.api.Game;
import misat11.bw.api.RunningTeam;
import misat11.bw.api.Team;
import misat11.bw.utils.Sounds;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import java.util.List;
import java.util.Map;

public class Trap extends SpecialItem implements misat11.bw.api.special.Trap {

	private List<Map<String, Object>> trapData;
	private Location location;

	private Player player;

	public Trap(Game game, Player player, Team team, List<Map<String, Object>> trapData) {
		super(game, player, team);
		
		this.trapData = trapData;
		this.player = player;

		game.registerSpecialItem(this);
	}

	@Override
	public Location getLocation() {
		return location;
	}

	@Override
	public boolean isPlaced() {
		return location != null;
	}

	public Player getOwner() {
		return this.player;
	}

	public void place(Location loc) {
		this.location = loc;
	}

	public void process(Player player, RunningTeam runningTeam) {
		game.unregisterSpecialItem(this);
		
		location.getBlock().setType(Material.AIR);

		if (runningTeam != game.getTeamOfPlayer(player)) {
			for (Map<String, Object> data : trapData) {
				if (data.containsKey("sound")) {
					String sound = (String) data.get("sound");
					Sounds.playSound(player, location, sound, null, 1, 1);
				}

				if (data.containsKey("effect")) {
					PotionEffect effect = (PotionEffect) data.get("effect");
					player.addPotionEffect(effect);
				}

				if (data.containsKey("damage")) {
					double damage = (double) data.get("damage");
					player.damage(damage);
				}
			}
		}
	}

}
