/**
 * 
 */

package me.merdril.RandomBattle;

import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerLoginEvent;

/**
 * @author mark
 * 
 */
public class RandomBattlePlayerListener extends PlayerListener
{
	public RandomBattle	plugin;
	
	/**
	 * 
	 */
	public RandomBattlePlayerListener(RandomBattle instance)
	{
		plugin = instance;
	}
	
	@Override
	public void onPlayerLogin(PlayerLoginEvent event)
	{	

	}
}
