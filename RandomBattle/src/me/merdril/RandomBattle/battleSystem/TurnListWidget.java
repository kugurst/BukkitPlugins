/**
 * 
 */

package me.merdril.RandomBattle.battleSystem;

import java.util.ArrayList;

import me.merdril.RandomBattle.RandomBattle;

import org.bukkit.entity.LivingEntity;
import org.getspout.spoutapi.gui.GenericListWidget;
import org.getspout.spoutapi.gui.ListWidgetItem;

/**
 * @author mark
 * 
 */
public class TurnListWidget extends GenericListWidget
{
	RandomBattle	        plugin;
	ArrayList<LivingEntity>	entities;
	
	/**
	 * 
	 */
	public TurnListWidget(RandomBattle instance, ArrayList<LivingEntity> entity)
	{
		super();
		plugin = instance;
		entities = entity;
		addFaces();
	}
	
	private void addFaces()
	{
		for (LivingEntity creature : entities)
		{
			this.addItem(new ListWidgetItem(creature.toString(), "Health: " + creature.getHealth()));
		}
	}
}
