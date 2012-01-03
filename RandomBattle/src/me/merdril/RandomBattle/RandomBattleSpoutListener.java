/**
 * 
 */

package me.merdril.RandomBattle;

import java.util.HashMap;

import org.getspout.spoutapi.event.spout.SpoutCraftEnableEvent;
import org.getspout.spoutapi.event.spout.SpoutListener;
import org.getspout.spoutapi.player.SpoutPlayer;

/**
 * @author Merdril
 * 
 */
public class RandomBattleSpoutListener extends SpoutListener
{
	public RandomBattle	                plugin;
	static HashMap<String, SpoutPlayer>	spoutPlayers	= new HashMap<String, SpoutPlayer>();
	
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
}