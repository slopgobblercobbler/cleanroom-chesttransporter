package com.cleanroom.chesttransporter;

public enum TransporterTier {
    WOOD(1, false, false, 1, 1),
    IRON(10, true, false, 0, -1),
    GOLD(20, true, false, -1, -1),
    DIAMOND(50, true, false, -1, -1),
    NETHERITE(200, true, true, -1, -1);

    private final int maxDurability;
    private final boolean canCarryDoubleChest;
    private final boolean canCarrySpawner;
    private final int slownessAmplifier;      // -1 means none, 0 = Slowness I, 1 = Slowness II
    private final int miningFatigueAmplifier; // -1 means none, 1 = Mining Fatigue II

    TransporterTier(int maxDurability, boolean canCarryDoubleChest, boolean canCarrySpawner,
                    int slownessAmplifier, int miningFatigueAmplifier) {
        this.maxDurability = maxDurability;
        this.canCarryDoubleChest = canCarryDoubleChest;
        this.canCarrySpawner = canCarrySpawner;
        this.slownessAmplifier = slownessAmplifier;
        this.miningFatigueAmplifier = miningFatigueAmplifier;
    }

    public int getMaxDurability() { return maxDurability; }
    public boolean canCarryDoubleChest() { return canCarryDoubleChest; }
    public boolean canCarrySpawner() { return canCarrySpawner; }
    public int getSlownessAmplifier() { return slownessAmplifier; }
    public int getMiningFatigueAmplifier() { return miningFatigueAmplifier; }

    public static TransporterTier fromItemId(String itemId) {
        if (itemId == null) return null;
        String lower = itemId.toLowerCase();
        if (lower.contains("wooden") || lower.contains("wood")) return WOOD;
        if (lower.contains("iron")) return IRON;
        if (lower.contains("gold")) return GOLD;
        if (lower.contains("diamond")) return DIAMOND;
        if (lower.contains("netherite")) return NETHERITE;
        return null;
    }
}
