package org.screamingsandals.bedwars.lobby;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.screamingsandals.bedwars.VersionInfo;
import org.screamingsandals.bedwars.config.MainConfig;
import org.screamingsandals.bedwars.statistics.PlayerStatisticManager;
import org.screamingsandals.bedwars.utils.MiscUtils;
import org.screamingsandals.lib.Server;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.player.PlayerWrapper;
import org.screamingsandals.lib.spectator.Color;
import org.screamingsandals.lib.spectator.Component;

@UtilityClass
public class LobbyUtils {
    @Contract("_ -> param1")
    @NotNull
    public Message setupPlaceholders(@NotNull Message message) {
        message
                .placeholder("date", MiscUtils.getFormattedDate(MainConfig.getInstance().node("main-lobby", "date-format").getString("MM/dd/yy")))
                .placeholder("players", () -> Component.text(Server.getConnectedPlayers().size()))
                .placeholder("name", sender -> sender instanceof PlayerWrapper ? ((PlayerWrapper) sender).getDisplayName() : Component.text(sender.getName()))
                .placeholder("version", VersionInfo.VERSION);

        if (PlayerStatisticManager.isEnabled()) {
            var playerStatisticManager = PlayerStatisticManager.getInstance();

            message.placeholder("goal", sender -> {
                if (sender instanceof PlayerWrapper) {
                    return Component.text(MiscUtils.roundForMainLobbySidebar(playerStatisticManager.getStatistic((PlayerWrapper) sender).getNeededScoreToNextLevel()));
                }
                return Component.text(0); // how
            }).placeholder("current-progress", sender -> {
                if (sender instanceof PlayerWrapper) {
                    return Component.text(MiscUtils.roundForMainLobbySidebar(playerStatisticManager.getStatistic((PlayerWrapper) sender).getScoreSincePreviousLevel()));
                }
                return Component.text(0); // how
            }).placeholder("level", sender -> {
                if (sender instanceof PlayerWrapper) {
                    return Component.text(playerStatisticManager.getStatistic((PlayerWrapper) sender).getLevel() + "\u272B", Color.GRAY); // TODO: use prestiges when they are implemented
                }
                return Component.text(0); // how
            }).placeholder("level-number", sender -> {
                if (sender instanceof PlayerWrapper) {
                    return Component.text(playerStatisticManager.getStatistic((PlayerWrapper) sender).getLevel());
                }
                return Component.text(0); // how
            }).placeholder("progress-bar", sender -> {
                if (sender instanceof PlayerWrapper) {
                    var statistic = playerStatisticManager.getStatistic((PlayerWrapper) sender);
                    var progress = statistic.getScoreSincePreviousLevel();
                    var needed = statistic.getNeededScoreToNextLevel();
                    if (needed <= 0) { // invalid
                        return Component.text().content("[").append(Component.text("\u25A0".repeat(10) + "]", Color.GRAY)).color(Color.DARK_GRAY).append("]").build();
                    }
                    int numberOfBoxesFilled = (int) (((double) progress / needed) * 10);
                    return Component.text()
                            .content("[")
                            .color(Color.DARK_GRAY)
                            .append(Component.text("\u25A0".repeat(numberOfBoxesFilled), Color.AQUA))
                            .append(Component.text("\u25A0".repeat(10 - numberOfBoxesFilled), Color.GRAY))
                            .append("]")
                            .build();
                }
                return Component.text(0); // how
            }).placeholder("kills", sender -> {
                if (sender instanceof PlayerWrapper) {
                    return Component.text(playerStatisticManager.getStatistic((PlayerWrapper) sender).getKills());
                }
                return Component.text(0); // how
            }).placeholder("deaths", sender -> {
                if (sender instanceof PlayerWrapper) {
                    return Component.text(playerStatisticManager.getStatistic((PlayerWrapper) sender).getDeaths());
                }
                return Component.text(0); // how
            }).placeholder("target-blocks-destroyed", sender -> {
                if (sender instanceof PlayerWrapper) {
                    return Component.text(playerStatisticManager.getStatistic((PlayerWrapper) sender).getDestroyedBeds());
                }
                return Component.text(0); // how
            }).placeholder("loses", sender -> {
                if (sender instanceof PlayerWrapper) {
                    return Component.text(playerStatisticManager.getStatistic((PlayerWrapper) sender).getLoses());
                }
                return Component.text(0); // how
            }).placeholder("wins", sender -> {
                if (sender instanceof PlayerWrapper) {
                    return Component.text(playerStatisticManager.getStatistic((PlayerWrapper) sender).getWins());
                }
                return Component.text(0); // how
            }).placeholder("score", sender -> {
                if (sender instanceof PlayerWrapper) {
                    return Component.text(playerStatisticManager.getStatistic((PlayerWrapper) sender).getScore());
                }
                return Component.text(0); // how
            }).placeholder("kd-ratio", sender -> {
                if (sender instanceof PlayerWrapper) {
                    return Component.text(playerStatisticManager.getStatistic((PlayerWrapper) sender).getKD());
                }
                return Component.text(0); // how
            }).placeholder("played-games", sender -> {
                if (sender instanceof PlayerWrapper) {
                    return Component.text(playerStatisticManager.getStatistic((PlayerWrapper) sender).getGames());
                }
                return Component.text(0); // how
            });
        }

        return message;
    }
}
