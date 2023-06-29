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

package org.screamingsandals.bedwars.utils;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.screamingsandals.bedwars.commands.BedWarsPermission;
import org.screamingsandals.bedwars.config.MainConfig;
import org.screamingsandals.bedwars.game.GameManagerImpl;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.bedwars.player.PlayerManagerImpl;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.player.PlayerWrapper;
import org.screamingsandals.lib.plugin.ServiceManager;
import org.screamingsandals.lib.signs.AbstractSignManager;
import org.screamingsandals.lib.signs.ClickableSign;
import org.screamingsandals.lib.spectator.Component;
import org.screamingsandals.lib.tasker.Tasker;
import org.screamingsandals.lib.utils.annotations.Service;
import org.screamingsandals.lib.utils.annotations.parameters.ConfigFile;
import org.screamingsandals.lib.block.BlockMapper;
import org.screamingsandals.lib.world.LocationHolder;
import org.screamingsandals.lib.block.state.BlockStateHolder;
import org.screamingsandals.lib.block.state.SignHolder;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service(dependsOn = {
        MainConfig.class,
        GameManagerImpl.class,
        Tasker.class,
        PlayerManagerImpl.class
})
@RequiredArgsConstructor
public class BedWarsSignService extends AbstractSignManager {
    @ConfigFile(value = "database/sign.yml", old = "sign.yml")
    @Getter(AccessLevel.PROTECTED)
    private final YamlConfigurationLoader loader;
    private final GameManagerImpl gameManager;
    private final MainConfig mainConfig;
    private final PlayerManagerImpl playerManager;

    public static BedWarsSignService getInstance() {
        return ServiceManager.get(BedWarsSignService.class);
    }

    @Override
    protected boolean isAllowedToUse(PlayerWrapper player) {
        return true;
    }

    @Override
    protected boolean isAllowedToEdit(PlayerWrapper player) {
        return player.hasPermission(BedWarsPermission.ADMIN_PERMISSION.asPermission());
    }

    @Override
    protected Optional<String> normalizeKey(Component key) {
        var key2 = key.toPlainText();
        if (gameManager.hasGame(key2)) {
            return Optional.of(key2);
        }
        return Optional.empty();
    }

    @Override
    protected void updateSign(ClickableSign sign) {
        var name = sign.getKey();
        gameManager.getGame(name).ifPresentOrElse(game ->
                        Tasker.build(game::updateSigns)
                                .afterOneTick()
                                .start(),
                () -> {
                    if ("leave".equalsIgnoreCase(name)) {
                        Tasker.build(() -> updateLeave(sign))
                                .afterOneTick()
                                .start();
                    }
                });
    }

    private void updateLeave(ClickableSign clickableSign) {
        var texts = mainConfig.node("sign", "lines")
                .childrenList()
                .stream()
                .map(ConfigurationNode::getString)
                .map(s -> Objects.requireNonNullElse(s, "")
                        .replace("%arena%", Message.of(LangKeys.IN_GAME_LOBBY_ITEMS_LEAVE_FROM_GAME_ITEM).getForAnyoneJoined().toLegacy())
                        .replace("%status%", "")
                        .replace("%players%", "")
                )
                .map(Component::fromLegacy)
                .collect(Collectors.toList());

        clickableSign.getLocation().asOptional(LocationHolder.class)
                .flatMap(location -> BlockMapper.getBlockAt(location).<BlockStateHolder>getBlockState())
                .ifPresent(blockState -> {
                    if (blockState instanceof SignHolder) {
                        for (int i = 0; i < texts.size(); i++) {
                            ((SignHolder) blockState).line(i, texts.get(i));
                        }
                        blockState.updateBlock();
                    }
                });
    }

    @Override
    protected void onClick(PlayerWrapper player, ClickableSign sign) {
        if (sign.getKey().equalsIgnoreCase("leave")) {
            if (playerManager.isPlayerInGame(player)) {
                playerManager.getPlayer(player).orElseThrow().changeGame(null);
            }
        } else {
            gameManager.getGame(sign.getKey()).ifPresentOrElse(
                    game -> game.joinToGame(PlayerManagerImpl.getInstance().getPlayerOrCreate(player)),
                    () -> Message.of(LangKeys.SIGN_ADMIN_UNKNOWN_GAME).defaultPrefix().send(player)
            );
        }
    }

    @Override
    protected boolean isFirstLineValid(Component firstLine) {
        var line = firstLine.toPlainText();
        return "[bedwars]".equalsIgnoreCase(line) || "[bwgame]".equalsIgnoreCase(line);
    }

    @Override
    protected Component signCreatedMessage(PlayerWrapper player) {
        return Message.of(LangKeys.SIGN_ADMIN_CREATED).defaultPrefix().asComponent(player);
    }

    @Override
    protected Component signCannotBeCreatedMessage(PlayerWrapper player) {
        return Message.of(LangKeys.SIGN_ADMIN_CANNOT_CREATE).defaultPrefix().asComponent(player);
    }

    @Override
    protected Component signCannotBeDestroyedMessage(PlayerWrapper player) {
        return Message.of(LangKeys.SIGN_ADMIN_CANNOT_DESTROY).defaultPrefix().asComponent(player);
    }
}
