```
     \                                   _)               
    _ \     __|   __|   _ \  __ \    __|  |   _ \   __ \  
   ___ \  \__ \  (      __/  |   | \__ \  |  (   |  |   | 
 _/    _\ ____/ \___| \___| _|  _| ____/ _| \___/  _|  _|                                             _|                         
```

# Description
An multiplayer action 2D sidescrolling platformer coded in pure Java.

## Version 0.18
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

# Credits

## Title & Menu Tracks
Hero - [geluf](https://soundcloud.com/geluf)

Through the Forest in Midwinter - [geluf](https://soundcloud.com/geluf)

