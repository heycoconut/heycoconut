# Heycoconut ![Travis build](https://travis-ci.com/alecc08/heycoconut.svg?branch=master) [![Codacy Badge](https://api.codacy.com/project/badge/Grade/c08f892014994bde9999fc569810938e)](https://www.codacy.com/project/alecc/heycoconut/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=alecc08/heycoconut&amp;utm_campaign=Badge_Grade_Dashboard)
Heycoconut is a slack bot that allows us to gift and track coconuts. It is written in Java and uses spring boot. It tracks coconuts in a mongodb server and uses reactive style objects for managing the REST endpoints and DB access. It is a fun little project we started developing to practice programming and to have a fun bot in our slack workspace that allows us to give kudos to each other.

## How to use
Once the app is installed in your workspace, invite the boy into a channel by tagging them. Once the bot is in the channel you can give someone a coconut (or more) by tagging them and putting coconut emojis. `e.g. @Raymond :coconut: Thanks for lending me your sick japanese knife!`

![image](https://user-images.githubusercontent.com/14881741/43834626-bd07ecba-9adc-11e8-904d-ea3e049079ec.png)

![image](https://user-images.githubusercontent.com/14881741/43834889-df96ffa4-9add-11e8-8cb5-47edf5c95944.png)


You can also tag multiple people to give to more than one person at a time. Heycoconut will also DM the person giving the coconuts to let them know how many coconuts they have left. 

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
    
