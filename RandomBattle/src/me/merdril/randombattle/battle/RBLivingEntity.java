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
	public enum Stat {
		HP, MP, CHP, CMP, STR, MAG, DEF, MDEF, AGL, ACC, EVA, LUCK, EXP, LEVEL
	}
	
	public enum Effect {
		DEAD, POISONED, SLEEPING, STONE, HASTE, BLIND, SLOW
	}
	
	public int	ADD	   = 0;
	public int	REMOVE	= 1;
	
	/**
	 * <p>
	 * Returns an integer representing the current amount of some status held by this
	 * RBLivingEntity.
	 * </p>
	 * <p>
	 * This is a simplification of creating n-different methods for each status. Each possible
	 * status is contained within the enum Stat that is a member of this class.
	 * </p>
	 * @param stat
	 *            the Stat to query.
	 * @return An int specifying the amount of the Stat this living entity contains.
	 */
	public int getStat(Stat stat);
	
	/**
	 * <p>
	 * Unreservedly changes the value of some Stat to the int provided. Returns the new value of the
	 * assigned stat, as a proof of transaction.
	 * </p>
	 * <p>
	 * Care should be used in assigning values to stats (other than CHP and CMP), considering that
	 * it may affect the balance of a battle.
	 * </p>
	 * @param stat
	 *            The Stat to modify.
	 * @param amount
	 *            An int representing the new amount to set this Stat to.
	 * @return The same int that was passed in, but the implementation should return the int
	 *         referenced by the Stat (which should in turn, have the same value as the int that was
	 *         passed in).
	 */
	public int setStat(Stat stat, int amount);
	
	/**
	 * <p>
	 * Returns an effect for use in damage calculation or turn calculation. Effects should be used,
	 * rather than overriding the value of some stat that is provided by the class.
	 * </p>
	 * @return A List&ltEffect&gt that this RBLivingEntity currently has.
	 */
	public List<Effect> getEffects();
	
	/**
	 * <p>
	 * Modifies the effects of this {@link RBLivingEntity} according to the action specified. Can be
	 * one of two things: ADD or REMOVE. Using ADD also implies to replace/update.
	 * </p>
	 * @param action
	 *            The course of action to take with the specified effects: whether to add them or to
	 *            remove them.
	 * @param effect
	 *            The effects to modify on this {@link RBLivingEntity}.
	 * @return The current {@link List}&lt{@link Effect}&gt that his {@link RBLivingEntity} possess.
	 */
	public List<Effect> setEffect(int action, Effect... effect);
	
	/**
	 * <p>
	 * Returns the {@link List}&lt{@link RBSkill}&gt of this entity.
	 * <p>
	 * @return A {@link List}&lt{@link RBSkill}&gt of the skills this LivingEntity knows. Returns an
	 *         empty list in the case the entity knows none.
	 */
	public List<RBSkill> getSkills();
	
	/**
	 * <p>
	 * Modifies the {@link List}&lt{@link RBSkill}&gt that this entity knows. This method either
	 * adds or removes from the list.
	 * </p>
	 * @param action
	 *            An int representing whether to add or remove the specified skills.
	 * @param skills
	 *            A {@link RBSkill}[] to either add or remove to the list of skills.
	 * @return The current {@link List}&lt{@link RBSkill}&gt that results from this modification.
	 */
	public List<RBSkill> setSkills(int action, RBSkill... skills);
	
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
	 * Modifies the magicks of this {@link RBLivingEntity} according to the action specified. Can be
	 * one of two things: ADD or REMOVE. Using ADD also implies to replace/update.
	 * </p>
	 * @param action
	 *            The course of action to take with the specified effects: whether to add them or to
	 *            remove them.
	 * @param magicks
	 *            The magicks to modify on this {@link RBLivingEntity}.
	 * @return The current {@link List}&lt{@link RBMagic}&gt that his {@link RBLivingEntity}
	 *         possess.
	 */
	public List<RBMagic> setMagicks(int action, RBMagic... magicks);
	
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
	 * Modifies the weaknesses of this {@link RBLivingEntity} according to the action specified. Can
	 * be one of two things: ADD or REMOVE. Using ADD also implies to replace/update.
	 * </p>
	 * @param action
	 *            The course of action to take with the specified effects: whether to add them or to
	 *            remove them.
	 * @param elems
	 *            The {@link RBElem}'s this {@link RBLivingEntity} is weak to. to modify on this
	 *            {@link RBLivingEntity}.
	 * @return The current {@link List}&lt{@link RBElem}&gt that his {@link RBLivingEntity} possess.
	 */
	public List<RBElem> setWeak(RBElem... elems);
	
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
