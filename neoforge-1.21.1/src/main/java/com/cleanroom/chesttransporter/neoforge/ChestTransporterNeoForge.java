package com.cleanroom.chesttransporter.neoforge;

import com.cleanroom.chesttransporter.ChestTransporterCore;

public class ChestTransporterNeoForge {
    private final ChestTransporterCore core = new ChestTransporterCore();

    public ChestTransporterNeoForge() {
        System.out.println("[CleanRoom NeoForge] Bootstrapping " + ChestTransporterCore.MOD_NAME);
        core.onInitializeCommon();
    }
}
