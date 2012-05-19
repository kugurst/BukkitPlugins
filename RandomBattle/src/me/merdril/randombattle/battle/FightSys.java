/**
 * 
 */

package me.merdril.RandomBattle.battle;

import java.util.ArrayList;

import me.merdril.RandomBattle.RandomBattle;

import org.bukkit.entity.LivingEntity;
import org.getspout.spoutapi.gui.GenericPopup;

/**
 * @author mark
 * 
 */
public class FightSys
{
	RandomBattle	          plugin;
	GenericPopup	          popup;
	ArrayList<RBLivingEntity>	creatures;
	LivingEntity[]	          turnList;
	
	/**
	 * 
	 */
	public FightSys(RandomBattle instance, GenericPopup popup,
	        ArrayList<RBLivingEntity> involvedEntities)
	{
		plugin = instance;
		this.popup = popup;
		creatures = involvedEntities;
		populateTurnList(involvedEntities);
	}
	
	public void populateTurnList(ArrayList<RBLivingEntity> involvedEntities)
	{
		// TODO Auto-generated method stub
		
	}
	
}
