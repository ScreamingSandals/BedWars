package misat11.bw.utils;

import misat11.bw.Main;
import misat11.bw.api.events.BedwarsOpenTeamSelectionEvent;
import misat11.bw.game.Game;
import misat11.bw.game.Team;
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

import java.util.Arrays;

import static misat11.lib.lang.I18n.i18n;
import static misat11.lib.lang.I18n.i18nonly;

public class TeamSelectorInventory implements Listener {

	private Game game;
	private SimpleGuiFormat format;

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
		
		format = new SimpleGuiFormat(options);
		
		FormatBuilder builder = new FormatBuilder();
		
		for (Team team : game.getTeams()) {
			ItemStack teamStack = team.color.getWool();
			ItemMeta im = teamStack.getItemMeta();
			im.setDisplayName(team.color.chatColor + team.name);
			im.setLore(Arrays.asList(i18n("click_to_join_team", false)));
			teamStack.setItemMeta(im);
			builder.add(teamStack).set("team", team);
		}
		
		format.load(builder);
		
		format.generateData();

		Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
	}

	public void destroy() {
		HandlerList.unregisterAll(this);
	}

	public void openForPlayer(Player player) {
		BedwarsOpenTeamSelectionEvent event = new BedwarsOpenTeamSelectionEvent(this.game, player);
		Main.getInstance().getServer().getPluginManager().callEvent(event);
		
		if (event.isCancelled()) {
			return;
		}
		
		format.openForPlayer(player);
	}
	
	@EventHandler
	public void onPostAction(PostActionEvent event) {
		if (event.getFormat() != format) {
			return;
		}
		
		MapReader reader = event.getItem().getReader();
		if (reader.containsKey("team")) {
			Team team = (Team) reader.get("team");
			System.out.println(team);
			game.selectTeam(Main.getPlayerGameProfile(event.getPlayer()), team.getName());
			event.getPlayer().closeInventory();
		}
	}

}
