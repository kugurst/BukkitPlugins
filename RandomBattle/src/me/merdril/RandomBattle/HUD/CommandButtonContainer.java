/**
 * 
 */

package me.merdril.RandomBattle.HUD;

import me.merdril.RandomBattle.RandomBattle;

import org.getspout.spoutapi.gui.GenericContainer;
import org.getspout.spoutapi.player.SpoutPlayer;

/**
 * @author mark
 * 
 */
public class CommandButtonContainer extends GenericContainer
{
	RandomBattle	      plugin;
	private SpoutPlayer	  player;
	protected FightButton	fight;
	protected MagicButton	magic;
	protected ItemButton	item;
	protected RunButton	  run;
	
	/**
	 * 
	 */
	public CommandButtonContainer(RandomBattle instance, SpoutPlayer player)
	{
		super();
		this.plugin = instance;
		this.player = player;
		fight = new FightButton(plugin, player);
		magic = new MagicButton(plugin, player);
		item = new ItemButton(plugin, player);
		run = new RunButton(plugin, player);
		this.addChildren(fight, magic, item, run).setHeight(player.getMainScreen().getHeight() / 5)
		        .setWidth(player.getMainScreen().getWidth() / 7);
		this.setAuto(true);
	}
}
