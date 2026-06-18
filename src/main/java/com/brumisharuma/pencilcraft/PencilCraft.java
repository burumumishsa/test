package com.brumisharuma.pencilcraft;

import com.brumisharuma.pencilcraft.commands.PencilCommand;
import com.brumisharuma.pencilcraft.commands.UndoCommand;
import com.brumisharuma.pencilcraft.gui.ColorGUI;
import com.brumisharuma.pencilcraft.listeners.GUIListener;
import com.brumisharuma.pencilcraft.listeners.ProtocolPaintListener;
import com.brumisharuma.pencilcraft.managers.PaintManager;
import com.brumisharuma.pencilcraft.managers.UndoManager;
import com.brumisharuma.pencilcraft.tasks.PaintTask;
import org.bukkit.plugin.java.JavaPlugin;

public class PencilCraft extends JavaPlugin {

    private PaintManager paintManager;
    private UndoManager undoManager;
    private ColorGUI colorGUI;

    @Override
    public void onEnable() {

        paintManager = new PaintManager();
        undoManager = new UndoManager();
        colorGUI = new ColorGUI();

        ProtocolPaintListener listener =
                new ProtocolPaintListener(this, paintManager, undoManager);

        getCommand("pencil").setExecutor(
                new PencilCommand(colorGUI, paintManager)
        );

        getCommand("undo").setExecutor(
                new UndoCommand(undoManager)
        );

        getServer().getPluginManager().registerEvents(
                new GUIListener(colorGUI, paintManager),
                this
        );

        new PaintTask(paintManager, undoManager, listener)
                .runTaskTimer(this, 1L, 1L);
    }
}