package com.cleanroom.chesttransporter;

import com.cleanroom.common.math.BlockVec3i;
import com.cleanroom.common.model.ContainerSlot;

import java.util.*;

public record CapturedContainerData(
        String blockId,
        Map<String, String> blockProperties,
        String customName,
        int containerSize,
        List<ContainerSlot> slots,
        boolean isDoubleChest,
        BlockVec3i partnerOffset,
        Map<String, Object> extraNbt
) {
    public CapturedContainerData {
        blockProperties = blockProperties != null ? Collections.unmodifiableMap(new HashMap<>(blockProperties)) : Collections.emptyMap();
        slots = slots != null ? Collections.unmodifiableList(new ArrayList<>(slots)) : Collections.emptyList();
        extraNbt = extraNbt != null ? Collections.unmodifiableMap(new HashMap<>(extraNbt)) : Collections.emptyMap();
    }

    public static CapturedContainerData single(
            String blockId,
            Map<String, String> blockProperties,
            String customName,
            int containerSize,
            List<ContainerSlot> slots,
            Map<String, Object> extraNbt
    ) {
        return new CapturedContainerData(blockId, blockProperties, customName, containerSize, slots, false, BlockVec3i.ZERO, extraNbt);
    }

    public static CapturedContainerData doubleChest(
            String blockId,
            Map<String, String> blockProperties,
            String customName,
            int containerSize,
            List<ContainerSlot> slots,
            BlockVec3i partnerOffset,
            Map<String, Object> extraNbt
    ) {
        return new CapturedContainerData(blockId, blockProperties, customName, containerSize, slots, true, partnerOffset, extraNbt);
    }
}
