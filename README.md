# Vanilla Enchant Additions
A set of tweaks/mechanical changes to vanilla enchantments.
___
### List of tweaks:<br>
Bow:
- *infinityForAll* - Infinity applies to all types of arrows

Crossbow:
- *piercingDmgBoost* - slightly increases damage per level (damage roll range increased, 7-16 at Piercing IV) [float, default `0.2`]

Trident:
- *channelingAlways* - Channeling functions outside of rain [boolean value, default `true`]
- *riptideAlways* - Riptide functions outside of rain or water [boolean value, default `true`]
- *extendedImpaling* - damage bonus applies to all mobs in water/rain and drowned [boolean value, default `true`]
- *voidLoyalty* - automatically returns from the void [boolean value, default `true`]
- ___
### Configuration
Configuration can be done in-game via the `vea-config` command or by editing `vanilla_enchant_additions.conf` in the world save folder.</br>
`/vea-config get [optional setting argument]` - gets the value of a setting, or all settings if not provided. Usable by all players by default.</br>
`/vea-config set [setting] [value]` - sets the value of a setting and updates the file. Fails if value type does not match.</br>
`/vea-config reload` - reloads settings if file was directly edited

