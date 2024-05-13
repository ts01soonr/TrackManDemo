package jtcom.Dev.lib.aos;


import jtcom.lib.Datainfo;
import jtcom.lib.Job;
import jtcom.lib.Sys;
import jtcom.lib.aOS;
import org.apache.log4j.Logger;
import org.openqa.selenium.remote.RemoteWebDriver;


public class aTrackMan extends aBase {
    private final static Logger log = Sys.getLogger("[aTrackMan]");

    public String projects = "";
    public String[] OS = Datainfo.aOS.split(":");
    private String sid = "";

    public aTrackMan() { // default with reset
        if (driver == null) driver = aOS.getDriver();
        if (driver != null) return;
        if (loadSession()) return;
        driver = newdriver(false);
        Sys.sleep(30);
        wait4Activity(30);
    }

    public aTrackMan(boolean reset) {
        super(reset);
    }

    public aTrackMan(RemoteWebDriver dr) {
        driver = (dr == null ? newdriver(false) : dr);
    }

    public aTrackMan(String sid) {
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
        if (!has("#quickLoginButton")) Restart();
    }

    public void print() {
        log.info("I am TrackMan");
        log.info(getContent());
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
		aTrackMan app = new aTrackMan(sid);
		//app.continueAsGuest();
		//log.info(app.getResult());

		if (true) return;
		System.out.println("done");
	}
}
