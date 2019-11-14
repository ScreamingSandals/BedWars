package org.screamingsandals.bedwars.utils;

public enum Permissions {
    /************GAME***********/
    BASE("screamingbedwars.base"),
    ADMIN("screamingbedwars.admin"),
    VIP("screamingbedwars.vip"),
    VIP_START_ITEM("screamingbedwars.vip.startitem"),
    VIP_JOIN_FULL("screamingbedwars.vip.joinfull"),

    SEE_OTHER_STATS("screamingbedwars.others.statistics");

    public String permission;

    Permissions(String permission) {
        this.permission = permission;
    }
}
