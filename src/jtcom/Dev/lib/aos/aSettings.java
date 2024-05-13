package jtcom.Dev.lib.aos;

import org.openqa.selenium.remote.RemoteWebDriver;

public class aSettings extends aBase {
    /*
     * Launch Setting->viewBattery
     */
    Boolean sandbox = null;

    public aSettings() { // default with reset
        driver = newdriver(sandbox);
    }

    public aSettings(boolean reset) {
        driver = newdriver(reset);
    }

    public aSettings(RemoteWebDriver dr) {
        driver = dr;
    }

    public static String getBattery() {
        String usage = null;
        aSettings ad = new aSettings();
        usage = ad.viewBattery();
        ad.quit();
        return usage;
    }
    public void print() {
        System.out.println("I am on .Setting");
    }

    public String viewBattery() {
        String batterId = "//*[@resource-id='com.android.settings:id/battery_percent']";
        if (wait4("//*[@text='Battery']")) {
            WE.click();
            if (wait4(batterId)) {
                log.info("Battery=" + WE.getText());
                return WE.getText();
            }
        }
        return null;
    }

    public static void main(String[] args) {
        aSettings ad = new aSettings();
        ad.viewBattery();
        ad.quit();
    }
}
