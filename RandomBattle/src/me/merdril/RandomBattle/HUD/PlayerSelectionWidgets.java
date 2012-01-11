/**
 * 
 */

package me.merdril.RandomBattle.HUD;

import me.merdril.RandomBattle.RandomBattle;

import org.getspout.spoutapi.gui.GenericButton;
import org.getspout.spoutapi.gui.InGameScreen;
import org.getspout.spoutapi.player.SpoutPlayer;

/**
 * @author Merdril
 * 
 */
public class PlayerSelectionWidgets
{
	RandomBattle	     plugin;
	private InGameScreen	screen;
	private SpoutPlayer	 player;
	
	/**
	 * 
	 */
	public PlayerSelectionWidgets(RandomBattle instance, SpoutPlayer player, InGameScreen screen)
	{
		this.plugin = instance;
		this.screen = screen;
		this.player = player;
		screen.attachWidgets(plugin, new FightButton(), new MagicButton(), new ItemButton(),
		        new RunButton());
	}
	
	class FightButton extends GenericButton
	{
		public FightButton()
		{
			this.setText("Fight");
		}
	}
	
	class MagicButton extends GenericButton
	{
		public MagicButton()
		{
			this.setText("Magic");
		}
	}
	
	class ItemButton extends GenericButton
	{
		public ItemButton()
		{
			this.setText("Items");
		}
	}
	
	class RunButton extends GenericButton
	{
		public RunButton()
		{
			this.setText("Run");
		}
	}
}
