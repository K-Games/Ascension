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

## v0.17 Server Update 1
### Server Changes
* Mob(Monster) System improved - MobSkill is now it's own type, Mob Skills were subtypes of Skill before.
* Mob Buffs now parity Player buffs - Mobs now correctly take Damage Amplification and Reduction buffs.
* Slight change in Damage Reduction calculation.
* Fixed rare case where a level would not reset when finished.