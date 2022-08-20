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

package org.screamingsandals.bedwars.inventories;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.api.events.BedwarsOpenTeamSelectionEvent;
import org.screamingsandals.bedwars.api.events.BedwarsPlayerLeaveEvent;
import org.screamingsandals.bedwars.game.Game;
import org.screamingsandals.bedwars.game.GamePlayer;
import org.screamingsandals.bedwars.game.Team;
import org.screamingsandals.simpleinventories.SimpleInventories;
import org.screamingsandals.simpleinventories.builder.FormatBuilder;
import org.screamingsandals.simpleinventories.events.PostActionEvent;
import org.screamingsandals.simpleinventories.inventory.GuiHolder;
import org.screamingsandals.simpleinventories.inventory.Options;
import org.screamingsandals.simpleinventories.utils.MapReader;

import java.util.ArrayList;
import java.util.List;

import static org.screamingsandals.bedwars.lib.lang.I18n.i18nonly;

public class TeamSelectorInventory implements Listener {
    private Player player;
    private Game game;
    private SimpleInventories simpleGuiFormat;
    private Options options;


    public TeamSelectorInventory(Main plugin, Game game, Player player) {
        this.game = game;
        this.player = player;

        options = new Options(Main.getInstance());
        options.setPrefix(i18nonly("team_selection_name", "Select team - %arena%").replace("%arena%", game.getName()));
        options.setShowPageNumber(false);
        options.setRender_header_start(54); // Disable header
        options.setRender_offset(0);
        int teamCount = game.getTeams().size();
        if (teamCount <= 9) {
            options.setRender_actual_rows(1);
        } else if (teamCount <= 18) {
            options.setRender_actual_rows(2);
        }

        createData();

        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void destroy() {
        HandlerList.unregisterAll(this);
    }

    public void openForPlayer() {
        BedwarsOpenTeamSelectionEvent event = new BedwarsOpenTeamSelectionEvent(this.game, player);
        Main.getInstance().getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return;
        }

        createData();
        simpleGuiFormat.openForPlayer(player);
    }

    private void createData() {
    	SimpleInventories simpleGuiFormat = new SimpleInventories(options);
        FormatBuilder builder = new FormatBuilder();
        
        ItemStack stack = Main.getConfigurator().readDefinedItem("team-select", Main.isLegacy() ? "WOOL" : "WHITE_WOOL");
        
        for (Team team : game.getTeams()) {
            ItemStack teamStack = Main.applyColor(team.color, stack, true);
            ItemMeta teamMeta = teamStack.getItemMeta();

            List<GamePlayer> playersInTeam = game.getPlayersInTeam(team);
            int playersInTeamCount = playersInTeam.size();

            teamMeta.setDisplayName(i18nonly("team_select_item")
                    .replace("%teamName%", team.color.chatColor + team.getName())
                    .replace("%inTeam%", String.valueOf(playersInTeamCount))
                    .replace("%maxInTeam%", String.valueOf(team.maxPlayers)));
            teamMeta.setLore(formatLore(team, game));
            teamStack.setItemMeta(teamMeta);

            builder.add(teamStack).set("team", team);
        }

        simpleGuiFormat.load(builder);
        simpleGuiFormat.generateData();

        this.simpleGuiFormat = simpleGuiFormat;
    }

    private List<String> formatLore(Team team, Game game) {
        List<String> loreList = new ArrayList<>();
        List<GamePlayer> playersInTeam = game.getPlayersInTeam(team);
        int playersInTeamCount = playersInTeam.size();

        if (playersInTeamCount >= team.maxPlayers) {
            loreList.add(team.color.chatColor + i18nonly("team_select_item_lore_full"));
        } else {
            loreList.add(team.color.chatColor + i18nonly("team_select_item_lore_join"));
        }

        if (!playersInTeam.isEmpty()) {
            loreList.add(i18nonly("team_select_item_lore"));

            for (GamePlayer gamePlayer : playersInTeam) {
                loreList.add(team.color.chatColor + gamePlayer.player.getDisplayName());
            }
        }

        return loreList;
    }

    private void repaint() {
        GuiHolder guiHolder = simpleGuiFormat.getCurrentGuiHolder(player);
        if (guiHolder == null) {
            return;
        }

        createData();
        guiHolder.setFormat(simpleGuiFormat);
        guiHolder.repaint();
    }

    @EventHandler
    public void onPostAction(PostActionEvent event) {
        if (event.getFormat() != simpleGuiFormat || !event.getPlayer().equals(player)) {
            return;
        }

        MapReader reader = event.getItem().getReader();
        if (reader.containsKey("team")) {
            Team team = (Team) reader.get("team");
            game.selectTeam(Main.getPlayerGameProfile(player), team.getName());
            player.closeInventory();

            repaint();
        }
    }

    @EventHandler
    public void onPlayerLeave(BedwarsPlayerLeaveEvent event) {
        if (event.getGame() != game) {
            return;
        }
        repaint();
    }
}
