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

package org.screamingsandals.bedwars.special;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.screamingsandals.bedwars.api.special.Trap;
import org.screamingsandals.bedwars.game.GameImpl;
import org.screamingsandals.bedwars.game.TeamImpl;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.bedwars.player.BedWarsPlayer;
import org.screamingsandals.bedwars.utils.MiscUtils;
import org.screamingsandals.lib.block.Block;
import org.screamingsandals.lib.item.meta.PotionEffect;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.spectator.sound.SoundSource;
import org.screamingsandals.lib.spectator.sound.SoundStart;
import org.screamingsandals.lib.utils.ResourceLocation;
import org.screamingsandals.lib.world.Location;

import java.util.List;
import java.util.Map;

@Getter
@EqualsAndHashCode(callSuper = true)
public class TrapImpl extends SpecialItemImpl implements Trap {
    private final List<Map<String, Object>> trapData;
    private Location location;

    public TrapImpl(GameImpl game, BedWarsPlayer player, TeamImpl team, List<Map<String, Object>> trapData) {
        super(game, player, team);
        this.trapData = trapData;

        game.registerSpecialItem(this);
    }

    @Override
    public boolean isPlaced() {
        return location != null;
    }

    public void place(Location loc) {
        this.location = loc;
    }

    public void process(BedWarsPlayer player, TeamImpl runningTeam, boolean forceDestroy) {
        if (runningTeam == this.team || forceDestroy) {
            game.unregisterSpecialItem(this);
            location.getBlock().block(Block.air());
            return;
        }

        for (var data : trapData) {
            if (data.containsKey("sound")) {
                var sound = (String) data.get("sound");
                try {
                    player.playSound(
                            SoundStart.sound(
                                    ResourceLocation.of(sound),
                                    SoundSource.AMBIENT,
                                    1f,
                                    1f
                            ),
                            location.getX(), location.getY(), location.getZ()
                    );
                } catch (Throwable ignored) {}
            }

            if (data.containsKey("effect")) {
                var effect = PotionEffect.ofNullable(data.get("effect"));
                if (effect != null) {
                    player.addPotionEffect(effect);
                }
            }

            if (data.containsKey("damage")) {
                var damage = (double) data.get("damage");
                player.damage(damage);
            }
        }

        for (var p : this.team.getPlayers()) {
            MiscUtils.sendActionBarMessage(p, Message.of(LangKeys.SPECIALS_TRAP_CAUGHT_TEAM).placeholder("player", player.getDisplayName()));
        }
        MiscUtils.sendActionBarMessage(player, Message.of(LangKeys.SPECIALS_TRAP_CAUGHT).placeholder("team", getTeam().getName()));
    }
}
