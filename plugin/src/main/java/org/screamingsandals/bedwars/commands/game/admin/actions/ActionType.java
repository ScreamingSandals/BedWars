package org.screamingsandals.bedwars.commands.game.admin.actions;

public enum ActionType {
    EDIT;

    public enum Add {
        STORE,
        SPAWNER,
        TEAM;
    }

    public enum Set {
        BORDER,
        SPAWN
    }
}
