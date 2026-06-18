package com.brumisharuma.pencilcraft.listeners;

import com.brumisharuma.pencilcraft.managers.PaintManager;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.*;
import com.comphenix.protocol.PacketType;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ProtocolPaintListener {

    private final Map<UUID, Long> lastClick = new HashMap<>();

    public ProtocolPaintListener(Plugin plugin,
                                 PaintManager paint,
                                 com.brumisharuma.pencilcraft.managers.UndoManager undo) {

        ProtocolLibrary.getProtocolManager().addPacketListener(
                new PacketAdapter(plugin, PacketType.Play.Client.ARM_ANIMATION) {

                    @Override
                    public void onPacketReceiving(PacketEvent event) {

                        UUID uuid = event.getPlayer().getUniqueId();

                        paint.startPainting(uuid);
                        lastClick.put(uuid, System.currentTimeMillis());
                    }
                }
        );
    }

    public void update(PaintManager paint) {

        long now = System.currentTimeMillis();

        for (Map.Entry<UUID, Long> e : lastClick.entrySet()) {

            if (now - e.getValue() > 250) {
                paint.stopPainting(e.getKey());
            }
        }
    }
}