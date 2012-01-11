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
	RandomBattle	               plugin;
	private GenericPopup	       screen;
	private SpoutPlayer	           player;
	private int	                   screenHeight;
	private int	                   screenWidth;
	private CommandButtonContainer	buttons;
	
	/**
	 * 
	 */
	public PlayerSelectionWidgets(RandomBattle instance, SpoutPlayer sPlayer)
	{
		plugin = instance;
		screen = new GenericPopup();
		screen.setTransparent(true);
		player = sPlayer;
		screenHeight = sPlayer.getMainScreen().getHeight();
		screenWidth = sPlayer.getMainScreen().getWidth();
		screen.setHeight(screenHeight);
		screen.setWidth(screenWidth);
		player.getMainScreen().closePopup();
		buttons = new CommandButtonContainer();
		screen.attachWidgets(plugin, buttons);
		sPlayer.getMainScreen().attachPopupScreen(screen);
	}
	
	class CommandButtonContainer extends GenericContainer
	{
		public CommandButtonContainer()
		{
			this.addChildren(new GenericButton("Fight"), new GenericButton("Magic"),
			        new GenericButton("Item"), new GenericButton("Run"));
			this.setHeight(screenHeight / 5);
			this.setWidth(screenWidth / 5);
			this.setAnchor(WidgetAnchor.BOTTOM_RIGHT);
			this.shiftXPos(-this.getWidth());
			this.shiftYPos(-this.getHeight());
		}
	}
}
