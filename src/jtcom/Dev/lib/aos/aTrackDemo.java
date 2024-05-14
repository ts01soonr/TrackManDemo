package jtcom.Dev.lib.aos;


import jtcom.lib.Datainfo;
import jtcom.lib.Job;
import jtcom.lib.Sys;
import jtcom.Dev.lib.aOStm;
import org.apache.log4j.Logger;
import org.openqa.selenium.remote.RemoteWebDriver;


public class aTrackDemo extends aBase {
    private final static Logger log = Sys.getLogger("[aTrackDemo]");
    public String projects = "";
    public String[] OS = Datainfo.aOS2.split(":");
    private String sid = "";

    public aTrackDemo() { // default with reset
        if (driver == null) driver = aOStm.getDriver();
        if (driver != null) return;
        if (loadSession()) return;
        driver = newdriver(false);
        Sys.sleep(30);
        wait4Activity(30);
    }

    public aTrackDemo(boolean reset) {
        super(reset);
    }

    public aTrackDemo(RemoteWebDriver dr) {
        driver = (dr == null ? newdriver(false) : dr);
    }

    public aTrackDemo(String sid) {
        super(sid);
    }

    private synchronized boolean loadSession() {
        OS = load();
        IP = OS[0];
        if (!Datainfo.aOS2.equals(":")) if (Job.te == 0) return false; //Multi exectuion
        sid = RemoteExec("sid");
        if (sid.equals("null")) return false;
        driver = newRemotedriver(sid);
        RemoteExec("reload");
        Sys.sleep(10);
        //if(!wait4("$content")) RemoteExec("restart");
        return driver != null;
    }

    public void gotoMain() {
        skipWelcome();
        continueAsGuest();
        //exitDemo if it has previously Demo session
        exitDemo();
    }

    public void print() {
        log.info("I am TrackMan");
        log.info(getContent());
    }
    public String getSinglePoint(int nr){
        String shotPoint="#shotViewNumberViewNumberText";
        if(nr==0) nr=account(shotPoint);
        WE=find(shotPoint,nr-1);
        if(WE==null) return "0";
        return WE.getText();
    }
    public String hitShot(){
        String hitShotButton="#hitShotButton";
        if(has(hitShotButton)) WE.click();
        return WE==null?"FAIL":"";
    }
    public int playDemo() {
        int total = 0;
        for (int i = 1; i < 4; i++) {
            log.info("Round NR. " + i + " start");
            total += playAround();
        }
        return total;
    }

    public int playAround() {
        if (has("#btnPlayAgain")) WE.click();
        int total = 0;
        String[] points = {"","","",""};
        for (int i = 1; i < 4; i++) {
            log.info("@RULES=" + wait4("@RULES"));
            if (WE == null) break;
            Sys.sleep(3);
            click("#hitShotButton");
            Sys.sleep("click [hitShotButton]", 2);
            //log.info("@CARRY="+wait4("@CARRY"));
            log.info("wait-for [Points]");
            if(wait4("#tvPoints"))
                points[i] = WE.getText();
            else
                points[i]= getSinglePoint(i);
            total +=Integer.parseInt(points[i]);;
            wait4disappear("#tvPoints");
            //tvPoints
            Sys.sleep(i + ".point=" + points[i], 4);
        }
        points[0]=(Integer.parseInt(points[1])+Integer.parseInt(points[2])+Integer.parseInt(points[3]))+"";
        log.info("points[]="+Sys.arrayToString(points,":"));
        log.info("this_round_points=" + total);
        return total;
    }

    public int playAgain() {
        log.info("PLAY AGAIN");
        if (has("#btnPlayAgain")) WE.click();
        return playDemo();
    }

    public String getResult() {
        String points = "";
        log.info("VIEW RESULT");
        if (has("#btnShareResult"))
            WE.click();
        wait4("#tvPoints");
        points = WE.getText();
        log.info("total_points=" + points);
        return points;
    }

    public void exitDemo() {
        //exitDemoButton
        if (has("#exitDemoButton")) {
            log.info("Exit Demo");
            WE.click();
            W4C("#alertViewYesButton");
        }
    }

    public void quitGame() {
        //QUITDemoButton
        log.info("QUIT Demo");
        click("#btnQuitGame");
        W4C("#alertViewYesButton");
    }

    public void closeResult() {
        log.info("CLICK [X]");
        click("//android.widget.ImageView[@clickable=\"true\"]");
    }

    public void startDemo() {
        if (has("@RULES")) return;
        if(has("!Range")) gotoMain();
        if(!wait4("!Range")) return;
        log.info("click [RANGE]");
        WE.click();
        for (int i = 0; i < 4; i++) {
            if (has("#rangeDemoView")) break;
            log.info("scrollDown to [Demo]");
            scrollDown();
        }
        if (!has("#rangeDemoView")) return;
        log.info("click [TRY-THE-DEMO]");
        WE.click();
        log.info("wait&click [START-DEMO]");
        if(!W4C("#demoInfoStartDemoButton")) return;
        log.info("wait&click [BULLSEYE]");
        if(!W4C("#bullCardView")) return;
        log.info("wait&click [BAY]");
        if(!W4C("#bayTypeSelectionBayText")) return;
        log.info("wait&click [1]");
        if(!W4C("@1")) return;
        String name = "#etPlayerName";
        log.info("wait&input_player [Mr.Fang]");
        if(!wait4(name))    return;
        send(name, "Mr.Fang");
        log.info("wait&click [START GAME]");
        if(!W4C("#btnStartGame")) return;
        log.info("wait&click [CONTINUE]");
        if(!W4C("#gameHowToPlayContinue")) return;
    }

    public String exec(String command) {
        try {
            log.info("exec " + command);
            String[] tokens = command.split(" ");
            String cmd = tokens[0];
            String p2 = tokens.length > 1 ? tokens[1] : "";
            String p3 = tokens.length > 2 ? tokens[2] : "";
            String p4 = tokens.length > 3 ? tokens[3] : "";
            String p5 = tokens.length > 4 ? tokens[4] : "";
            String p6 = tokens.length > 5 ? tokens[5] : "";
            String p7 = tokens.length > 6 ? tokens[6] : "";
            String p27 = command.substring(cmd.length()).trim();
            String p37 = command.substring((cmd + " " + p2).trim().length()).trim();
            if (cmd.isEmpty()) {
                gotoMain();
                return "";
            } else if (cmd.equals("start")) {
                startDemo();
                Sys.sleep("loading", 5);
                return wait4("@RULES") ? "" : "FAIL";
            } else if (cmd.equals("play")) {
                int points = playDemo();
                return points + (wait4("#btnPlayAgain") ? "" : " FAIL");
            } else if (cmd.equals("exit")) {
                exitDemo();
                return "";
            } else if (cmd.equals("hit")) {
                return hitShot();
            } else if (cmd.equals("point")) {
                //if(p2.isEmpty()) return "[1,2.3] FAIL";
                if(p2.isEmpty()) p2="0";
                return getSinglePoint(Integer.parseInt(p2))+"";
            } else if (cmd.equals("result")) {
                String result = getResult();
                if (p2.equals("x")) closeResult();
                return result + "";
            } else if (cmd.equals("again")|| cmd.equals("playagain")) {
                int points = playAgain();
                return points + "";
            } else if (cmd.equals("around") || cmd.equals("playaround")) {
                return playAround() +"";
            } else if (cmd.equals("close")) {
                //close result
                closeResult();
                return "";
            } else if (cmd.equals("quit")) {
                //quit game
                quitGame();
                return "";
            } else if (cmd.equals("help")) {
                return "start|exit|result|again|around|close|quit|hit|point";
            }

        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return "[start|exit|result|again|around|close|quit|hit|point] FAIL";
    }

	public static void main(String[] args) {
		System.out.println("Testing android driver");
		//System.out.println("using remote driver");
		//WebDriver aa=aOS.driver();
		//Sys.sleep(10);
		//aa.quit();
		//System.out.println("remotedriver work fine");

		//System.out.println("Try Appium driver");
		boolean reset = false;
		String sid = "a64a5a6d-bd96-41a9-b329-7b9cba1cd569";
		aTrackDemo app = new aTrackDemo(sid);
		//app.continueAsGuest();
		//log.info(app.getResult());
		//app.startDemo();
		//app.playDemo();
		//app.getResult();
		if (true) return;
		System.out.println("done");
	}
}
