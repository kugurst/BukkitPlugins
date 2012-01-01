/**
 * 
 */

package me.merdril.RandomBattle;

import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerLoginEvent;

/**
 * @author Merdril
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
		System.out.println("[RandomBattle] This player logged in: "
				+ event.getPlayer().getDisplayName());
	}
}
