package org.screamingsandals.bedwars.commands.game.actions;

public enum ActionType {
    CREATE;

    public enum Add {
        STORE,
        SPAWNER,
        TEAM;
    }

    public enum Set {
        GAME,
        LOBBY;
    }
}
