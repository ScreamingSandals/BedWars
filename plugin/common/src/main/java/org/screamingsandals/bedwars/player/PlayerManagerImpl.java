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

package org.screamingsandals.bedwars.player;

import lombok.RequiredArgsConstructor;
import org.screamingsandals.bedwars.api.player.PlayerManager;
import org.screamingsandals.bedwars.game.GameImpl;
import org.screamingsandals.bedwars.game.GameManagerImpl;
import org.screamingsandals.lib.player.PlayerMapper;
import org.screamingsandals.lib.player.PlayerWrapper;
import org.screamingsandals.lib.plugin.ServiceManager;
import org.screamingsandals.lib.utils.annotations.Service;

import java.util.*;

@Service(dependsOn = {
        GameManagerImpl.class
})
@RequiredArgsConstructor
public class PlayerManagerImpl implements PlayerManager {
    private final Map<UUID, BedWarsPlayer> players = new HashMap<>();

    {
        PlayerMapper
                .UNSAFE_getPlayerConverter()
                .registerP2W(BedWarsPlayer.class, bwPlayer -> (PlayerWrapper) bwPlayer.raw())
                .registerW2P(BedWarsPlayer.class, playerWrapper -> getPlayer(playerWrapper).orElseThrow());
    }

    public static PlayerManagerImpl getInstance() {
        return ServiceManager.get(PlayerManagerImpl.class);
    }

    public BedWarsPlayer getPlayerOrCreate(PlayerWrapper playerWrapper) {
        return getPlayer(playerWrapper)
                .orElseGet(() -> {
                    var p = new BedWarsPlayer(playerWrapper);
                    players.put(p.getUuid(), p);
                    return p;
                });
    }

    public Optional<BedWarsPlayer> getPlayer(UUID uuid) {
        return PlayerMapper.getPlayer(uuid).flatMap(this::getPlayer);
    }

    public Optional<BedWarsPlayer> getPlayer(PlayerWrapper playerWrapper) {
        return Optional.ofNullable(players.get(playerWrapper.getUuid()));
    }

    public boolean isPlayerInGame(PlayerWrapper playerWrapper) {
        return getPlayer(playerWrapper).map(p -> p.getGame() != null).orElse(false);
    }

    public boolean isPlayerInGame(UUID uuid) {
        return getPlayer(uuid).map(p -> p.getGame() != null).orElse(false);
    }

    public void dropPlayer(BedWarsPlayer player) {
        player.changeGame(null);
        players.remove(player.getUuid());
    }

    public boolean isPlayerRegistered(PlayerWrapper playerWrapper) {
        return getPlayer(playerWrapper).isPresent();
    }

    @Override
    public boolean isPlayerRegistered(UUID uuid) {
        return getPlayer(uuid).isPresent();
    }

    @Override
    public Optional<GameImpl> getGameOfPlayer(UUID uuid) {
        return getPlayer(uuid).map(BedWarsPlayer::getGame);
    }

    public Optional<GameImpl> getGameOfPlayer(PlayerWrapper playerWrapper) {
        return getPlayer(playerWrapper).map(BedWarsPlayer::getGame);
    }
}
