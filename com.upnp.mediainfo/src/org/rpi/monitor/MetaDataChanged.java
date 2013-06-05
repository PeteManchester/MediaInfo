package org.rpi.monitor;

import java.io.StringReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.openhome.net.controlpoint.IPropertyChangeListener;
import org.openhome.net.controlpoint.proxies.CpProxyAvOpenhomeOrgInfo1;
import org.openhome.net.controlpoint.proxies.CpProxyAvOpenhomeOrgInfo1.Track;
import org.rpi.monitor.workqueue.CustomTrack;
import org.rpi.monitor.workqueue.WorkQueue;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;


public class MetaDataChanged implements IPropertyChangeListener {
	
	private static Logger log = Logger.getLogger(MetaDataChanged.class);
	
	private CpProxyAvOpenhomeOrgInfo1 info = null;
	
	public MetaDataChanged(CpProxyAvOpenhomeOrgInfo1 info)
	{
		this.info = info;
	}

	@Override
	public void notifyChange() {
		try {
			String title = "";
			String artist = "";
			String artwork = "";
			String album = "";
			Track track = info.syncTrack();
			String text = track.getMetadata();
			log.debug(text);
			if(text.equalsIgnoreCase(""))
				return;
			StringBuilder temp = new StringBuilder();
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			InputSource insrc = new InputSource(new StringReader(text));
			Document doc = builder.parse(insrc);
			Node node = doc.getFirstChild();
			Node item = node.getFirstChild();
			NodeList childs = item.getChildNodes();
			temp.append("<item>");
			for (int i = 0; i < childs.getLength(); i++) {
				Node n = childs.item(i);
				if (n.getNodeName() == "dc:title") {
					title = n.getTextContent();
				}
				else if (n.getNodeName() == "upnp:album") {
					album = n.getTextContent();
					log.debug("Album: " + album);
				}
				
				else if (n.getNodeName() == "upnp:artist") {
					NamedNodeMap map = n.getAttributes();
					Node role = map.getNamedItem("role");
					if(role.getTextContent().equalsIgnoreCase("AlbumArtist"))
					{
						log.debug("Album Artist: " + n.getTextContent());
					}	
					if(role.getTextContent().equalsIgnoreCase("Performer"))
					{
						log.debug("Album Artist: " + n.getTextContent());
						artist = n.getTextContent();
					}
				}
				
				else if(n.getNodeName() == "upnp:class")
				{
				}
				
				else if(n.getNodeName() == "upnp:albumArtURI")
				{
					artwork = n.getTextContent();
				}
				

			}
			if(title.equalsIgnoreCase("") && artist.equalsIgnoreCase(""))
			{
				return;
			}
			getLyrics(artist,title,artwork,album);


		} catch (Exception e) {
			log.error(e);
		}
		
		
		
	}
	
	private void getLyrics(String artist, String song,String artwork,String album)
	{
		try
		{
			String sURL = "http://lyrics.wikia.com/" + artist.trim() + ":" + song.trim();
			sURL = sURL.replace(" ", "_");
			CustomTrack ct = new CustomTrack();
			ct.setArtist(artist.trim());
			ct.setTitle(song.trim());
			ct.setArtwork_url(artwork);
			ct.setAlbum(album);
			WorkQueue.getInstance().put(ct);
 		}
		catch(Exception e)
		{
			log.error(e);
		}
	}

}
