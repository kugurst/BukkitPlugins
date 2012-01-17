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
			plugin.getServer().getConsoleSender()
			        .sendMessage("[RandomBattle] Something was closed!");
	}
}
