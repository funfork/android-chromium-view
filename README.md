Android Chromium View
=====================
# Table Of Contents
* [Introduction](https://github.com/davisford/android-chromium-view#introduction)
* [The Why](https://github.com/davisford/android-chromium-view#why)
* [Difference Between This & ChromeView](https://github.com/davisford/android-chromium-view#what-is-the-difference-between-this--chromeview)
* [What Do I Do With This](https://github.com/davisford/android-chromium-view#what-do-i-do-with-this)
* [Artifacts: Assets & Libraries](https://github.com/davisford/android-chromium-view#artifacts-assets--libraries)
* [Gradle Support](https://github.com/davisford/android-chromium-view#gradle-support)
* [Updating Chromium](https://github.com/davisford/android-chromium-view#updating-chromium)
* [License](https://github.com/davisford/android-chromium-view#license)
* [What Version Of Chromium Is It?](https://github.com/davisford/android-chromium-view#what-version-of-chromium-is-it)
* [Pre-requisites For Building Chromium](https://github.com/davisford/android-chromium-view#pre-requisites-for-building-chromium)
* [Debugging](https://github.com/davisford/android-chromium-view#debugging)
  * [Java](https://github.com/davisford/android-chromium-view#java)
  * [Remote Chrome DevTools](https://github.com/davisford/android-chromium-view#remote-chrome-devtools)
  * [GDB](https://github.com/davisford/android-chromium-view#gdb)
* [Pull Requests](https://github.com/davisford/android-chromium-view#pull-requests)
* [Invaluable Resources](https://github.com/davisford/android-chromium-view#invaluable-resources)

# Introduction

This project was inspired by [pwnall's chromeview](https://github.com/pwnall/chromeview), but it shares no common code.  The goal of this project is similar to that project -- which is to allow you to embed a replacement for the stock [Android WebView](https://developer.android.com/reference/android/webkit/WebView.html), using the latest [Chromium](http://www.chromium.org/Home).

## Why

* Android WebView does not have the same performance characteristics as Chromium (the latter is faster)
* Android WebView lacks feature parity with Chromium.  The Chromium team moves fast, it is usually one of the first browsers to receive new W3C features, etc.
* Android WebView is embedded and tied to the platform -- updating it therefore becomes problematic since you have to update the whole platform to update the browser

## What Is The Difference Between This & ChromeView
The [chromeview](https://github.com/pwnall/chromeview) project was an awesome start to solving this problem, and I started there, but henceforth, the author has stated that he lacks the time to dedicate to it.  So, I originally [forked](http://github.com/davisford/chromeview) it, and updated Chromium to a newer build.  Then I looked into the [scrolling issues](https://github.com/pwnall/chromeview/pull/6) and after some digging, decided that there was a separate build artifact out of Chromium that would provide a better base than the one chromeview was currently using.  The code was different enough that I felt it just warranted a separate repo -- so that's what you have here.  Scrolling does work great here.  There are some quirks, so check the [issues](https://github.com/davisford/android-chromium-view/issues?state=open) to figure out what isn't quite working (yet).

## What Do I Do With This
You should be able to import all of these projects into ADT and they should compile without error.  Each project may have dependencies on the other projects.  `content-shell` is the only application project.  It needs to include all the library projects in its deployable apk.  Make sure that the `project.properties` file for that project contains:

```
android.library.reference.3=../eyes-free
android.library.reference.4=../media
android.library.reference.1=../base
android.library.reference.2=../content
android.library.reference.6=../ui
android.library.reference.5=../net
```

Next, connect some Android ARM hardware, or use an emulator (slow), and run `content-shell` as an Android Application.  It should bring up a browser with a simple address bar and back/forward buttons.  The underlying browser is Chromium (obviously).  You're on your own if you want to modify / tweak the sources from here, but this is a good starting place.

From here, you can decide to use that as a stock browser wrapper, or obviously modify / add / edit / delete all the Java, C++, or JavaScript to your heart's content to build it into whatever you want.

## Artifacts: Assets & Libraries
The main Chromium artifact is a native shared library `.so`.  It also depends on a `.pak` file in the `/assets` folder (you'll find these in the `content-shell` app).  As built, these need to be included in your `/libs` and `/assets` folder of your project, and they *will* add a good 30-40MB of binary goodness to the size of your app.  However, if you're savvy, you could figure out a way to load those from a central location on the platform, so they don't have to be included in `.apk` file.  This will require code modifications.

This project contains a snapshot in time binary build of Chromium for Android ([ARM](https://github.com/davisford/android-chromium-view/tree/master/content-shell/libs/armeabi-v7a) and [x86](https://github.com/davisford/android-chromium-view/tree/master/content-shell/libs/x86)).  Instructions below if you want to update Chromium.

The rest of the projects include java source and Android assets that were authored by Google to get Chromium to show up in a standard Android view and interact with it.

## Gradle Support
Asuming you have the Android build environment up and running (and the environment variable ANDROID_HOME is defined), you should be able to build the project from the command line by running `./gradlew build`.

If you have an Android device connected to your development machine, you can build and install the application on your device by running `./gradlew installDebug`.

You can also import the project into Android Studio and run it from there. Android Studio (as of version 0.2.6) might think there are some dependencies unresolved, but it should still build fine.

# Updating Chromium

Build the latest Chromium for Android (see instructions below).  Each project contained herein has a `scripts/` directory.  There is also and `env.sh` script in the root dir -- modify that first to set it to your environment.  Now, you can run each subdir's sh script as necessary to copy the latest artifacts over from the Chromium build tree.

Google updates this code constantly, so if you git pull on Chromium repo and rebuild, then copy the stuff over here, there's no guarantee that all of this won't break.

## License
Nearly all of the source in here is copied from the Chromium project and thus subject to Chromium license(s) -- `LICENSE` files are found w/in each project. 

## What Version of Chromium Is It?

TODO: I aim to add git tags to the repo to identify various Chromium builds.  Currently, the one in here was build in mid-August 2013 (more details coming).

# Pre-requisites for Building Chromium
@pwnall has some [nice instructions](https://github.com/pwnall/chromeview/blob/master/crbuild/vm-build.md) for how to setup a Chromium build machine on a VM.  I have made a [couple of tweaks to it on my own fork](https://github.com/davisford/chromeview/blob/master/crbuild/vm-build.md) -- namely stick with Ubuntu 12.04 for the least amount of headache.

I build on a MBP using an Ubuntu 12.04 image I created in VMWare Fusion.  You will need some decent hardware to build Chromium.  On 4 cores with 4GB RAM it can take several hours to complete the build.  If you have a single core, forget it.

If you have a spare machine to setup a build machine, I suppose that would be even nicer.

# Debugging

## Java
Just use ADT and launch `content-shell` as: `Debug as Android Application`

## Remote Chrome DevTools

Yes, you can debug the Chromium browser running on your Android device from your laptop using [Chrome DevTools](https://developers.google.com/chrome-developer-tools/), thanks for asking.  You need a fairly recent copy of Google Chrome running on your dev machine along with the ADT bundle.

First, remote shell into the device and add the command line switch to enable remote debug over USB:

```shell
$ adb shell
shell@android:/ $
```

Content shell will read the file `/data/local/tmp/content-shell-command-line` at startup and apply whatever valid [switches](http://peter.sh/experiments/chromium-command-line-switches/) it finds.  You can quickly create this file with the remote usb debug switch as follows:

```shell
shell@android:/ $ echo chrome --remote-debugging-raw-usb > /data/local/tmp/content-shell-command-line
```

Connect your device with USB, and use ADT (or Gradle or command line tools, etc.) to start the `content-shell` Android application.  Now, you need to setup adb port forwarding for the debug protocol.  Execute this on your dev machine (not on the device itself):

```shell
$ adb forward tcp:9222 localabstract:content_shell_devtools_remote
```

Now, you can navigate to http://localhost:9222 using Chrome on your dev machine, and you should see the instance of `content_shell` -- you can inspect it and it will open up DevTools and allow you to remote debug.

## GDB
If you want to debug native code, you'll need a rooted device.  I suggest [http://www.cyanogenmod.org/](Cyanogenmod), but I guess you'll figure out how to root your own platform.  More specifically, if the command `adb root` works, you'll be in pretty good business.  

If you built Chromium yourself, you're in luck, b/c there is an [adb_gdb_content_shell](https://code.google.com/p/chromium/codesearch#chromium/src/build/android/adb_gdb_content_shell&sq=package:chromium&type=cs) script which can connect gdb to your instance of `ContentShell1`.  It ends up calling out to [adb_gdb](https://code.google.com/p/chromium/codesearch#chromium/src/build/android/adb_gdb&sq=package:chromium&type=cs) which has a little more meat if you want to investigate how it works and re-write for your environment. 

# Pull Requests

Pull requests welcome to help improve the environment / shell scripts -- or even re-factor the projects.  In Android, the project's namespace is important, and if you have multiple package namespaces with resources, then it becomes a PITA to resolve the `R.java` files.  This is why I broke it out (one project per namespace) -- to avoid this problem.  Some of the projects don't contain any resources, however, and they could really be merged into a single library, but I left them separate, as you never know if Google decides to add resources to a project, then this structure already works and requires no re-factoring.

Don't send pull requests for Google authored code b/c it will just be re-written on the next update.  Don't send pull requests for updates to Chromium, I'd prefer you just fork the repo and update those yourself.

# Invaluable Resources

* [Online Chromium Code Search](https://code.google.com/p/chromium/codesearch) - I use this a lot.
* [Chromium For Developers Docs](http://dev.chromium.org/developers) - best start reading if you want to really get into this
* [Chromium For Android Build Instructions](https://code.google.com/p/chromium/wiki/AndroidBuildInstructions) - instructions and scripts provided by @pwnall in [chromeview](https://github.com/davisford/chromeview/tree/master/crbuild) parse this down, but I'm just dropping this here for reference
* [Android WebView Talk At Google I/O 2012](https://developers.google.com/events/io/2012/sessions/gooio2012/122/) - might prove useful for some background info

