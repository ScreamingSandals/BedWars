package misat11.bw.placeholderapi;

import org.bukkit.entity.Player;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import misat11.bw.Main;
import misat11.bw.api.statistics.PlayerStatistic;

public class BedwarsExpansion extends PlaceholderExpansion {

	@Override
	public String getAuthor() {
		return "Misat11";
	}

	@Override
	public String getIdentifier() {
		return "bedwars";
	}

	@Override
	public String getVersion() {
		return Main.getVersion();
	}
	
	@Override
	public String getPlugin() {
		return Main.getInstance().getName();
	}
	
	@Override
	public String onPlaceholderRequest(Player player, String identifier) {
		// About game
		
		
		// Player
		if (player == null) {
			return "";
		}
		
		// TODO current game
		
		// Stats
		
		if (!Main.isPlayerStatisticsEnabled()) {
			return null;
		}
		
		PlayerStatistic stats = Main.getPlayerStatisticsManager().getStatistic(player);
		
		if (stats == null) {
			return null;
		}
		
		switch (identifier.toLowerCase()) {
		case "stats_deaths":
			return Integer.toString(stats.getCurrentDeaths() + stats.getDeaths());
		case "stats_destroyed_beds":
			return Integer.toString(stats.getCurrentDestroyedBeds() + stats.getDestroyedBeds());
		case "stats_kills":
			return Integer.toString(stats.getCurrentKills() + stats.getKills());
		case "stats_loses":
			return Integer.toString(stats.getCurrentLoses() + stats.getLoses());
		case "stats_score":
			return Integer.toString(stats.getCurrentScore() + stats.getScore());
		case "stats_wins":
			return Integer.toString(stats.getCurrentWins() + stats.getWins());
		case "stats_games":
			return Integer.toString(stats.getCurrentGames() + stats.getGames());
		case "stats_kd":
			return Double.toString(stats.getCurrentKD() + stats.getKD());
		}
		
		return null;
	}

}
