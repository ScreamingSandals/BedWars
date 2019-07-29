package misat11.bw.special;

import misat11.bw.Main;
import misat11.bw.api.Game;
import misat11.bw.api.Team;
import misat11.bw.game.TeamColor;
import misat11.bw.utils.ColorChanger;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

import static misat11.lib.lang.I18n.i18n;

public class RescuePlatform extends SpecialItem implements misat11.bw.api.special.RescuePlatform {
	private Game game;
	private Player player;
	private Team team;
	private List<Block> platformBlocks;

	private Material buildingMaterial;
	private ItemStack item;

	private int useDelay;
	private int breakingTime;
	private int platformLivingTime;

	public RescuePlatform(Game game, Player player, Team team, ItemStack item, int delay) {
		super(game, player, team);
		this.game = game;
		this.player = player;
		this.team = team;
		this.useDelay = delay;
		this.item = item;
	}

	@Override
	public int getBreakingTime() {
		return breakingTime;
	}

	@Override
	public boolean canBreak() {
		return Main.getConfigurator().config.getBoolean("specials.rescue-platform.is-breakable", true);
	}

	@Override
	public Material getMaterial() {
		return buildingMaterial;
	}

	@Override
	public ItemStack getStack() {
		return item;
	}

	@Override
	public void runTask() {
		new BukkitRunnable() {

			@Override
			public void run() {
				platformLivingTime++;

				if (platformLivingTime == 5) {
					player.sendMessage(i18n("specials_rescue_platform_destroy").replace("%time%", Integer.toString(platformLivingTime)));
				}

				if (platformLivingTime == breakingTime) {
					for (Block block : RescuePlatform.this.platformBlocks) {
						block.getChunk().load(true);
						block.setType(Material.AIR);

						game.getRegion().removeBlockBuildedDuringGame(block.getLocation());
						game.unregisterSpecialItem(RescuePlatform.this);

						this.cancel();
					}
				}
			}
		}.runTaskTimer(Main.getInstance(), 20L, 20L);
	}

	@Override
	public List<Block> getPlatformBlocks() {
		return platformBlocks;
	}

	private void addBlockToList(Block block)  {
		platformBlocks.add(block);
	}

	public void createPlatform() {
		breakingTime = Main.getConfigurator().config.getInt("specials.rescue-platform.break-time", 10);
		buildingMaterial = Main.getConfigurator().getDefinedMaterial("specials.rescue-platform.material", "GLASS");

		platformBlocks = new ArrayList<>();
		Location center = player.getLocation().clone();

		center.setY(center.getY() - Main.getConfigurator().config.getInt("specials.rescue-platform.distance", 1));

		for (BlockFace blockFace : BlockFace.values()) {
			if (blockFace.equals(BlockFace.DOWN) || blockFace.equals(BlockFace.UP)) {
				continue;
			}

			Block placedBlock = center.getBlock().getRelative(blockFace);
			if (placedBlock.getType() != Material.AIR) {
				continue;
			}

			placedBlock.setType(ColorChanger.changeStackColor(buildingMaterial, TeamColor.fromApiColor(team.getColor())));

			game.getRegion().addBuildedDuringGame(placedBlock.getLocation());

			addBlockToList(placedBlock);
		}

		if (breakingTime > 0) {
			game.registerSpecialItem(this);
			runTask();

			item.setAmount(item.getAmount() - 1);
			player.updateInventory();

			player.sendMessage(i18n("specials_rescue_platform_created").replace("%time%", Integer.toString(breakingTime)));
		}
	}
}
