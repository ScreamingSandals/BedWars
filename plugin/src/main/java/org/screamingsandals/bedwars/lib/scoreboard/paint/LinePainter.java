package org.screamingsandals.bedwars.lib.scoreboard.paint;

import lombok.Data;
import lombok.NonNull;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.screamingsandals.bedwars.lib.scoreboard.holder.ScoreboardHolder;
import org.screamingsandals.bedwars.lib.scoreboard.scoreboard.ScreamingScoreboard;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

@Data
public class LinePainter {
    private final ScreamingScoreboard scoreboard;
    private final Map<Integer, String> paintedLines = new HashMap<>();
    private @NonNull String objectiveName;


    public void paintLines() {
        paintLines(Collections.emptyMap());
    }

    public void paintLines(Map<String, String> available) {
        final ScoreboardHolder holder = scoreboard.getScoreboardHolder();
        final TreeMap<Integer, String> lines = holder.getLines(available);
        final Scoreboard bukkitScoreboard = holder.getBukkitScoreboard();

        bukkitScoreboard.getEntries().forEach(bukkitScoreboard::resetScores);

        for (Map.Entry<Integer, String> entry : lines.entrySet()) {
            String value = entry.getValue();
            final int valueCharCount = value.toCharArray().length;

            if (valueCharCount > 40) {
                value = "error!";
                System.out.println("Invalid value, longer than 40 characters!");
                System.out.println(entry.getValue());
            }

            paintLine(entry.getKey(), value);
        }
    }

    public void paintLine(int line, String content) {
        final Objective objective = getObjective();

        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        final Score score = objective.getScore(content);
        score.setScore(line);

        paintedLines.put(line, content);
    }

    private boolean isObjectiveExists(String objectiveName) {
        return scoreboard.getScoreboardHolder().getBukkitScoreboard().getObjective(objectiveName) != null;
    }

    private Objective getObjective() {
        final ScoreboardHolder holder = scoreboard.getScoreboardHolder();
        final Scoreboard bukkitScoreboard = holder.getBukkitScoreboard();
        final String displayedName = holder.getDisplayedName();
        Objective objective = bukkitScoreboard.getObjective(objectiveName);

        if (objective == null) {
            objective = bukkitScoreboard.registerNewObjective(objectiveName, "dummy");
            objective.setDisplayName(displayedName);
        }
        return objective;
    }

    public void setDisplayName(String displayName) {
        Objective obj = getObjective();
        obj.setDisplayName(displayName);
    }


    public void setObjective(String objectiveName, String displayName) {
        if (this.objectiveName.equals(objectiveName)) {
            return;
        }

        final ScoreboardHolder holder = scoreboard.getScoreboardHolder();

        if (this.objectiveName != null) {
            final Scoreboard bukkitScoreboard = holder.getBukkitScoreboard();
            final Objective previousObjective = bukkitScoreboard.getObjective(this.objectiveName);

            //Make sure previous objectives are completely unregistered
            if (previousObjective != null) {
                try {
                    previousObjective.unregister();
                } catch (Throwable ignored) {}
            }
        }

        this.objectiveName = objectiveName;
        holder.setDisplayedName(displayName);
        setDisplayName(displayName);
    }
}
