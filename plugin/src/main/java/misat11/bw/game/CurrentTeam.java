package misat11.bw.game;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import misat11.bw.api.RunningTeam;
import misat11.bw.api.TeamColor;

public class CurrentTeam implements RunningTeam {
	public final Team teamInfo;
	public final List<GamePlayer> players = new ArrayList<>();
	private org.bukkit.scoreboard.Team scoreboardTeam;

	public boolean isBed = true;

	public CurrentTeam(Team team) {
		this.teamInfo = team;
	}

	public boolean isDead() {
		return players.isEmpty();
	}

	public boolean isAlive() {
		return !players.isEmpty();
	}

	public org.bukkit.scoreboard.Team getScoreboardTeam() {
		return scoreboardTeam;
	}

	public void setScoreboardTeam(org.bukkit.scoreboard.Team scoreboardTeam) {
		this.scoreboardTeam = scoreboardTeam;
	}

	@Override
	public String getName() {
		return teamInfo.name;
	}

	@Override
	public TeamColor getColor() {
		return teamInfo.color.toApiColor();
	}

	@Override
	public Location getTeamSpawn() {
		return teamInfo.spawn;
	}

	@Override
	public Location getTargetBlock() {
		return teamInfo.bed;
	}

	@Override
	public int getMaxPlayers() {
		return teamInfo.maxPlayers;
	}

	@Override
	public int countConnectedPlayers() {
		return players.size();
	}

	@Override
	public List<Player> getConnectedPlayers() {
		List<Player> playerList = new ArrayList<Player>();
		for (GamePlayer gamePlayer : players) {
			playerList.add(gamePlayer.player);
		}
		return playerList;
	}

	@Override
	public boolean isPlayerInTeam(Player player) {
		for (GamePlayer gamePlayer : players) {
			if (gamePlayer.player.equals(player)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isTargetBlockExists() {
		return isBed;
	}
}
