package org.example.wowinventory.models;

import org.example.wowinventory.utils.StatCalculator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

public class Character {
    private String name;
    private String race;
    private String characterClass;
    private int level;
    private int gold;
    private int baseStrength, bonusStrength;
    private int baseAgility, bonusAgility;
    private int baseStamina, bonusStamina;
    private int baseIntellect, bonusIntellect;
    private int baseSpirit, bonusSpirit;
    private int baseArmor, bonusArmor;
    private int currentHealth, maxHealth;
    private int currentMana, maxMana;
    private int currentExp, expToNextLevel;

    private Stats stats; // Added field
    private ObservableMap<Item.ItemType, Item> equippedItems; // Added field

    public Character(String name, String race, String characterClass, int level, Stats stats) {
        this.name = name;
        this.race = race;
        this.characterClass = characterClass;
        this.level = level;
        this.stats = stats;
        this.equippedItems = FXCollections.observableHashMap();
    }

    public void equipItem(Item item) {
        equippedItems.put(item.getType(), item);
    }

    public void unequipItem(Item.ItemType type) {
        equippedItems.remove(type);
    }

    // Getters
    public String getName() { return name; }
    public String getRace() { return race; }
    public String getCharacterClass() { return characterClass; }
    public int getLevel() { return level; }
    public int getGold() { return gold; }
    public int getBaseStrength() { return baseStrength; }
    public int getBonusStrength() { return bonusStrength; }
    public int getBaseAgility() { return baseAgility; }
    public int getBonusAgility() { return bonusAgility; }
    public int getBaseStamina() { return baseStamina; }
    public int getBonusStamina() { return bonusStamina; }
    public int getBaseIntellect() { return baseIntellect; }
    public int getBonusIntellect() { return bonusIntellect; }
    public int getBaseSpirit() { return baseSpirit; }
    public int getBonusSpirit() { return bonusSpirit; }
    public int getBaseArmor() { return baseArmor; }
    public int getBonusArmor() { return bonusArmor; }
    public int getCurrentHealth() { return currentHealth; }
    public int getMaxHealth() { return maxHealth; }
    public int getCurrentMana() { return currentMana; }
    public int getMaxMana() { return maxMana; }
    public int getCurrentExp() { return currentExp; }
    public int getExpToNextLevel() { return expToNextLevel; }
    public void setMaxHealth(int maxHealth) { this.maxHealth = maxHealth; }
    public void setMaxMana(int maxMana) { this.maxMana = maxMana; }
    public void resetBonuses() {
        bonusStrength = bonusAgility = bonusStamina = bonusIntellect = bonusSpirit = bonusArmor = 0;
    }
    public void addBonusStrength(int value) { bonusStrength += value; }
    public void addBonusAgility(int value) { bonusAgility += value; }
    public void addBonusStamina(int value) { bonusStamina += value; }
    public void addBonusIntellect(int value) { bonusIntellect += value; }
    public void addBonusSpirit(int value) { bonusSpirit += value; }
    public void addBonusArmor(int value) { bonusArmor += value; }
    public void setCurrentHealth(int currentHealth) { this.currentHealth = currentHealth; }
    public void setCurrentMana(int currentMana) { this.currentMana = currentMana; }
}