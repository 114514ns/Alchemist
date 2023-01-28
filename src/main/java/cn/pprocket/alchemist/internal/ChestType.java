package cn.pprocket.alchemist.internal;

public enum ChestType {
    MAP_COLLECTION,
    WEAPON_CHEST;

    public static ChestType getType(String name) {
        if (name.contains("itemset")) {
            return ChestType.MAP_COLLECTION;
        } else {
            return ChestType.WEAPON_CHEST;
        }
    }
}
