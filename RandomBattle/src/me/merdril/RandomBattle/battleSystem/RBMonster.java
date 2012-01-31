/**
 * 
 */

package me.merdril.RandomBattle.battleSystem;

import java.util.HashMap;

import me.merdril.RandomBattle.RBUtilities;
import me.merdril.RandomBattle.RandomBattle;

import org.bukkit.entity.Monster;

/**
 * @author mark
 * 
 */
public class RBMonster extends RBLivingEntity
{
	RandomBattle	                 plugin;
	private Monster	                 monster;
	private HashMap<String, Integer>	stats	= new HashMap<String, Integer>();
	
	/**
	 * 
	 */
	public RBMonster(RandomBattle instance, Monster monster, int[] stat)
	{
		this.plugin = instance;
		this.monster = monster;
		if (stat.length != RBUtilities.statNames.length)
			throw new ArrayIndexOutOfBoundsException("The stats must be of correct length!");
		fillStats(stat);
		AI();
	}
	
	public void fillStats(int[] stat)
	{
		for (int i = 0; i < stat.length; i++)
			stats.put(RBUtilities.statNames[i], stat[i]);
	}
	
	private void AI()
	{
		try
		{
			wait();
		}
		catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
