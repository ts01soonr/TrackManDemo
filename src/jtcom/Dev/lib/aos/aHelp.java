/**
 *
 */
package jtcom.Dev.lib.aos;

/**
 * @author fang
 *
 */

public class aHelp {

    /**
     ** output help message
     */
    public static boolean has(String txt) {
        for (aCommand cmd : aCommand.values())
            if (cmd.toString().equals(txt)) return true;
        return false;

    }

    public static String print() {
        String txt = "";
        for (aCommand cmd : aCommand.values())
            txt += cmd + "-";
        return txt + aCommand.values().length;
    }

    public static aCommand get(String txt) {
        for (aCommand cmd : aCommand.values())
            if (cmd.toString().equals(txt)) return cmd;
        return null;
    }
    public static boolean hasWildcard(String txt) {
        if (txt.contains("*")) return true;
        if (txt.contains("?")) return true;
        if (txt.contains("[")) return true;
        if (txt.contains("]")) return true;
        if (txt.contains("|")) return true;
        return false;
    }

    public static String search(String txt) {
        if (hasWildcard(txt)) {
            String list = "";
            for (aCommand cmd : aCommand.values()) {
                if (cmd.toString().matches(txt.replace("?", ".?").replace("*", ".*?")))
                    list += cmd.toString() + "-";
            }
            return list;
        }
        return txt;
    }
    public static String getByInfo(){
        String txt="\r\n";
        txt += "  #ID  -> dk.TrackMan.Range:id\r\n";
        txt += "  $ID  -> android:id\r\n";
        txt += "  @TXT -> //android.widget.TextView[@Text=.]\r\n";
        txt += "  !TXT -> //LinearLayout[@content-desc=.]\r\n";
        txt += "  //XP -> By.xpath[XP]";
        return txt;
    }
    public static String print(String cmd) {
        if (cmd.length() == 0) return print();
        String txt = "";
        aCommand C = get(cmd);
        if (C == null) return "Unknown command";
        switch (C) {
            //aos2 cmd
            case air:
                txt = "[on|off] turn on or turn off airplane-mode";
                break;
            case back:
                txt = "navigate back";
                break;
            case up:
            case down:
            case left:
            case right:
                txt = "navigate direction";
                break;
            case install:
            case uninstall:
                txt = "apk installation";
                break;
            case clean:
                txt = "clean apk setting/cache";
                break;
            case version:
                txt = "check version info of trackMan app";
                break;
            case isv:
                txt = "check whether trackMan is installed";
                break;
            case stop:
            case start:
            case restart:
            case reload:
                txt = "control trackMan via given operation";
                break;
            case xml:
                txt = "return xml of the current screen";
                break;
            case pwd:
                txt = "return current activity path";
                break;
            case save2:
                txt = "takescreen and xml under log/a.xml|i.png";
                break;
            case session:
            case sid:
                txt = "return appium sessionId";
                break;
            case view:
                txt = "output text info the current screen";
                break;
            case check:
                txt = "output sessionId together text info";
                break;
            case has:
                txt = "check whether it had find given element"+getByInfo();
                break;
            case click:
                txt = "operate given element"+getByInfo();
                break;
            case wait:
                txt = "wait given element to show up"+getByInfo();
                break;
            case w4c:
                txt = "wait and then click"+getByInfo();
                break;
            case w4d:
                txt = "wait element until it is disappear"+getByInfo();
                break;
            case key:
                txt = "code [repeat] \r\n";
                txt += "  //send given key once\r\n";
                txt += "  //send given key with given repeat number";
                break;
            case send:
            case fill:
                txt = " [element] [text]  \r\n";
                txt += "    //aos2 fill #name Peter";
                break;
            case pos:
                txt = " output x/y postion of given element";
                break;
            case kill:
                txt = "terminate the current running emulator";
                break;
            case quit:
                txt = "terminate current appium session";
                break;
            case guest:
                txt = "continue as guest";
                break;
            case demo:
                txt = "[start|exit|result|again|around|close|quit]\r\n";
                txt += "  //fx aos2 demo start  -start demo session \r\n";
                txt += "  //fx aos2 demo play   -play 3 rounds\r\n";
                txt += "  //fx aos2 demo again  -play again\r\n";
                txt += "  //fx aos2 demo around -play a round\r\n";
                txt += "  //fx aos2 demo result -check Final results\r\n";
                txt += "  //fx aos2 demo close  -Close results screen\r\n";
                txt += "  //fx aos2 demo result -view results and close\r\n";
                txt += "  //fx aos2 demo exit   -exit Demo";
                break;
            default:
                System.out.println("not implement yet");
                break;
        }

        return txt;
    }

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        //Help.print(Command.bye);
        System.out.println(aHelp.print("demo"));
        System.out.println(aHelp.search("*k"));

        //System.out.println(Help.has("byes"));
    }

}
