package misat11.bw.statistics;

// From BedwarsRel

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerStatistic implements ConfigurationSerializable, misat11.bw.api.statistics.PlayerStatistic {

	private int currentDeaths = 0;
	private int currentDestroyedBeds = 0;
	private int currentKills = 0;
	private int currentLoses = 0;
	private int currentScore = 0;
	private int currentWins = 0;
	private int deaths = 0;
	private int destroyedBeds = 0;
	private int kills = 0;
	private int loses = 0;
	private String name = "";
	private int score = 0;
	private UUID uuid;
	private int wins = 0;

	public PlayerStatistic(UUID uuid) {
		this.uuid = uuid;

		Player player = Bukkit.getPlayer(uuid);
		if (player != null && !this.name.equals(player.getName())) {
			this.name = player.getName();
		}
	}

	public PlayerStatistic(OfflinePlayer player) {
		this.uuid = player.getUniqueId();
		this.name = player.getName();
	}

	public PlayerStatistic() {

	}

	public PlayerStatistic(Map<String, Object> deserialize) {
		if (deserialize.containsKey("deaths")) {
			this.deaths = (int) deserialize.get("deaths");
		}
		if (deserialize.containsKey("destroyedBeds")) {
			this.destroyedBeds = (int) deserialize.get("destroyedBeds");
		}
		if (deserialize.containsKey("kills")) {
			this.kills = (int) deserialize.get("kills");
		}
		if (deserialize.containsKey("loses")) {
			this.loses = (int) deserialize.get("loses");
		}
		if (deserialize.containsKey("score")) {
			this.score = (int) deserialize.get("score");
		}
		if (deserialize.containsKey("wins")) {
			this.wins = (int) deserialize.get("wins");
		}
		if (deserialize.containsKey("name")) {
			this.name = (String) deserialize.get("name");
		}
		if (deserialize.containsKey("uuid")) {
			this.uuid = UUID.fromString((String) deserialize.get("uuid"));
		}
	}

	public void addCurrentValues() {
		this.deaths = this.deaths + this.currentDeaths;
		this.currentDeaths = 0;
		this.destroyedBeds = this.destroyedBeds + this.currentDestroyedBeds;
		this.currentDestroyedBeds = 0;
		this.kills = this.kills + this.currentKills;
		this.currentKills = 0;
		this.loses = this.loses + this.currentLoses;
		this.currentLoses = 0;
		this.score = this.score + this.currentScore;
		this.currentScore = 0;
		this.wins = this.wins + this.currentWins;
		this.currentWins = 0;

	}

	public int getCurrentGames() {
		return this.getCurrentWins() + this.getCurrentLoses();
	}

	public double getCurrentKD() {
		double kd = 0.0;
		if (this.getDeaths() + this.getCurrentDeaths() == 0) {
			kd = this.getKills();
		} else if (this.getKills() + this.getCurrentKills() == 0) {
			kd = 0.0;
		} else {
			kd = ((double) this.getKills() + this.getCurrentKills())
					/ ((double) this.getDeaths() + this.getCurrentDeaths());
		}
		kd = Math.round(kd * 100.0) / 100.0;

		return kd;
	}

	public int getGames() {
		return this.getWins() + this.getLoses();
	}

	public UUID getId() {
		return this.uuid;
	}

	public void setId(UUID uuid) {
		this.uuid = uuid;
	}

	public double getKD() {
		double kd = 0.0;
		if (this.getDeaths() == 0) {
			kd = this.getKills();
		} else if (this.getKills() == 0) {
			kd = 0.0;
		} else {
			kd = ((double) this.getKills()) / ((double) this.getDeaths());
		}
		kd = Math.round(kd * 100.0) / 100.0;

		return kd;
	}

	@Override
	public Map<String, Object> serialize() {
		HashMap<String, Object> playerStatistic = new HashMap<>();
		playerStatistic.put("deaths", this.deaths);
		playerStatistic.put("destroyedBeds", this.destroyedBeds);
		playerStatistic.put("kills", this.kills);
		playerStatistic.put("loses", this.loses);
		playerStatistic.put("score", this.score);
		playerStatistic.put("wins", this.wins);
		playerStatistic.put("name", this.name);
		return playerStatistic;
	}

	public int getCurrentDeaths() {
		return currentDeaths;
	}

	public void setCurrentDeaths(int currentDeaths) {
		this.currentDeaths = currentDeaths;
	}

	public int getCurrentDestroyedBeds() {
		return currentDestroyedBeds;
	}

	public void setCurrentDestroyedBeds(int currentDestroyedBeds) {
		this.currentDestroyedBeds = currentDestroyedBeds;
	}

	public int getCurrentKills() {
		return currentKills;
	}

	public void setCurrentKills(int currentKills) {
		this.currentKills = currentKills;
	}

	public int getCurrentLoses() {
		return currentLoses;
	}

	public void setCurrentLoses(int currentLoses) {
		this.currentLoses = currentLoses;
	}

	public int getCurrentScore() {
		return currentScore;
	}

	public void setCurrentScore(int currentScore) {
		this.currentScore = currentScore;
	}

	public int getCurrentWins() {
		return currentWins;
	}

	public void setCurrentWins(int currentWins) {
		this.currentWins = currentWins;
	}

	public int getDeaths() {
		return deaths;
	}

	public int getDestroyedBeds() {
		return destroyedBeds;
	}

	public int getKills() {
		return kills;
	}

	public int getLoses() {
		return loses;
	}

	public String getName() {
		return name;
	}

	public int getScore() {
		return score;
	}

	public UUID getUuid() {
		return uuid;
	}

	public int getWins() {
		return wins;
	}
	
	public void setName(String name) {
		this.name = name;
	}

}
