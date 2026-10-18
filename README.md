# Android TV Home

A tiny, ad-free home screen for old Android TVs, plus the Claude Code prompt I used to clean up my parents' TV.

![The home screen](docs/home-screen.png)

*Example layout with US streaming apps. Your own apps and HDMI names show up on your TV.*

My parents have an old Android TV with 2 GB of RAM. It was slow, and the home screen was full of sponsored rows you couldn't turn off.

So I had Claude Code fix it from my laptop, over Wi-Fi. It turned off the bloatware and the tracking, uninstalled 11 apps nobody used, and built this home screen from scratch:

- A big clock and the date
- One row with your streaming apps
- One row with the Play Store, your HDMI inputs and Settings
- A plain, still background. No ads, no recommendations, no video
- About 12 MB of RAM

## Disclaimer

This is just what worked for us, not professional advice. Try it at your own risk. You're responsible for your own TV, and I'm not liable if something goes wrong.

## Before you start

Your laptop needs a way to talk to the TV. It takes about two minutes. Menu names differ a little by brand.

1. On the TV, open **Settings**, then **Device Preferences** (or **System**), then **About**.
2. Scroll to **Build** and press OK on it 7 times. You'll see "You are now a developer."
3. Go back one step. Open the new **Developer options** menu and turn on **USB debugging**. If you see **Network debugging** or **Wireless debugging**, turn that on too.
4. Find your TV's IP address: **Settings**, then **Network & Internet**, then your Wi-Fi network. It looks like `192.168.1.25`.
5. Make sure your laptop is on the same Wi-Fi as the TV, and install [Claude Code](https://claude.com/claude-code).
6. The first time your laptop connects, the TV asks "Allow debugging?" Tick "Always allow from this computer" and press Allow.

Not sure how your TV does it? Tell Claude your TV's brand and model, and ask:

> I have a \<brand and model\> TV. How can you connect to it from my laptop and take control of it? Walk me through every step.

## Clean up your TV with Claude

Paste this into Claude Code on your laptop:

> I want to clean up my Android TV and make it faster. Its IP address is \<TV_IP\>. Help me connect to it over Wi-Fi first, and walk me through anything I need to tap on the TV.
>
> Then, before changing anything, show me every app on the TV in plain English, split into three lists: safe to turn off, needed by the TV, and apps I use. Also check how much free memory it has right now.
>
> Ask me before every change. Turn apps off instead of deleting them, so I can undo anything later. Never turn off Google Play, the keyboard, Bluetooth (my remote needs it) or anything my HDMI inputs need. Keep a list of everything you change and how to undo it.
>
> When that's done, restart the TV, make sure everything stayed off, and show me free memory before and after.
>
> Finally, turn off animations and the screensaver, and make the TV go to sleep after 5 minutes.

Two tips: let it ask before every change, and never turn off Google Play or the keyboard.

## Install the home screen

The easy way: open this folder in Claude Code and say

> Build this home screen with my apps and install it on my TV at \<TV_IP\>.

Or do it yourself:

1. Put your apps in `apps.txt`, one per line, in the order you want. Only the first six show.
2. Install Java 17 and the Android SDK. On a Mac:
   ```sh
   brew install openjdk@17
   brew install --cask android-commandlinetools
   sdkmanager "platforms;android-28" "build-tools;34.0.0"
   ```
3. Build and install:
   ```sh
   sh install.sh <TV_IP>
   ```

Your HDMI inputs show up on their own, with the names you gave them in the TV's settings.

**Keep `key.jks`.** It's made on your first build, and every update has to be signed with the same key.

### If your TV still opens Live TV or an HDMI input at startup

Some TVs jump to their last input when they turn on. That's a TV setting, not the home screen. Ask Claude: "My TV opens Live TV at startup instead of the home screen. Find the setting that does that and turn it off."

### Undo

```sh
sh undo.sh <TV_IP>
```

If you turned off your TV's original launcher, turn it back on first, or the TV will have no home screen.

## How it works

It's one screen of plain Java with no libraries, built straight from the command line (no Android Studio, no Gradle). The background is drawn once and never moves. The only animation is the lift when you move between apps. The app list rebuilds only when you install or remove an app.

Made by [Ansub Khan](https://www.youtube.com/@ansubkhan). I build things like this with AI on YouTube.

## License

MIT
