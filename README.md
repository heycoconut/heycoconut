# Heycoconut ![Travis build](https://travis-ci.com/alecc08/heycoconut.svg?branch=master) [![Codacy Badge](https://api.codacy.com/project/badge/Grade/c08f892014994bde9999fc569810938e)](https://www.codacy.com/project/alecc/heycoconut/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=alecc08/heycoconut&amp;utm_campaign=Badge_Grade_Dashboard) [![Coverage Status](https://coveralls.io/repos/github/alecc08/heycoconut/badge.svg?branch=master)](https://coveralls.io/github/alecc08/heycoconut?branch=master)
Heycoconut is a slack bot that allows us to gift and track coconuts. It is written in Java and uses spring boot. It tracks coconuts in a mongodb server and uses reactive style objects for managing the REST endpoints and DB access. It is a fun little project we started developing to practice programming and to have a fun bot in our slack workspace that allows us to give kudos to each other.

## How to use
Once the app is installed in your workspace, invite the boy into a channel by tagging them. Once the bot is in the channel you can give someone a coconut (or more) by tagging them and putting coconut emojis. `e.g. @Raymond :coconut: Thanks for lending me your sick japanese knife!`

![image](https://user-images.githubusercontent.com/14881741/43834626-bd07ecba-9adc-11e8-904d-ea3e049079ec.png)

![image](https://user-images.githubusercontent.com/14881741/43834889-df96ffa4-9add-11e8-8cb5-47edf5c95944.png)


You can also tag multiple people to give to more than one person at a time. Heycoconut will also DM the person giving the coconuts to let them know how many coconuts they have left.

- You can add a reaction to a post to give someone a coconut without the need to type anything.

![image](https://user-images.githubusercontent.com/14881741/44824034-81dc1980-abd1-11e8-9994-4551a7193f75.png)

- You can ask HeyCoconut for a random fact by tagging the bot and saying trivia

![image](https://user-images.githubusercontent.com/14881741/44823873-cc10cb00-abd0-11e8-9deb-03a6ca3bdaaa.png)

- You can ask HeyCoconut for the leaderboard stats

![image](https://user-images.githubusercontent.com/14881741/44823924-ffebf080-abd0-11e8-8b92-e5ef62135007.png)

## Slack Request Validation
There are a couple of features implemented to avoid people "spoofing" the requests, or replaying requests multiple times. The first and most basic validation
keeps track of the last event id's processed, and will avoid re-processing any request that was already processed.

The second and most secure way is to use slack's signing secret to validate the signature in the request header. To do this
we re-encrypt the signature by appending the timestamp with the data and encrypting using the slack signing secret given
when registering a bot in a workspace. If this generated signature matches the one sent in the request header, then the request
is determined to be valid.

## Requirements
 - Mongodb server
    - You need to specify these environment variables for the app to connect to the database
    - `MONGO_HOST`: hostname of the mongodb server
    - `MONGO_PORT`: mongo server port to connect to 
    - `MONGO_DB`  : db name
    - `MONGO_USER`: username to use to connect to the db
    - `MONGO_PW`  : password for the `MONGO_USER`
 - Other environment variables
    - `DAILY_COCONUT_LIMIT` : Max number of coconuts anyone can gift in a day
    - `SLACK_BOT_TOKEN`     : The token which was generated by slack for the bot `e.g. xoxb-XXXXXXXXXXXX-XXXXXXXXXXXX-XXXXXXXXXXXXXXXXXX`
    - `SLACK_SIGNING_SECRET`: The signing secret given by slack when registering a bot in a workspace. Used to validate requests.
    
## Adding new commands
Commands are easy to add, all you have to do is:
 - Add a command which extends the abstract coconut command class.
 - You must annotate the class with @Command and specify the EventType to link the Command to. e.g. `@Command(EventType.MESSAGE)`
 - You also need to create a static "getPredicate" function on your class `public static Predicate<SlackRequestDTO> getPredicate()`,
 - Also a static "build" function which returns an instance of your new command. `public static CoconutCommand build(SlackRequestDTO request)`

The *predicate* will decide in addition to the EventType linked to your command when it should be executed based off the request sent by slack.
An example would be to check the text in the message for certain keywords that may trigger your command

By following those steps, your command will be detected at runtime, and be executed by the CoconutCommandManager class at the right moment.

## Slack App Configuration

Get your signing secret from the `Basic Information` section
![image](https://user-images.githubusercontent.com/9341349/45330809-40366180-b535-11e8-990f-779c1f3ff7c6.png)

You can get your `OAuth Access Token` and `Bot User OAuth Access Token` in the `OAuth & Permissions` section
![image](https://user-images.githubusercontent.com/9341349/45330930-d074a680-b535-11e8-892b-e6270d5fcd65.png)

Define these scopes
![image](https://user-images.githubusercontent.com/9341349/45330961-0154db80-b536-11e8-96fa-e47506a5a99a.png)

Define your request URL and Subscribe to bot events in the `Event Subscriptions` section
![image](https://user-images.githubusercontent.com/9341349/45331021-61e41880-b536-11e8-9b35-53cef43bfdeb.png)

![image](https://user-images.githubusercontent.com/9341349/45331285-db303b00-b537-11e8-9a2c-687381e054c5.png)
