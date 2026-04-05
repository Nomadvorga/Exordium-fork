# Exordium (Fork)

> Fork of [Exordium](https://modrinth.com/mod/exordium) with scoreboard customization features.

__Render the GUI and screens at a lower framerate to speed up what's really important: the worldrendering.__

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
- All Screens (except the main menu)

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
