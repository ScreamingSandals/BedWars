package org.screamingsandals.bedwars.lib.scoreboard.holder;

import lombok.Data;
import lombok.var;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import sun.reflect.generics.tree.Tree;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Data
public class ScoreboardHolder {
    private transient Player owner;
    private transient org.bukkit.scoreboard.Scoreboard bukkitScoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
    private String displayedName;
    private DisplaySlot displaySlot;
    private TreeMap<Integer, String> originalLines = new TreeMap<>();
    private boolean papi;

    public ScoreboardHolder(Player player, String displayName) {
        this.owner = player;
        this.displayedName = displayName;
        papi = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;
    }

    public TreeMap<Integer, String> getOriginalLines() {
        return new TreeMap<>(originalLines);
    }

    /**
     * Gets lines map with replaced placeholders
     *
     * @param availablePlaceholders map with available placeholders to replace
     * @return map with replaced lines
     */
    public TreeMap<Integer, String> getLines(Map<String, String> availablePlaceholders) {
        final TreeMap<Integer, String> toReturn = new TreeMap<>();
        for (Map.Entry<Integer, String> entry : originalLines.entrySet()) {
            toReturn.put(entry.getKey(), replace(entry.getValue(), availablePlaceholders));
        }
        return toReturn;
    }

    /**
     * Sorts lines from String list in the order
     *
     * @param fromConfig list of lines from config
     * @return sorted tree map
     */
    public static TreeMap<Integer, String> sortLines(List<String> fromConfig) {
        Collections.reverse(fromConfig);
        final TreeMap<Integer, String> toReturn = new TreeMap<Integer, String>();

        for (int i = 0; i < fromConfig.size(); i++) {
            String content = fromConfig.get(i);

            if (content.isEmpty()) {
                content = convertToInvisibleString(String.valueOf(i));
            }

            toReturn.put(i, content);
        }

        return toReturn;
    }

    private String replace(String input, Map<String, String> available) {
        String toReturn = input;

        for (Map.Entry<String, String> entry : available.entrySet()) {
            final String entryValue = entry.getValue();
            final String valueToPrint = entryValue != null ? entry.getValue() : "nothing";
            toReturn = toReturn.replaceAll(entry.getKey(), valueToPrint);
        }

        if (papi && owner != null) {
            toReturn = PlaceholderAPI.setPlaceholders(owner, toReturn);
        }

        return toReturn;
    }

    private static String convertToInvisibleString(String input) {
        final StringBuilder hidden = new StringBuilder();
        for (char character : input.toCharArray()) {
            hidden.append(ChatColor.COLOR_CHAR + "").append(character);
        }
        return hidden.toString();
    }
}