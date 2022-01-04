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
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Nullable;
import org.screamingsandals.bedwars.game.GameManagerImpl;
import org.screamingsandals.bedwars.inventories.GamesInventory;
import org.screamingsandals.bedwars.player.PlayerManagerImpl;
import org.screamingsandals.bedwars.utils.SerializableLocation;
import org.screamingsandals.lib.Server;
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

@Data
@ConfigSerializable
public class BedWarsNPC {
    private SerializableLocation location;
    @Nullable
    private NPCSkin skin;
    private Action action = Action.DUMMY;
    private String value;
    private boolean shouldLookAtPlayer = true;
    private final List<Component> hologramAbove = new ArrayList<>();

    @Getter
    private transient NPC npc;

    public void spawn() {
        if (npc == null && location != null && location.isWorldLoaded()) {
            npc = NPC.of(location.as(LocationHolder.class))
                    .setDisplayName(hologramAbove)
                    .setTouchable(true)
                    .setShouldLookAtPlayer(shouldLookAtPlayer);

            if (skin != null && skin.getValue() != null) {
                npc.setSkin(skin);
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
        if (action != null && value != null && !value.isEmpty()) {
            action.handler.accept(this, player, type);
        }
    }

    @RequiredArgsConstructor
    public enum Action {
        DUMMY((bedWarsNPC, playerWrapper, type) -> {
        }),
        PLAYER_COMMAND((bedWarsNPC, playerWrapper, type) -> {
            Server.runSynchronously(() -> playerWrapper.tryToDispatchCommand(bedWarsNPC.value));
        }),
        CONSOLE_COMMAND((bedWarsNPC, playerWrapper, type) -> {
            Server.runSynchronously(() -> PlayerMapper.getConsoleSender().tryToDispatchCommand(bedWarsNPC.value));
        }),
        OPEN_GAMES_INVENTORY((bedWarsNPC, player, type) -> {
            Server.runSynchronously(() -> GamesInventory.getInstance().openForPlayer(player, bedWarsNPC.value));
        }),
        JOIN_GAME((bedWarsNPC, player, type) -> {
            GameManagerImpl.getInstance().getGame(bedWarsNPC.value).ifPresent(game1 ->
                    Server.runSynchronously(() -> game1.joinToGame(PlayerManagerImpl.getInstance().getPlayerOrCreate(player)))
            );
        });

        private final TriConsumer<BedWarsNPC, PlayerWrapper, InteractType> handler;

        public boolean requireArguments() {
            return this != DUMMY;
        }
    }

}
