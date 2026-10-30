package com.cleanroom.chesttransporter.fabric;

import com.cleanroom.chesttransporter.ChestTransporterCore;

public class ChestTransporterFabric {
    private final ChestTransporterCore core = new ChestTransporterCore();

    public void onInitialize() {
        System.out.println("[CleanRoom Fabric] Bootstrapping " + ChestTransporterCore.MOD_NAME);
        core.onInitializeCommon();
    }
}
