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
public class RBPlayer extends RBLivingEntity
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
	 * @throws Exception
	 */
	public RBPlayer(RandomBattle instance, SpoutPlayer player, int[] stat)
	        throws ArrayIndexOutOfBoundsException
	{
		plugin = instance;
		this.player = player;
		if (stat.length != RBUtilities.statNames.length)
			throw new ArrayIndexOutOfBoundsException("The stats must be of correct length!");
		fillStats(stat);
	}
	
	public void fillStats(int[] stat)
	{
		for (int i = 0; i < stat.length; i++)
			stats.put(RBUtilities.statNames[i], stat[i]);
	}
	
}
