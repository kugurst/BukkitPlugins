/**
 * 
 */

package me.merdril.RandomBattle.listeners;

import java.util.HashMap;

import me.merdril.RandomBattle.RandomBattle;

import org.getspout.spoutapi.event.spout.SpoutCraftEnableEvent;
import org.getspout.spoutapi.event.spout.SpoutListener;
import org.getspout.spoutapi.event.spout.SpoutcraftFailedEvent;
import org.getspout.spoutapi.player.SpoutPlayer;

/**
 * @author Merdril
 * 
 */
public class RandomBattleSpoutListener extends SpoutListener
{
	public RandomBattle	                                plugin;
	public static volatile HashMap<String, SpoutPlayer>	spoutPlayers	=
	                                                                         new HashMap<String, SpoutPlayer>();
	
	/**
	 * 
	 */
	public RandomBattleSpoutListener(RandomBattle instance)
	{
		plugin = instance;
	}
	
	@Override
	public void onSpoutCraftEnable(SpoutCraftEnableEvent event)
	{
		spoutPlayers.put(event.getPlayer().getDisplayName(), event.getPlayer());
	}
	
	@Override
	public void onSpoutcraftFailed(SpoutcraftFailedEvent event)
	{
		spoutPlayers.remove(event.getPlayer().getDisplayName());
	}
}
