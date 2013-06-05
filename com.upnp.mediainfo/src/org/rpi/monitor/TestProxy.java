package org.rpi.monitor;

import java.awt.TrayIcon;
import java.io.FileInputStream;
import java.net.Inet4Address;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;
import org.openhome.net.controlpoint.CpAttribute;
import org.openhome.net.controlpoint.CpDevice;
import org.openhome.net.controlpoint.CpDeviceListUpnpServiceType;
import org.openhome.net.controlpoint.ICpDeviceListListener;
import org.openhome.net.controlpoint.proxies.CpProxyAvOpenhomeOrgInfo1;
import org.openhome.net.core.DebugLevel;
import org.openhome.net.core.InitParams;
import org.openhome.net.core.Library;
import org.openhome.net.core.NetworkAdapter;
import org.openhome.net.core.SubnetList;
import org.rpi.config.Config;
import org.rpi.monitor.workqueue.WorkQueue;

public class TestProxy implements ICpDeviceListListener {

	private static Logger log = Logger.getLogger(TestProxy.class);

	//private List<CpDevice> iDeviceList;

	//private CpProxyUpnpOrgConnectionManager1 iConnMgr;

	private CpProxyAvOpenhomeOrgInfo1 info = null;
	private static TrayIcon trayIcon = null;

	// private JFrame frame = new JFrame("");

	public TestProxy(int aMsearchTimeSecs) {
		WorkQueue.getInstance().setProxy(this);
		CpDeviceListUpnpServiceType list = new CpDeviceListUpnpServiceType("av.openhome.org", "Info", 1, this);

		Semaphore sem = new Semaphore(1);
		sem.acquireUninterruptibly();
		try {
			sem.tryAcquire(aMsearchTimeSecs * 1000, TimeUnit.MILLISECONDS);
		} catch (InterruptedException ie) {
			log.error(ie);
		}

	}


	public void deviceAdded(CpDevice aDevice) {
		String sDevice = Config.mediapalyer_name;
		CpAttribute name = aDevice.getAttribute("Upnp.FriendlyName");
		log.info("DeviceAdded: " + aDevice.getUdn() + " Name: " + name.getValue());
		if (name.getValue().equals(sDevice)) {
			log.info("Our Device has been Added " + sDevice + " Start to Monitor");
			subscribeToInfo(aDevice);
		}
	}


	private void subscribeToInfo(CpDevice aDevice) {
		log.debug("Subscribing for Info Changes");
		info = new CpProxyAvOpenhomeOrgInfo1(aDevice);
		info.setPropertyMetatextChanged(new MetaTextChanged(info));
		info.setPropertyMetadataChanged(new MetaDataChanged(info));
		info.subscribe();
		log.debug("Subscribed for Info Changes");
	}

	public void notifyChange() {
		log.debug("SomeThing Changed");
		try {
			String text = info.syncMetatext();
			log.debug(text);
			trayIcon.displayMessage("Caption", text, TrayIcon.MessageType.INFO);
		} catch (Exception e) {
			log.error(e);
		}
	}

	public void deviceRemoved(CpDevice aDevice) {
		log.info("DeviceRemoved: " + aDevice.getUdn());
	}

//	private static void printDeviceInfo(String aPrologue, CpDevice aDevice) {
//		CpAttribute location = aDevice.getAttribute("Upnp.Location");
//		CpAttribute friendlyName = aDevice.getAttribute("Upnp.FriendlyName");
//		System.out.println(aPrologue + "\n    udn = " + aDevice.getUdn() + "\n    location = " + location.getValue() + "\n    name = " + friendlyName.getValue());
//	}

	public static void main(String[] args) {
		getConfig();
		ConfigureLogging();				

		InitParams initParams = new InitParams();
		Library lib = new Library();
		lib.initialise(initParams);
		lib.setDebugLevel(DebugLevel.Error.intValue());
		SubnetList subnetList = new SubnetList();
		NetworkAdapter nif = subnetList.getSubnet(0);
		Inet4Address subnet = nif.getSubnet();
		subnetList.destroy();
		lib.startCp(subnet);

		new TestProxy(initParams.getMsearchTimeSecs());
		boolean run = true;
		while (run) {
			try {
				Thread.sleep(100000);
			} catch (InterruptedException e) {
				log.error(e);
			}
		}
		lib.close();
	}

//	private static void initSystemTray() {
//		if (!SystemTray.isSupported()) {
//			log.debug("SystemTray is not supported");
//			return;
//		}
//		final PopupMenu popup = new PopupMenu();
//		final SystemTray tray = SystemTray.getSystemTray();
//		trayIcon = new TrayIcon(createImage("C:\\Keep\\Code\\UPNP\\mediaplayer\\workspace\\com.upnp.renderer\\image.png", "tray icon"));
//		// Create a pop-up menu components
//		MenuItem aboutItem = new MenuItem("About");
//		CheckboxMenuItem cb1 = new CheckboxMenuItem("Set auto size");
//		CheckboxMenuItem cb2 = new CheckboxMenuItem("Set tooltip");
//		Menu displayMenu = new Menu("Display");
//		MenuItem errorItem = new MenuItem("Error");
//		MenuItem warningItem = new MenuItem("Warning");
//		MenuItem infoItem = new MenuItem("Info");
//		MenuItem noneItem = new MenuItem("None");
//		MenuItem exitItem = new MenuItem("Exit");
//
//		// Add components to pop-up menu
//		popup.add(aboutItem);
//		popup.addSeparator();
//		popup.add(cb1);
//		popup.add(cb2);
//		popup.addSeparator();
//		popup.add(displayMenu);
//		displayMenu.add(errorItem);
//		displayMenu.add(warningItem);
//		displayMenu.add(infoItem);
//		displayMenu.add(noneItem);
//		popup.add(exitItem);
//
//		trayIcon.setPopupMenu(popup);
//
//		try {
//			tray.add(trayIcon);
//		} catch (AWTException e) {
//			log.error(e);
//		}
//	}

//	protected static Image createImage(String path, String description) {
//		try {
//			int size = 2;
//			File file = new File(path);
//			Image image = ImageIO.read(file);
//			if (image == null)
//				return null;
//			BufferedImage bi = new BufferedImage(size, size, BufferedImage.TRANSLUCENT);
//			Graphics2D g2d = (Graphics2D) bi.createGraphics();
//			g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
//			g2d.drawImage(image, 0, 0, size, size, null);
//			g2d.dispose();
//			return image;
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
	
	/***
	 * Read the app.properties file
	 */
	private static void getConfig() {
		Properties pr = new Properties();
		try {
			pr.load(new FileInputStream("app.properties"));

			Config.mediapalyer_name = pr.getProperty("mediaplayer.name");
			Config.debug = pr.getProperty("openhome.debug.level");
			Config.logfile = pr.getProperty("log.file");
			Config.loglevel = pr.getProperty("log.file.level");
			Config.logconsole = pr.getProperty("log.console.level");


		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/***
	 * Set up our logging
	 */
	private static void ConfigureLogging() {
		RollingFileAppender fileAppender = new RollingFileAppender();
		fileAppender.setAppend(true);
		fileAppender.setMaxFileSize("5mb");
		fileAppender.setMaxBackupIndex(5);
		fileAppender.setFile(Config.logfile);
		fileAppender.setThreshold(Config.getLogFileLevel());
		PatternLayout pl = new PatternLayout();
		pl.setConversionPattern("%d [%t] %-5p [%-10c] %m%n");
		pl.activateOptions();
		fileAppender.setLayout(pl);
		fileAppender.activateOptions();
		Logger.getRootLogger().addAppender(fileAppender);
		ConsoleAppender consoleAppender = new ConsoleAppender();
		consoleAppender.setLayout(pl);
		consoleAppender.activateOptions();
		consoleAppender.setThreshold(Config.getLogConsoleLevel());
		Logger.getRootLogger().addAppender(consoleAppender);
	}
}
