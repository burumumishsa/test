package com.brumisharuma.pencilcraft.gui;

import com.brumisharuma.pencilcraft.managers.PaintManager;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ColorGUI {

    public static final String TITLE = "§6Pencil Colors";

    public void open(Player player, PaintManager manager) {
        Inventory inv = Bukkit.createInventory(null, 54, TITLE);

        int i = 0;
        for (DyeColor color : DyeColor.values()) {
            inv.setItem(i++, new ItemStack(Material.WOOL, 1, (short) color.getWoolData()));
        }

        player.openInventory(inv);
    }
}