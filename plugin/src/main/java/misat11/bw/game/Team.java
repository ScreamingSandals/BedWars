package misat11.bw.game;

import misat11.bw.api.Game;
import org.bukkit.Location;

public class Team implements Cloneable, misat11.bw.api.Team {
    public TeamColor color;
    public String name;
    public Location bed;
    public Location spawn;
    public int maxPlayers;
    public Game game;

    public Team clone() {
        Team t = new Team();
        t.color = this.color;
        t.name = this.name;
        t.bed = this.bed;
        t.spawn = this.spawn;
        t.maxPlayers = this.maxPlayers;
        t.game = this.game;
        return t;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public misat11.bw.api.TeamColor getColor() {
        return color.toApiColor();
    }

    @Override
    public Location getTeamSpawn() {
        return spawn;
    }

    @Override
    public Location getTargetBlock() {
        return bed;
    }

    @Override
    public int getMaxPlayers() {
        return maxPlayers;
    }

    @Override
    public Game getGame() {
        return game;
    }
}
