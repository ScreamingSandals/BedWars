package misat11.bw.game;

import org.bukkit.Location;

public class Team implements Cloneable {
	public TeamColor color;
	public String name;
	public Location bed;
	public Location spawn;
	public int maxPlayers;
	
	public Team clone() {
		Team t = new Team();
		t.color = this.color;
		t.name = this.name;
		t.bed = this.bed;
		t.spawn = this.spawn;
		t.maxPlayers = this.maxPlayers;
		return t;
	}
}
