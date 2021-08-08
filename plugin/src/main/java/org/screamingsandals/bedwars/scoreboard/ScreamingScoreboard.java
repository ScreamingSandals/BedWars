package org.screamingsandals.bedwars.scoreboard;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.screamingsandals.bedwars.api.RunningTeam;
import org.screamingsandals.bedwars.api.config.ConfigurationContainer;
import org.screamingsandals.bedwars.api.game.GameStatus;
import org.screamingsandals.bedwars.config.MainConfig;
import org.screamingsandals.bedwars.game.GameImpl;
import org.screamingsandals.bedwars.game.TeamColor;
import org.screamingsandals.bedwars.listener.Player116ListenerUtils;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.player.PlayerMapper;
import org.screamingsandals.lib.player.PlayerWrapper;
import org.screamingsandals.lib.sidebar.Sidebar;
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
    private final Sidebar sidebar = Sidebar.of();
    private final TaskerTask task;

    public ScreamingScoreboard(GameImpl game) {
        this.game = game;
        this.sidebar
                .title(AdventureHelper.toComponent(MainConfig.getInstance().node("lobby-scoreboard", "title").getString("§eBEDWARS")));
        MainConfig.getInstance().node("lobby-scoreboard", "content")
                .childrenList()
                .stream()
                .map(ConfigurationNode::getString)
                .filter(Objects::nonNull)
                .map(message -> Message.ofPlainText(message)
                        .placeholder("arena", game.getName())
                        .placeholder("players", () -> Component.text(game.countConnectedPlayers()))
                        .placeholder("maxplayers", game.getMaxPlayers())
                        .placeholder("time", () -> Component.text(game.getFormattedTimeLeft()))
                )
                .forEach(sidebar::bottomLine);
        sidebar.show();
        game.getConnectedPlayers().forEach(player -> sidebar.addViewer(PlayerMapper.wrapPlayer(player)));

        this.task = Tasker
                .build(this::update)
                .async()
                .repeat(20, TaskerTime.TICKS)
                .start();
    }

    private void switchToRunning() {
        sidebar.title(
                Message.ofPlainText(MainConfig.getInstance().node("scoreboard", "title").getString(""))
                        .placeholder("game", game.getName())
                        .placeholder("time", () -> Component.text(game.getFormattedTimeLeft()))
        );

        final var msgs = new ArrayList<Message>();
        game.getRunningTeams().forEach(team ->
                msgs.add(Message.ofPlainText(() ->
                        List.of(formatScoreboardTeam(team,
                                !team.isTargetBlockExists(),
                                team.isTargetBlockExists()
                                        && "RESPAWN_ANCHOR".equals(team.getTargetBlock().getBlock().getType().name())
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

    private void update() {
        sidebar.update();

        if (game.getStatus() == GameStatus.RUNNING && status != GameStatus.RUNNING) {
            sidebar.setLines(List.of());

            switchToRunning();
            status = GameStatus.RUNNING;
        }

        game.getRunningTeams().forEach(team -> {
            if (sidebar.getTeam(team.getName()).isEmpty()) {
                sidebar.team(team.getName())
                        .color(NamedTextColor.NAMES.value(TeamColor.fromApiColor(team.getColor()).chatColor.name().toLowerCase()))
                        .friendlyFire(game.getConfigurationContainer().getOrDefault(ConfigurationContainer.FRIENDLY_FIRE, Boolean.class, false));
            }
            var sidebarTeam = sidebar.getTeam(team.getName()).orElseThrow();

            List.copyOf(sidebarTeam.players())
                    .forEach(teamPlayer -> {
                        if (!team.getConnectedPlayers().contains(teamPlayer.as(Player.class))) {
                            sidebarTeam.removePlayer(teamPlayer);
                        }
                    });

            team.getConnectedPlayers()
                    .stream()
                    .map(PlayerMapper::wrapPlayer)
                    .filter(player -> !sidebarTeam.players().contains(player))
                    .forEach(sidebarTeam::player);
        });
    }

    private String formatScoreboardTeam(RunningTeam team, boolean destroy, boolean empty) {
        if (team == null) {
            return "";
        }

        return MainConfig.getInstance().node("experimental", "new-scoreboard-system", "teamTitle").getString("")
                .replace("%team_size%", String.valueOf(
                        team.getConnectedPlayers().size()))
                .replace("%color%", TeamColor.fromApiColor(team.getColor())
                        .chatColor.toString()).replace("%team%", team.getName())
                .replace("%bed%", destroy ? GameImpl.bedLostString() : (empty ? GameImpl.anchorEmptyString() : GameImpl.bedExistString()));
    }

    public void destroy() {
        task.cancel();
        sidebar.destroy();
    }

    public void addPlayer(PlayerWrapper player) {
        sidebar.addViewer(player);
    }

    public void removePlayer(PlayerWrapper player) {
        sidebar.removeViewer(player);
    }
}
