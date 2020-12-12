package org.screamingsandals.bedwars.lib.scoreboard.scoreboard;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.api.RunningTeam;
import org.screamingsandals.bedwars.game.Game;
import org.screamingsandals.bedwars.game.TeamColor;
import org.screamingsandals.bedwars.lib.scoreboard.holder.ScoreboardHolder;
import org.screamingsandals.bedwars.lib.scoreboard.paint.LinePainter;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Data
public class ScreamingScoreboard {
    protected ScoreboardHolder scoreboardHolder;
    protected String identifier;
    @Getter(value = AccessLevel.PRIVATE)
    protected final List<Team> activeTeams;
    private Game game;


    private LinePainter linePainter;

    public ScreamingScoreboard(Player owner, String objectiveName, String displayName) {
        scoreboardHolder = new ScoreboardHolder(owner, displayName);
        activeTeams = new LinkedList<>();
        linePainter = new LinePainter(this, objectiveName);
    }

    public void addTeams(List<RunningTeam> gameTeams) {
        gameTeams.forEach(gameTeam -> addTeam(gameTeam.getName(), TeamColor.fromApiColor(
                gameTeam.getColor()).chatColor));
    }

    public void paintLines() {
        linePainter.paintLines();
    }

    public void paintLines(Map<String, String> available) {
        linePainter.paintLines(available);
    }

    public Scoreboard getBukkitScoreboard() {
        return getScoreboardHolder().getBukkitScoreboard();
    }

    public boolean isTeamExists(String name) {
        return getBukkitScoreboard().getTeam(name) != null;
    }

    public void addTeam(String name, ChatColor color) {
        if (!isTeamExists(name)) {
            final Team team = getBukkitScoreboard().registerNewTeam(name);
            if (!Main.isLegacy()) {
                team.setColor(color);
            } else {
                team.setPrefix(color.toString());
            }
            team.setDisplayName(name);
            team.setAllowFriendlyFire(false);
            if (!activeTeams.contains(team))
                activeTeams.add(team);
        }
    }

    public void addPlayerToTeam(Player player, String teamName) {
        if (isTeamExists(teamName)) {
            final Team team = getBukkitScoreboard().getTeam(teamName);
            activeTeams.forEach(t-> {
                if (t.hasEntry(player.getName())) {
                    t.removeEntry(player.getName());
                }
            });

            if (team != null) {
                team.addEntry(player.getName());
            }


        }
    }

    public void removePlayer(Player player) {
        activeTeams.forEach(team -> {
            if (team.hasEntry(player.getName()))
                team.removeEntry(player.getName());
        });
    }


    public void removeTeam(String name) {
        if (isTeamExists(name)) {
            final Team team = getBukkitScoreboard().getTeam(name);
            if (team != null) {
                activeTeams.remove(team);
                try {
                    team.unregister();
                } catch (Throwable ignroed) {}
            }
        }
    }

    public Optional<Team> getTeam(String name) {
        for (Team team : activeTeams) {
            if (team.getName().equals(name)) {
                return Optional.of(team);
            }
        }
        return Optional.empty();
    }

    public List<Team> getTeams() {
        return new LinkedList<>(activeTeams);
    }

    /**
     * Registers new objective if not exists, and unregisters old objective if exists
     *
     * @param objectiveName
     * @param displayName
     */
    public void registerNewObjective(String objectiveName, String displayName) { linePainter.setObjective(objectiveName, displayName);}


}
