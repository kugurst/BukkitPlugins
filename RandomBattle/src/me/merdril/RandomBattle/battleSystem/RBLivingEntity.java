/**
 * 
 */

package me.merdril.RandomBattle.battleSystem;

import java.util.HashMap;

import me.merdril.RandomBattle.RBUtilities;

import org.bukkit.entity.LivingEntity;

/**
 * @author mark
 * 
 */
public class RBLivingEntity
{
	LivingEntity	                 creature;
	private HashMap<String, Integer>	stats	= new HashMap<String, Integer>();
	
	/**
	 * 
	 */
	public RBLivingEntity(LivingEntity entity, int[] stat)
	{
		creature = entity;
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
