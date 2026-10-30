package com.cleanroom.chesttransporter;

import com.cleanroom.common.math.BlockVec3i;
import com.cleanroom.common.model.ContainerSlot;

import java.util.List;
import java.util.Map;

public interface ChestTransporterWorldView {
    String getBlockId(BlockVec3i pos);
    Map<String, String> getBlockProperties(BlockVec3i pos);
    String getCustomName(BlockVec3i pos);
    int getContainerSize(BlockVec3i pos);
    List<ContainerSlot> getContainerSlots(BlockVec3i pos);
    Map<String, Object> getExtraTileData(BlockVec3i pos);

    boolean isAirOrReplaceable(BlockVec3i pos);
    void setBlock(BlockVec3i pos, String blockId, Map<String, String> properties);
    void restoreContainer(BlockVec3i pos, String customName, int size, List<ContainerSlot> slots, Map<String, Object> extraNbt);
    void removeBlock(BlockVec3i pos);
}
