/*
 * Copyright (C) 2022 ScreamingSandals
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

package org.screamingsandals.bedwars.special;

import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.bedwars.api.RunningTeam;
import org.screamingsandals.bedwars.utils.MiscUtils;
import org.screamingsandals.bedwars.utils.Sounds;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import java.util.List;
import java.util.Map;

import static org.screamingsandals.bedwars.lib.lang.I18n.i18nonly;

public class Trap extends SpecialItem implements org.screamingsandals.bedwars.api.special.Trap {
    private List<Map<String, Object>> trapData;
    private Location location;
    private RunningTeam runningTeam;

    public Trap(Game game, Player player, RunningTeam team, List<Map<String, Object>> trapData) {
        super(game, player, team);
        this.trapData = trapData;
        this.runningTeam = team;

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

    public void place(Location loc) {
        this.location = loc;
    }

    public void process(Player player, RunningTeam runningTeam, boolean forceDestroy) {
        if (runningTeam == this.runningTeam || forceDestroy) {
            game.unregisterSpecialItem(this);
            location.getBlock().setType(Material.AIR);
            return;
        }

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

        for (Player p : this.runningTeam.getConnectedPlayers()) {
            MiscUtils.sendActionBarMessage(p, i18nonly("specials_trap_caught_team").replace("%player%", player.getDisplayName()));
        }
        MiscUtils.sendActionBarMessage(player, i18nonly("specials_trap_caught").replace("%team%", getTeam().getName()));
    }
}
