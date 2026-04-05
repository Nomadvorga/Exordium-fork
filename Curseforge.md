# Exordium (Fork)

> Fork of [Exordium](https://www.curseforge.com/minecraft/mc-mods/exordium) with scoreboard customization features.

__Render the GUI and screens at a lower framerate to speed up what's really important: the worldrendering.__

[![Discord](https://tr7zw.dev/curse/Discord-long.png)](https://discord.gg/2wKH8yeThf)

Renders the GUI at a lower fixed framerate (configurable in the settings), freeing up CPU and GPU time for the world rendering. There is no good reason to render the hotbar at 100+ FPS.

## Features

- Render the gui at a lower framerate (configurable in the settings)
- Speedup gui rendering during fade animations (configurable in the settings)
- Render screens at a lower framerate (configurable in the settings)
- Pre-render sign text (glowing signs render multiple times each frame in vanilla)
- Buffer full screen name-tags to reduce the amount of draw calls in game lobbies
- **Scoreboard Editor** — reposition, scale and change opacity of the sidebar scoreboard

## Fork additions

- In-game Scoreboard Editor screen (drag, scale, opacity)
- Option to hide scoreboard score numbers
- Custom scoreboard rendering matching vanilla draw order

## Compatibility

__This is still work in progress software! There will be visual issues/compatibility issues with other mods!__

### Affected vanilla features

- Overlays (except Vignette)
- Hotbar (all parts of it)
- Crosshair
- Bossbars
- Debug Screen (F3 Menu)
- Titles
- Scoreboard
- Chat
- All Screens (except the main menu) (including the pause menu, inventory, crafting, etc.)

### Tested and working with

- Sodium
- Iris
- Optifine (not recommended/officially supported)
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
- High resolution fonts on signs (disable buffering in the config)

## FAQ

### Forge support?

No.

### Does this work with mod xyz?

If you find a mod that doesn't work with this mod, you can report it on Github.

### Backport to 1.16.5/1.12.2/1.8.9?

No backports are planned. Mainly due to engine limitations in older versions.
