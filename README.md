# Currency Converter Bot [![Build Status](https://app.travis-ci.com/Thibstars/Currency-Converter-Bot.svg?branch=master)](https://app.travis-ci.com/Thibstars/Currency-Converter-Bot) [![codecov](https://codecov.io/gh/Thibstars/Currency-Converter-Bot/branch/master/graph/badge.svg)](https://codecov.io/gh/Thibstars/Currency-Converter-Bot) [![Discord Bots](https://discordbots.org/api/widget/status/563655936263061544.svg)](https://discordbots.org/bot/563655936263061544) # 
Discord bot allowing currency conversions and rates listing.
This bot uses the same API as https://currencyconversion.be.

## Hosting ##
### Public ###
This bot is currently publicly hosted and available. It is listed on [top.gg](https://top.gg/bot/563655936263061544), so you could simply invite the bot to your server from there.

[![Discord Bots](https://discordbots.org/api/widget/563655936263061544.svg)](https://discordbots.org/bot/563655936263061544)

[![DigitalOcean Referral Badge](https://web-platforms.sfo2.cdn.digitaloceanspaces.com/WWW/Badge%201.svg)](https://www.digitalocean.com/?refcode=9d2be90e794d&utm_campaign=Referral_Invite&utm_medium=Referral_Program&utm_source=badge)

### Private (0.2.7 and before) ###
This bot can also be self-hosted up until version 0.2.7. The newer version calls an API maintained separately.

#### Usage ####

In order to run the application, one must first add a Discord bot token to `bot.token` in the `token.properties` file.
**Note that a bot token should never be committed in git!**

When running directly using `java -jar` you can also pass your token as a first run argument instead. This is also the used approach in the `Dockerfile`.

To start using the bot itself, it is recommended to issue the 'help' command (don't forget the prefix, which is '/' by default) to retrieve an overview of available commands. 
Each command has a similar help overview which can be called with the option: -h or --help (e.g.: convert -h).

##### Docker #####
When running the application from the `Dockerfile` make sure to add a new `BOT_TOKEN` environment variable with the bot token as value so it can be picked up 
in the underlying `java -jar` entrypoint command.