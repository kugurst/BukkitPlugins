/**
 * 
 */

package me.merdril.RandomBattle.HUD;

import me.merdril.RandomBattle.RandomBattle;

import org.getspout.spoutapi.gui.GenericContainer;

/**
 * @author mark
 * 
 */
public class CommandButtonContainer extends GenericContainer
{
	RandomBattle	                plugin;
	private RandomBattlePopupScreen	popup;
	protected FightButton	        fight;
	protected MagicButton	        magic;
	protected ItemButton	        item;
	protected RunButton	            run;
	
	/**
	 * 
	 */
	public CommandButtonContainer(RandomBattle instance, RandomBattlePopupScreen popup)
	{
		this.plugin = instance;
		this.popup = popup;
		fight = new FightButton(plugin);
		magic = new MagicButton(plugin);
		item = new ItemButton(plugin);
		run = new RunButton(plugin);
		this.setHeight(popup.getHeight() / 5);
		this.setWidth(popup.getWidth() / 5);
		this.addChildren(fight, magic, item, run);
		this.setAuto(true);
	}
}
