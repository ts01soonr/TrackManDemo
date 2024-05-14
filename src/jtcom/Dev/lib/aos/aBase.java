package jtcom.Dev.lib.aos;

import io.appium.java_client.AndroidKeyCode;
import io.appium.java_client.AppiumDriver;
import jtcom.lib.Datainfo;
import jtcom.lib.Requester;
import jtcom.lib.Sys;
import jtcom.lib.Wdrive;
import org.apache.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Fang on 16/10/14.
 */
public abstract class aBase {

    ///////// help method for play Android app
    final static Logger log = Sys.getLogger("[Andriod]");
    private static final String AID = "android:id/";
    private static final String XID = "//android.widget.";
    public static String appPackage = "dk.TrackMan.Range";
    private static final String PID = appPackage + ":id/";
    public static String appInitActivity = "dk.TrackMan.SplashScreen";
    public static String appActivity = "dk.TrackMan.SplashScreen";
    public static String APKLink = "https://us.workplace.datto.com/filelink/6813-7a1f89c4-6b6001872b-2";
    private static String[] aOS = Datainfo.aOS2.split(":");
    public static String RID = aOS.length == 6 ? aOS[5] : Datainfo.RID;
    public WebElement WE = null;
    public By BY = null;
    public String IP = aOS[0];
    public RemoteWebDriver driver = null;
    public AppiumDriver adriver = null;
    private String cmd = "", result = "";

    public aBase() {
    }

    public aBase(boolean reset) {
        driver = newdriver(reset);
    }

    public aBase(String sid) {
        driver = newRemotedriver(sid);
    }

    public aBase(RemoteWebDriver dr) {
        driver = dr;
    }

    public synchronized static String[] load() {
        if (Datainfo.aOS2.contains(":")) return Datainfo.aOS2.split(":");
        aOS = Sys.getRes("aOS").split(":");
        RID = aOS.length == 6 ? aOS[5] : Datainfo.RID;
        return aOS;
    }

    public synchronized static void release(String[] cfg) {
        if (Datainfo.aOS2.contains(":")) return;
        Sys.putRes("aOS", Sys.arrayToString(cfg, ":"));
    }

    public synchronized static void release() {
        if (Datainfo.aOS2.contains(":")) return;
        Sys.putRes("aOS", Sys.arrayToString(aOS, ":"));
        aOS = Datainfo.aOS2.split(":");
        RID = aOS.length == 6 ? aOS[5] : Datainfo.RID;
    }

    public static String setAPK(String path) {
        aOS[4] = path;
        return getAPK();
    }

    public static String getAPK() {
        if (aOS.length < 5) return "";
        String apath = aOS[4];
        if (apath.startsWith("//"))
            return apath.replace("//", "C:/").replaceAll("/", "\\\\");
        if (!aOS[0].equals("127.0.0.1")) return apath;
        if (apath.startsWith("/")) if (Sys.isWindows()) apath = "C:" + apath;
        File APK = new File(apath);
        String fullapk = new File(apath).getAbsolutePath();
        if (!APK.exists()) {
            log.info("download .." + fullapk);
            Sys.wget(fullapk, APKLink);
        }
        return fullapk;
    }

    public synchronized static String RemoteExec(String cmd) {
        Requester R = new Requester(aOS[0] + ":9999");
        if (!R.isOK) return "null";
        String exec = cmd.startsWith("adb ") ? "run " : "aos2 ";
        String info = R.getInfo(exec + cmd);
        log.info(info);
        R.disconnect();
        return info;
    }

    public synchronized static DesiredCapabilities sandboxCaps() {
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability("appPackage", "com.android.settings");
        caps.setCapability("takesScreenshot", true);
        caps.setCapability("automationName", "UIAutomator2");
        caps.setCapability("platformName", "Android");
        caps.setCapability("appActivity", ".Settings");
        caps.setCapability("platformVersion", aOS[3]);
        caps.setCapability("deviceName", aOS[2]);
        caps.setCapability("noReset", true);
        return caps;
    }

    public synchronized static DesiredCapabilities initCaps(Boolean reset) {
        if (reset == null) return sandboxCaps();
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability("appPackage", appPackage);
        caps.setCapability("takesScreenshot", true);
        caps.setCapability("automationName", "UIAutomator2");
        caps.setCapability("platformName", "Android");
        caps.setCapability("newCommandTimeout", 600); //number of seconds[600=10 minutes]
        String apk2 = getAPK();
        log.info("apk=" + apk2);
        caps.setCapability("app", apk2);
        caps.setCapability("platformVersion", aOS[3]);
        caps.setCapability("deviceName", aOS[2]);
        if (reset)
            caps.setCapability("appActivity", appActivity);
        else {
            caps.setCapability("noReset", true);
            caps.setCapability("skipDeviceInitialization", true);
            caps.setCapability("skipServerInstallation", true);
            caps.setCapability("appActivity", appActivity);
            if (aOS.length == 6) Datainfo.RID = aOS[5];
        }
        return caps;
    }

    public synchronized static RemoteWebDriver newRemotedriver(Boolean reset) {
        //
        log.info("loading remotedriver .." + Sys.arrayToString(aOS, ":"));
        String url = "http://" + aOS[0] + ":" + aOS[1] + "/wd/hub";
        //default Android emulator located at 192.168.1.153
        try {
            return new RemoteWebDriver(new URL(url), initCaps(reset));
        } catch (MalformedURLException e) {
            log.info("Appium status at " + url);
            log.info("Appium server might be not running or Emulator is crash");
            e.printStackTrace();
        }
        return null;

    }

    public synchronized static RemoteWebDriver newRemotedriver(String sessionId) {
        //loading driver from appium session
        log.info("loading from sid = " + sessionId);
        String url = "http://" + aOS[0] + ":" + aOS[1] + "/wd/hub";
        try {

            return new aRemote(new URL(url), sessionId);
        } catch (Exception e) {
            log.info("Appium status at " + url);
            log.info("Appium session expired, reinitialize session now");
            //e.printStackTrace();
        }
        return newdriver(false);
    }

    //Using AppiumDriver.  java_client_1.2
    public synchronized static AppiumDriver newdriver(Boolean reset) {

        log.info("loading AppiumDriver from " + Sys.arrayToString(aOS, ":"));
        String url = "http://" + aOS[0] + ":" + aOS[1] + "/wd/hub";
        try {
            return new AppiumDriver(new URL(url), initCaps(reset));
        } catch (Exception e) {
            // Fail and retry
            log.info("Appium status at " + url);
            log.info("Appium crash, retry after 30 seconds");
            try {
                Sys.sleep(30);
                return new AppiumDriver(new URL(url), initCaps(reset));
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        return null;
    }

    public void save2() {
        // user for debug, takescreen and xml file;
        Wdrive.save2(driver);
    }

    public void saveScreen(String fn) {
        // user for debug, takescreen and xml file;
        File logi = new File("log/i" + fn + ".png");
        Wdrive.saveScreen(driver, logi);
    }

    public void saveScreen(File fn) {
        Wdrive.saveScreen(driver, fn);
    }

    public By getBy(String txt) {
        if (txt.startsWith("//")) return By.xpath(txt);
        By by = By.xpath(XID + txt); // default xpath
        String subs = txt.substring(1);
        if (txt.startsWith("#")) return By.id(PID + subs);
        if (txt.startsWith("$")) return By.id(AID + subs);
        if (txt.startsWith("@")) return By.xpath(XID + "TextView[@text='" + subs + "']"); //@Projects
        if (txt.startsWith("!")) return By.xpath(XID + "LinearLayout[@content-desc='" + subs + "']");
        if (txt.startsWith("&")) {
            if (txt.equals("&+")) return By.id(PID + "fab_button");
            if (txt.equals("&UP")) return By.xpath(XID + "ImageButton[@content-desc='Navigate up']");
            if (txt.equals("&Allow")) return By.id("com.android.permissioncontroller:id/permission_allow_button");
            if (txt.equals("&Strike")) return By.xpath(XID + "ImageButton[@content-desc='Strike Out']");
        }

        return by;
    }

    public WebElement find(String txt) {
        //recur find
        String[] list = txt.split(">");
        WE = null;
        for (String i : list)
            if (!hasWE(i)) return null;
        return WE;
    }

    public WebElement find(String txt, int index) {
        return driver.findElements(getBy(txt)).get(index);
    }

    public int account(String txt) {
        List<WebElement> list = driver.findElements(getBy(txt));
        if (!list.isEmpty()) WE = list.get(0);
        return list.size();
    }

    public void click(String txt) {
        log.info(txt);
        try {
            find(txt).click();
        } catch (Throwable t) {
            Sys.sleep(2);
            if (has(txt)) WE.click();
        }
    }

    public void clickB(String txt) {
        log.info(txt);
        wait4("Button[@text='" + txt + "']");
        click("Button[@text='" + txt + "']");
    }

    public void clickT(String txt) {
        log.info(txt);
        wait4("TextView[@text='" + txt + "']");
        click("TextView[@text='" + txt + "']");
    }

    public void click(String txt, int index) {
        log.info(txt);
        find(txt, index).click();
        Sys.delay(2000);
    }

    public void send(String txt, String msg) {
        if (wait4(txt)) {
            find(txt).clear();
            find(txt).sendKeys(msg);
        }
    }

    public boolean clickE(String by) {
        boolean result = false;
        int attempts = 0;
        while (attempts < 2) {
            try {
                driver.findElement(getBy(by)).click();
                result = true;
                break;
            } catch (StaleElementReferenceException e) {
            }
            attempts++;
        }
        return result;
    }

    public boolean W4C(String txt) {
        //wait-and-then-click
        if (!wait4(txt)) RemoteExec("reload");
        if (!wait4(txt)) return false;
        WE.click();
        return true;
    }

    public boolean wait4(String txt) {
        return wait4(txt, 30);
    }

    public boolean wait4(String txt, int n) {
        try {
            WE = null;
            WebDriverWait wait = new WebDriverWait(driver, n);
            WE = wait.until(ExpectedConditions.visibilityOfElementLocated(getBy(txt)));
            return true;
        } catch (Throwable t) {
            log.info("can not find element " + txt);
            return false;
        }
    }

    public boolean wait_4_exists(String ele, int s) {
        int i = 1;
        while (true) {
            if (has(ele)) return true;
            Sys.sleep(2 * i);
            if (i * (i + 1) >= s) break;
            if (i == 5) scrollUp();
            i++;
        }
        return false;
    }

    // if file is deleted, return null;
    public boolean wait_4_deleted(String ele, int s) {
        int i = 1;
        String fid = null;
        while (true) {
            if (!has(ele)) return true;
            Sys.sleep(2 * i);
            if (i * (i + 1) >= s) break;
            if (i == 5) scrollUp();
            i++;
        }
        return false;
    }

    public boolean wait4showup(String txt) {
        for (int i = 0; i < 30; i++) { // 30 sec
            Sys.delay(1000);
            WE = find(txt);
            if (WE == null) continue;
            if (WE.isDisplayed()) return true;
        }
        return false;
    }

    public boolean wait4disappear(String txt) {
        try {
            for (int i = 0; i < 30; i++) { // 30 sec
                Sys.delay(1000);
                WE = find(txt);
                if (WE == null) return true;
                if (!WE.isDisplayed()) return true;
            }
        } catch (StaleElementReferenceException e) {
        }
        return false;
    }

    public void wait4Activity(int n) {
        if (driver instanceof AppiumDriver) {
            String pwd = ((AppiumDriver) driver).currentActivity();
            for (int i = 0; 10 * i < n; i++) { //
                Sys.sleep(10);
                //if(pwd.equals(appMainActivity)) break;
                if (pwd.equals(appActivity)) break;
                if (pwd.endsWith(".OnboardingActivity")) break;
                if (pwd.endsWith(".MainActivity")) break;

            }
        }
    }

    public boolean has(String txt) {
        //simple-check
        try {
            WE = null;
            WE = driver.findElement(getBy(txt));
            return WE != null;
        } catch (Throwable t) {
            return false;
        }
    }

    public boolean hasWE(String txt) {
        //support recur element allocator base on previous search
        if (WE == null) return has(txt);
        try {
            WE = WE.findElement(getBy(txt));
        } catch (Throwable t) {
            WE = null;
        }
        return WE != null;
    }

    public boolean hasR(String txt) {
        //support recur element allocator base on previous search
        WE = null;
        String[] list = txt.split(">");
        for (String i : list)
            if (!hasWE(i)) return false;
        return true;
    }

    public void Launch() {
        //if(driver instanceof RemoteWebDriver ) return;
        String pwd = ((AppiumDriver) driver).currentActivity();
        if (!pwd.contains("trackman")) ((AppiumDriver) driver).launchApp();
        Sys.sleep(2);
    }

    public String Start() {
        if (driver instanceof AppiumDriver) {
            ((AppiumDriver) driver).launchApp();
        } else RemoteExec("start");
        return result;
    }

    public String Stop() {
        if (driver instanceof AppiumDriver) {
            ((AppiumDriver) driver).closeApp();
        } else RemoteExec("stop");
        return result;
    }

    public String Restart() {
        log.info("restart application");
        if (driver instanceof AppiumDriver) {
            ((AppiumDriver) driver).closeApp();
            Sys.sleep(1);
            ((AppiumDriver) driver).launchApp();
        } else RemoteExec("restart");
        wait4("$content", 60);
        return "";
    }

    public String ADBWiFi(boolean on) {

        return "";
    }

    public String ADBAirplane(String on) {
        if (driver instanceof AppiumDriver) {
            cmd = "adb shell am start -a android.settings.AIRPLANE_MODE_SETTINGS";
            result = Sys.shell(cmd).trim();
            String text = "";
            Sys.sleep(2);
            //wait4("$switch_widget");
            if (has(("$switch_widget"))) {
                text = WE.getText();
                log.info(text);
                if (on.isEmpty()) return text;
                if (text.contains(on.toUpperCase())) text = " [already] ";
                else {
                    text = " [click]";
                    WE.click();
                }
                Sys.sleep(1);
                ((AppiumDriver) driver).sendKeyEvent(AndroidKeyCode.BACK);
            }
            return text;
        } else return RemoteExec("air " + on);
    }

    public String ADBstart() {
        cmd = "adb shell am start -n " + aBase.appPackage + "/" + aBase.appActivity;
        result = Sys.shell(cmd).trim();
        //log.info(result);
        return result;
    }

    public String ADBstop() {
        cmd = "adb shell am force-stop " + aBase.appPackage;
        result = Sys.shell(cmd).trim();
        log.info("stop andriod agent");
        return result;
    }

    public String ADBversion() {
        cmd = "adb shell dumpsys package  " + aBase.appPackage + " | " + (Sys.isWindows() ? "findstr" : "grep") + " version";
        result = Sys.shell(cmd).trim();
        int v = result.indexOf("versionName=");
        if (v != -1)
            result = result.substring(v);
        v = result.indexOf("\r\n");
        if (v != -1)
            result = result.substring(0, v);
        log.info(result);
        return result;
    }

    public String ADBrestart() {
        ADBstop();
        Sys.sleep(1);
        ADBstart();
        return "";
    }

    public boolean login(String UN, String SPWD) {
        log.info("login as " + UN + "/" + SPWD);
        //pending

        return wait4("#action_search");

    }

    public void continueAsGuest() {
        if (has("#guestTextView")) {
            WE.click();
            //if(!has("!RANGE"))
            wait4("#quickLoginButton");
        }
    }

    public void skipWelcome() {
        if (has("#skipTextView")) {
            WE.click();
            //if(!has("!RANGE"))
            wait4("#guestTextView");
        }
    }

    public String getText() {
        if (WE == null) return "";
        return WE.getAttribute("text");
    }

    public String getList() {
        //return text under id:list
        if (!has("$list")) return "";
        String list = "";
        if (has("#emptyText")) return WE.getAttribute("text");
        wait4("#label");
        for (WebElement w : WE.findElements(getBy("#label"))) {
            String name = w.getAttribute("text");
            if (list.isEmpty()) list = name;
            else list = list + ":" + name;
        }
        //Sys.println(list);
        return list;
    }

    public String getContent() {
        String list = "";
        for (WebElement w : driver.findElements(getBy("TextView"))) {
            if (!w.isDisplayed()) continue;
            String name = w.getAttribute("text");
            if (list.isEmpty()) list = "[" + name + "]";
            else list += "-[" + name + "]";
        }
        //log.info(list);
        return list;
    }

    public String getText(WebElement we) {
        if (we == null) return "";
        return we.getAttribute("text");
    }

    public void draw() {

        switch (Sys.getrnd(4)) {
            case 0:
                scrollUp();
                break;
            case 1:
                scrollDown();
                break;
            case 2:
                swipeNext();
                break;
            case 3:
                swipeBack();
                break;
        }
    }

    public void scrollDown() {
        if (driver instanceof AppiumDriver) {

            log.info("scrollDown");
            Dimension size = driver.manage().window().getSize();
            int rnd = Sys.getrnd(50);
            int starty = (int) (size.height * 0.70) - rnd;
            int endy = (int) (size.height * 0.30) - rnd;
            int startx = size.width / 2 - rnd;
            //System.out.println("starty = " + starty + " ,endy = " + endy + " , startx = " + startx);
            ((AppiumDriver) driver).swipe(startx, starty, startx, endy, 500);
            Sys.delay(1000);
        } else
            RemoteExec("down");

    }

    public void scrollUp() {
        if (driver instanceof AppiumDriver) {

            log.info("scrollUp");
            Dimension size = driver.manage().window().getSize();
            int rnd = Sys.getrnd(50);
            int starty = (int) (size.height * 0.70) + rnd;
            int endy = (int) (size.height * 0.30) + rnd;
            int startx = size.width / 2 + rnd;
            //System.out.println("starty = " + starty + " ,endy = " + endy + " , startx = " + startx);
            ((AppiumDriver) driver).swipe(startx, endy, startx, starty, 500);
            Sys.delay(1000);
        } else
            RemoteExec("up");
    }

    public void scrollTo(String txt) {

        HashMap<String, String> scrollObject = new HashMap<String, String>();

        //Must be an element with scrollable  property; Android (ListView,ScrollabeView) IOS (UIAScrollableView

        RemoteWebElement element = (RemoteWebElement) driver.findElement(By.className("android.widget.ListView"));
        JavascriptExecutor js = driver;
        String widId = element.getId();
        //Text for search on the screen
        scrollObject.put("text", txt);
        scrollObject.put("element", widId);
        js.executeScript("mobile: scrollTo", scrollObject);
    }

    public void swipeNext() {
        if (driver instanceof AppiumDriver) {
            log.info("swipe next");
            Dimension size = driver.manage().window().getSize();
            int rnd = Sys.getrnd(50);
            int startx = (int) (size.width * 0.70) + rnd;
            int endx = (int) (size.width * 0.30) + rnd;
            int endy = (int) (size.height * 0.50) + rnd;
            System.out.println("startx = " + startx + " ,endy = " + endy + " , endx = " + endx);
            ((AppiumDriver) driver).swipe(startx, endy, endx, endy, 500);
            Sys.delay(1000);
        } else
            RemoteExec("right");
    }

    public void swipeBack() {
        if (driver instanceof AppiumDriver) {

            log.info("swipe Back");
            Dimension size = driver.manage().window().getSize();
            int rnd = Sys.getrnd(50);
            int startx = (int) (size.width * 0.30) - rnd;
            int endx = (int) (size.width * 0.70) - rnd;
            int endy = (int) (size.height * 0.50) - rnd;
            System.out.println("startx = " + startx + " ,endy = " + endy + " , endx = " + endx);
            ((AppiumDriver) driver).swipe(startx, endy, endx, endy, 500);
            Sys.delay(1000);
        } else
            RemoteExec("left");

        //driver.swipe(50,400,100,400,500);
    }
    /////////////////////////////////////////////////////////////////////////////////////////////////////

    public void NavUp() {
        if (!has("&UP")) return;
        log.info("Navigate Up");
        WE.click();
    }

    public String Back() {
        if (driver == null) return "null";
        if (driver instanceof AppiumDriver) {
            ((AppiumDriver) driver).sendKeyEvent(AndroidKeyCode.BACK);
            Sys.delay(1000);
            return AndroidKeyCode.BACK + "";
        } else return RemoteExec("back");

    }

    public String sendKey(int key, int times) {
        if (driver == null) return "null";
        if (driver instanceof AppiumDriver) {
            for (int i = 0; i < times; i++)
                ((AppiumDriver) driver).sendKeyEvent(key);
            return "";
        } else return RemoteExec("key " + key + " " + times);

    }

    public boolean goTo(String page) {
        clickT(page);
        log.info("goTo->" + page);
        wait4showup("@" + page);
        wait4showup("#menu_list_context_menu");
        return has("@" + page);
    }

    public String outputXY(String txt) {

        if (txt.isEmpty()) return "missing";
        if (!has(txt)) return "";
        //x="-355" y="646" width="335" height="21"
        Point P = WE.getLocation();
        int x = P.getX();
        int y = P.getY();
        result = "x=" + x + " y=" + y;
        log.info(result);
        //((AppiumDriver)driver).swipe(x+5, y+5, x, y+5, 500);
        return result;

    }

    public boolean isVisible(String id) {
        if (has(id))
            return WE.isDisplayed();
        return false;
    }

    public void tapOnXY(String x, String y) {
        log.info("pending...");
    }

    public RemoteWebDriver getDriver() {
        return driver;
    }

    public AppiumDriver getADriver() {
        if (driver == null) return null;
        if (driver instanceof RemoteWebDriver) return null;
        if (driver instanceof AppiumDriver)
            adriver = (AppiumDriver) driver;
        return adriver;
    }

    public void quit() {
        if (driver instanceof AppiumDriver) {
            driver.quit();
            driver = null;
        }
    }

    public String sendKeyEvent(int key) {
        if (driver instanceof AppiumDriver) ((AppiumDriver) driver).sendKeyEvent(key);
        else return RemoteExec("key " + key);
        return "";
    }

    public String getXML() {
        String xml = driver.getPageSource();
        if (!xml.contains("\r\n"))
            xml = xml.replaceAll("\n", "\r\n");
        File fx = new File("log");
        if (fx.exists())
            Sys.saveTextFile(xml, "log/aos.xml");
        return xml;
    }

    public String Exec(String cmd) {
        return RemoteExec(cmd);
    }

}

//create remotewebdriver for Appium
class aRemote extends RemoteWebDriver {

    public aRemote(URL url, String sessionId) {
        super();
        setSessionId(sessionId);
        setCommandExecutor(new HttpCommandExecutor(url) {
            @Override
            public Response execute(Command command) throws IOException {
                if (command.getName() != "newSession") {
                    return super.execute(command);
                }
                return super.execute(new Command(getSessionId(), "getCapabilities"));
            }
        });
        startSession(new DesiredCapabilities());
    }

}

