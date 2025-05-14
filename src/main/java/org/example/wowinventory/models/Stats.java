package org.example.wowinventory.models;

public class Stats {
    private double strength;
    private double agility;
    private int stamina;
    private double intellect;
    private double spirit;
    private double armor;

    // Constructor, getters y setters
    public Stats(double strength, double agility, int stamina,
                 double intellect, double spirit, double armor) {
        this.strength = strength;
        this.agility = agility;
        this.stamina = stamina;
        this.intellect = intellect;
        this.spirit = spirit;
        this.armor = armor;
    }

    // Getters y Setters
    public double getStrength() { return strength; }
    public void setStrength(double strength) { this.strength = strength; }

    public double getAgility() { return agility; }
    public void setAgility(double agility) { this.agility = agility; }

    public int getStamina() { return stamina; }
    public void setStamina(int stamina) { this.stamina = stamina; }

    public double getIntellect() { return intellect; }
    public void setIntellect(double intellect) { this.intellect = intellect; }

    public double getSpirit() { return spirit; }
    public void setSpirit(double spirit) { this.spirit = spirit; }

    public double getArmor() { return armor; }
    public void setArmor(double armor) { this.armor = armor; }
}