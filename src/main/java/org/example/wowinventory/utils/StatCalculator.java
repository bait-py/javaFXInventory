package org.example.wowinventory.utils;

public class StatCalculator {
    public static int calcularDañoMinimo(int strength, int level) {
        return strength * level / 10; // Example calculation
    }

    public static int calcularDañoMaximo(int strength, int level) {
        return strength * level / 8; // Example calculation
    }

    public static double calcularProbabilidadCritico(int agility, int level) {
        return agility * 0.1; // Example calculation
    }

    public static int calcularPoderAtaque(int strength) {
        return strength * 2; // Example calculation
    }

    public static double calcularPrecision(int level) {
        return level * 0.5; // Example calculation
    }

    public static int calcularSaludMaxima(int stamina, int level) {
        return stamina * level * 10; // Example calculation
    }

    public static int calcularManaMaximo(int intellect, int level) {
        return intellect * level * 8; // Example calculation
    }
}
