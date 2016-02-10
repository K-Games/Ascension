```
 __ __|                                 ___|                                          |   
    |   _ \ \ \  \   /  _ \   __|      |       _ \   __ \    _` |  |   |   _ \   __|  __| 
    |  (   | \ \  \ /   __/  |         |      (   |  |   |  (   |  |   |   __/ \__ \  |   
   _| \___/   \_/\_/  \___| _|        \____| \___/  _|  _| \__, | \__,_| \___| ____/ \__| 
                                                               _|                         
```

# Description
An multiplayer action 2D sidescrolling platformer coded in pure Java.

## v0.16 Client Update 17
### Client Changes
* Abstracted Particle Loading.
* Particles now properly unload when leaving a game.
* Packets are no longer sent when socket is closed.
* Classes that require LogicModule references get the reference with a static initialization.
* Sound Module can play a WAV sfx without a location.
* Maps now prerender any required assets.
* Corrected final constants names for convention.

## v0.16 Server Update 17
### Server Changes
* Corrected final constants names for convention.
