```
 __ __|                                 ___|                                          |   
    |   _ \ \ \  \   /  _ \   __|      |       _ \   __ \    _` |  |   |   _ \   __|  __| 
    |  (   | \ \  \ /   __/  |         |      (   |  |   |  (   |  |   |   __/ \__ \  |   
   _| \___/   \_/\_/  \___| _|        \____| \___/  _|  _| \__, | \__,_| \___| ____/ \__| 
                                                               _|                         
```

# Description
An multiplayer action 2D sidescrolling platformer coded in pure Java.

## v0.17 Client Update 1
### Client Changes
* Fixed loading into map before loading completed.
* Buff Particles are less visible. Less visual cluttering around players.
* Player/Save Data are now uniquely identified with UUIDs.
* Added visual effects when player is critically hit.
* Ingame number changed for less visual clutter.
* Added HP Bar for players.

## v0.17 Server Update 1
### Server Changes
* Mob(Monster) System improved - MobSkill is now it's own type, Mob Skills were subtypes of Skill before.
* Mob Buffs now parity Player buffs - Mobs now correctly take Damage Amplification and Reduction buffs.
* Slight change in Damage Reduction calculation.
* Added a 5 second grace period between clearing a level and resetting.
* Fixed rare case where a level would not reset when finished. 
* Arena - When a player is killed, 20% of their required experience to level up is awarded to the killer.
* Fixed a bug not denying login request when a player is logging in with the same character from the same IP.