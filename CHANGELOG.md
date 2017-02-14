![{Soul}Ascension](https://github.com/kenofnz/Ascension/raw/master/Ascension/resources/sprites/ui/menu/title.png)

![Game Updates](https://github.com/K-Games/AscensionInfo/raw/master/images/updates.png)

## Version 0.23.2

### Client Changes
* Fixed rare bug where Adrenaline particle would cause everything to render transparently.
* New key bind settings default to "Not Assigned".
* Added Scoreboard - Existing key binds will not have open Scoreboard binded.
* Players now have colour associated with them. 
	* Scoreboard and Kill notifications will have matching colours for quick readability.
	* Player colour is shown next to player's name.
* Improved player name visibilty with a dark background.
* Ingame Damage numbers are now formatted for easier readability.
* Ingame Damage number position is now relative to screen space instead of relative to damage location.
* Update some skill descriptions.

### Server Changes
* Fixed server not sending info to Hub when unable to retrieve country/area name.
* Added Win Condition
	* Default is 5 minute match, first to 30 kills to win.
	* If a match ends by timeout, player with highest kill count is considered the winner.
* Added impassable platforms - Platforms can be flagged as solid to prevent players from moving through it.

---

## Version 0.23.1

### Gameplay Changes

#### Skill Changes

#### Stats
* Damage
	* Damage formula changed to have lower variance.
		* Average damage remains the same.

##### Sword
* Blade Flurry
	* Damage increased from 75% + 3% per level to 85% + 3% per level.
	* Level 30 Bonus - HP restored increased from 0.25% per hit to 0.75% per hit.
* Rend
	* Damage reduced from 100% + 6% per level to 100% + 5% per level.

##### Utility
* Fortify renamed to Adrenaline
	* Increases movement speed by 60% + 0.5% per level.
	* Increased damage reduction from 1% + 0.5% per level to 5% + 0.7% per level.

### Client Changes
* Ingame Damage numbers now distinguishes Critical Hits received with larger font and "!" - the same format as dealing a Critical Hit.
* Ingame Damage numbers colours have been changed for visual clarity.
* Ingame Damage numbers movement path changed from vertical to falling curve.
* Fixed player jitter when there are a lot of players on screen.
* Fixed particle keys not being cleaned up correctly.
* Fixed minor memory leak with Equipment Icons.
* Screen shakes adjusted.
* New particle effect when damage is dealt.
* Separated base and bonus Primary Stats data sent to server.

### Server Changes

---

## Version 0.23.0

### Gameplay Changes
* New mechanic - Hyper Stance - Certain Skills grants players with Hyper Stance, preventing and removing Knockback and Stun debuffs.
* Fixed Bombardment particle effect not matching actual duration.
* Players are now drawn on top on their respective screens.
* Fixed rare bug where screen would translate incorrectly and become stuck.

#### Stats
* Stat Points per Level increased from 7 to 15.
* Armor
	* Damage Reduction from Armor has been changed from Armor/(Armor + 150) to Armor/(Armor + 175)
		* This means players take more damage overall.
* Regen(HP/Sec)
	* Regen increased from 1.5 per Spirit to 60 + 3.4 per Spirit.

#### Equipment Stats
* Regen(HP/Sec)
	* New Equip roll increased from (level + roll(3)) * 5 to (level + roll(3)) * 10
		* Existing Equipments are not updated.

#### Infusion
* Upgrades renamed to Infusion.
* Tome of Enhancements renamed to Soul Stones.
* Equipment can now be enhanced with up to 3 Soul Stones.
	* Using more than 1 Soul Stone will use the highest level Soul Stone as the base level.
	* Each additional Soul Stone adds a level to the base.
* Rescaled upgrade bonus Power, Defense, and Spirit stats from 4% + 0.75 per upgrade to a flat 1.25 per upgrade.
* Reduced upgrade Armor bonus from 24 to 18 per upgrade.
* Increased upgrade HP/Sec bonus from 8 to 10 per upgrade.
* Increased upgrade HP/Sec internal multiplier from 1 + Rarity Multiplier to 2 + Rarity Multiplier
* Increased Critical Hit Chance internal multiplier factor - The internal multiplier now provides twice as much.
* Upgrade Power, Defense, Spirit, Armor and HP/Sec bonuses are now multiplied by the internal multipler.
	* These changes means that lower level equipment with extremely high amounts of upgrades are no longer significantly better than higher level equipments with upgrades.
	* Lower level equipment will now achieve the same stats as higher level equipments with equivalent amount of upgrades.

| Level 1 Equip | Level 50 Equivalent | Level 99 Equivalent | Level 100 Equivalent |
| --- | --- | --- | --- |
| Lvl 1 +99 Upgrades | Lvl 50 +50 Upgrades | Lvl 99 +1 Upgrade | Lvl 100 No Upgrades |
| Lvl 1 +100 Upgrades | Lvl 50 +51 Upgrades | Lvl 99 +2 Upgrades | Lvl 100 +1 Upgrade |
| Lvl 1 +101 Upgrades | Lvl 50 +52 Upgrades | Lvl 99 +3 Upgrades | Lvl 100 +2 Upgrades |
| Lvl 1 +102 Upgrades | Lvl 50 +53 Upgrades | Lvl 99 +5 Upgrades | Lvl 100 +3 Upgrades |
| Lvl 1 +103 Upgrades | Lvl 50 +54 Upgrades | Lvl 99 +6 Upgrades | Lvl 100 +4 Upgrades |

* Divine Equipment Tier removed - Is not possible to attain with upgrade changes.

#### Skill Changes

##### Sword
* Rend
	* Damage increased from 100% + 4% per level to 100% + 6% per level.
* Aggression
	* Max Level Bonus - Grants Hyper Stance.
* Vorpal Strike
	* No longer removes debuffs.
	* Grants Hyper Stance.

##### Bow
* Cannon Fire
	* Max Level Bonus - Grants Hyper Stance.

### Client Changes
* Exposed CUSTOM_VALUES to allow fetching custom data of Skills.
* Fixed main hand and offhand equip slot string.

### Server Changes
* Packets are now sent in large batches and asynchronously.
* Improved network performance allows rooms to have up to 128 players.

---

## Version 0.22.1

### Gameplay Changes

#### Sword
* Blade Flurry
	* Removed knockback.
* Rend
	* Removed knockback.
* Firebrand
	* Cooldown decreased from 20 to 12 seconds.
* Aggression
	* Cooldown decreased from 25 to 18 seconds.
* Vorpal Strike
	* Cooldown decreased from 14 to 8 seconds.
	* Damage decreased from 450 + 20% per level to 400 + 20% per level.
* Phantom Reaper
	* Cooldown decreased from 20 to 16 seconds.
	* Damage decreased from 800 + 20% per level to 650 + 20% per level.
	
#### Bow
* Cannon Fire
	* Cooldown decreased from 16 to 8 seconds.
* Bombardment
	* Cooldown decreased from 20 to 12 seconds.
	* Duration decreased from 5 to 3.5 seconds.
* Vortex Bolts
	* Cooldown decreased from 17 to 11 seconds.
	* Knockback distance reduced.
* Frost Bind
	* Cooldown decreased from 22 to 12 seconds.
	
#### Shield
* Hellion Roar
	* Cooldown decreased from 20 to 13 seconds.
* Overwhelm
	* Cooldown decreased from 17 to 9 seconds.
* Reflect Damage
	* Cooldown decreased from 15 to 9 seconds.
* Magnetize
	* Cooldown decreased from 15 to 11 seconds.
	* Level 30 Bonus - Decreased damage from 3x to 2x.

#### Utility
* Dash
	* Cooldown decreased from 13 to 6.5 seconds.
* Fortify
	* Cooldown reduced from 19 to 12 seconds.

#### Passive
* Resistance
	* Base cooldown increased from 35 to 40 seconds.
	
### Client Changes
* Fixed Max Skill button working incorrectly.

### Server Changes

---

## Version 0.22.0

### Gameplay Changes
* Equipment icons now show its Tier colour.
* Equipment Tier colours updated.
* Skills now have a `Max` button to allocates maximum amount of points into the Skill.
* Equipment Rarity drop rate adjusted. Divine rarity remains only achievable via Upgrades.

| Tier | Drop Rate |
| --- | --- |
| Common   | ~51.6% |
| Uncommon | ~20.7% |
| Rare | ~15.5% |
| Runic | ~5.2 |
| Legendary | ~5% |
| Mystic | ~2% |
| Divine | 0% |

* EXP is now split between every player that dealt damage to the dying player in proportion to damage dealt. The killer receives a 10% EXP bonus.
* Notifications have been improved.
* Added Player Kill notifications.
* Critical hit numbers are now larger.

### Client Changes
* Minor Server List browser performance improvement.
* Added a Max button in Skill screen to add maximum about of possible Skill Points into a Skill.
* Loading ingame item sprites on a different thread to avoid frame freezes.
* Added a check for game updates when going through the Title screen.
* Fixed a rare crash caused by Phantom Reaper particles.
* Improve Sound Module performance and consistency.

### Server Changes
* Fixed a room not returning a player key if a player does not fully connect.

---

## Version 0.21.0

### Gameplay Changes
* Movement speed reduced from 4.5 to 3.8.
* Lowered HP per Defense from 200 to 170.
* Allow facing direciton to be changed while knocked back.
* A new Server List browser.

### Skill Changes

#### Sword
* Blade Flurry
	* Added knockback.
* Rend
	* Added knockback.
* Firebrand
	* Increased knockback from 30 to 150.
* Aggression
	* Increased knockback 30 to 300.
* Vorpal Strike
	* Reworked - Dash a short distance and strike enemies.
* Phantom Reaper
	* Fixed not being at a valid Y position if Phantom Reaper ends inside a platform.
 
#### Bow
* Arc Shot
	* Increased knockback from 5 to 40.
* Rapid Fire
	* Increased knockback from 15 to 30.
* Vortex Bolts
	* Level 30 Bonus - Increased buff duration from 4 to 7 seconds.
	* Increased knockback 5 to 12.5.

#### Shield
* Overwhelm
	* Increased knockback 120 to 600.
* Magnetize
	* Added knockback.

#### Utility
* Fortify
	* Cooldown reduced from 24 to 19 seconds.

#### Passive
* Defender Mastery
	* Decreased Damage Reduction from 5% + 0.5% per Lvl to 4% + 0.3% per Lvl
* Keen Eye
	* Increased Critical Hit Chance from 1% + 0.3% per Lvl to 5% + 0.3% per Lvl
* Vital Hit
	* Increased Critical Hit Damage from 10% + 2% per Lvl to 25% + 2% per Lvl
 
### Client Changes
* Fixed unable to draw ingame equips without offsets set in its item data.
* Fixed sword attack animation having 1 extra frame.
* Fixed EXP bar not filling more than once when leveling up more than once.
* Skill UI values are read from data files.
* Tome of Enhancement now shows Tome's level its the icon.
* Various performance improvements.
* Can now start up a server by using the `-server` option.
* Disable launching client with `-noclient` option
* Skill UI values read from data files to sync with server data.
* Various performance improvements.
* Added UDP connection mode. Connections now use UDP by default. Use `-tcpmode` option to use TCP mode.
* Can retrieve a list of avaliable servers from a remote Hub. Use `-hubaddress` and `-hubport` to get server list from a specific Hub.

### Server Changes
* Improve netcode for player position.
* Uses Skill data values from data files. Data values no longer hard coded and can be modified in data files.
* Options are now `-gui` and `-default`
* UDP socket is now used by default. Use `udpmode` config property to use UDP/TCP mode.
* Removed outdated batch packet sending.
* Sends server information to a central Hub for clients to retrieve. `hubconnect`, `hubport` and `hubaddress` configuration option can be set to send to specific hub.

---

## Version 0.20.2

### Gameplay Changes
* Lowered Maximum Armor roll for new Rings.
* Knockback no longer disables Skill casting.
* Firebrand - Range increased from 170 to 280
* Aggression - Buff duration decreased from 10 seconds to 5 seconds.
* Bombardment - Damage instance delay decreased from 0.2 to 0.1 - Double number of instances. Same total damage.
* Bombardment - Players hit will suffer a minor knockback.
* Fixed Bombardment damage being too low.
* Magnetize - Damage is now dealt at the end.
* Shield Skill Removed - Iron Fortress
* New Shield Skill - Hellion Roar - Send enemies flying with a ferocious roar.

### Client Changes
* New skill sprites
* More responsive movement when tapping movement keys.
* Dynamic Skill tooltip sizing to easily add and modify new Skills.
* Fixed and updated Weapon Inventory offhand hint rendering too many times.
* Added Support for upscaling.

### Server Changes
* Screen shake duration and intensity can be now adjusted.
* Fixed rare case where players would get hit by projectiles at death location when respawning.

---

## Version 0.20.1

### Gameplay Changes
* Added Emotes. Assign a key to use the Emotes in the Keybind menu.
* Equipment Upgrades now add an additional flat 0.75 stats to existing Primary Stats(Power, Defense, Spirit) per upgrade.
* Map Update - Removed the far left and right platforms on the highest level.
* Fixed Phantom Slash not doing correct damage.

### Client Changes
* Added keybinds for Emotes.
* Added sending and handling received Emote action.
* Enabled NUMPAD 0 to 9 as bindable keys.
* Added Splash screen

### Server Changes
* Added broadcast received Emote action.
* Logs no longer gets seperated into folders. All logs append to `ErrorLog.log` and `DataLog.log` in `logs` folder.
* Added support for non-flat platforms.
* Abstracted Projectiles for higher level implementations.
* Removed unused Mob classes.

---

## Version 0.20.0

### Gameplay Changes
* Some skills now have screen shake when dealing damage.

### Client Changes
* Moved the client player key to Logic Module.
* Fixed getting stuck logging in when receiving an invalid packet response.
* Client version check now checks Update Number.
* Removed redundant code.
* Screen shake implemented.
* Added EXP bar to ingame HUD.
* Fixed 'Reset' buttons click area being slightly too small.

### Server Changes
* Server now sends client Update Number for version check.
* Send screen shake when some projectiles deal damage.
* Improved performance on projectile collision detection with spatial hashing implementation.
* Maps now have TOP and BOTTOM boundaries.
* Improved perforamnce on map platform collision detection with spatial hashing implementation.
* Players now accelerate/deccelerate to a target speed in the air instead of instantly changing speed.
* GUI is now disabled by default. To enable GUI, launch with `--gui` argument.

---

## Version 0.19.0

### Gameplay Changes
* Players can now control their direction in midair.
* Map - Moved platforms slightly closer.
* Fixed Firebrand burn damage missing one tick.
* Magnetize pull time increased from 0.2s to 0.35s.

### Client Changes
* Fixed a typo in the Title in the title screen.
* Critical hit damage in the Stats screen now match with the Inventory screen.
* Players cannot login with no weapon or skills equipped.
* Minor visual bug fix in the Login screen when logging out.
* Update map visuals.
* Fixed being disconnected if loading took too long.

### Server Changes
* Fixed a rare concurrentcy bug with Reflect and Resist.
* EXP gains is a modifiable property in config using `expmult`. Defaults to 0.05.
* Max packets per connection can now be configured with `maxpackets`. Defaults to 1000.
* Network performance improvement.
* Unused UDP support integrated.
* Improved performance when removing entities.

---

## Version 0.18.2

### Client Changes
* Critical hit damage in the Stats screen now match with the Inventory screen.
* Players cannot login with no weapon or skills equipped.

### Server Changes

---

## Version 0.18.1

### Gameplay Changes
* Passives Removed: Tactical Execution
* Shield Skill Removed: Shield Toss
* New Shield Skill - Magnetize - Pull nearby enemies towards you.
* New Passive - Tough Skin - Boost your natural resilience to damage.
* New Passive - Vigor - Gain additional damage base on Max HP.
* New Passive - Static Charge - Chance to shock a nearby enemy when dealing damage.
* Rapid Fire damage increased from 75 + 2/Lvl% to 80 + 2/Lvl%.
* Resistance Passive reworked - When taking damage over 25% of your HP in 2 seconds, block all damage for 2 seconds.
* Phantom Reaper damage decreased from 110 + 3/Lvl% to 75 + 2/Lvl%
* Vortex Bolts damage increased from 75 + 3/Lvl% to 85% + 3/Lvl%
* Aggression damage increased from 600 + 20/Lvl% to 800 + 20/Lvl%

### Client Changes
* Fixed never timing out when trying to login to a live server with an invalid room.
* New Soundtrack!
* Text changes in Connect menu
* Item drop notifications now also describe the Item Tier.
* Various visual changes.
* Volley Renamed to Vortex Bolts
* New Skill Icons - Power of Will, Flurry
* Improved standards for Equipment sprite file structure.
* Fixed a rare bug where Charge not showing visuals.

### Server Changes
* Added batched packet sending. Disabled by default.
* Fixed some items not dropping.
* Moved getItemType from Player to Item class.
* Moved sendParticle, sendSfx from Player to PacketSender class.
* Fixed Dual Sword not being considered a passive.
* Phantom delay between strikes increased from 0.08s to 0.1s
* Update bow attack animation values.
* Fixed some skills rarely not activating and would go on cooldown.
* Fixed rare bug where player would take damage after respawning if there was a damage source on their death location at the time of respawn.
* Fixed not casting active skills when a passive skill hotkey is being held down.
* More consistent workflow for disconnecting player.
* Only player connection originated action commands are accepted by the server - Player hijacking prevention.

---

## Version 0.18.0

### Client Changes
* Minor adjustments to sound effects.
* Minor adjust to some particle visuals.
* Added `-port` launch parameter to specify port to connect.
* Added responses when failing to login to a room.
* Added a leave button in game - Can still leave a room with ESC button.
* Map Levels removed - Replaced with Arena Level Selection.
* New sprites for Dash, Walk, Stand and Death.
* Notifications when receiving EXP or items.
* Complete Netcode rework.
* UI Overhaul
* Added Title Screen

### Server Changes
* Dash distance decreased from 375 over 0.25s to 340 over 0.4s.
* Dash Damage Buff is now applied at the end instead of the beginning.
* Standardized logging format - Format is now [TIMESTAMP] Log Type:Class:Info
* Player state valid hash maps changed to use hash sets for performance.
* All maps are now Arenas - Restricted between levels.
* Players now have a chance to get an item when killing another player.
* Item drops are defined by server itemcode.txt list.
* Servers can now setup with different room numbers. Doesn't have to boot with sequential room numbers.
* Complete Netcode rework. Now backed by Kyronet TCP.

---

## Version 0.17.1

### Client Changes
* Fixed loading into map before loading completed.
* Buff Particles are less visible. Less visual cluttering around players.
* Player/Save Data are now uniquely identified with UUIDs.
* Added visual effects when player is critically hit.
* Ingame number changed for less visual clutter.
* Added HP Bar for players.

### Server Changes
* Mob(Monster) System improved - MobSkill is now it's own type, Mob Skills were subtypes of Skill before.
* Mob Buffs now parity Player buffs - Mobs now correctly take Damage Amplification and Reduction buffs.
* Slight change in Damage Reduction calculation.
* Added a 5 second grace period between clearing a level and resetting.
* Fixed rare case where a level would not reset when finished. 
* Arena - When a player is killed, 20% of their required experience to level up is awarded to the killer.
* Fixed a bug not denying login request when a player is logging in with the same character from the same IP.

---

## Version 0.17.0

### Client Changes
* Added Debug Mode - Currently displays some hidden values in the menu.
* Prototype screen shake - Nothing triggers this yet.
* Unified Particle keys
* Client file structure modified - Separated resources from binary.
* Simplified resource loading into Globals class.
* Added parallax map backgrounds.
* Particle key generation changed.

### Server Changes
* Mob(Monster) System improved - Added spawning, unified keys per map.
* Refactored Skill use implementation - Moved from Player class to Skill specific class.
* Passive Skills now have a flag to indicate its a passive skill.
* Projectile keys are no longer a constructor parameter - Retrieved in the constructor itself.
* Removed unused/retired Skills.

---

## Version 0.16.18

### Client Changes
* Renamed Global PLAYER_STATE constants to PLAYER_ANIM_STATE
* Rendering now uses hardware acceleration.
* Abstracted item sprite and data loading.
* Abstracted player sprite loading.
* Item data files now has formatting.
* Quiver renamed to Arrow Enchantment.
* Constant renamed from ITEM_QUIVER to ITEM_ARROW.
* Critical Hit Chance bonus per upgrade reduced from 0.2% to 0.1%
* Minor adjustments to stat generation for new equipments.
* Method name changes for ItemEquip for readability.
* Renamed Gash -> Blade Flurry, Echoing Fury -> Vorpal Strike.

### Server Changes
* Renamed Global PLAYER_STATE constants to PLAYER_ANIM_STATE
* Constant renamed from ITEM_QUIVER to ITEM_ARROW.

---

## Version 0.16.17

### Client Changes
* Abstracted Particle Loading.
* Particles now properly unload when leaving a game.
* Packets are no longer sent when socket is closed.
* Classes that require LogicModule references get the reference with a static initialization.
* Sound Module can play a WAV sfx without a location.
* Maps now prerender any required assets.
* Corrected final constants names for convention.

### Server Changes
* Corrected final constants names for convention.
