/**
 * 
 */

package me.merdril.RandomBattle;

import org.getspout.spoutapi.material.block.GenericCustomBlock;

/**
 * @author Merdril
 * 
 */
public class RandomBattleBlock extends GenericCustomBlock
{
	RandomBattle	plugin;
	
	/**
	 * 
	 */
	public RandomBattleBlock(RandomBattle instance)
	{
		super(instance, "STOPBLOCK", false);
		this.plugin = instance;
	}
}
