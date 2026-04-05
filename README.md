<img src="https://raw.githubusercontent.com/tr7zw/Exordium/1.19/Shared/src/main/resources/assets/exordium/icon.png" align="right" width=200>

# Exordium (Fork)

> Fork of [tr7zw/Exordium](https://github.com/tr7zw/Exordium) with additional customization features.

__Render the HUD and screens at a lower framerate to speed up what's really important: the worldrendering.__

Renders the HUD at a lower fixed framerate (configurable in the settings), freeing up CPU and GPU time for the world rendering. There is no good reason to render the hotbar at 100+ FPS.

## Fork additions

- **Scoreboard Editor** — in-game screen to reposition, scale and change opacity of the sidebar scoreboard
  - Drag with LMB to move
  - Scroll to change scale, Shift+Scroll to change opacity
  - Right-click or press R to reset to default (vanilla) position
- Custom scoreboard rendering that matches vanilla draw order

## Compatibility

__This is still work in progress software! There will be issues, please report them!__

### Affected vanilla features

- Overlays except Vignette (Pumpkin, freezing, spyglass, portal...)
- Hotbar (all parts of it)
- Crosshair
- Bossbars
- Debug Screen (F3 Menu)
- Titles
- Scoreboard
- Chat

### Tested and working with

- Sodium
- Iris
- Optifine
- AppleSkin
- Better Ping Display
- Chat Heads
- Detail Armor Bar
- WTHIT (What the hell is that?)
- JourneyMap
- ToroHealth Damage Indicator
- BetterF3

### Not compatible

- VulkanMod

### Screen setting notice

This feature is still not done (disabled by default) and has mainly transparency issues. It heavily speeds up mods like REI (Roughly Enough Items), but with visual issues.

## Building

```bash
./gradlew :1.21.11-fabric:remapJar
```

The output JAR will be in `versions/1.21.11-fabric/build/libs/`.

## License

This project is licensed under [LGPL-3.0](LICENSE).

Original mod by [tr7zw](https://github.com/tr7zw).
