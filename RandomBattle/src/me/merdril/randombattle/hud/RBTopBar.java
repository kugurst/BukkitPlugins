/**
 * 
 */

package me.merdril.randombattle.hud;

import me.merdril.randombattle.RandomBattle;

import org.getspout.spoutapi.gui.GenericLabel;

/**
 * @author mark
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
