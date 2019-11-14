package org.screamingsandals.bedwars.utils;

public enum Permissions {
    /************GAME***********/
    BASE_PERMISSION("screamingbedwars.base"),
    ADMIN_PERMISSIONS("screamingbedwars.admin"),
    VIP_PERMISSIONS("screamingbedwars.vip"),

    SEE_OTHER_STATS("screamingbedwars.others.statistics"),

    VIP_JOIN_FULL_PERMISSIONS("screamingbedwars.vip.joinfull");


    public String permission;

    Permissions(String permission) {
        this.permission = permission;
    }
}
