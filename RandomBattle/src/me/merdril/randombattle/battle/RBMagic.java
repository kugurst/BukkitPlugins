
package me.merdril.randombattle.battle;

import java.util.HashMap;

public enum RBMagic {
	TELEPORT, FIREBALL, KILL, BIO, FOUL_BREATH, FIRE, BLIZZARD, THUNDER, FIRA, BLIZZARA, THUNDARA, FIRAGA, BLIZZAGA,
	THUNDAGA, AERO, AERORA, AEROGA, GRAVTIY, GRAVIGA, DEMI, WATER, WATERA, WATERGA;
	public static final HashMap<String, RBMagic>	magicMap	= new HashMap<String, RBMagic>();
	static {
		for (RBMagic magic : RBMagic.values())
			magicMap.put(magic.toString().toLowerCase(), magic);
	}
}
