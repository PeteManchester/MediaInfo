package org.rpi.monitor;

import java.io.StringReader;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.openhome.net.controlpoint.IPropertyChangeListener;
import org.openhome.net.controlpoint.proxies.CpProxyAvOpenhomeOrgInfo1;
import org.rpi.monitor.workqueue.CustomTrack;
import org.rpi.monitor.workqueue.WorkQueue;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;


public class MetaTextChanged implements IPropertyChangeListener {
	
	private static Logger log = Logger.getLogger(MetaTextChanged.class);

	private CpProxyAvOpenhomeOrgInfo1 info = null;
	private String artwork = "";
	String details = "";

	public MetaTextChanged(CpProxyAvOpenhomeOrgInfo1 info) {
		this.info = info;
	}

	@Override
	public void notifyChange() {
		try {

			String text = info.syncMetatext();
			StringBuilder temp = new StringBuilder();
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			InputSource insrc = new InputSource(new StringReader(text));
			if (text.equalsIgnoreCase(""))
				return;
			Document doc = builder.parse(insrc);
			Node node = doc.getFirstChild();
			Node item = node.getFirstChild();
			NodeList childs = item.getChildNodes();
			temp.append("<item>");
			for (int i = 0; i < childs.getLength(); i++) {
				Node n = childs.item(i);
				if (n.getNodeName() == "dc:title") {
					System.out.println(n.getTextContent());
					details = n.getTextContent();
				} else if (n.getNodeName() == "upnp:album") {

				}

				else if (n.getNodeName() == "upnp:artist") {
					NamedNodeMap map = n.getAttributes();
					Node role = map.getNamedItem("role");
					if (role.getTextContent().equalsIgnoreCase("AlbumArtist")) {
					}
					if (role.getTextContent().equalsIgnoreCase("Performer")) {
					}
				}

				else if (n.getNodeName() == "upnp:class") {
				}

				else if (n.getNodeName() == "upnp:albumArtURI") {
					artwork = n.getTextContent();
				}
			}

		} catch (Exception e) {
			log.error(e);
		}
		getLyrics(details, artwork);
	}

	private void getLyrics(String details, String artwork) {
		try {
			String[] splits = details.split("-");
			if (splits.length == 2) {
				String artist = splits[0].trim();
				String song = splits[1].trim();
				CustomTrack ct = new CustomTrack();
				ct.setArtist(artist);
				ct.setTitle(song);
				ct.setArtwork_url(artwork);
				WorkQueue.getInstance().put(ct);

			}
		} catch (Exception e) {
			log.error(e);
		}
	}

}
