package misat11.bw.special;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;

import misat11.bw.api.Game;
import misat11.bw.api.Team;

public class TNTSheep extends SpecialItem implements misat11.bw.api.special.TNTSheep {

	public TNTSheep(Game game, Player player, Team team) {
		super(game, player, team);
		// TODO Auto-generated constructor stub
	}

	@Override
	public LivingEntity getEntity() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isUsed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Location getInitialLocation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TNTPrimed getTNT() {
		// TODO Auto-generated method stub
		return null;
	}

}
