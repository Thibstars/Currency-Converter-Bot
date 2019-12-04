# Currency Converter Bot [![Build Status](https://travis-ci.org/Thibstars/Currency-Converter-Bot.svg?branch=master)](https://travis-ci.org/Thibstars/Currency-Converter-Bot) [![codecov](https://codecov.io/gh/Thibstars/Currency-Converter-Bot/branch/master/graph/badge.svg)](https://codecov.io/gh/Thibstars/Currency-Converter-Bot) [![Discord Bots](https://discordbots.org/api/widget/status/563655936263061544.svg)](https://discordbots.org/bot/563655936263061544) # 
Discord bot allowing currency conversions and rates listing.

## Hosting ##
### Public ###
This bot is currently publicly hosted and available. It is listed on discordbots.org, so you could simply invite the bot to your server from there.

[![Discord Bots](https://discordbots.org/api/widget/563655936263061544.svg)](https://discordbots.org/bot/563655936263061544)

### Private ###
This bot can also be self-hosted.

#### Usage ####

In order to run the application, one must first add a Discord bot token to `bot.token` in the `token.properties` file.
**Note that a bot token should never be committed in git!**

When running directly using `java -jar` you can also pass your token as a first run argument instead. This is also the used approach in the `Dockerfile`.

##### Docker #####
When running the application from the `Dockerfile` make sure to add a new `BOT_TOKEN` environment variable with the bot token as value so it can be picked up 
in the underlying `java -jar` entrypoint command.