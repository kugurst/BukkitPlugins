/**
 * 
 */

package me.merdril.RandomBattle.battleSystem;

import me.merdril.RandomBattle.RandomBattle;

import org.bukkit.entity.Monster;

/**
 * @author mark
 * 
 */
public class RBMonster extends RBLivingEntity
{
	RandomBattle	plugin;
	private Monster	monster;
	
	/**
	 * 
	 */
	public RBMonster(RandomBattle instance, Monster monster, int[] stat)
	{
		super(stat);
		this.plugin = instance;
		this.monster = monster;
		AI();
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
