package jtcom.Dev.lib;

import io.appium.java_client.AndroidKeyCode;
import io.appium.java_client.AppiumDriver;
import jtcom.Dev.lib.aos.*;
import jtcom.lib.Datainfo;
import jtcom.lib.Sys;
import jtcom.lib.Web;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.JavascriptExecutor;

import java.io.File;


public class aOStm {
    // perform control to Android device, which
    private final static Logger log = Sys.getLogger("[aOStm]");
    private final static boolean isWin = Sys.isWindows();
    public static String rid = "";
    private static final boolean SR = false; //screen record
    private static final String[] cfg = Datainfo.aOS2.split(":");
    private static final String version = "";
    private static String url = aBase.APKLink;
    private static boolean timeout = true;
    private static int tc = 0;
    private static aBase app = null;
    private static AppiumDriver driver = null;
    private static String cmd = "";
    private static String result = "";

    public aOStm() {
        log.info("Android Object is created");
    }

    public static AppiumDriver getDriver() {
        if (driver == null) return null;
        if (driver.getSessionId() == null) return null;
        return driver;
    }

    public static aBase getAPP() {
        return app;
    }

    public static String quit() {
        if (driver != null) {
            try {
                driver.quit();
            } catch (Exception e) {
            }
        }
        driver = null;
        app = null;
        timeout = true;
        tc = 0;
        return "";
    }

    public static void init(boolean reset) {
        /*
         * reset:false -> initial app without reset
         * reset:true -> perform full reset
         */
        quit();
        driver = aBase.newdriver(reset);
        if (driver == null) return;
        int n = reset ? 60 : 30;
        app = new aTrackMan(driver);
        if (reset) {
            String skipLink="#skipTextView";
            Sys.sleep("waiting for @Skip after full reset..", 15);
            if(!app.has(skipLink)) Sys.sleep("waiting for @Skip once more..", 15);
            app.wait4(skipLink, n);
            if (app.W4C(skipLink))
                app.continueAsGuest();
            else
                Sys.sleep(10);
        }
        app.continueAsGuest();
        timeout = false;
    }

    public static void init() {
        if (app == null) init(false);
        if (app == null) init(true);
        app.skipWelcome();
        app.continueAsGuest();
    }

    public static String start() {
        cmd = "adb shell am start -n " + aBase.appPackage + "/" + aBase.appActivity;
        result = Sys.shell(cmd).trim();
        //log.info(result);
        return result;
    }

    public static String stop() {
        cmd = "adb shell am force-stop " + aBase.appPackage;
        result = Sys.shell(cmd).trim();
        log.info("stop andriod agent");
        return result;
    }

    public static String version() {
        cmd = "adb shell dumpsys package " + aBase.appPackage + " | " + (isWin ? "findstr" : "grep") + " version";
        result = Sys.shell(cmd).trim();
        int v = result.indexOf("versionName=");
        if (v != -1)
            result = result.substring(v + 12);
        v = result.indexOf("\r\n");
        if (v != -1)
            result = result.substring(0, v);
        //log.info(result);
        return result;
    }

    public static String ping() {
        if(tc != 0)
            try {
                log.info("ping to appium server");
                JavascriptExecutor js = driver;
                js.executeScript("mobile: deviceInfo");
            } catch (Exception e) {
                log.info("recreate session after unexpected timeout");
                driver.quit();
                driver=null;
                init();

            }
        return "";
    }

    public static String ping2() {
        //keep appium session live
        if (tc == 0)
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (!timeout) {
                        tc++;
                        log.info("appium[HB]:"+tc);
                        ping();
                        for(int i=0;i<2*6;i++){
                            //ping every 2 minutes
                            Sys.delay(10*1000);
                            if(timeout) break;
                        }
                    }
                    log.info("appium[HB]:exit");
                }
            }).start();
        return "";
    }
    public void setURL(String www) {
        url = www;
    }

    public String restart() {
        stop();
        Sys.sleep(1);
        start();
        return "";
    }

    public String reload() {
        //adb uninstall -k
        exec("key 3");
        Sys.sleep(2);
        cmd = "adb shell am start -n " + aBase.appPackage + "/" + aBase.appActivity;
        result = Sys.shell(cmd).trim();
        log.info("reload");
        Sys.sleep(2);
        return "";
    }

    public String kill() {
        //adb uninstall -k
        quit();
        cmd = "adb -s emulator-5554 emu kill";
        result = Sys.shell(cmd).trim();
        log.info("kill emulator");
        return result;
    }

    public String clean() {
        //adb uninstall -k
        cmd = "adb shell pm clear " + aBase.appPackage;
        result = Sys.shell(cmd).trim();
        log.info("clear app data");
        return result;
    }

    public String uninstall() {
        //adb uninstall -k
        cmd = "adb uninstall " + aBase.appPackage;
        result = Sys.shell(cmd).trim();
        log.info("unistall app ");
        return result;
    }

    public String install() {
        String fapk = aBase.getAPK();
        if (!new File(fapk).exists()) Web.saveAs(url, fapk);
        cmd = "adb install " + fapk;
        log.info(cmd);
        result = Sys.shell(cmd).trim();
        log.info(result);
        return result;
    }

    public boolean isinstalled() {
        if (driver == null) return false;
        return driver.isAppInstalled(aBase.appPackage);
    }

    public String login(String un, String pwd) {
        //init(true);
        result = "TODO-pending";
        return result;
    }

    public String reset() {
        init(true);
        //pending
        return result;
    }

    public String view() {
        init();
        aTrackMan pro = (aTrackMan) app;
        result = app.getContent();
        return result;
    }

    public String sendKey(int k, int times) {
        if (driver == null) return "null";
        for (int i = 0; i < times; i++)
            driver.sendKeyEvent(k);
        return "";
    }

    public String sendKey(int k) {
        return sendKey(k, 1);
    }

    public String back() {
        if (driver == null) return "null";
        driver.sendKeyEvent(AndroidKeyCode.BACK);
        Sys.delay(1000);
        return AndroidKeyCode.BACK + "";
    }

    public String up() {
        if (driver == null) return "null";
        app = new aTrackMan(driver);
        app.scrollUp();
        return "";
    }

    public String down() {
        if (driver == null) return "null";
        app = new aTrackMan(driver);
        app.scrollDown();
        return "";
    }

    public String left() {
        if (driver == null) return "null";
        app = new aTrackMan(driver);
        app.swipeBack();
        return "";
    }

    public String right() {
        if (driver == null) return "null";
        app = new aTrackMan(driver);
        app.swipeNext();
        return "";
    }

    public String session() {
        if (driver == null) return "null";
        else
            return driver.getSessionId().toString();
    }

    public String exec(String command) {
        log.info("exec " + command);
        String[] tokens = command.split(" ");
        String cmd = tokens[0];
        String p2 = tokens.length > 1 ? tokens[1] : "";
        String p3 = tokens.length > 2 ? tokens[2] : "";
        String p4 = tokens.length > 3 ? tokens[3] : "";
        String p5 = tokens.length > 4 ? tokens[4] : "";
        String p6 = tokens.length > 5 ? tokens[5] : "";
        String p7 = tokens.length > 6 ? tokens[6] : "";
        String p27 = (p2 + " " + p3 + " " + p4 + " " + p5 + " " + p6 + " " + p7).trim();
        if (cmd.isEmpty()||cmd.equals("help")) return aHelp.print(p2);
        else if (cmd.equals("quit")) return quit();
        else if (cmd.equals("settings")) return aSettings.getBattery();
        else if (cmd.equals("stop") || cmd.equals("stopagent")) return app.Stop();
        else if (cmd.equals("start") || cmd.equals("startagent")) return app.Start();
        else if (cmd.equals("restart") || cmd.equals("rsagent")) return app.Restart();
        else if (cmd.equals("air")) return app.ADBAirplane(p2);
        else if (cmd.equals("version")) return "trackman.golf="+version();
        else if (cmd.equals("clean")) return clean();
        else if (cmd.equals("kill")) return kill();
        else if (cmd.equals("reload")) return reload();
        else if (cmd.equals("init")) {init(true);return "";}
        else if (cmd.equals("login")) return login(p2, p3);
        else if (cmd.equals("uninstall")) return uninstall();
        else if (cmd.equals("install")) return install();
        else if (cmd.equals("isinstall")) return isinstalled() + "";
        else if (cmd.equals("pwd") || cmd.equals("url")) return driver.currentActivity();
        else if (cmd.equals("reset")) return reset();
        else if (cmd.equals("save2")) {app.save2();return "";}
        else if (cmd.equals("back")) return back();
        else if (cmd.equals("up")) return up();
        else if (cmd.equals("down")) return down();
        else if (cmd.equals("left")) return left();
        else if (cmd.equals("right")) return right();
        else if (cmd.equals("session") || cmd.equals("sid")) return session();
        else if (cmd.equals("env")) return Datainfo.aOS2;
        else if (cmd.equals("ping")) return ping();
        else if (cmd.equals("apk")) return aBase.setAPK(command.substring(4));
        else if (cmd.equals("xml")) return app.getXML();
        else if (cmd.equals("has")) return app.has(p27) + "";
        else if (cmd.equals("wait")) return app.wait4(p27) + "";
        else if (cmd.equals("w4c")) return app.W4C(p27) + "";
        else if (cmd.equals("w4d")) return app.wait4disappear(p27) + "";
        else if (cmd.equals("account")) return app.account(p27) + "";
        else if (cmd.equals("key")) return app.sendKey(Integer.valueOf(p2), p3.isEmpty() ? 1 : Integer.valueOf(p3));
        else if (cmd.equals("pos")) return app.outputXY(p27);
        else if (cmd.equals("isv")) return app.isVisible(p27) ? "true" : "false";
        else if (cmd.equals("send") || cmd.equals("fill")) {
            if (app.isVisible(p2)) app.send(p2, p3);
            return "";
        } else if (cmd.equals("click")) {
            if (StringUtils.isNumeric(p2) && StringUtils.isNumeric(p3)) app.tapOnXY(p2, p3);
            else app.click(p27);
            return "";
        } else if (cmd.equals("view")) {
            init();
            if (p2.equals(".")) return "[...]";
            return view();
        } else if (cmd.equals("check")) {
            if (app == null) return "null";
            app = new aTrackMan(driver);
            return session() + ":" + app.getContent() + ":" + tc;
        } else if (cmd.equals("guest")) {
            if (app == null) return "null";
            app = new aTrackMan(driver);
            app.continueAsGuest();
            return app.has("#quickLoginButton") + "";
        } else if (cmd.equals("demo")) {
            if (app == null) return "null";
            return new aTrackDemo(driver).exec(p27);
        }
        return "aos2 help [..] FAIL";
    }

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//String apk=aOS[3].endsWith(".apk")?aOS[3]:new File("a/VFS"+aOS[3]+".apk").getAbsolutePath();

		aOStm app = new aOStm();
		//app.view();
		//log.info(app.driver.getSessionId());
		//app.driver.quit();
		//log.info(app.driver.getSessionId());
		log.info(app.exec("rid"));
		log.info("done");
		//log.info(app.version());
		//app.install();
		//log.info(app.uninstall());
		//app.reset();
		//ClientAPI C=new ClientAPI();
		//log.info(C.loginResult);
		//log.info(C.isOnline());
		//app.stop();
		//C.wait4off();
		//log.info(C.isOnline());
		//app.start();
		//C.wait4on();
		//log.info(C.isOnline());
		//C.logout();
	}
}
