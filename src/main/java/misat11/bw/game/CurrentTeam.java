package misat11.bw.game;

import java.util.ArrayList;
import java.util.List;

public class CurrentTeam {
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
}
