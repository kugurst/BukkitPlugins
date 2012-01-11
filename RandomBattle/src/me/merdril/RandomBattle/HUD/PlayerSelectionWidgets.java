/**
 * 
 */

package me.merdril.RandomBattle.HUD;

import me.merdril.RandomBattle.RandomBattle;

import org.getspout.spoutapi.gui.GenericButton;
import org.getspout.spoutapi.gui.GenericContainer;
import org.getspout.spoutapi.gui.GenericPopup;
import org.getspout.spoutapi.gui.WidgetAnchor;
import org.getspout.spoutapi.player.SpoutPlayer;

/**
 * @author Merdril
 * 
 */
public class PlayerSelectionWidgets
{
	RandomBattle	     plugin;
	private GenericPopup	screen;
	private SpoutPlayer	 player;
	private int	         screenHeight;
	private int	         screenWidth;
	
	/**
	 * 
	 */
	public PlayerSelectionWidgets(RandomBattle instance, SpoutPlayer sPlayer)
	{
		plugin = instance;
		screen = new GenericPopup();
		screen = (GenericPopup) screen.setTransparent(false);
		player = sPlayer;
		screenHeight = sPlayer.getMainScreen().getHeight();
		screenWidth = sPlayer.getMainScreen().getWidth();
		screen.setHeight(screenHeight);
		screen.setWidth(screenWidth);
		player.getMainScreen().closePopup();
		screen.attachWidgets(plugin, new CommandButtonContainer());
		sPlayer.getMainScreen().attachPopupScreen(screen);
	}
	
	class CommandButtonContainer extends GenericContainer
	{
		public CommandButtonContainer()
		{
			this.addChildren(new FightButton(), new MagicButton(), new ItemButton(),
			        new RunButton());
			this.setHeight(screenHeight / 5);
			this.setWidth(screenWidth / 5);
			this.setAuto(true);
			this.setAnchor(WidgetAnchor.BOTTOM_RIGHT);
		}
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
