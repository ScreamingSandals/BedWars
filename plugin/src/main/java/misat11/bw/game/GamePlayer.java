package misat11.bw.game;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;

public class GamePlayer {

	public final Player player;
	private Game game = null;
	private String latestGame = null;
	
	public boolean isSpectator = false;

	private StoredInventory oldinventory = new StoredInventory();

	public GamePlayer(Player player) {
		this.player = player;
	}

	public void changeGame(Game game) {
		if (this.game != null && game == null) {
			this.game.leavePlayer(this);
			this.game = null;
			this.isSpectator = false;
			this.clean();
			this.restoreInv();
		} else if (this.game == null && game != null) {
			this.storeInv();
			this.clean();
			this.game = game;
			this.isSpectator = false;
			this.game.joinPlayer(this);
			this.latestGame = this.game.getName();
		} else if (this.game != null && game != null) {
			this.game.leavePlayer(this);
			this.game = game;
			this.isSpectator = false;
			this.clean();
			this.game.joinPlayer(this);
			this.latestGame = this.game.getName();
		}
	}

	public Game getGame() {
		return game;
	}
	
	public String getLatestGameName() {
		return this.latestGame;
	}

	public boolean isInGame() {
		return game != null;
	}

	public void storeInv() {
		oldinventory.inventory = player.getInventory().getContents();
		oldinventory.armor = player.getInventory().getArmorContents();
		oldinventory.xp = Float.valueOf(player.getExp());
		oldinventory.effects = player.getActivePotionEffects();
		oldinventory.mode = player.getGameMode();
		oldinventory.left = player.getLocation();
		oldinventory.level = player.getLevel();
		oldinventory.listName = player.getPlayerListName();
		oldinventory.displayName = player.getDisplayName();
		oldinventory.foodLevel = player.getFoodLevel();
	}

	public void restoreInv() {
		player.getInventory().setContents(oldinventory.inventory);
		player.getInventory().setArmorContents(oldinventory.armor);

		player.addPotionEffects(oldinventory.effects);
		player.setLevel(oldinventory.level);
		player.setExp(oldinventory.xp);
		player.setFoodLevel(oldinventory.foodLevel);

		for (PotionEffect e : player.getActivePotionEffects())
			player.removePotionEffect(e.getType());

		player.addPotionEffects(oldinventory.effects);

		player.setPlayerListName(oldinventory.listName);
		player.setDisplayName(oldinventory.displayName);

		player.setGameMode(oldinventory.mode);

		if (oldinventory.mode == GameMode.CREATIVE)
			player.setAllowFlight(true);
		else
			player.setAllowFlight(false);

		player.updateInventory();
		player.teleport(oldinventory.left);
		player.resetPlayerTime();
		player.resetPlayerWeather();
	}

	public void clean() {

		PlayerInventory inv = this.player.getInventory();
		inv.setArmorContents(new ItemStack[4]);
		inv.setContents(new ItemStack[] {});

		this.player.setAllowFlight(false);
		this.player.setFlying(false);
		this.player.setExp(0.0F);
		this.player.setLevel(0);
		this.player.setSneaking(false);
		this.player.setSprinting(false);
		this.player.setFoodLevel(20);
		this.player.setSaturation(10);
		this.player.setExhaustion(0);
		this.player.setHealth(20.0D);
		this.player.setFireTicks(0);
		this.player.setGameMode(GameMode.SURVIVAL);

		if (this.player.isInsideVehicle()) {
			this.player.leaveVehicle();
		}

		for (PotionEffect e : this.player.getActivePotionEffects()) {
			this.player.removePotionEffect(e.getType());
		}

		this.player.updateInventory();
	}

}
