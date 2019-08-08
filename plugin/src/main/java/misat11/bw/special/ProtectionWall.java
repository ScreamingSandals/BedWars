package misat11.bw.special;

import misat11.bw.Main;
import misat11.bw.api.Game;
import misat11.bw.api.Team;
import misat11.bw.game.TeamColor;
import misat11.bw.utils.ColorChanger;
import misat11.bw.utils.MiscUtils;
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
import static misat11.lib.lang.I18n.i18nonly;

public class ProtectionWall extends SpecialItem implements misat11.bw.api.special.ProtectionWall {
	private Game game;
	private Player player;
	private Team team;

	private int breakingTime;
	private int livingTime;
	private int width;
	private int height;
	private int distance;
	private boolean canBreak;

	private ItemStack item;
	private Material buildingMaterial;
	private List<Block> wallBlocks;

	public ProtectionWall(Game game, Player player, Team team, ItemStack item) {
		super(game, player, team);
		this.game = game;
		this.player = player;
		this.team = team;
		this.item = item;
	}

	@Override
	public int getBreakingTime() {
		return breakingTime;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public int getDistance() {
		return distance;
	}

	@Override
	public boolean canBreak() {
		return canBreak;
	}

	@Override
	public Material getMaterial() {
		return buildingMaterial;
	}

	@Override
	public void runTask() {
		new BukkitRunnable() {

			@Override
			public void run() {
				livingTime++;
				int time = breakingTime - livingTime;

				if (time < 6) {
					MiscUtils.sendActionBarMessage(
							player, i18nonly("specials_protection_wall_destroy").replace("%time%", Integer.toString(time)));
				}

				if (livingTime == breakingTime) {
					for (Block block : ProtectionWall.this.wallBlocks) {
						block.getChunk().load(true);
						block.setType(Material.AIR);
						removeBlockFromList(block);

						game.getRegion().removeBlockBuiltDuringGame(block.getLocation());
						game.unregisterSpecialItem(ProtectionWall.this);
						game.getRegion().removeBlockBuildedDuringGame(block.getLocation());

						this.cancel();
					}
				}
			}
		}.runTaskTimer(Main.getInstance(), 20L, 20L);
	}

	@Override
	public List<Block> getWallBlocks() {
		return wallBlocks;
	}

	private void addBlockToList(Block block)  {
		wallBlocks.add(block);
		game.getRegion().addBuildedDuringGame(block.getLocation());
	}

	private void removeBlockFromList(Block block) {
		game.getRegion().removeBlockBuiltDuringGame(block.getLocation());
	}

	public void createWall(boolean bre, int time, int wid, int hei, int dis, Material mat) {
		canBreak = bre;
		breakingTime = time;
		width = wid;
		height = hei;
		distance = dis;
		buildingMaterial = mat;
		wallBlocks = new ArrayList<>();

		if (width % 2 == 0) {
			player.sendMessage(i18n("The width of a protection block has to be odd! " + width + " is not an odd number."));
			width = width + 1;
			if (width % 2 == 0) {
				return;
			}
		}

		Location wallLocation = player.getLocation();
		wallLocation.add(wallLocation.getDirection().setY(0).normalize().multiply(distance));

		BlockFace face = MiscUtils.getCardinalDirection(player.getLocation());
		int widthStart = (int) Math.floor(((double) width) / 2.0);

		for (int w = widthStart * (-1); w < width - widthStart; w++) {
			for (int h = 0; h < height; h++) {
				Location wallBlock = wallLocation.clone();

				switch (face) {
					case SOUTH:
					case NORTH:
					case SELF:
						wallBlock.add(0, h, w);
						break;
					case WEST:
					case EAST:
						wallBlock.add(w, h, 0);
						break;
					case SOUTH_EAST:
						wallBlock.add(w, h, w);
						break;
					case SOUTH_WEST:
						wallBlock.add(w, h, w * (-1));
						break;
					case NORTH_EAST:
						wallBlock.add(w * (-1), h, w);
						break;
					case NORTH_WEST:
						wallBlock.add(w * (-1), h, w * (-1));
						break;
					default:
						wallBlock = null;
						break;
				}

				if (wallBlock == null) {
					continue;
				}

				Block placedBlock = wallBlock.getBlock();
				if (!placedBlock.getType().equals(Material.AIR)) {
					continue;
				}

				if (Main.isLegacy()) {
					ItemStack itemStack = new ItemStack(buildingMaterial);
					placedBlock.setType(ColorChanger.changeLegacyStackColor(itemStack, TeamColor.fromApiColor(team.getColor())).getType());
				} else {
					placedBlock.setType(ColorChanger.changeStackColor(buildingMaterial, TeamColor.fromApiColor(team.getColor())));
				}

				addBlockToList(placedBlock);
			}
		}

		if (breakingTime > 0) {
			game.registerSpecialItem(this);
			runTask();

			MiscUtils.sendActionBarMessage(player, i18nonly("specials_protection_wall_created").replace("%time%", Integer.toString(breakingTime)));

			item.setAmount(item.getAmount() - 1);
			player.updateInventory();
		} else {
			game.registerSpecialItem(this);

			MiscUtils.sendActionBarMessage(player, i18nonly("specials_protection_wall_created_unbreakable"));
			item.setAmount(item.getAmount() - 1);
			player.updateInventory();
		}
	}

}
