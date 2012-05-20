/**
 * 
 */

package me.merdril.randombattle.listeners;

import java.util.ArrayList;

import me.merdril.randombattle.RandomBattle;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.getspout.spoutapi.event.screen.ScreenCloseEvent;
import org.getspout.spoutapi.gui.GenericPopup;
import org.getspout.spoutapi.gui.GenericWidget;

/**
 * @author Merdril
 */
public class RBScreenListener implements Listener
{
	private GenericPopup	         screen;
	private ArrayList<GenericWidget>	invisibleWidgets;
	
	/**
	 * @param screen
	 * @param invisibleWidgets
	 */
	public RBScreenListener(GenericPopup screen, ArrayList<GenericWidget> invisibleWidgets)
	{
		this.screen = screen;
		this.invisibleWidgets = invisibleWidgets;
	}
	
	@EventHandler
	public void onScreenClose(ScreenCloseEvent event)
	{
		if (event.getScreen() == screen) {
			event.getScreen().getPlayer().sendMessage(RandomBattle.prefix + "Your RandomBattle screen was closed.");
			for (GenericWidget widget : invisibleWidgets)
				widget.setVisible(true);
		}
	}
}
