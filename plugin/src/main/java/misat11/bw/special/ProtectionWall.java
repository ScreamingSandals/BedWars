package misat11.bw.special;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import misat11.bw.api.Game;
import misat11.bw.api.Team;

public class ProtectionWall extends SpecialItem implements misat11.bw.api.special.ProtectionWall {

	public ProtectionWall(Game game, Player player, Team team) {
		super(game, player, team);
		// TODO Auto-generated constructor stub
	}

	@Override
	public int getBreakingTime() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getWidth() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getHeight() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getDistance() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean canBreak() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Material getMaterial() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void runTask() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<Block> getWallBlocks() {
		// TODO Auto-generated method stub
		return null;
	}

}
