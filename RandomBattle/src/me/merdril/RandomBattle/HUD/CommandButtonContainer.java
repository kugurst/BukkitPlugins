/**
 * 
 */

package me.merdril.RandomBattle.HUD;

import me.merdril.RandomBattle.RandomBattle;

import org.getspout.spoutapi.gui.GenericButton;
import org.getspout.spoutapi.gui.GenericContainer;

/**
 * @author mark
 * 
 */
public class CommandButtonContainer extends GenericContainer
{
	RandomBattle	                plugin;
	private RandomBattlePopupScreen	popup;
	
	/**
	 * 
	 */
	public CommandButtonContainer(RandomBattle instance, RandomBattlePopupScreen popup)
	{
		this.plugin = instance;
		this.popup = popup;
		this.setHeight(popup.getHeight() / 5);
		this.setWidth(popup.getWidth() / 5);
		this.addChildren(new GenericButton("Fight"), new GenericButton("Magic"), new GenericButton(
		        "Item"), new GenericButton("Run"));
		this.setAuto(true);
	}
	
}
