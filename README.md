```
 __ __|                                 ___|                                          |   
    |   _ \ \ \  \   /  _ \   __|      |       _ \   __ \    _` |  |   |   _ \   __|  __| 
    |  (   | \ \  \ /   __/  |         |      (   |  |   |  (   |  |   |   __/ \__ \  |   
   _| \___/   \_/\_/  \___| _|        \____| \___/  _|  _| \__, | \__,_| \___| ____/ \__| 
                                                               _|                         
```

# Description
An multiplayer action 2D sidescrolling platformer coded in pure Java.

## v0.16 Client Update 19
### Client Changes
* Added Debug Mode - Currently displays some hidden values in the menu.
* Prototype screen shake - Nothing triggers this yet.
* Unified Particle keys
* Client file structure modified - Separated resources from binary.
* Simplified resource loading into Globals class.
* Added parallax map backgrounds.
* Particle key generation changed.

## v0.16 Server Update 19
### Server Changes
* Mob(Monster) System improved - Added spawning, unified keys per map.
* Refactored Skill use implementation - Moved from Player class to Skill specific class.
* Passive Skills now have a flag to indicate its a passive skill.
* Projectile keys are no longer a constructor parameter - Retrieved in the constructor itself.
* Removed unused/retired Skills.