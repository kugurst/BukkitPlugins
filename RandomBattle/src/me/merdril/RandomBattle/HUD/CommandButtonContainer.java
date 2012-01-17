/**
 * 
 */

package me.merdril.RandomBattle.HUD;

import java.util.ArrayList;

import me.merdril.RandomBattle.RandomBattle;

import org.bukkit.entity.Monster;
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
	 * @param screen
	 * 
	 */
	public CommandButtonContainer(RandomBattle instance, RandomBattlePopupScreen screen,
	        SpoutPlayer player, ArrayList<Monster> monsters)
	{
		super();
		this.plugin = instance;
		this.player = player;
		fight = new FightButton(plugin, screen, player, monsters);
		magic = new MagicButton(plugin, player, monsters);
		item = new ItemButton(plugin, player, monsters);
		run = new RunButton(plugin, player);
		this.addChildren(fight, magic, item, run).setHeight(player.getMainScreen().getHeight() / 5)
		        .setWidth(player.getMainScreen().getWidth() / 7);
		this.setAuto(true);
	}
}
