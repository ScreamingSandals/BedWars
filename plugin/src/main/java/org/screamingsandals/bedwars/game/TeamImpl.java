package org.screamingsandals.bedwars.game;

import lombok.Getter;
import lombok.Setter;
import org.screamingsandals.bedwars.api.Team;
import org.screamingsandals.lib.world.LocationHolder;

@Getter
@Setter
public class TeamImpl implements Cloneable, Team<LocationHolder, TeamColorImpl, GameImpl> {
    private TeamColorImpl color;
    private String name;
    private LocationHolder targetBlock;
    private LocationHolder teamSpawn;
    private int maxPlayers;
    private GameImpl game;

    public TeamImpl clone() {
        TeamImpl t = new TeamImpl();
        t.color = this.color;
        t.name = this.name;
        t.targetBlock = this.targetBlock;
        t.teamSpawn = this.teamSpawn;
        t.maxPlayers = this.maxPlayers;
        t.game = this.game;
        return t;
    }
}
