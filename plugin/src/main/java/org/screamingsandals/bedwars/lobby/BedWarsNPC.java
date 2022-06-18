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

package org.screamingsandals.bedwars.lobby;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import org.screamingsandals.bedwars.game.GameManagerImpl;
import org.screamingsandals.bedwars.game.GroupManagerImpl;
import org.screamingsandals.bedwars.inventories.GamesInventory;
import org.screamingsandals.bedwars.player.PlayerManagerImpl;
import org.screamingsandals.bedwars.utils.MiscUtils;
import org.screamingsandals.bedwars.utils.SerializableLocation;
import org.screamingsandals.lib.Server;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.npc.NPC;
import org.screamingsandals.lib.npc.skin.NPCSkin;
import org.screamingsandals.lib.player.PlayerMapper;
import org.screamingsandals.lib.player.PlayerWrapper;
import org.screamingsandals.lib.utils.InteractType;
import org.screamingsandals.lib.utils.TriConsumer;
import org.screamingsandals.lib.world.LocationHolder;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@ConfigSerializable
public class BedWarsNPC {
    private SerializableLocation location;
    @Nullable
    private NPCSkin skin;
    private Action action = Action.DUMMY;
    private String value;
    private boolean shouldLookAtPlayer = true;
    private final List<String> hologramAbove = new ArrayList<>();

    @Getter
    private transient NPC npc;

    public void spawn() {
        if (npc == null && location != null && location.isWorldLoaded()) {
            npc = NPC.of(location.as(LocationHolder.class))
                    .touchable(true)
                    .lookAtPlayer(shouldLookAtPlayer);

            var holo = npc.hologram();
            hologramAbove.forEach(s -> holo.bottomLine(Message.ofRichText(s)));

            if (skin != null && skin.getValue() != null) {
                npc.skin(skin);
            }

            npc.show();

            Server.getConnectedPlayers().forEach(npc::addViewer);
        }
    }

    public void destroy() {
        if (npc != null) {
            npc.destroy();
            npc = null;
        }
    }

    public void handleClick(PlayerWrapper player, InteractType type) {
        if (action != null && (!action.requireArguments() || (value != null && !value.isEmpty()))) {
            action.handler.accept(this, player, type);
        }
    }

    @RequiredArgsConstructor
    public enum Action {
        DUMMY((bedWarsNPC, player, type) -> {
        }),
        PLAYER_COMMAND((bedWarsNPC, player, type) -> {
            Server.runSynchronously(() -> player.tryToDispatchCommand(bedWarsNPC.value));
        }),
        CONSOLE_COMMAND((bedWarsNPC, player, type) -> {
            Server.runSynchronously(() -> PlayerMapper.getConsoleSender().tryToDispatchCommand(bedWarsNPC.value));
        }),
        OPEN_GAMES_INVENTORY((bedWarsNPC, player, type) -> {
            Server.runSynchronously(() -> GamesInventory.getInstance().openForPlayer(player, bedWarsNPC.value));
        }),
        JOIN_GAME((bedWarsNPC, player, type) -> {
            GameManagerImpl.getInstance().getGame(bedWarsNPC.value).ifPresent(game1 ->
                    Server.runSynchronously(() -> game1.joinToGame(PlayerManagerImpl.getInstance().getPlayerOrCreate(player)))
            );
        }),
        JOIN_GROUP((bedWarsNPC, player, type) -> {
            MiscUtils.getGameWithHighestPlayers(GroupManagerImpl.getInstance().getGamesInGroup(bedWarsNPC.value), false).ifPresent(game ->
                    Server.runSynchronously(() -> game.joinToGame(PlayerManagerImpl.getInstance().getPlayerOrCreate(player)))
            );
        }),
        JOIN_VARIANT((bedWarsNPC, player, type) -> {
            MiscUtils.getGameWithHighestPlayers(GameManagerImpl.getInstance().getGames().stream().filter(game -> game.getGameVariant().getName().equals(bedWarsNPC.value)).collect(Collectors.toList()), false).ifPresent(game ->
                    Server.runSynchronously(() -> game.joinToGame(PlayerManagerImpl.getInstance().getPlayerOrCreate(player)))
            );
        }),
        JOIN_RANDOM((bedWarsNPC, player, type) -> {
            GameManagerImpl.getInstance().getGameWithHighestPlayers(false).ifPresent(game ->
                    Server.runSynchronously(() -> game.joinToGame(PlayerManagerImpl.getInstance().getPlayerOrCreate(player)))
            );
        });

        private final TriConsumer<BedWarsNPC, PlayerWrapper, InteractType> handler;

        public boolean requireArguments() {
            return this != DUMMY && this != JOIN_RANDOM;
        }
    }

}
