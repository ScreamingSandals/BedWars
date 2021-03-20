package org.screamingsandals.bedwars.inventories;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.api.RunningTeam;
import org.screamingsandals.bedwars.api.events.BedwarsOpenTeamSelectionEvent;
import org.screamingsandals.bedwars.api.events.BedwarsPlayerJoinedTeamEvent;
import org.screamingsandals.bedwars.api.events.BedwarsPlayerLeaveEvent;
import org.screamingsandals.bedwars.config.MainConfig;
import org.screamingsandals.bedwars.game.CurrentTeam;
import org.screamingsandals.bedwars.game.Game;
import org.screamingsandals.bedwars.game.Team;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.player.PlayerMapper;
import org.screamingsandals.lib.utils.AdventureHelper;
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

public class TeamSelectorInventory implements Listener {
    private final Game game;
    private final InventorySet inventorySet;
    private final Map<Team, GenericItemInfo> items = new HashMap<>();

    public TeamSelectorInventory(Game game) {
        this.game = game;

        inventorySet = SimpleInventoriesCore.builder()
                .categoryOptions(localOptions -> {
                    localOptions.prefix(Message.of(LangKeys.IN_GAME_TEAM_SELECTION_INVENTORY_NAME).placeholder("arena", game.getName()).asComponent())
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

                        // TODO: teach BW how to color Item wrappers
                        categoryBuilder.item(Main.applyColor(team.color, item.as(ItemStack.class), true), itemInfoBuilder -> {
                            try {
                                itemInfoBuilder.stack(itemBuilder ->
                                        itemBuilder.name(Message.of(LangKeys.IN_GAME_TEAM_SELECTION_SELECT_ITEM)
                                                .placeholder("teamName", AdventureHelper.toComponent(team.color.chatColor + team.getName()))
                                                .placeholder("inTeam", playersInTeamCount)
                                                .placeholder("maxInTeam", team.maxPlayers)
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

        Main.getInstance().registerBedwarsListener(this);
    }

    public void destroy() {
        HandlerList.unregisterAll(this);
        SimpleInventoriesCore.getAllInventoryRenderersForInventorySet(this.inventorySet).forEach(InventoryRenderer::close);
    }

    public void openForPlayer(Player player) {
        var event = new BedwarsOpenTeamSelectionEvent(this.game, player);
        Bukkit.getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return;
        }

        PlayerMapper.wrapPlayer(player).openInventory(inventorySet);
    }

    private List<Component> formatLore(Team team, Game game) {
        var loreList = new ArrayList<Component>();
        var playersInTeam = game.getPlayersInTeam(team);
        var playersInTeamCount = playersInTeam.size();

        if (playersInTeamCount >= team.maxPlayers) {
            loreList.add(Message.of(LangKeys.IN_GAME_TEAM_SELECTION_SELECT_ITEM_LORE_FULL).asComponent().color(NamedTextColor.NAMES.value(team.color.chatColor.name())));
        } else {
            loreList.add(Message.of(LangKeys.IN_GAME_TEAM_SELECTION_SELECT_ITEM_LORE_JOIN).asComponent().color(NamedTextColor.NAMES.value(team.color.chatColor.name())));
        }

        if (!playersInTeam.isEmpty()) {
            loreList.add(Message.of(LangKeys.IN_GAME_TEAM_SELECTION_SELECT_ITEM_LORE).asComponent());
            playersInTeam.forEach(gamePlayer ->
                    loreList.add(AdventureHelper.toComponent(team.color.chatColor + gamePlayer.player.getDisplayName()))
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
                Message.of(LangKeys.IN_GAME_TEAM_SELECTION_SELECT_ITEM)
                        .placeholder("teamName", AdventureHelper.toComponent(team.color.chatColor + team.getName()))
                        .placeholder("inTeam", playersInTeamCount)
                        .placeholder("maxInTeam", team.maxPlayers)
                        .asComponent()
        );

        item.getLore().clear();
        item.getLore().addAll(formatLore(team, game));

        itemInfo.repaint();
    }
}
