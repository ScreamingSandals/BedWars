package org.screamingsandals.bedwars.inventories;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.api.RunningTeam;
import org.screamingsandals.bedwars.api.events.BedwarsOpenTeamSelectionEvent;
import org.screamingsandals.bedwars.api.events.BedwarsPlayerJoinTeamEvent;
import org.screamingsandals.bedwars.api.events.BedwarsPlayerJoinedTeamEvent;
import org.screamingsandals.bedwars.api.events.BedwarsPlayerLeaveEvent;
import org.screamingsandals.bedwars.game.CurrentTeam;
import org.screamingsandals.bedwars.game.Game;
import org.screamingsandals.bedwars.game.Team;
import org.screamingsandals.lib.player.PlayerUtils;
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

import static org.screamingsandals.bedwars.lib.lang.I18n.i18nonly;

public class TeamSelectorInventory implements Listener {
    private final Game game;
    private final InventorySet inventorySet;
    private final Map<Team, GenericItemInfo> items = new HashMap<>();

    public TeamSelectorInventory(Main plugin, Game game) {
        this.game = game;

        inventorySet = SimpleInventoriesCore.builder()
                .categoryOptions(localOptions -> {
                    localOptions.prefix(i18nonly("team_selection_name", "Select team - %arena%").replace("%arena%", game.getName()))
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
                    var item = Main.getConfigurator().readDefinedItem("team-select", "WHITE_WOOL");

                    game.getTeams().forEach(team -> {
                        var playersInTeam = game.getPlayersInTeam(team);
                        var playersInTeamCount = playersInTeam.size();

                        // TODO: teach BW how to color Item wrappers
                        categoryBuilder.item(Main.applyColor(team.color, item.as(ItemStack.class), true), itemInfoBuilder -> {
                            try {
                                itemInfoBuilder.stack(itemBuilder ->
                                    itemBuilder.name(
                                            i18nonly("team_select_item")
                                                    .replace("%teamName%", team.color.chatColor + team.getName())
                                                    .replace("%inTeam%", String.valueOf(playersInTeamCount))
                                                    .replace("%maxInTeam%", String.valueOf(team.maxPlayers))
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
                    var player = event.getPlayer().as(Player.class);
                    event.getItem().getFirstPropertyByName("selector").ifPresent(property -> {
                        try {
                            var team = property.getPropertyData().get(Team.class);
                            game.selectTeam(Main.getPlayerGameProfile(player), team.getName());
                        } catch (SerializationException | NullPointerException e) {
                            e.printStackTrace();
                        }
                    });
                })
                .process()
                .getInventorySet();

        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void destroy() {
        HandlerList.unregisterAll(this);
        SimpleInventoriesCore.getAllInventoryRenderersForInventorySet(this.inventorySet).forEach(InventoryRenderer::close);
    }

    public void openForPlayer(Player player) {
        var event = new BedwarsOpenTeamSelectionEvent(this.game, player);
        Main.getInstance().getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return;
        }

        PlayerUtils.wrapPlayer(player).openInventory(inventorySet);
    }

    private List<String> formatLore(Team team, Game game) {
        var loreList = new ArrayList<String>();
        var playersInTeam = game.getPlayersInTeam(team);
        var playersInTeamCount = playersInTeam.size();

        if (playersInTeamCount >= team.maxPlayers) {
            loreList.add(team.color.chatColor + i18nonly("team_select_item_lore_full"));
        } else {
            loreList.add(team.color.chatColor + i18nonly("team_select_item_lore_join"));
        }

        if (!playersInTeam.isEmpty()) {
            loreList.add(i18nonly("team_select_item_lore"));
            playersInTeam.forEach(gamePlayer ->
                loreList.add(team.color.chatColor + gamePlayer.player.getDisplayName())
            );
        }

        return loreList;
    }

    @EventHandler
    public void onPlayerLeave(BedwarsPlayerLeaveEvent event) {
        if (event.getGame() != game) {
            return;
        }

        if (event.getTeam() != null) {
            repaintTeam(event.getTeam());
        }
    }

    @EventHandler
    public void onTeamSelected(BedwarsPlayerJoinedTeamEvent event) {
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

    private void repaintTeam(RunningTeam runningTeam) {
        var currentTeam = (CurrentTeam) runningTeam;
        var team = currentTeam.teamInfo;
        var playersInTeamCount = currentTeam.players.size();
        var itemInfo = items.get(team);
        var item = itemInfo.getItem();

        item.setDisplayName(
                i18nonly("team_select_item")
                        .replace("%teamName%", team.color.chatColor + team.getName())
                        .replace("%inTeam%", String.valueOf(playersInTeamCount))
                        .replace("%maxInTeam%", String.valueOf(team.maxPlayers))
        );

        item.setLore(formatLore(team, game));

        itemInfo.repaint();
    }
}
