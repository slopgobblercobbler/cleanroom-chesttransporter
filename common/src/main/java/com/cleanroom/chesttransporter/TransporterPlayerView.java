package com.cleanroom.chesttransporter;

import java.util.Map;

public interface TransporterPlayerView {
    boolean isSneaking();
    String getHeldItemId();
    int getHeldDurability();
    void setHeldDurability(int durability);
    int getHeldMaxDurability();

    Map<String, Object> getHeldCustomData();
    void setHeldCustomData(Map<String, Object> data);

    void applyEffect(String effectId, int amplifier);
    void removeEffect(String effectId);
    boolean hasEffect(String effectId);
    int getEffectAmplifier(String effectId);
    void clearHeldItem();
}
