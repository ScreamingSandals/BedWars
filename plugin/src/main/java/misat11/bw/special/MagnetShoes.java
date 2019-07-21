package misat11.bw.special;

import org.bukkit.entity.Player;

import misat11.bw.api.Game;
import misat11.bw.api.Team;

public class MagnetShoes extends SpecialItem implements misat11.bw.api.special.MagnetShoes {

	public MagnetShoes(Game game, Player player, Team team) {
		super(game, player, team);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Player getWearer() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isAnybodyWearing() {
		// TODO Auto-generated method stub
		return false;
	}

}
