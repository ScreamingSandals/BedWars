/*
 * Copyright (C) 2024 ScreamingSandals
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

import org.jetbrains.annotations.ApiStatus;
import org.screamingsandals.bedwars.api.player.BWPlayer;

import java.util.List;

/**
 * @author ScreamingSandals
 *
 */
@ApiStatus.NonExtendable
public interface StatusBar {
	
	/**
	 * @param player
	 */
	void addPlayer(BWPlayer player);
	
	/**
	 * @param player
	 */
	void removePlayer(BWPlayer player);
	
	/**
	 * @param progress
	 */
	void setProgress(float progress);
	
	/**
	 * @return list of all viewers
	 */
	List<? extends BWPlayer> getViewers();
	
	/**
	 * @return progress of status bar
	 */
	float getProgress();
	
	/**
	 * @return visibility of status bar
	 */
	boolean isVisible();
	
	/**
	 * @param visible
	 */
	void setVisible(boolean visible);
}
