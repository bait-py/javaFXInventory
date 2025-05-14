// Before
// Missing Item class and ItemType enum

// After
package org.example.wowinventory.models;

public class Item {
    public enum ItemType {
        HEAD, NECK, SHOULDERS, CHEST, WAIST, LEGS, FEET, WRISTS, HANDS, FINGER, TRINKET, BACK, WEAPON, OFFHAND, RANGED
    }

    public enum Rarity {
        COMMON, UNCOMMON, RARE, EPIC, LEGENDARY
    }

    private String name;
    private ItemType type;
    private String icon;
    private int requiredLevel;
    private String statsDescription;
    private Rarity rarity;

    public Item(String name, ItemType type, String icon, int requiredLevel, String statsDescription) {
        this.name = name;
        this.type = type;
        this.icon = icon;
        this.requiredLevel = requiredLevel;
        this.statsDescription = statsDescription;
        this.rarity = Rarity.COMMON; // Default rarity
    }

    public String getName() { return name; }
    public ItemType getType() { return type; }
    public String getIcon() { return icon; }
    public int getRequiredLevel() { return requiredLevel; }
    public String getStatsDescription() { return statsDescription; }
    public Rarity getRarity() { return rarity; }
}