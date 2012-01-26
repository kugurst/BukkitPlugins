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
public class RBTopBar extends GenericLabel
{
	RandomBattle	plugin;
	
	/**
	 * 
	 */
	public RBTopBar(RandomBattle instance)
	{
		super("Battle Start!");
		plugin = instance;
	}
	
}
