/**
 * 
 */

package me.merdril.RandomBattle.HUD;

import me.merdril.RandomBattle.RandomBattle;

import org.getspout.spoutapi.event.screen.ButtonClickEvent;
import org.getspout.spoutapi.gui.GenericButton;

/**
 * @author Merdril
 * 
 */
public class MagicButton extends GenericButton
{
	RandomBattle	plugin;
	
	/**
	 * 
	 */
	public MagicButton(RandomBattle instance)
	{
		this.plugin = instance;
		this.setText("Magic");
	}
	
	@Override
	public void onButtonClick(ButtonClickEvent event)
	{
		plugin.getServer().getConsoleSender()
		        .sendMessage("[RandomBattle] " + this.getText() + "was clicked");
	}
	
}
