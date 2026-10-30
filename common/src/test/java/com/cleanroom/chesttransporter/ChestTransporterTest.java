package com.cleanroom.chesttransporter;

import com.cleanroom.common.math.BlockVec3i;
import com.cleanroom.common.model.ContainerSlot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class ChestTransporterTest {

    private static class MockChestWorld implements ChestTransporterWorldView {
        private final Map<BlockVec3i, String> blocks = new HashMap<>();
        private final Map<BlockVec3i, Map<String, String>> properties = new HashMap<>();
        private final Map<BlockVec3i, String> customNames = new HashMap<>();
        private final Map<BlockVec3i, Integer> sizes = new HashMap<>();
        private final Map<BlockVec3i, List<ContainerSlot>> slots = new HashMap<>();

        public void setTestChest(BlockVec3i pos, String blockId, Map<String, String> props, String name, int size, List<ContainerSlot> itemSlots) {
            blocks.put(pos, blockId);
            properties.put(pos, props != null ? new HashMap<>(props) : new HashMap<>());
            if (name != null) customNames.put(pos, name);
            sizes.put(pos, size);
            slots.put(pos, itemSlots != null ? new ArrayList<>(itemSlots) : new ArrayList<>());
        }

        @Override
        public String getBlockId(BlockVec3i pos) {
            return blocks.getOrDefault(pos, "minecraft:air");
        }

        @Override
        public Map<String, String> getBlockProperties(BlockVec3i pos) {
            return properties.getOrDefault(pos, Collections.emptyMap());
        }

        @Override
        public String getCustomName(BlockVec3i pos) {
            return customNames.get(pos);
        }

        @Override
        public int getContainerSize(BlockVec3i pos) {
            return sizes.getOrDefault(pos, 0);
        }

        @Override
        public List<ContainerSlot> getContainerSlots(BlockVec3i pos) {
            return slots.getOrDefault(pos, Collections.emptyList());
        }

        @Override
        public Map<String, Object> getExtraTileData(BlockVec3i pos) {
            return Collections.emptyMap();
        }

        @Override
        public boolean isAirOrReplaceable(BlockVec3i pos) {
            return "minecraft:air".equals(getBlockId(pos));
        }

        @Override
        public void setBlock(BlockVec3i pos, String blockId, Map<String, String> props) {
            blocks.put(pos, blockId);
            properties.put(pos, props != null ? new HashMap<>(props) : new HashMap<>());
        }

        @Override
        public void restoreContainer(BlockVec3i pos, String customName, int size, List<ContainerSlot> slotList, Map<String, Object> extraNbt) {
            if (customName != null) customNames.put(pos, customName);
            sizes.put(pos, size);
            slots.put(pos, slotList != null ? new ArrayList<>(slotList) : new ArrayList<>());
        }

        @Override
        public void removeBlock(BlockVec3i pos) {
            blocks.remove(pos);
            properties.remove(pos);
            customNames.remove(pos);
            sizes.remove(pos);
            slots.remove(pos);
        }
    }

    private static class MockTransporterPlayer implements TransporterPlayerView {
        boolean sneaking = true;
        String heldItem;
        int durability = 0;
        int maxDurability = 1;
        Map<String, Object> customData = new HashMap<>();
        Map<String, Integer> activeEffects = new HashMap<>();

        @Override
        public boolean isSneaking() { return sneaking; }

        @Override
        public String getHeldItemId() { return heldItem; }

        @Override
        public int getHeldDurability() { return durability; }

        @Override
        public void setHeldDurability(int durability) { this.durability = durability; }

        @Override
        public int getHeldMaxDurability() { return maxDurability; }

        @Override
        public Map<String, Object> getHeldCustomData() { return customData; }

        @Override
        public void setHeldCustomData(Map<String, Object> data) { this.customData = data; }

        @Override
        public void applyEffect(String effectId, int amplifier) { activeEffects.put(effectId, amplifier); }

        @Override
        public void removeEffect(String effectId) { activeEffects.remove(effectId); }

        @Override
        public boolean hasEffect(String effectId) { return activeEffects.containsKey(effectId); }

        @Override
        public int getEffectAmplifier(String effectId) { return activeEffects.getOrDefault(effectId, -1); }

        @Override
        public void clearHeldItem() {
            heldItem = null;
            durability = 0;
            customData.clear();
        }
    }

    private MockChestWorld world;
    private MockTransporterPlayer player;
    private ChestTransporterEngine engine;

    @BeforeEach
    void setUp() {
        world = new MockChestWorld();
        player = new MockTransporterPlayer();
        engine = new ChestTransporterEngine();
    }

    @Test
    void testWoodenTransporterCapturesChestWithDebuffs() {
        BlockVec3i chestPos = new BlockVec3i(5, 64, 5);
        List<ContainerSlot> items = List.of(new ContainerSlot(0, "minecraft:diamond", 64, 64, Collections.emptyMap()));
        world.setTestChest(chestPos, "minecraft:chest", Collections.emptyMap(), "Treasury", 27, items);

        player.heldItem = "chesttransporter:wooden_transporter";
        player.maxDurability = 1;
        player.sneaking = true;

        ChestTransporterEngine.Result res = engine.capture(world, player, chestPos);

        assertTrue(res.success());
        assertEquals("SUCCESS", res.reason());
        assertTrue(player.customData.containsKey("CapturedContainer"));
        assertEquals("minecraft:air", world.getBlockId(chestPos));

        // Debuffs applied: Slowness II (amplifier 1) and Mining Fatigue II (amplifier 1)
        assertTrue(player.hasEffect("slowness"));
        assertEquals(1, player.getEffectAmplifier("slowness"));
        assertTrue(player.hasEffect("mining_fatigue"));
        assertEquals(1, player.getEffectAmplifier("mining_fatigue"));
    }

    @Test
    void testPlacementRestoresChestRemovesDebuffsAndDamagesTool() {
        BlockVec3i chestPos = new BlockVec3i(5, 64, 5);
        List<ContainerSlot> items = List.of(new ContainerSlot(0, "minecraft:emerald", 32, 64, Collections.emptyMap()));
        world.setTestChest(chestPos, "minecraft:chest", Collections.emptyMap(), "Store", 27, items);

        player.heldItem = "chesttransporter:iron_transporter";
        player.maxDurability = 10;
        player.durability = 0;
        player.sneaking = true;

        engine.capture(world, player, chestPos);
        assertTrue(player.hasEffect("slowness"));

        BlockVec3i placePos = new BlockVec3i(8, 64, 8);
        ChestTransporterEngine.Result placeRes = engine.place(world, player, placePos);

        assertTrue(placeRes.success());
        assertEquals("SUCCESS", placeRes.reason());
        assertEquals(1, player.durability);
        assertFalse(player.customData.containsKey("CapturedContainer"));
        assertFalse(player.hasEffect("slowness"));

        // Container restored in world
        assertEquals("minecraft:chest", world.getBlockId(placePos));
        assertEquals(27, world.getContainerSize(placePos));
        assertEquals("minecraft:emerald", world.getContainerSlots(placePos).get(0).itemId());
    }

    @Test
    void testIronTransporterCapturesDoubleChest() {
        BlockVec3i pos1 = new BlockVec3i(0, 64, 0);
        BlockVec3i pos2 = new BlockVec3i(1, 64, 0);
        world.setTestChest(pos1, "minecraft:chest", Map.of("type", "left"), null, 54, Collections.emptyList());
        world.setTestChest(pos2, "minecraft:chest", Map.of("type", "right"), null, 54, Collections.emptyList());

        player.heldItem = "chesttransporter:iron_transporter";
        player.maxDurability = 10;
        player.sneaking = true;

        ChestTransporterEngine.Result res = engine.capture(world, player, pos1);
        assertTrue(res.success());
        assertEquals("SUCCESS", res.reason());
        assertEquals("minecraft:air", world.getBlockId(pos1));
        assertEquals("minecraft:air", world.getBlockId(pos2));
    }

    @Test
    void testWoodenTransporterRejectsDoubleChest() {
        BlockVec3i pos = new BlockVec3i(0, 64, 0);
        world.setTestChest(pos, "minecraft:chest", Map.of("type", "right"), null, 54, Collections.emptyList());

        player.heldItem = "chesttransporter:wooden_transporter";
        player.maxDurability = 1;
        player.sneaking = true;

        ChestTransporterEngine.Result res = engine.capture(world, player, pos);
        assertFalse(res.success());
        assertEquals("TIER_TOO_LOW_FOR_DOUBLE_CHEST", res.reason());
    }

    @Test
    void testNetheriteTransporterCapturesSpawnerDiamondRejects() {
        BlockVec3i spawnerPos = new BlockVec3i(12, 50, 12);
        world.blocks.put(spawnerPos, "minecraft:spawner");

        player.sneaking = true;

        // Diamond transporter
        player.heldItem = "chesttransporter:diamond_transporter";
        player.maxDurability = 50;
        ChestTransporterEngine.Result diaRes = engine.capture(world, player, spawnerPos);
        assertFalse(diaRes.success());
        assertEquals("TIER_TOO_LOW_FOR_SPAWNER", diaRes.reason());

        // Netherite transporter
        player.heldItem = "chesttransporter:netherite_transporter";
        player.maxDurability = 200;
        ChestTransporterEngine.Result nethRes = engine.capture(world, player, spawnerPos);
        assertTrue(nethRes.success());
        assertEquals("SUCCESS", nethRes.reason());
        assertEquals("minecraft:air", world.getBlockId(spawnerPos));
    }

    @Test
    void testRequiresSneaking() {
        BlockVec3i pos = new BlockVec3i(0, 64, 0);
        world.setTestChest(pos, "minecraft:chest", Collections.emptyMap(), null, 27, Collections.emptyList());

        player.heldItem = "chesttransporter:iron_transporter";
        player.sneaking = false; // Not sneaking!

        ChestTransporterEngine.Result res = engine.capture(world, player, pos);
        assertFalse(res.success());
        assertEquals("MUST_SNEAK", res.reason());
    }
}
