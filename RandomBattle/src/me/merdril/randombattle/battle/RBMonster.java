/**
 * 
 */

package me.merdril.randombattle.battle;

import me.merdril.randombattle.RandomBattle;

import org.bukkit.entity.Monster;

/**
 * @author mark
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
		try {
			wait();
		}
		catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
