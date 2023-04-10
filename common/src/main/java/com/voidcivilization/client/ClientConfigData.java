package com.voidcivilization.client;


public class ClientConfigData {

    private static int nucleusProtectionRadius;
    private static int forceFieldRadius;
    private static int maxNucleusHealth;

    public static int getNucleusProtectionRadius() {
        return nucleusProtectionRadius;
    }

    public static void setNucleusProtectionRadius(int nucleusProtectionRadius) {
        ClientConfigData.nucleusProtectionRadius = nucleusProtectionRadius;
    }

    public static int getForceFieldRadius() {
        return forceFieldRadius;
    }

    public static void setForceFieldRadius(int forceFieldRadius) {
        ClientConfigData.forceFieldRadius = forceFieldRadius;
    }

    public static int getMaxNucleusHealth() {
        return maxNucleusHealth;
    }

    public static void setMaxNucleusHealth(int maxNucleusHealth) {
        ClientConfigData.maxNucleusHealth = maxNucleusHealth;
    }
}
