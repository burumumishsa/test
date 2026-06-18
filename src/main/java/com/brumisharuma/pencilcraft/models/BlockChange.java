package com.brumisharuma.pencilcraft.models;

import org.bukkit.Location;
import org.bukkit.Material;

public class BlockChange {

    private final Location location;
    private final Material oldMaterial;
    private final byte oldData;
    private final long time;

    public BlockChange(Location location, Material oldMaterial, byte oldData, long time) {
        this.location = location;
        this.oldMaterial = oldMaterial;
        this.oldData = oldData;
        this.time = time;
    }

    public Location getLocation() {
        return location;
    }

    public Material getOldMaterial() {
        return oldMaterial;
    }

    public byte getOldData() {
        return oldData;
    }

    public long getTime() {
        return time;
    }
}