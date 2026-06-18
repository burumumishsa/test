package com.brumisharuma.pencilcraft.commands;

import com.brumisharuma.pencilcraft.gui.ColorGUI;
import com.brumisharuma.pencilcraft.managers.PaintManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PencilCommand implements CommandExecutor {

    private final ColorGUI gui;
    private final PaintManager manager;

    public PencilCommand(ColorGUI gui, PaintManager manager) {
        this.gui = gui;
        this.manager = manager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player)) return true;

        Player p = (Player) sender;

        gui.open(p, manager);

        return true;
    }
}