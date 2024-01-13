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

package org.screamingsandals.bedwars.listener;

import org.screamingsandals.bedwars.api.config.GameConfigurationContainer;
import org.screamingsandals.bedwars.api.events.TargetInvalidationReason;
import org.screamingsandals.bedwars.config.MainConfig;
import org.screamingsandals.bedwars.events.BedDestroyedMessageSendEventImpl;
import org.screamingsandals.bedwars.events.PostTargetInvalidatedEventImpl;
import org.screamingsandals.bedwars.game.target.TargetBlockImpl;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.bedwars.lib.debug.Debug;
import org.screamingsandals.bedwars.player.BedWarsPlayer;
import org.screamingsandals.bedwars.player.PlayerManagerImpl;
import org.screamingsandals.bedwars.statistics.PlayerStatisticManager;
import org.screamingsandals.bedwars.utils.EconomyUtils;
import org.screamingsandals.bedwars.utils.SpawnEffects;
import org.screamingsandals.bedwars.utils.TitleUtils;
import org.screamingsandals.lib.event.EventManager;
import org.screamingsandals.lib.event.OnEvent;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.spectator.Component;
import org.screamingsandals.lib.spectator.sound.SoundSource;
import org.screamingsandals.lib.spectator.sound.SoundStart;
import org.screamingsandals.lib.utils.ResourceLocation;
import org.screamingsandals.lib.utils.annotations.Service;

@Service
public class TargetInvalidatedListener {
    @OnEvent
    public void onTargetInvalidated(PostTargetInvalidatedEventImpl event) {
        var game = event.getGame();
        var team = event.getTeam();
        var target = event.getTarget();
        var reason = event.getReason();

        var initiator = event.getInitiator();

        var cpsTitles = game.getConfigurationContainer().getOrDefault(GameConfigurationContainer.USE_CERTAIN_POPULAR_SERVER_TITLES, false);
        if (reason == TargetInvalidationReason.TIMEOUT && (!cpsTitles || !(target instanceof TargetBlockImpl))) {
            Message
                    .of(LangKeys.IN_GAME_TARGET_BLOCK_DESTROYED_DEATH_MODE)
                    .join(LangKeys.IN_GAME_TARGET_BLOCK_DESTROYED_SUBTITLE_VICTIM)
                    .times(TitleUtils.defaultTimes())
                    .title(team.getPlayers());
        }

        if (target instanceof TargetBlockImpl) {
            Debug.info(game.getName()+ ": target block of  " + team.getName() + " has been destroyed");

            var type = event.getBlockType();
            var isItBedBlock = type != null && type.is("#beds");
            var isItAnchor = type != null && type.isSameType("respawn_anchor");
            var isItCake = type != null && type.isSameType("cake");
            var isItDoor = type != null && type.is("#doors");

            var allPlayers = game.getConnectedPlayers();

            if (reason == TargetInvalidationReason.TIMEOUT) {
                Message
                        .of(LangKeys.IN_GAME_TARGET_BLOCK_DESTROYED_KILLABLE)
                        .placeholder("team", Component.text(team.getName(), team.getColor().getTextColor()))
                        .prefixOrDefault(game.getCustomPrefixComponent())
                        .send(allPlayers);
            } else {
                Component coloredDestroyer = Component.text("explosion");
                if (initiator != null) {
                    coloredDestroyer = initiator.getDisplayName().withColor(game.getPlayerTeam(PlayerManagerImpl.getInstance().getPlayer(initiator.getUuid()).orElseThrow()).getColor().getTextColor());
                }
                for (var player : allPlayers) {
                    if (!cpsTitles || game.getPlayerTeam(player) == team) {
                        String[] keys;
                        if (cpsTitles) {
                            keys = isItDoor ? LangKeys.IN_GAME_TARGET_BLOCK_DESTROYED_DOOR_CERTAIN_POPULAR_SERVER : (isItBedBlock ? LangKeys.IN_GAME_TARGET_BLOCK_DESTROYED_BED_CERTAIN_POPULAR_SERVER : (isItAnchor ? LangKeys.IN_GAME_TARGET_BLOCK_DESTROYED_ANCHOR_CERTAIN_POPULAR_SERVER : (isItCake ? LangKeys.IN_GAME_TARGET_BLOCK_DESTROYED_CAKE_CERTAIN_POPULAR_SERVER : LangKeys.IN_GAME_TARGET_BLOCK_DESTROYED_ANY_CERTAIN_POPULAR_SERVER)));
                        } else {
                            keys = isItDoor ? LangKeys.IN_GAME_TARGET_BLOCK_DESTROYED_DOOR : (isItBedBlock ? LangKeys.IN_GAME_TARGET_BLOCK_DESTROYED_BED : (isItAnchor ? LangKeys.IN_GAME_TARGET_BLOCK_DESTROYED_ANCHOR : (isItCake ? LangKeys.IN_GAME_TARGET_BLOCK_DESTROYED_CAKE : LangKeys.IN_GAME_TARGET_BLOCK_DESTROYED_ANY)));
                        }
                        var message = Message
                                .of(keys)
                                .placeholder("team", Component.text(team.getName(), team.getColor().getTextColor()))
                                .placeholder("broker", coloredDestroyer);

                        message.clone()
                                .join(game.getPlayerTeam(player) == team ? LangKeys.IN_GAME_TARGET_BLOCK_DESTROYED_SUBTITLE_VICTIM : LangKeys.IN_GAME_TARGET_BLOCK_DESTROYED_SUBTITLE)
                                .times(TitleUtils.defaultTimes())
                                .title(player);


                        var bbdmsEvent = new BedDestroyedMessageSendEventImpl(game, player, initiator != null ? initiator.as(BedWarsPlayer.class) : null, team, message);
                        EventManager.fire(bbdmsEvent);
                        if (!bbdmsEvent.isCancelled()) {
                            bbdmsEvent.getMessage().send(player);
                        }
                    }
                }
            }

            for (var player : allPlayers) {
                if (game.getPlayerTeam(player) == team) {
                    player.playSound(
                            SoundStart.sound(
                                    ResourceLocation.of(MainConfig.getInstance().node("sounds", "my_bed_destroyed", "sound").getString("entity.ender_dragon.growl")),
                                    SoundSource.AMBIENT,
                                    (float) MainConfig.getInstance().node("sounds", "my_bed_destroyed", "volume").getDouble(1),
                                    (float) MainConfig.getInstance().node("sounds", "my_bed_destroyed", "pitch").getDouble(1)
                            )
                    );
                } else if (reason != TargetInvalidationReason.TIMEOUT) {
                    player.playSound(
                            SoundStart.sound(
                                    ResourceLocation.of(MainConfig.getInstance().node("sounds", "bed_destroyed", "sound").getString("entity.ender_dragon.growl")),
                                    SoundSource.AMBIENT,
                                    (float) MainConfig.getInstance().node("sounds", "bed_destroyed", "volume").getDouble(1),
                                    (float) MainConfig.getInstance().node("sounds", "bed_destroyed", "pitch").getDouble(1)
                            )
                    );
                }
            }

            SpawnEffects.spawnEffect(game, ((TargetBlockImpl) target).getTargetBlock(), "game-effects.beddestroy");

            if (team.getHologram() != null) {
                team.getHologram().replaceLine(0, Message.of(isItDoor ? LangKeys.IN_GAME_TARGET_BLOCK_HOLOGRAM_DESTROYED_DOOR : (isItBedBlock ? LangKeys.IN_GAME_TARGET_BLOCK_HOLOGRAM_DESTROYED_BED : (isItAnchor ? LangKeys.IN_GAME_TARGET_BLOCK_HOLOGRAM_DESTROYED_ANCHOR : (isItCake ? LangKeys.IN_GAME_TARGET_BLOCK_HOLOGRAM_DESTROYED_CAKE : LangKeys.IN_GAME_TARGET_BLOCK_HOLOGRAM_DESTROYED_ANY)))));
                team.getPlayers().forEach(team.getHologram()::addViewer);
            }

            if (team.getProtectHologram() != null) {
                team.getProtectHologram().destroy();
                team.setProtectHologram(null);
            }
        }


        if (initiator != null) {
            if (PlayerStatisticManager.isEnabled()) {
                var statistic = PlayerStatisticManager.getInstance().getStatistic(initiator);
                statistic.addDestroyedBeds(1);
                statistic.addScore(game.getConfigurationContainer().getOrDefault(GameConfigurationContainer.STATISTICS_SCORES_BED_DESTROY, 25));
            }
            EconomyUtils.deposit(initiator, game.getConfigurationContainer().getOrDefault(GameConfigurationContainer.ECONOMY_REWARD_BED_DESTROY, 0.0));

            game.dispatchRewardCommands(
                    "player-destroy-bed",
                    initiator,
                    game.getConfigurationContainer().getOrDefault(GameConfigurationContainer.STATISTICS_SCORES_BED_DESTROY, 25)
            );
        }
    }
}
