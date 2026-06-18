package com.brumisharuma.pencilcraft.tasks;

import com.brumisharuma.pencilcraft.listeners.ProtocolPaintListener;
import com.brumisharuma.pencilcraft.managers.PaintManager;
import com.brumisharuma.pencilcraft.managers.UndoManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class PaintTask extends BukkitRunnable {

    private final PaintManager paint;
    private final UndoManager undo;
    private final ProtocolPaintListener listener;

    public PaintTask(PaintManager paint,
                     UndoManager undo,
                     ProtocolPaintListener listener) {
        this.paint = paint;
        this.undo = undo;
        this.listener = listener;
    }

    @Override
    public void run() {

        listener.update(paint);

        for (Player player : Bukkit.getOnlinePlayers()) {

            if (!paint.isPainting(player.getUniqueId())) continue;

            paintAtCamera(player);
        }
    }

    private void paintAtCamera(Player player) {

        java.util.UUID uuid = player.getUniqueId();

        org.bukkit.inventory.ItemStack item = paint.getSelectedItem(uuid);
        byte data = item.getData().getData();

        org.bukkit.Location eye = player.getEyeLocation();
        org.bukkit.util.Vector dir = eye.getDirection().normalize();

        double maxDistance = 200;

        for (double d = 0; d <= maxDistance; d += 0.5) {

            org.bukkit.Location point = eye.clone().add(dir.clone().multiply(d));
            org.bukkit.block.Block block = point.getBlock();

            // FIX 1.12 AIR CHECK
            if (block.getType() == org.bukkit.Material.AIR) continue;

            if (block.getType() == org.bukkit.Material.WOOL &&
                block.getData() == data) return;

            undo.record(uuid, block);

            block.setType(org.bukkit.Material.WOOL);
            block.setData(data);

            return;
        }
    }
}