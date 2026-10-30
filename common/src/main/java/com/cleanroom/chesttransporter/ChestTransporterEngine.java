package com.cleanroom.chesttransporter;

import com.cleanroom.common.math.BlockVec3i;
import com.cleanroom.common.model.ContainerSlot;

import java.util.*;

public class ChestTransporterEngine {

    public record Result(boolean success, String reason, CapturedContainerData data) {
        public static Result success(CapturedContainerData data) {
            return new Result(true, "SUCCESS", data);
        }

        public static Result failure(String reason) {
            return new Result(false, reason, null);
        }
    }

    public Result capture(ChestTransporterWorldView world, TransporterPlayerView player, BlockVec3i pos) {
        if (!player.isSneaking()) {
            return Result.failure("MUST_SNEAK");
        }

        TransporterTier tier = TransporterTier.fromItemId(player.getHeldItemId());
        if (tier == null) {
            return Result.failure("NOT_A_TRANSPORTER");
        }

        String blockId = world.getBlockId(pos);
        if ("minecraft:air".equals(blockId)) {
            return Result.failure("AIR_BLOCK");
        }

        // Mob Spawner capture check
        boolean isSpawner = "minecraft:spawner".equals(blockId) || blockId.contains("spawner");
        if (isSpawner) {
            if (!tier.canCarrySpawner()) {
                return Result.failure("TIER_TOO_LOW_FOR_SPAWNER");
            }

            Map<String, String> props = world.getBlockProperties(pos);
            String customName = world.getCustomName(pos);
            Map<String, Object> extra = world.getExtraTileData(pos);

            CapturedContainerData capData = CapturedContainerData.single(
                    blockId, props, customName, 0, Collections.emptyList(), extra
            );

            world.removeBlock(pos);
            player.getHeldCustomData().put("CapturedContainer", capData);
            return Result.success(capData);
        }

        Map<String, String> props = world.getBlockProperties(pos);
        String chestType = props.get("type");
        int containerSize = world.getContainerSize(pos);
        boolean isDoubleChest = (chestType != null && ("left".equalsIgnoreCase(chestType) || "right".equalsIgnoreCase(chestType)))
                || containerSize > 27;

        if (isDoubleChest && !tier.canCarryDoubleChest()) {
            return Result.failure("TIER_TOO_LOW_FOR_DOUBLE_CHEST");
        }

        String customName = world.getCustomName(pos);
        List<ContainerSlot> slots = world.getContainerSlots(pos);
        Map<String, Object> extra = world.getExtraTileData(pos);

        CapturedContainerData capData;
        if (isDoubleChest) {
            BlockVec3i partnerOffset = new BlockVec3i(1, 0, 0); // Default East partner
            // Check adjacent horizontal positions for partner chest
            for (BlockVec3i neighbor : pos.getNeighbors6()) {
                if (neighbor.y() == pos.y() && world.getBlockId(neighbor).equals(blockId)) {
                    partnerOffset = new BlockVec3i(neighbor.x() - pos.x(), 0, neighbor.z() - pos.z());
                    world.removeBlock(neighbor);
                    break;
                }
            }
            capData = CapturedContainerData.doubleChest(blockId, props, customName, containerSize, slots, partnerOffset, extra);
        } else {
            capData = CapturedContainerData.single(blockId, props, customName, containerSize, slots, extra);
        }

        world.removeBlock(pos);

        // Apply carrying debuffs based on transporter tier and container type
        if (tier.getSlownessAmplifier() >= 0) {
            player.applyEffect("slowness", tier.getSlownessAmplifier());
        } else if (tier == TransporterTier.GOLD && isDoubleChest) {
            player.applyEffect("slowness", 0); // Slowness I for double chest on Gold
        }

        if (tier.getMiningFatigueAmplifier() >= 0) {
            player.applyEffect("mining_fatigue", tier.getMiningFatigueAmplifier());
        }

        player.getHeldCustomData().put("CapturedContainer", capData);
        return Result.success(capData);
    }

    public Result place(ChestTransporterWorldView world, TransporterPlayerView player, BlockVec3i targetPos) {
        Object rawData = player.getHeldCustomData().get("CapturedContainer");
        if (!(rawData instanceof CapturedContainerData data)) {
            return Result.failure("NO_CONTAINER_CAPTURED");
        }

        if (!world.isAirOrReplaceable(targetPos)) {
            return Result.failure("TARGET_OBSTRUCTED");
        }

        if (data.isDoubleChest()) {
            BlockVec3i partnerPos = targetPos.offset(
                    data.partnerOffset().x(),
                    data.partnerOffset().y(),
                    data.partnerOffset().z()
            );
            if (!world.isAirOrReplaceable(partnerPos)) {
                return Result.failure("TARGET_OBSTRUCTED");
            }

            // Restore both halves
            world.setBlock(targetPos, data.blockId(), data.blockProperties());
            world.setBlock(partnerPos, data.blockId(), data.blockProperties());
            world.restoreContainer(targetPos, data.customName(), data.containerSize(), data.slots(), data.extraNbt());
        } else {
            world.setBlock(targetPos, data.blockId(), data.blockProperties());
            world.restoreContainer(targetPos, data.customName(), data.containerSize(), data.slots(), data.extraNbt());
        }

        // Damage transporter tool
        int newDurability = player.getHeldDurability() + 1;
        player.setHeldDurability(newDurability);
        player.getHeldCustomData().remove("CapturedContainer");

        if (newDurability >= player.getHeldMaxDurability()) {
            player.clearHeldItem();
        }

        // Clear debuffs
        player.removeEffect("slowness");
        player.removeEffect("mining_fatigue");

        return Result.success(data);
    }
}
