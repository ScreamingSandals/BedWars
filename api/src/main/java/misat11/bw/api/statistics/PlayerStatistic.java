package misat11.bw.api.statistics;

import java.util.UUID;

public interface PlayerStatistic {
	public int getCurrentDeaths();

	public void setCurrentDeaths(int currentDeaths);

	public int getCurrentDestroyedBeds();

	public void setCurrentDestroyedBeds(int currentDestroyedBeds);

	public int getCurrentKills();

	public void setCurrentKills(int currentKills);

	public int getCurrentLoses();

	public void setCurrentLoses(int currentLoses);

	public int getCurrentScore();

	public void setCurrentScore(int currentScore);

	public int getCurrentWins();

	public void setCurrentWins(int currentWins);

	public double getCurrentKD();

	public int getCurrentGames();

	public int getDeaths();

	public int getDestroyedBeds();

	public int getKills();

	public int getLoses();

	public String getName();

	public int getScore();

	public UUID getUuid();

	public int getWins();

	public double getKD();

	public int getGames();
}
