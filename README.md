# Nyan

A little macOS app to let you know when work is ongoing.

![Illustration](/etc/images/illus.gif?raw=true "Illustration")

It shows a Nyan Cat **in your macOS menu bar** whenever something is "ongoing" (loading, compiling, building, searching, thinking...)

This will help you switch to another window temporarily while knowing at a glance when the task is finished.

## Why?

To optimize your work/nonwork task switching ;)

People often switch to their browser/slack/minesweeper/whatever window while a long task
is running. Having a system-wide "ongoing progress" indicator allows to
know right away when it's time to go back to work, even from another window or space.

That's it!

## Download / Install

- Get the [latest app](https://github.com/BoD/nyan/releases/latest) and put it in your `/Applications` folder.
- Add `/Applications/Nyan.app/Contents/Resources/scripts` to your shell's `PATH` to be able to call the `nyan` tool.

## Run and use

- Run `Nyan.app`: this will start a local server and show the idle Nyan Cat in the menu bar.
- From your shell, call the bundled `nyan` tool like this:

```sh
nyan some-command arg1 arg2
```

This will show the animated Nyan Cat for the duration of the command.

- You can also use `nyan --busy <id> [--description <text>]` and `nyan --idle <id>` to set and clear
  the busy state for a given ID manually.

### Codex integration

The bundled scripts include hooks for Codex. To configure them, edit the `~/.codex/hooks.json` file and add the following:

```json
{
  "hooks": {
    "UserPromptSubmit": [
      {
        "hooks": [
          {
            "type": "command",
            "command": "/Applications/Nyan.app/Contents/Resources/scripts/nyan-hook-codex-UserPromptSubmit"
          }
        ]
      }
    ],
    "Stop": [
      {
        "hooks": [
          {
            "type": "command",
            "command": "/Applications/Nyan.app/Contents/Resources/scripts/nyan-hook-codex-Stop"
          }
        ]
      }
    ]
  }
}
```

You'll also need to approve the hooks in the Codex settings.

## See also

[This plugin](https://github.com/BoD/NyanTrayIntelliJPlugin) for IntelliJ (and Android Studio, and other IntelliJ based IDEs) integrates
with this app.

## Building

Build the app with `./gradlew :desktopApp:bundleMacApp`.

## Licence

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program. If not, see <http://www.gnu.org/licenses/>.
