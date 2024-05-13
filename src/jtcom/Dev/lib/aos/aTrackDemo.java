package jtcom.Dev.lib.aos;


import jtcom.lib.Datainfo;
import jtcom.lib.Job;
import jtcom.lib.Sys;
import jtcom.lib.aOS;
import org.apache.log4j.Logger;
import org.openqa.selenium.remote.RemoteWebDriver;


public class aTrackDemo extends aBase {
    private final static Logger log = Sys.getLogger("[aTrackDemo]");
    public String projects = "";
    public String[] OS = Datainfo.aOS.split(":");
    private String sid = "";

    public aTrackDemo() { // default with reset
        if (driver == null) driver = aOS.getDriver();
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
        if (!Datainfo.aOS.equals(":")) if (Job.te == 0) return false; //Multi exectuion
        sid = RemoteExec("sid");
        if (sid.equals("null")) return false;
        driver = newRemotedriver(sid);
        RemoteExec("reload");
        Sys.sleep(10);
        //if(!wait4("$content")) RemoteExec("restart");
        return driver != null;
    }

    public void gotoMain() {
        if (has("@Skip")) WE.click();
        continueAsGuest();
        //exitDemo if it has previously Demo session
        exitDemo();
    }

    public void print() {
        log.info("I am TrackMan");
        log.info(getContent());
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
        for (int i = 1; i < 4; i++) {
            log.info("@RULES=" + wait4("@RULES"));
            if (WE == null) break;
            Sys.sleep(1);
            click("#hitShotButton");
            Sys.sleep("click [hitShotButton]", 2);
            //log.info("@CARRY="+wait4("@CARRY"));
            log.info("wait-for [Points]");
            wait4("#tvPoints");
            String point = WE.getText();
            total += Integer.parseInt(point);
            wait4disappear("#tvPoints");
            //tvPoints
            Sys.sleep(i + ".point=" + point, 3);
        }
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
        log.info("click [RANGE]");
        click("!Range");
        for (int i = 0; i < 4; i++) {
            if (has("#rangeDemoView")) break;
            log.info("scrollDown to [Demo]");
            scrollDown();
        }
        if (!has("#rangeDemoView")) return;
        log.info("click [TRY-THE-DEMO]");
        WE.click();
        log.info("wait&click [START-DEMO]");
        W4C("#demoInfoStartDemoButton");
        log.info("wait&click [BULLSEYE]");
        W4C("#bullCardView");
        log.info("wait&click [BAY]");
        W4C("#bayTypeSelectionBayText");
        log.info("wait&click [1]");
        W4C("@1");
        String name = "#etPlayerName";
        log.info("wait&input_player [Mr.Fang]");
        wait4(name);
        send(name, "Mr.Fang");
        log.info("wait&click [START GAME]");
        W4C("#btnStartGame");
        log.info("wait&click [CONTINUE]");
        W4C("#gameHowToPlayContinue");
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
                return "OK";
            } else if (cmd.equals("start")) {
                startDemo();
                Sys.sleep("loading", 5);
                return wait4("@RULES") ? "OK" : "FAIL";
            } else if (cmd.equals("play")) {
                int points = playDemo();
                return points + (wait4("#btnPlayAgain") ? " OK" : " FAIL");
            } else if (cmd.equals("exit")) {
                exitDemo();
                return "OK";
            } else if (cmd.equals("result")) {
                String result = getResult();
                if (p2.equals("x")) closeResult();
                return result + " OK";
            } else if (cmd.equals("again")) {
                int points = playAgain();
                return points + " OK";
            } else if (cmd.equals("around") || cmd.equals("playaround")) {
                return playAround() + " OK";
            } else if (cmd.equals("close")) {
                //close result
                closeResult();
                return "OK";
            } else if (cmd.equals("quit")) {
                //quit game
                quitGame();
                return "OK";
            } else if (cmd.equals("help")) {
                return "start|exit|result|again|around|close|quit";
            }

        } catch (Exception e) {

        }
        return "unknown_cmd FAIL";
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
