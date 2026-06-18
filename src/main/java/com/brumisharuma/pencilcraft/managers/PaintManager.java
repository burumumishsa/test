package com.brumisharuma.pencilcraft.managers;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class PaintManager {

    private final Map<UUID, Byte> selected = new HashMap<>();
    private final Set<UUID> painting = new HashSet<>();

    public List<ItemStack> getPalette() {
        List<ItemStack> list = new ArrayList<>();

        for (DyeColor color : DyeColor.values()) {
            list.add(new ItemStack(Material.WOOL, 1, (short) color.getWoolData()));
        }

        return list;
    }

    public ItemStack getSelectedItem(UUID uuid) {
        byte data = selected.getOrDefault(uuid, DyeColor.WHITE.getWoolData());
        return new ItemStack(Material.WOOL, 1, (short) data);
    }

    public void setSelected(UUID uuid, byte data) {
        selected.put(uuid, data);
    }

    public boolean isPainting(UUID uuid) {
        return painting.contains(uuid);
    }

    public void startPainting(UUID uuid) {
        painting.add(uuid);
    }

    public void stopPainting(UUID uuid) {
        painting.remove(uuid);
    }
}