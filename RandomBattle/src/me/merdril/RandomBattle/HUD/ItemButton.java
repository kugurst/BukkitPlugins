/**
 * 
 */

package me.merdril.RandomBattle.HUD;

import java.util.ArrayList;

import me.merdril.RandomBattle.RandomBattle;

import org.bukkit.entity.Monster;
import org.getspout.spoutapi.event.screen.ButtonClickEvent;
import org.getspout.spoutapi.gui.GenericButton;
import org.getspout.spoutapi.player.SpoutPlayer;

/**
 * @author Merdril
 * 
 */
public class ItemButton extends GenericButton
{
	RandomBattle	plugin;
	
	/**
	 * @param monsters
	 * 
	 */
	public ItemButton(RandomBattle instance, SpoutPlayer player, ArrayList<Monster> monsters)
	{
		super();
		this.plugin = instance;
		this.setText("Item");
	}
	
	@Override
	public void onButtonClick(ButtonClickEvent event)
	{
		plugin.getServer().getConsoleSender()
		        .sendMessage("[RandomBattle] " + this.getText() + " was clicked");
	}
	
}
