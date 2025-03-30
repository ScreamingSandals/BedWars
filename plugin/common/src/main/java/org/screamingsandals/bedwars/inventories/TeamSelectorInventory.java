/*
 * Copyright (C) 2025 ScreamingSandals
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

import org.screamingsandals.bedwars.BedWarsPlugin;
import org.screamingsandals.bedwars.config.MainConfig;
import org.screamingsandals.bedwars.events.OpenTeamSelectionEventImpl;
import org.screamingsandals.bedwars.events.PlayerJoinedTeamEventImpl;
import org.screamingsandals.bedwars.events.PlayerLeaveEventImpl;
import org.screamingsandals.bedwars.game.GameImpl;
import org.screamingsandals.bedwars.game.TeamImpl;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.bedwars.player.BedWarsPlayer;
import org.screamingsandals.bedwars.player.PlayerManagerImpl;
import org.screamingsandals.lib.event.EventHandler;
import org.screamingsandals.lib.event.EventManager;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.spectator.Component;
import org.screamingsandals.simpleinventories.SimpleInventoriesCore;
import org.screamingsandals.simpleinventories.inventory.GenericItemInfo;
import org.screamingsandals.simpleinventories.inventory.InventorySet;
import org.screamingsandals.simpleinventories.render.InventoryRenderer;
import org.spongepowered.configurate.BasicConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TeamSelectorInventory {
    private final GameImpl game;
    private final InventorySet inventorySet;
    private final Map<TeamImpl, GenericItemInfo> items = new HashMap<>();
    private final List<EventHandler<?>> handlers = new ArrayList<>();

    public TeamSelectorInventory(GameImpl game) {
        this.game = game;

        inventorySet = SimpleInventoriesCore.builder()
                .categoryOptions(localOptions -> {
                    localOptions.prefix(Message.of(LangKeys.IN_GAME_TEAM_SELECTION_INVENTORY_NAME).placeholder("arena", game.getDisplayNameComponent()).asComponent())
                            .showPageNumber(false)
                            .renderHeaderStart(54)
                            .renderOffset(0);

                    var teamCount = game.getTeams().size();
                    if (teamCount <= 9) {
                        localOptions.renderActualRows(1);
                    } else if (teamCount <= 18) {
                        localOptions.renderActualRows(2);
                    }
                })
                .call(categoryBuilder -> {
                    var item = MainConfig.getInstance().readDefinedItem("team-select", "WHITE_WOOL");

                    game.getTeams().forEach(team -> {
                        var playersInTeam = game.getPlayersInTeam(team);
                        var playersInTeamCount = playersInTeam.size();

                        categoryBuilder.item(BedWarsPlugin.getInstance().getColorChanger().applyColor(team.getColor(), item), itemInfoBuilder -> {
                            try {
                                itemInfoBuilder.stack(itemBuilder ->
                                        itemBuilder.name(Message.of(LangKeys.IN_GAME_TEAM_SELECTION_SELECT_ITEM)
                                                .placeholder("team", Component.text(team.getName(), team.getColor().getTextColor()))
                                                .placeholder("inteam", playersInTeamCount)
                                                .placeholder("maxinteam", team.getMaxPlayers())
                                                .asComponent()
                                        ).lore(formatLore(team, game))
                                ).property("selector", BasicConfigurationNode.root().set(team));
                            } catch (SerializationException e) {
                                e.printStackTrace();
                            }

                            items.put(team, itemInfoBuilder.getItemInfo());
                        });
                    });
                })
                .click(event -> {
                    event.getItem().getFirstPropertyByName("selector").ifPresent(property -> {
                        try {
                            var team = property.getPropertyData().get(TeamImpl.class);
                            game.selectTeam(PlayerManagerImpl.getInstance().getPlayerOrCreate(event.getPlayer()), team.getName());
                        } catch (SerializationException | NullPointerException e) {
                            e.printStackTrace();
                        }
                    });
                })
                .process()
                .getInventorySet();

        handlers.add(EventManager.getDefaultEventManager().register(PlayerLeaveEventImpl.class, this::onPlayerLeave));
        handlers.add(EventManager.getDefaultEventManager().register(PlayerJoinedTeamEventImpl.class, this::onTeamSelected));
    }

    public void destroy() {
        handlers.forEach(EventManager.getDefaultEventManager()::unregister);
        SimpleInventoriesCore.getAllInventoryRenderersForInventorySet(this.inventorySet).forEach(InventoryRenderer::close);
    }

    public void openForPlayer(BedWarsPlayer player) {
        var event = new OpenTeamSelectionEventImpl(this.game, player);
        EventManager.fire(event);

        if (event.isCancelled()) {
            return;
        }

        player.openInventory(inventorySet);
    }

    private List<Component> formatLore(TeamImpl team, GameImpl game) {
        var loreList = new ArrayList<Component>();
        var playersInTeam = game.getPlayersInTeam(team);
        var playersInTeamCount = playersInTeam.size();

        if (playersInTeamCount >= team.getMaxPlayers()) {
            loreList.add(Message.of(LangKeys.IN_GAME_TEAM_SELECTION_SELECT_ITEM_LORE_FULL).asComponent().withColor(team.getColor().getTextColor()));
        } else {
            loreList.add(Message.of(LangKeys.IN_GAME_TEAM_SELECTION_SELECT_ITEM_LORE_JOIN).asComponent().withColor(team.getColor().getTextColor()));
        }

        if (!playersInTeam.isEmpty()) {
            loreList.add(Message.of(LangKeys.IN_GAME_TEAM_SELECTION_SELECT_ITEM_LORE).asComponent());
            playersInTeam.forEach(gamePlayer ->
                    loreList.add(gamePlayer.getDisplayName().withColor(team.getColor().getTextColor()))
            );
        }

        return loreList;
    }

    public void onPlayerLeave(PlayerLeaveEventImpl event) {
        if (event.getGame() != game) {
            return;
        }

        if (event.getTeam() != null) {
            repaintTeam(event.getTeam());
        }
    }

    public void onTeamSelected(PlayerJoinedTeamEventImpl event) {
        if (event.getGame() != game) {
            return;
        }

        if (event.getPreviousTeam() != null) {
            repaintTeam(event.getPreviousTeam());
        }

        if (event.getTeam() != null) {
            repaintTeam(event.getTeam());
        }

    }

    private void repaintTeam(TeamImpl team) {
        var playersInTeamCount = team.countConnectedPlayers();
        var itemInfo = items.get(team);
        itemInfo.setItem(itemInfo.getItem()
                .withDisplayName(
                        Message.of(LangKeys.IN_GAME_TEAM_SELECTION_SELECT_ITEM)
                                .placeholder("team", Component.text(team.getName()).withColor(team.getColor().getTextColor()))
                                .placeholder("inteam", playersInTeamCount)
                                .placeholder("maxinteam", team.getMaxPlayers())
                                .asComponent()
                )
                .withItemLore(formatLore(team, game))
        );

        itemInfo.repaint();
    }
}
