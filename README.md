# Vanilla Enchant Additions
A set of tweaks/mechanical changes to vanilla enchantments. 1.18.2 only, does not require Fabric API.
___
### List of tweaks:<br>
Axe:
- *extendedAxeEnchants* - allows sword enchants to be applied to axes in an anvil. [boolean, default `true`]

Boots:
- *soulSpeedBreakChance* - chance per tick of soul speed using durability. [float, default `0.04`]

Bow:
- *extendedBowEnchants* - allows Piercing to be applied to bows (armor piercing effect instead of mob piercing) [boolean value, default `true`]
- *infinityForAll* - Infinity applies to all types of arrows. [boolean value, default `true`]

Crossbow:
- *crossbowInfinity* - Infinity applies to crossbows. [boolean value, default `true`]
- *piercingDmgBoost* - slightly increases damage per level. [double, default `0.25`]
- *multishotBurstAlt* - while crouching: burst fires half the arrows, ignores invincibility frames. [boolean, default `true`]
- *multishotBurstDelay* - fires arrows as a burst instead of at once, delay value configurable. 0 disables. [int value, default `0`]
- *multishotCount* - additional arrows to be fired by a Multishot crossbow. 0 disables.[int value, default `0`]

Elytra:
- *extendedElytraEnchants* - elytra accepts chestplate enchantments [boolean value, default `true`]

Trident:
- *channelingAlways* - Channeling functions outside of rain. [boolean value, default `true`]
- *extendedTridentEnchants* - allows sword enchants and piercing to be applied to tridents in an anvil. [boolean, default `true`]
- *riptideAlways* - Riptide functions outside of rain or water. Due to technical limitations, you cannot hold another item in the other hand to use this functionality. [boolean value, default `true`]
- *extendedImpaling* - damage bonus applies to all mobs in water/rain and drowned. [boolean value, default `true`]
- *voidLoyalty* - automatically returns from the void. [boolean value, default `true`]

Misc/multiple enchants:
- *infinityCompatibleWithMending* - Self explanatory. [boolean value, default `true`]
- *piercingIgnoreArmorPercent* - percentage of armor to ignore per level of piercing for Bows and Tridents. [double, default `0.05`]
___
### Configuration
Configuration can be done in-game via the `vea-config` command or by editing `vanilla_enchant_additions.conf` in the world save folder.</br>
`/vea-config get [optional setting argument]` - gets the value of a setting, or all settings if not provided. Usable by all players by default.</br>
`/vea-config set [setting] [value]` - sets the value of a setting and updates the file. Fails if value type does not match.</br>
`/vea-config reload` - reloads settings if file was directly edited

