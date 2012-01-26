/**
 * 
 */

package me.merdril.RandomBattle.battleSystem;

import java.util.HashMap;

import me.merdril.RandomBattle.RBUtilities;
import me.merdril.RandomBattle.RandomBattle;

import org.getspout.spoutapi.player.SpoutPlayer;

/**
 * @author mark
 * 
 */
public class RBPlayer
{
	RandomBattle	                 plugin;
	private SpoutPlayer	             player;
	private HashMap<String, Integer>	stats	= new HashMap<String, Integer>();
	
	/**
	 * <code>public RBPlayer(RandomBattle instance, SpoutPlayer player, Integer[] stats)</code> <br/>
	 * <br/>
	 * 
	 * Defines a Random Battle player which is a SpoutPlayer with stats and corresponding methods
	 * for manipulating them.
	 * 
	 * @param instance
	 *            - The RandomBattle instance
	 * @param player
	 *            - The player to represent
	 * @param stats
	 *            - The stats of the player
	 */
	public RBPlayer(RandomBattle instance, SpoutPlayer player, Integer[] stats)
	{
		plugin = instance;
		this.player = player;
		if (stats.length != RBUtilities.statNames.length)
			throw new SizeMismatch("The stats must be of correct length!");
	}
	
}
