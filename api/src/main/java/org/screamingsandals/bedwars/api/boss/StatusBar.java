/*
 * Copyright (C) 2023 ScreamingSandals
 *
 * This file is part of Screaming BedWars.
 *
 * Screaming BedWars is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Screaming BedWars is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Screaming BedWars. If not, see <https://www.gnu.org/licenses/>.
 */

package org.screamingsandals.bedwars.api.boss;

import java.util.List;

import org.bukkit.entity.Player;

/**
 * @author Bedwars Team
 *
 */
public interface StatusBar {
	
	/**
	 * @param player
	 */
	public void addPlayer(Player player);
	
	/**
	 * @param player
	 */
	public void removePlayer(Player player);
	
	/**
	 * @param progress
	 */
	public void setProgress(double progress);
	
	/**
	 * @return list of all viewers
	 */
	public List<Player> getViewers();
	
	/**
	 * @return progress of status bar
	 */
	public double getProgress();
	
	/**
	 * @return visibility of status bar
	 */
	public boolean isVisible();
	
	/**
	 * @param visible
	 */
	public void setVisible(boolean visible);
}
