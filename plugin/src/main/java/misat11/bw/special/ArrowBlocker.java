package misat11.bw.special;

import org.bukkit.entity.Player;

import misat11.bw.api.Game;
import misat11.bw.api.Team;

public class ArrowBlocker extends SpecialItem implements misat11.bw.api.special.ArrowBlocker {

	public ArrowBlocker(Game game, Player player, Team team) {
		super(game, player, team);
		// TODO Auto-generated constructor stub
	}

	@Override
	public int getProtectionTime() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isActivated() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isProtecting() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void runTask() {
		// TODO Auto-generated method stub
		
	}

}
