# Android TV Home

**Make an old, slow Android TV fast again, with Claude Code or Codex doing the work.**

[![YouTube](https://img.shields.io/badge/YouTube-@ansubkhan-FF0000?logo=youtube&logoColor=white)](https://www.youtube.com/@ansubkhan)

![The home screen](docs/home-screen.png)

- **Removes the junk:** bloatware, ads and sponsored rows, gone.
- **Gives you this home screen:** your apps, your HDMI inputs and a clock. Nothing else.
- **Undo anytime:** nothing is deleted for good.

## How to do it (3 steps)

### 1. Turn on developer mode on your TV

Settings → Device Preferences → About → press **Build** 7 times. Then open the new **Developer options** and turn on **USB debugging**. ([Need more help?](#turning-on-developer-mode))

### 2. Get [Claude Code](https://claude.com/claude-code) or [Codex](https://openai.com/codex) on your laptop

Your laptop and TV must be on the same Wi-Fi.

### 3. Paste this in

```
Help me clean up my Android TV and make it faster.
My TV is a <brand and model>.

1. Connect to my TV over Wi-Fi. Walk me through anything
   I need to tap on the TV.
2. Before changing anything, list every app in plain English:
   safe to turn off, needed by the TV, and apps I use.
   Check free memory.
3. Ask me before every change. Turn apps off instead of
   deleting them. Never turn off Google Play, the keyboard,
   Bluetooth or my HDMI inputs. Keep a list of what you
   changed and how to undo it.
4. Install the home screen: download
   https://github.com/Ansub/android-tv-home and run its
   install.sh with my TV and these apps: <your apps>.
5. Turn off animations and the screensaver, and make the TV
   sleep after 5 minutes.
6. Restart the TV and show me free memory before and after.
```

That's it. It does the rest and asks you before each change.

> **Disclaimer:** this is just what worked for us, not professional advice. Try it at your own risk. You're responsible for your own TV, and I'm not liable if something goes wrong.

---

## More help

### Turning on developer mode

1. On the TV, open **Settings**, then **Device Preferences** (or **System**), then **About**.
2. Press OK on **Build** 7 times until you see "You are now a developer."
3. Go back, open **Developer options** and turn on **USB debugging**. If you see **Network debugging** or **Wireless debugging**, turn that on too.
4. The first time your laptop connects, the TV asks "Allow debugging?" Tick "Always allow" and press **Allow**.

Menus differ a bit by brand. If you're stuck, ask Claude or Codex: *"I have a \<brand and model\> TV. How can you connect to it from my laptop and take control of it?"*

### Tested on

Android TV 9 (a real TV), 11 and 12, and Google TV 14. Every change is tested automatically on emulators.

### The TV still opens Live TV or HDMI when it turns on

That's a TV setting, not this home screen. Ask it: *"My TV opens Live TV at startup instead of the home screen. Find that setting and turn it off."*

### Install the home screen yourself

You only need `adb` (Mac: `brew install android-platform-tools`). Download this repo, then:

```sh
sh install.sh <TV_IP>                                   # picks your TV apps for you
sh install.sh <TV_IP> "com.netflix.ninja,com.hulu.livingroomplus"   # or choose them
```

Most TVs keep opening their own home screen, so the script offers to turn that off. Nothing is uninstalled.

### Undo

```sh
sh undo.sh <TV_IP>
```

This turns your TV's own home screen back on and removes this one. To undo the cleanup too, ask Claude or Codex to reverse everything on its list.

<details>
<summary><b>For developers: build it yourself</b></summary>

1. Put your default apps in `apps.txt` (package names, one per line, first six show). You can also pass them to `install.sh` instead.
2. Install Java 17 and the Android SDK. On a Mac:
   ```sh
   brew install openjdk@17
   brew install --cask android-commandlinetools
   sdkmanager "platforms;android-34" "build-tools;34.0.0"
   ```
3. `sh build.sh`, then `sh install.sh <TV_IP>` installs your build instead of the release.
4. `sh ci/smoke-test.sh <TV_IP or emulator>` checks it all works.

HDMI inputs are found on their own. Keep `key.jks`, which is made on your first build: updates must be signed with the same key.

It's one screen of plain Java, no libraries, no Gradle. The background is drawn once, the only animation is the focus lift, and it uses about 12 MB of RAM.

</details>

---

Made by Ansub Khan. I build things like this with AI every week. **[Watch on YouTube →](https://www.youtube.com/@ansubkhan)**

MIT License
