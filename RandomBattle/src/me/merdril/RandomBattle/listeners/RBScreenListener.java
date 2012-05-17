/**
 * 
 */

package me.merdril.RandomBattle.listeners;

import me.merdril.RandomBattle.RandomBattle;
import me.merdril.RandomBattle.HUD.RandomBattlePopupScreen;

import org.bukkit.event.Listener;
import org.getspout.spoutapi.event.screen.ScreenCloseEvent;

/**
 * @author Merdril
 * 
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

	public void onScreenClose(ScreenCloseEvent event)
	{
		if (event.getScreen() instanceof RandomBattlePopupScreen)
			event.getPlayer().sendMessage("[RandomBattle] Screen class: " + event.getScreen().getClass() + " was closed.");
	}
}
