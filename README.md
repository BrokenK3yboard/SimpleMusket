# Simple Musket
This mod adds a musket and a few unique ammo types to Minecraft.
* Holding rightmouse while unloaded will load a bullet from the inventory, similarly to the crossbow.
* Holding rightmouse when loaded aims the musket, and releasing fires the bullet. Aim time determines the accuracy.
* Reloading takes 1.5 seconds, and aiming for 1.5 seconds is enough for perfectly accurate shots by default.
* Firing produces a visible cloud of smoke and the shot can be heard from far away, giving away the user's position.

There are also unique **Gunslinger** enemies added to the game which are armed with muskets.
* Spawns near outposts and will appear as part of raid waves with the same frequency as Evokers.
* Being expert shooters, they have an attack range of 24 and do not cause friendly fire damage.
* They retreat when approached and use a sawn-off to protect themselves in close quarters.

## Ammunition
There are three cartridge types that can be fired from muskets or dispensers. Cartridges are deflected by breezes and iron golems resist 75% of the damage.
* **Standard cartridge**: Deals 16 damage and ignores 10% of the target's armor efficacy.
* **Hellfire cartridge**: Deals 25% more damage than standard cartridges and decreases the target's armor by 25% for 30 seconds. Found in bastion chests or by trading with piglins.
* **Enchanted cartridge**: Has 25% higher velocity than standard cartridges and boosts musket enchantment effects. Crafted with an enchanted book and an equal number of gold nuggets and cartridges in a crafting table. Also sold by weaponsmith villagers.

## Enchantments
This mod adds three musket enchantments. All enchantments are mutually exclusive with each other.
* **Firepower (I-V)**: Increases armor piercing of fired bullets by 10% per level. Enchanted cartridges increase this amount by 10%.
* **Longshot (I-II)**: Amplifies damage against distant targets. (15-60% bonus at 16-48 blocks, 25-100% at level 2). Enchanted cartridges multiply this amount by 1.2.
* **Repeating**: Allows the musket to fire twice per reload. This amount is increased by 1 when reloading after a kill, and enchanted cartridges double the bonus.

## Configuration
* Reload and aim time are configurable between 20-80 ticks.
* Base cartridge damage is configurable between 16-24 damage.
* The damage multiplier of musket wielding mobs is configurable.
* Crafting recipes and availability of ammunition types are configurable.

## Integration
* If **Consecration** is installed, enchanted/hex cartridges remove undead protection. 1.20.1/1.21.1 Forge/Neoforge only.
* If **Useful Spyglass** is installed, Muskets can be used with the Precision enchantment for increased damage and zoom while aiming.
* If **Enchantment descriptions** (or equivalent mod) is installed, the details of each enchantment will be displayed in-game.

## Credits:
Cartridge textures, sounds, smoke particle code, projectile velocity sync code from ewewukek's musket mod: https://github.com/ewewukek/mc-musketmod<br>
Reload animations inspired by WinterComputer's resource pack: https://modrinth.com/resourcepack/muskets-and-sabres<br>