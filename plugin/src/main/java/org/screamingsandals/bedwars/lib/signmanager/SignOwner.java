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

package org.screamingsandals.bedwars.lib.signmanager;

import java.util.List;

import org.bukkit.entity.Player;

public interface SignOwner {
	boolean isNameExists(String name);
	
	void updateSign(SignBlock sign);
	
	List<String> getSignPrefixes();
	
	void onClick(Player player, SignBlock sign);
	
	List<String> getSignCreationPermissions();
	
	String returnTranslate(String key);
}
