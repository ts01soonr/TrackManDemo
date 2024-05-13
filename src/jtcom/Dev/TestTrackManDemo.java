package jtcom.Dev;

import jtcom.Dev.lib.aos.aTrackDemo;
import jtcom.lib.Sys;
import jtcom.Dev.lib.*;
import jtcom.lib.aos.aProjects;
import org.apache.log4j.Logger;
import org.junit.*;
import org.junit.rules.TestName;
import org.junit.runners.MethodSorters;

import java.io.File;

import static org.junit.Assert.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestTrackManDemo {

	/*
	 *  Junit Automation Template
	 *  @Fang
	 *  //demo command
	 *
	 *
	 */


	private static boolean go;
	private static String cls = "TestTrackManDemo",method,cfg;
	private static String TMP = Sys.DesktopTmp() + "/"+cls;
	private static aTrackDemo APP= null;
	private static String[] OS;
	final static Logger log = Sys.getLogger(cls);
	@Rule public TestName name = new TestName();

	@BeforeClass
	public static void tearUP() {
		APP=new aTrackDemo();
		OS=APP.OS;
		cfg=Sys.arrayToString(OS, ":");
		assertNotNull("Issue with Appium from "+cfg,APP.driver);
		APP.gotoMain();

		go=true;
	}
	@Before
	public void start() {
		method=name.getMethodName();
		System.out.println("* * * "+method+" -> start");
		assertTrue(go);
		go=false;
	}
	@After
	public void end() {
		System.out.println("- - - "+method+" <- end");
		System.out.println("  Result : "+go+"\n");
	}
	@AfterClass
	public static void tearDown(){
		if(go)  APP.exitDemo();
		System.out.println(cls+" Done");
	}

	@Test(timeout = 60*60000)  // 60 min
	public void testBullseye() throws Exception{
		/* Start App and continue as guest
		Steps:
		1. TRY-THE-DEMO and PLAY Bullseye with following setting
			a.Total number of players: 1
			b.Number of rounds: 3 rounds (As game settings).
		2. Collect total point from each round
		3. Compare with final results with 3 round's total points
		*/
		log.info("Goto Demo");
		APP.startDemo();
		int p1= APP.playAround();
		Sys.sleep("the-1st-round's point="+p1,5);
		int p2= APP.playAround();
		Sys.sleep("the-2nd-round's point="+p2,5);
		int p3= APP.playAround();
		Sys.sleep("the-3th-round's point="+p3,5);
		int points = p1+p2+p3;
		log.info(String.format("TOTAL POINTS(%1$s+%2$s+%3$s) = %4$s",p1,p2,p3,points));

		String result=APP.getResult();
		log.info("FINAL RESULTS = "+result);
		int total = Integer.parseInt(result);

		assertEquals(points+"[1,2,3]-VS-[Final]"+total,points,total);

		go=true;
	}


}
