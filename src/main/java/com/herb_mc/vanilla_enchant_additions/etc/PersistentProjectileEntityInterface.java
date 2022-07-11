package com.herb_mc.vanilla_enchant_additions.etc;

public interface PersistentProjectileEntityInterface {

    void setIgnoreInvulnerability(boolean t);

    void setKnockback(boolean t);

    boolean getKnockback();

    void setIgnoresArmor(int i);

    int getArmorPierce();

}
