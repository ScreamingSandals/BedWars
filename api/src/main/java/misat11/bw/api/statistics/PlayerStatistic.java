package misat11.bw.api.statistics;

import java.util.UUID;

/**
 * @author Bedwars Team
 *
 */
public interface PlayerStatistic {
	/**
	 * @return
	 */
	public int getCurrentDeaths();

	/**
	 * @param currentDeaths
	 */
	public void setCurrentDeaths(int currentDeaths);

	/**
	 * @return
	 */
	public int getCurrentDestroyedBeds();

	/**
	 * @param currentDestroyedBeds
	 */
	public void setCurrentDestroyedBeds(int currentDestroyedBeds);

	/**
	 * @return
	 */
	public int getCurrentKills();

	/**
	 * @param currentKills
	 */
	public void setCurrentKills(int currentKills);

	/**
	 * @return
	 */
	public int getCurrentLoses();

	/**
	 * @param currentLoses
	 */
	public void setCurrentLoses(int currentLoses);

	/**
	 * @return
	 */
	public int getCurrentScore();

	/**
	 * @param currentScore
	 */
	public void setCurrentScore(int currentScore);

	/**
	 * @return
	 */
	public int getCurrentWins();

	/**
	 * @param currentWins
	 */
	public void setCurrentWins(int currentWins);

	/**
	 * @return
	 */
	public double getCurrentKD();

	/**
	 * @return
	 */
	public int getCurrentGames();

	/**
	 * @return
	 */
	public int getDeaths();

	/**
	 * @return
	 */
	public int getDestroyedBeds();

	/**
	 * @return
	 */
	public int getKills();

	/**
	 * @return
	 */
	public int getLoses();

	/**
	 * @return
	 */
	public String getName();

	/**
	 * @return
	 */
	public int getScore();

	/**
	 * @return
	 */
	public UUID getUuid();

	/**
	 * @return
	 */
	public int getWins();

	/**
	 * @return
	 */
	public double getKD();

	/**
	 * @return
	 */
	public int getGames();
}
