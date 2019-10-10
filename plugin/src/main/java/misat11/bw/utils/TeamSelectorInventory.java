package misat11.bw.utils;

import misat11.bw.Main;
import misat11.bw.api.events.BedwarsOpenTeamSelectionEvent;
import misat11.bw.api.events.BedwarsPlayerJoinTeamEvent;
import misat11.bw.game.Game;
import misat11.bw.game.GamePlayer;
import misat11.bw.game.Team;
import misat11.lib.sgui.GuiHolder;
import misat11.lib.sgui.MapReader;
import misat11.lib.sgui.Options;
import misat11.lib.sgui.SimpleGuiFormat;
import misat11.lib.sgui.builder.FormatBuilder;
import misat11.lib.sgui.events.PostActionEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

import static misat11.lib.lang.I18n.i18n;
import static misat11.lib.lang.I18n.i18nonly;

public class TeamSelectorInventory implements Listener {
    private Game game;
    private SimpleGuiFormat simpleGuiFormat;
    private List<Player> openedForPlayers = new ArrayList<>();

    public TeamSelectorInventory(Main plugin, Game game) {
        this.game = game;

        Options options = new Options();
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

        simpleGuiFormat = new SimpleGuiFormat(options);

        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void destroy() {
    	openedForPlayers.clear();
        HandlerList.unregisterAll(this);
    }

    public void openForPlayer(Player player) {
        BedwarsOpenTeamSelectionEvent event = new BedwarsOpenTeamSelectionEvent(this.game, player);
        Main.getInstance().getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return;
        }

        simpleGuiFormat.openForPlayer(player);
		openedForPlayers.add(player);
    }

    private SimpleGuiFormat createData() {
		FormatBuilder builder = new FormatBuilder();
		for (Team team : game.getTeams()) {
			ItemStack teamStack = team.color.getWool();
			ItemMeta teamMeta = teamStack.getItemMeta();
			int playersInTeam = game.getPlayersCountInTeam(team);

			teamMeta.setDisplayName(i18nonly("team_select_item")
					.replace("%teamName%", team.color.chatColor + team.getName())
					.replace("%inTeam%", String.valueOf(playersInTeam))
					.replace("maxInTeam", String.valueOf(team.maxPlayers)));
			teamMeta.setLore(formatLore(team, game));
			teamStack.setItemMeta(teamMeta);

			builder.add(teamStack).set("team", team);
		}

		simpleGuiFormat.load(builder);
		simpleGuiFormat.generateData();

		return simpleGuiFormat;
	}

	private List<String> formatLore(Team team, Game game) {
		List<String> loreList = new ArrayList<>();
		int playersInTeam = game.getPlayersCountInTeam(team);

		if (playersInTeam >= team.maxPlayers) {
			loreList.add(team.color.chatColor + i18nonly("team_select_item_lore_full"));
		} else {
			loreList.add(team.color.chatColor + i18nonly("click_to_join_team"));
		}

		loreList.add(i18nonly("team_select_item_lore"));
		for (GamePlayer gamePlayer : game.getPlayersInTeam(team)) {
			loreList.add(team.color.chatColor + gamePlayer.player.getDisplayName());
		}

		return loreList;
	}

	private void repaint() {
		for (Player player : openedForPlayers) {
			GuiHolder guiHolder = simpleGuiFormat.getCurrentGuiHolder(player);
			if (guiHolder == null) {
				return;
			}

			guiHolder.setFormat(createData());
			guiHolder.repaint();
		}
	}

    @EventHandler
    public void onPostAction(PostActionEvent event) {
        if (event.getFormat() != simpleGuiFormat) {
            return;
        }

        Player player = event.getPlayer();
        MapReader reader = event.getItem().getReader();
        if (reader.containsKey("team")) {
            Team team = (Team) reader.get("team");
            game.selectTeam(Main.getPlayerGameProfile(player), team.getName());
            player.closeInventory();

            openedForPlayers.remove(player);
            repaint();
        }
    }
}
