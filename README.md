
![Logo](https://demo.italle.dk/img/computers.png)


# TrackMan Golf App (Android) Demo

Here is automation setup via using Appium with Java for
Automate playing a Bullseye game in Demo mode as a guest player and verify the final results.




## Task Details

Automate playing a Bullseye game in Demo mode as a guest player:

```
  a. Total number of players: 1
  b. Number of rounds: 3 rounds (As game settings).
  c. validating the score while playing against the final results.
```
    
## Video Demo

Link to video recording [https://us.workplace.datto.com/filelink/680c-7a1f8f6e-8f48e45b8e-2 ]


## Command & Examples

```bash
telnet localhost 9999
aos2 settings
aos2 init
aos2 view
aos2 save2
aos2 demo start
aos2 demo play
aos2 has @Skip
aos2 demo help
....
bye

```


## Running Tests

To run tests, run the following command

```bash
  npm run test
```


## System Environment

To run this project, you will need to add the following environment to your System

`Java 8 [8 or 11]`

`APPIUM 1.X[v1.15 or v1.22]`

`Android Emulator[Nexus 6, API-level 30]`



## Run Locally

Start Appium & Emulator

```bash
  emulator.exe -verbose -avd [Nexus6] -writable-system -memory 4096
  Appium 
```

Clone the project
```bash
  git clone https://github.com/ts01soonr/TrackManDemo
```

Go to the project directory
```bash
  cd TrackManDemo
```

Start the server

```bash
  start_demo.bat    //for Windows
  start_demo.sh     //for Mac/Linux
  .....
```

Check the server via telnet after server is started

```bash
  telnet localhost 9999
```
Setup AppiumDriver inside telnet session

```bash
  aos2 settings
```
Initial TrackMan API inside telnet session

```bash
  aos2 init
```

If all good, then run scripts for playing a Bullseye game
Initial TrackMan API inside telnet session

```bash
  run jtcom.Dev.TestTrackManDemo
```
## run via GitHub Action

To deploy this project run

```bash
  npm run deploy
```

