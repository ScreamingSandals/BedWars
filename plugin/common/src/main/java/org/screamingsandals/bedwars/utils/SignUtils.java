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

package org.screamingsandals.bedwars.utils;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;
import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.bedwars.config.MainConfig;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.lib.block.Block;
import org.screamingsandals.lib.block.snapshot.SignBlockSnapshot;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.signs.ClickableSign;
import org.screamingsandals.lib.spectator.Component;
import org.screamingsandals.lib.utils.BlockFace;
import org.screamingsandals.lib.block.BlockPlacement;
import org.screamingsandals.lib.world.Location;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@UtilityClass
public class SignUtils {
    public Optional<BlockPlacement> getBlockBehindSign(ClickableSign sign) {
        return Optional.ofNullable(getGlassBehind(sign));
    }

    private BlockPlacement getGlassBehind(ClickableSign sign) {
        var location = sign.getLocation().as(Location.class);
        var block = location.getBlock();

        var type = block.block();
        if (!type.is("#signs")) {
            return null;
        }

        var data = type.get("facing");
        if (data != null) {
            return location.add(BlockFace.valueOf(data).getOppositeFace()).getBlock();
        } else {
            return location.add(BlockFace.DOWN).getBlock();
        }
    }

    public void updateSigns(@NotNull Game game) {
        final var config = MainConfig.getInstance();
        final var gameSigns = BedWarsSignService.getInstance().getSignsForKey(game.getName());

        if (gameSigns.isEmpty()) {
            return;
        }

        String[] statusLine;
        String[] playersLine;
        Block blockBehindMaterial;
        switch (game.getStatus()) {
            case REBUILDING:
                statusLine = LangKeys.SIGN_STATUS_REBUILDING_STATUS;
                playersLine = LangKeys.SIGN_STATUS_REBUILDING_PLAYERS;
                blockBehindMaterial = MiscUtils.getBlockTypeFromString(config.node("sign", "block-behind", "rebuilding").getString(), "BROWN_STAINED_GLASS");
                break;
            case RUNNING:
            case GAME_END_CELEBRATING:
                statusLine = LangKeys.SIGN_STATUS_RUNNING_STATUS;
                playersLine = LangKeys.SIGN_STATUS_RUNNING_PLAYERS;
                blockBehindMaterial = MiscUtils.getBlockTypeFromString(config.node("sign", "block-behind", "in-game").getString(), "GREEN_STAINED_GLASS");
                break;
            case WAITING:
                statusLine = LangKeys.SIGN_STATUS_WAITING_STATUS;
                playersLine = LangKeys.SIGN_STATUS_WAITING_PLAYERS;
                blockBehindMaterial = MiscUtils.getBlockTypeFromString(config.node("sign", "block-behind", "waiting").getString(), "ORANGE_STAINED_GLASS");
                break;
            case DISABLED:
            default:
                statusLine = LangKeys.SIGN_STATUS_DISABLED_STATUS;
                playersLine = LangKeys.SIGN_STATUS_DISABLED_PLAYERS;
                blockBehindMaterial = MiscUtils.getBlockTypeFromString(config.node("sign", "block-behind", "game-disabled").getString(), "RED_STAINED_GLASS");
                break;
        }

        var statusMessage = Message.of(statusLine);
        var playerMessage = Message.of(playersLine)
                .placeholder("players", game.countConnectedPlayers())
                .placeholder("maxplayers", game.getMaxPlayers());

        final var texts = MainConfig.getInstance().node("sign", "lines").childrenList().stream()
                .map(ConfigurationNode::getString)
                .map(s -> Objects.requireNonNullElse(s, "")
                        .replace("%arena%", ((Component) game.getDisplayNameComponent()).toLegacy())
                        .replace("%status%", statusMessage.asComponent().toLegacy())
                        .replace("%players%", playerMessage.asComponent().toLegacy()))
                .collect(Collectors.toList());

        final var finalBlockBehindMaterial = blockBehindMaterial;
        for (var signBlock : gameSigns) {
            signBlock.getLocation().asOptional(Location.class)
                    .ifPresent(location -> {
                        if (location.getChunk().isLoaded()) {
                            var blockState = location.getBlock().blockSnapshot();
                            if (blockState instanceof SignBlockSnapshot) {
                                var sign = (SignBlockSnapshot) blockState;
                                for (int i = 0; i < texts.size() && i < 4; i++) {
                                    sign.frontLine(i, Component.fromLegacy(texts.get(i)));
                                }
                                sign.updateBlock();
                            }

                            if (config.node("sign", "block-behind", "enabled").getBoolean(false)) {
                                final var optionalBlock = SignUtils.getBlockBehindSign(signBlock);
                                if (optionalBlock.isPresent()) {
                                    final var glassBlock = optionalBlock.get();
                                    glassBlock.block(finalBlockBehindMaterial);
                                }
                            }
                        }
                    });
        }
    }
}
