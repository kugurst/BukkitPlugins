/**
 * 
 */

package me.merdril.RandomBattle.HUD;

import me.merdril.RandomBattle.RandomBattle;

import org.getspout.spoutapi.gui.GenericLabel;

/**
 * @author mark
 * 
 */
public class RandomBattleTopBar extends GenericLabel
{
	RandomBattle	plugin;
	
	/**
	 * 
	 */
	public RandomBattleTopBar(RandomBattle instance)
	{
		super("Battle Start!");
		plugin = instance;
	}
	
}
