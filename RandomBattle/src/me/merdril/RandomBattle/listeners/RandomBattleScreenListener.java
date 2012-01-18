/**
 * 
 */

package me.merdril.RandomBattle.listeners;

import me.merdril.RandomBattle.RandomBattle;
import me.merdril.RandomBattle.HUD.RandomBattlePopupScreen;

import org.getspout.spoutapi.event.screen.ScreenCloseEvent;
import org.getspout.spoutapi.event.screen.ScreenListener;

/**
 * @author Merdril
 * 
 */
public class RandomBattleScreenListener extends ScreenListener
{
	RandomBattle	plugin;
	
	/**
	 * 
	 */
	public RandomBattleScreenListener(RandomBattle instance)
	{
		this.plugin = instance;
	}
	
	@Override
	public void onScreenClose(ScreenCloseEvent event)
	{
		if (event.getScreen() instanceof RandomBattlePopupScreen)
			event.getPlayer()
			        .sendMessage(
			                "[RandomBattle] Screen class: " + event.getScreen().getClass()
			                        + " was closed.");
	}
}
