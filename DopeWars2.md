# DopeWars2 #

## A history ##

DopeWars has a long and robust history as a game.  It's been out there on just about every platform imaginable, in all kinds of variations.  I first played it back around the mid 90's, on MS-DOS, using a version built sometime in the mid 80's.

The basic story of the game, in its original and most current forms, is this:  You are a drug dealer, living in New York City.  You take turns moving around the city, and at each stop you can buy or sell drugs at prices that vary for each turn.  The original version is very much a luck-based game, with a little strategy thrown in.  Like all good luck-based games, it's pretty short, usually comprising 30 turns.

There are lots of details that exist in different versions of the game, but most all include an initial bit of debt (from a loan shark) that accumulates interest over time.  There is usually a bank mechanic that you can put money in and accumulate your own interest over time as well.  What really always made the game for me was the random events, which gave the game flavor and color.  There are some that lose you money, and in most versions also a "cops are attacking you" event that is anchored by Officer Hardass.

## DopeWars on Android ##

I decided way back when that android needed its own version of DopeWars, dammit!  So I wrote one, back when there was one android phone (the G1) and the Android marketplace was pretty immature.  Then I left it alone for a while.

People seem to like it, which is nice.  It's gotten a bunch of downloads off the marketplace, and despite some more graphical competitors, seems to remain pretty viable as a game.  Looking at it now, though, I'm thinking I can do better.  One reason is that my girlfriend has graduated from her design program and now has time to help me with graphics and UI, an area in which I am generally weak.  Another reason is that the Android app situation in general and me in particular have both matured a little bit.  I think I can make better decisions about data storage and network communication now that will lead to a more robust and easier to play game.

I don't want to change the gameplay much at all, though.  I notice a lot of people want to change the game a bunch when they develop versions of it, to make it more strategic, or persistent, or whatever.  I maintain that's not a good idea.  The basic version of the game is FUN, and that's hard.  It's also, I believe, particularly appropriate for the phone, more so than a longer-term strategic game.

## The plan ##

So the big P plan is to develop the code here, and start to rope in my graphics and UI person as I get the code back into playable shape.  Most of the gameplay, as mentioned, will be the same, but I'm totally changing the underlying data storage.  The old version used temp files that are assigned to the android app, which I am positive have cause a lot of issues on different hardware.

The other thing I want to change a lot is the back-end high score list and commenting system.  Only by expanding it and making it more robust though.  Currently the high score list on the backend is occasionally overloaded and stops responding (I'm using Google AppEngine's free tier of quota).  I'm positive this can be redesigned to use a lot less calculation per request, and stop going out of quota.  I also think that more score info can be remembered, and a better high score list can be kept.

And then of course we have graphics and UI.  So if you get what's in this repository right now, it will look terrible, because I'm focusing just on the gameplay mechanics right now, and will start to integrate graphics and UI as I get them from my new graphics and UI person.  There is no way this does not end up with a much prettier interface, while maintaining speed and robustness across phone platforms.