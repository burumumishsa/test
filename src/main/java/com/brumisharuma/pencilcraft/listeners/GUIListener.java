package com.brumisharuma.pencilcraft.listeners;

import com.brumisharuma.pencilcraft.gui.ColorGUI;
import com.brumisharuma.pencilcraft.managers.PaintManager;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class GUIListener implements Listener {

    private final ColorGUI gui;
    private final PaintManager manager;

    public GUIListener(ColorGUI gui, PaintManager manager) {
        this.gui = gui;
        this.manager = manager;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {

        if (!(e.getWhoClicked() instanceof Player)) return;

        Player p = (Player) e.getWhoClicked();

        if (!e.getView().getTitle().equals(ColorGUI.TITLE)) return;

        e.setCancelled(true);

        ItemStack item = e.getCurrentItem();
        if (item == null || item.getType() != Material.WOOL) return;

        byte data = item.getData().getData();

        manager.setSelected(p.getUniqueId(), data);

        p.sendMessage("§aSelected: " + DyeColor.getByWoolData(data));

        p.closeInventory();
    }
}