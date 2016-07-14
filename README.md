```
 __ __|                                 ___|                                          |   
    |   _ \ \ \  \   /  _ \   __|      |       _ \   __ \    _` |  |   |   _ \   __|  __| 
    |  (   | \ \  \ /   __/  |         |      (   |  |   |  (   |  |   |   __/ \__ \  |   
   _| \___/   \_/\_/  \___| _|        \____| \___/  _|  _| \__, | \__,_| \___| ____/ \__| 
                                                               _|                         
```

# Description
An multiplayer action 2D sidescrolling platformer coded in pure Java.

## v0.17 Update 2
### Client Changes
* Minor adjustments to sound effects.
* Minor adjust to some particle visuals.
* Added `-port` launch parameter to specify port to connect.
* Added responses when failing to login to a room.
* Added a leave button in game - Can still leave a room with ESC button.
* Map Levels removed - Replaced with Arena Level Selection.
* New sprites for Dash, Walk, Stand and Death.

### Server Changes
* Dash distance decreased from 375 to 275.
* Standardized logging format - Format is now [TIMESTAMP] Log Type:Class:Info
* Player state valid hash maps changed to use hash sets for performance.
* All maps are now Arenas - Restricted between levels.
* Players now have a chance to get an item when killing another player.