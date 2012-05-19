/**
 * 
 */

package me.merdril.randombattle.battle;

import me.merdril.randombattle.RandomBattle;

import org.getspout.spoutapi.player.SpoutPlayer;

/**
 * @author mark
 */
public class RBPlayer extends RBLivingEntity
{
	RandomBattle	    plugin;
	private SpoutPlayer	player;
	
	/**
	 * <code>public RBPlayer(RandomBattle instance, SpoutPlayer player, Integer[] stats)</code> <br/>
	 * <br/>
	 * Defines a Random Battle player which is a SpoutPlayer with stats and corresponding methods
	 * for manipulating them.
	 * @param instance
	 *            - The RandomBattle instance
	 * @param player
	 *            - The player to represent
	 * @param stats
	 *            - The stats of the player
	 * @throws Exception
	 */
	public RBPlayer(RandomBattle instance, SpoutPlayer player, int[] stat) throws ArrayIndexOutOfBoundsException
	{
		super(stat);
		plugin = instance;
		this.player = player;
	}
	
}
