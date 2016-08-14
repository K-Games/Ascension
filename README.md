![Ascension](https://github.com/kenofnz/Ascension/raw/master/Ascension/resources/sprites/ui/menu/title.png)

# Description
An multiplayer action 2D PvP platformer coded in Java.
http://www.gdunlimited.net/games/ascension/

# Version 0.20 Release
## Gameplay Changes
* Some skills now have screen shake when dealing damage.

## Client Changes
* Moved the client player key to Logic Module.
* Fixed getting stuck logging in when receiving an invalid packet response.
* Client version check now checks Update Number.
* Removed redundant code.
* Screen shake implemented.
* Added EXP bar to ingame HUD.
* Fixed 'Reset' buttons click area being slightly too small.

## Server Changes
* Server now sends client Update Number for version check.
* Send screen shake when some projectiles deal damage.
* Improved performance on projectile collision detection with spatial hashing implementation.
* Maps now have TOP and BOTTOM boundaries.
* Improved perforamnce on map platform collision detection with spatial hashing implementation.
* Players now accelerate/deccelerate to a target speed in the air instead of instantly changing speed.
* GUI is now disabled by default. To enable GUI, launch with `--gui` argument.

# Credits
### Developer/Game Designer/Visual Designs
[KenOfNZ](https://github.com/kenofnz)

## Character Base
[TradnuxGames](http://tradnux.com/)

## Music
Title & Menu Tracks

* Hero - [geluf](https://soundcloud.com/geluf)
* Through the Forest in Midwinter - [geluf](https://soundcloud.com/geluf)

Arena Tracks

```
Music from the “JRPG Essentials” Series by Dibur
Copyright (c) 2016 Dibur
http://dibur.moe
```

* Redemption (Orchestral Style) - [Dibur](http://dibur.moe)
* Graceful Resistance - [Dibur](http://dibur.moe)
* Dibur - Inevitable Bloodshed (Rock Style) - [Dibur](http://dibur.moe)
