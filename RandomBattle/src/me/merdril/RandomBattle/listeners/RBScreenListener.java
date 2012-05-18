/**
 * 
 */

package me.merdril.RandomBattle.listeners;

import me.merdril.RandomBattle.RandomBattle;
import me.merdril.RandomBattle.HUD.RBPopupScreen;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.getspout.spoutapi.event.screen.ScreenCloseEvent;

/**
 * @author Merdril
 */
public class RBScreenListener implements Listener
{
	RandomBattle	plugin;
	
	/**
	 * 
	 */
	public RBScreenListener(RandomBattle instance)
	{
		this.plugin = instance;
	}
	
	@EventHandler
	public void onScreenClose(ScreenCloseEvent event)
	{
		if (event.getScreen() instanceof RBPopupScreen)
			event.getPlayer().sendMessage(
			        "[RandomBattle] Screen class: " + event.getScreen().getClass() + " was closed.");
	}
}
