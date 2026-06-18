package com.brumisharuma.pencilcraft.managers;

import com.brumisharuma.pencilcraft.models.BlockChange;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.Material;

import java.util.*;

public class UndoManager {

    private final Map<UUID, Deque<BlockChange>> history = new HashMap<>();

    public void record(UUID uuid, Block block) {
        history.putIfAbsent(uuid, new ArrayDeque<>());

        Deque<BlockChange> list = history.get(uuid);

        list.addLast(new BlockChange(
                block.getLocation().clone(),
                block.getType(),
                block.getData(),
                System.currentTimeMillis()
        ));
    }

    public void cleanupOld() {
        long cutoff = System.currentTimeMillis() - 60_000;

        for (Deque<BlockChange> list : history.values()) {
            list.removeIf(change -> change.getTime() < cutoff);
        }
    }

    public void undo(UUID uuid) {
        Deque<BlockChange> list = history.get(uuid);
        if (list == null) return;

        long cutoff = System.currentTimeMillis() - 60_000;

        Iterator<BlockChange> it = list.descendingIterator();

        while (it.hasNext()) {
            BlockChange change = it.next();

            if (change.getTime() < cutoff) continue;

            Location loc = change.getLocation();
            World world = loc.getWorld();
            if (world == null) continue;

            Block block = world.getBlockAt(loc);
            block.setType(change.getOldMaterial());
            block.setData(change.getOldData());

            it.remove();
        }
    }
}