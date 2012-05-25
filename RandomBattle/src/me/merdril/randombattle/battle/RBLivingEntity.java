/**
 * 
 */

package me.merdril.randombattle.battle;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import org.bukkit.entity.LivingEntity;

/**
 * <p>
 * An interface to encapsulate both Players and Monsters. The turn system is partially blind to the
 * distinction between a human intelligence and an artificial intelligence, but simply requests stat
 * information and move information.
 * </p>
 * <p>
 * This interfaces defines methods that will be visible to the turn system, and it is my hope that
 * these methods will enable the simplest implementation of the desired battle system.
 * </p>
 * @author Merdril
 */
public interface RBLivingEntity
{
	/**
	 * <p>
	 * Returns the current HP of this entity.
	 * </p>
	 * @return An int representing the current HP of this LivingEntity.
	 */
	public int getCurHP();
	
	/**
	 * <p>
	 * Returns the current MP of this entity.
	 * </p>
	 * @return An int representing the current MP of this LivingEntity.
	 */
	public int getCurMP();
	
	/**
	 * <p>
	 * Returns the maximum HP of this entity.
	 * </p>
	 * @return An int representing the maximum HP of this LivingEntity.
	 */
	public int getMaxHP();
	
	/**
	 * <p>
	 * Returns the maximum MP of this entity.
	 * </p>
	 * @return An int representing the maximum MP of this LivingEntity. Bounded between 0 and 999.
	 */
	public int getMaxMP();
	
	/**
	 * <p>
	 * Returns the base strength of this entity.
	 * </p>
	 * @return An int representing the strength of this LivingEntity. Bounded between 0 and 255.
	 */
	public int getSTR();
	
	/**
	 * <p>
	 * Returns the base magic of this entity.
	 * </p>
	 * @return An int representing the magic of this LivingEntity. Bounded between 0 and 255.
	 */
	public int getMAG();
	
	/**
	 * <p>
	 * Returns the base defense of this entity.
	 * </p>
	 * @return An int representing the defense of this LivingEntity. Bounded between 0 and 255.
	 */
	public int getDEF();
	
	/**
	 * <p>
	 * Returns the base magic defense of this entity.
	 * </p>
	 * @return An int representing the magic defense of this LivingEntity. Bounded between 0 and
	 *         255.
	 */
	public int getMDEF();
	
	/**
	 * <p>
	 * Returns the base agility of this entity.
	 * </p>
	 * @return An int representing the agility of this LivingEntity. Bounded between 0 and 255.
	 */
	public int getAGL();
	
	/**
	 * <p>
	 * Returns the base accuracy of this entity.
	 * </p>
	 * @return An int representing the accuracy of this LivingEntity. Bounded between 0 and 100.
	 */
	public int getACC();
	
	/**
	 * <p>
	 * Returns the base evasion of this entity.
	 * </p>
	 * @return An int representing the evasion of this LivingEntity. Bounded between 0 and 100.
	 */
	public int getEVA();
	
	/**
	 * <p>
	 * Returns the base luck of this entity.
	 * </p>
	 * @return An int representing the luck of this LivingEntity. Bounded between 0 and 255.
	 */
	public int getLUCK();
	
	/**
	 * <p>
	 * Returns the current/return exp of this entity.
	 * </p>
	 * @return An int representing the exp of this LivingEntity.
	 */
	public int getEXP();
	
	/**
	 * <p>
	 * Returns the level of this entity. It is only useful for players.
	 * </p>
	 * @return An int representing the level of this LivingEntity. Bounded between 0 and 50.
	 */
	public int getLevel();
	
	/**
	 * <p>
	 * Returns the List&ltRBSkill&gt of this entity.
	 * <p>
	 * @return A List&ltRBSkill&gt of the skills this LivingEntity knows. Returns an empty list in
	 *         the case the entity knows none.
	 */
	public List<RBSkill> getSkills();
	
	/**
	 * <p>
	 * Returns the List&ltRBMagic&gt of this entity.
	 * <p>
	 * @return A List&ltRBMagic&gt of the magicks this LivingEntity knows. Returns an empty list in
	 *         the case the entity knows none.
	 */
	public List<RBMagic> getMagicks();
	
	/**
	 * <p>
	 * Returns the List&ltRBElem&gt of this entity.
	 * <p>
	 * @return A List&ltRBElem&gt of the elements this LivingEntity is weak to. Returns an empty
	 *         list in the case it is weak to nothing.
	 */
	public List<RBElem> getWeak();
	
	/**
	 * <p>
	 * Asks this LivingEntity to decide on a next move based on its current status.
	 * <p>
	 * @param lock
	 *            The lock to pause execution of the turn system thread until this LivingEntity has
	 *            made a move.
	 * @return A RBMove containing the target entity(ies) and the RBAttack to execute.
	 */
	public RBMove getMove(ReentrantLock lock);
	
	/**
	 * <p>
	 * Returns the LivingEntity that this class encapsulates.
	 * <p>
	 * @return A LivingEntity which is affected by the actions of this class.
	 */
	public LivingEntity getEntity();
	
	@Override
	public boolean equals(Object obj);
	
	@Override
	public String toString();
	
	@Override
	public int hashCode();
}
