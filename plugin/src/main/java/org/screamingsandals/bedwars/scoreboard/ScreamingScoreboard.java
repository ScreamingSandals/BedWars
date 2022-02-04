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

package org.screamingsandals.bedwars.scoreboard;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.screamingsandals.bedwars.api.config.ConfigurationContainer;
import org.screamingsandals.bedwars.api.game.GameStatus;
import org.screamingsandals.bedwars.config.MainConfig;
import org.screamingsandals.bedwars.game.GameImpl;
import org.screamingsandals.bedwars.game.TeamImpl;
import org.screamingsandals.bedwars.listener.Player116ListenerUtils;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.player.PlayerWrapper;
import org.screamingsandals.lib.sidebar.ScoreSidebar;
import org.screamingsandals.lib.sidebar.Sidebar;
import org.screamingsandals.lib.sidebar.TeamedSidebar;
import org.screamingsandals.lib.tasker.Tasker;
import org.screamingsandals.lib.tasker.TaskerTime;
import org.screamingsandals.lib.tasker.task.TaskerTask;
import org.screamingsandals.lib.utils.AdventureHelper;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.*;
import java.util.stream.Collectors;

public class ScreamingScoreboard {

    private GameStatus status = GameStatus.WAITING;
    private final GameImpl game;
    private Sidebar sidebar = Sidebar.of();
    private ScoreSidebar scoreboard;
    private TeamedSidebar<?> teamedSidebar = sidebar;
    private final TaskerTask task;

    public ScreamingScoreboard(GameImpl game) {
        this.game = game;
        if (game.getConfigurationContainer().getOrDefault(ConfigurationContainer.LOBBY_SCOREBOARD, Boolean.class, false)) {
            this.sidebar
                    .title(AdventureHelper.toComponent(MainConfig.getInstance().node("lobby-scoreboard", "title").getString("§eBEDWARS")));
            MainConfig.getInstance().node("lobby-scoreboard", "content")
                    .childrenList()
                    .stream()
                    .map(ConfigurationNode::getString)
                    .filter(Objects::nonNull)
                    .map(message -> Message.ofPlainText(message)
                            .placeholder("arena", game.getDisplayNameComponent())
                            .placeholder("players", () -> Component.text(game.countConnectedPlayers()))
                            .placeholder("maxplayers", game.getMaxPlayers())
                            .placeholder("time", () -> Component.text(game.getFormattedTimeLeft()))
                    )
                    .forEach(sidebar::bottomLine);
            sidebar.show();
        }
        game.getConnectedPlayers().forEach(sidebar::addViewer);

        this.task = Tasker
                .build(this::update)
                .async()
                .repeat(20, TaskerTime.TICKS)
                .start();
    }

    private void switchToRunning() {
        sidebar.setLines(List.of());
        sidebar.title(
                Message.ofPlainText(MainConfig.getInstance().node("scoreboard", "title").getString(""))
                        .placeholder("game", game.getDisplayNameComponent())
                        .placeholder("time", () -> Component.text(game.getFormattedTimeLeft()))
        );

        final var msgs = new ArrayList<Message>();
        game.getActiveTeams().forEach(team ->
                msgs.add(Message.ofPlainText(() ->
                                List.of(formatScoreboardTeam(team,
                                        !team.isTargetBlockIntact(),
                                        team.isTargetBlockIntact()
                                                && team.getTargetBlock().getBlock().getType().isSameType("respawn_anchor")
                                                && Player116ListenerUtils.isAnchorEmpty(team.getTargetBlock().getBlock()))
                                )
                        )
                )
        );

        List<String> content = MainConfig.getInstance().node("experimental", "new-scoreboard-system", "content")
                .childrenList().stream().map(ConfigurationNode::getString).collect(Collectors.toList());

        content.forEach(line -> {
            if (line.trim().equalsIgnoreCase("%team_status%")) {
                msgs.forEach(sidebar::bottomLine);
                return;
            }
            sidebar.bottomLine(
                    Message.ofPlainText(line)
                            .placeholder("time", () -> Component.text(game.getFormattedTimeLeft()))
            );
        });
    }

    private void switchToRunningOld() {
        // Switch the sidebar
        var scoreboard = ScoreSidebar.of();
        var viewers = sidebar.viewers();
        // teams will be restored by update task
        sidebar.destroy();
        viewers.forEach(scoreboard::addViewer);
        sidebar = null;
        this.teamedSidebar = this.scoreboard = scoreboard;
        scoreboard.title(
                Message.ofPlainText(MainConfig.getInstance().node("scoreboard", "title").getString(""))
                        .placeholder("game", game.getDisplayNameComponent())
                        .placeholder("time", () -> Component.text(game.getFormattedTimeLeft()))
        );

        updateScoreboard();

        scoreboard.show();
    }

    private void update() {
        if (scoreboard != null) {
            updateScoreboard();
        }

        if (teamedSidebar != null) {
            teamedSidebar.update();
        }

        if (game.getStatus() == GameStatus.RUNNING && status != GameStatus.RUNNING) {
            if (game.getConfigurationContainer().getOrDefault(ConfigurationContainer.GAME_SCOREBOARD, Boolean.class, false)) {
                if (MainConfig.getInstance().node("experimental", "new-scoreboard-system", "enabled").getBoolean(false)) {
                    switchToRunning();
                } else {
                    switchToRunningOld();
                }
            } else {
                teamedSidebar.hide();
            }
            status = GameStatus.RUNNING;
        }

        game.getActiveTeams().forEach(team -> {
            if (teamedSidebar.getTeam(team.getName()).isEmpty()) {
                teamedSidebar.team(team.getName())
                        .color(NamedTextColor.nearestTo(team.getColor().getTextColor()))
                        .friendlyFire(game.getConfigurationContainer().getOrDefault(ConfigurationContainer.FRIENDLY_FIRE, Boolean.class, false));
            }
            var sidebarTeam = teamedSidebar.getTeam(team.getName()).orElseThrow();

            List.copyOf(sidebarTeam.players())
                    .forEach(teamPlayer -> {
                        if (team.getPlayers().stream().noneMatch(bedWarsPlayer -> bedWarsPlayer.equals(teamPlayer))) {
                            sidebarTeam.removePlayer(teamPlayer);
                        }
                    });

            team.getPlayers()
                    .stream()
                    .filter(player -> !sidebarTeam.players().contains(player))
                    .forEach(sidebarTeam::player);
        });
    }

    private String formatScoreboardTeam(TeamImpl team, boolean destroy, boolean empty) {
        if (team == null) {
            return "";
        }

        return MainConfig.getInstance().node("experimental", "new-scoreboard-system", "teamTitle").getString("")
                .replace("%team_size%", String.valueOf(team.countConnectedPlayers()))
                .replace("%color%", AdventureHelper.toLegacyColorCode(team.getColor().getTextColor()))
                .replace("%team%", team.getName())
                .replace("%bed%", destroy ? GameImpl.bedLostString() : (empty ? GameImpl.anchorEmptyString() : GameImpl.bedExistString()));
    }

    private Component formatScoreboardTeamOld(TeamImpl team, boolean destroy, boolean empty) {
        return AdventureHelper.toComponent(
                MainConfig.getInstance().node("scoreboard", "teamTitle").getString("%bed%%color%%team%")
                        .replace("%color%", AdventureHelper.toLegacyColorCode(team.getColor().getTextColor()))
                        .replace("%team%", team.getName())
                        .replace("%bed%", destroy ? GameImpl.bedLostString() : (empty ? GameImpl.anchorEmptyString() : GameImpl.bedExistString()))
        );
    }

    private void updateScoreboard() {
        game.getActiveTeams().forEach(team -> {
            scoreboard.entity(team.getName(), formatScoreboardTeamOld(team, !team.isTargetBlockIntact(), team.isTargetBlockIntact() && team.getTargetBlock().getBlock().getType().isSameType("respawn_anchor") && Player116ListenerUtils.isAnchorEmpty(team.getTargetBlock().getBlock())));
            scoreboard.score(team.getName(), team.countConnectedPlayers());
        });
    }

    public void destroy() {
        task.cancel();
        teamedSidebar.destroy();
    }

    public void addPlayer(PlayerWrapper player) {
        teamedSidebar.addViewer(player);
    }

    public void removePlayer(PlayerWrapper player) {
        teamedSidebar.removeViewer(player);
    }
}
