package com.brumisharuma.pencilcraft.commands;

import com.brumisharuma.pencilcraft.managers.UndoManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UndoCommand implements CommandExecutor {

    private final UndoManager undo;

    public UndoCommand(UndoManager undo) {
        this.undo = undo;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player)) return true;

        Player p = (Player) sender;

        undo.undo(p.getUniqueId());
        p.sendMessage("§cUndid last 60 seconds of painting.");

        return true;
    }
}