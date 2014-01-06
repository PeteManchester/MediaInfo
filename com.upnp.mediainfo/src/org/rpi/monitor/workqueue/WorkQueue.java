package org.rpi.monitor.workqueue;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Vector;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Renderer;

import org.apache.log4j.Logger;
import org.rpi.artist.info.ArtistInfo;
import org.rpi.artist.info.JenUttils;
import org.rpi.gui.DisplayInfo;
import org.rpi.monitor.TestProxy;

import com.echonest.api.v4.EchoNestException;

public class WorkQueue implements Runnable {

	private static Logger log = Logger.getLogger(WorkQueue.class);
	DisplayInfo di = new DisplayInfo();
	private static WorkQueue instance;
	private Vector mWorkQueue = new Vector();
	private boolean run = true;
	private TestProxy proxy = null;
	private JenUttils util = null;
	private String previous_artist = "";

	protected WorkQueue() {
		try {
			util = new JenUttils();
		} catch (EchoNestException e) {
			log.error(e);
		}
	}

	public static WorkQueue getInstance() {
		if (null == instance) {

			instance = new WorkQueue();
			Thread myThread = new Thread(instance);
			myThread.start();
		}
		return instance;
	}

	public void setProxy(TestProxy proxy) {
		this.proxy = proxy;
	}

	public synchronized boolean isEmpty() {
		return mWorkQueue.isEmpty();
	}

	public synchronized void put(Object object) {
		log.debug("Put Object in WorkQueue " + object.toString());
		try {
			mWorkQueue.addElement(object);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	/**
	 * Get the first object out of the queue. Return null if the queue is empty.
	 */
	public synchronized Object get() {
		Object object = peek();
		if (object != null)
			mWorkQueue.removeElementAt(0);
		return object;
	}

	/**
	 * Peek to see if something is available.
	 */
	public Object peek() {
		if (isEmpty())
			return null;
		return mWorkQueue.elementAt(0);
	}

	private void sleep(int value) {
		try {
			Thread.sleep(value);
		} catch (InterruptedException e) {
			log.error(e.getMessage(), e);
		}
	}

	public synchronized void clear() {
		try {
			log.info("Clearing Work Queue. Number of Items: " + mWorkQueue.size());
			mWorkQueue.clear();
			log.info("WorkQueue Cleared");
		} catch (Exception e) {
			log.debug(e.getMessage(), e);
		}
	}

	private synchronized void stopRunning() {
		log.info("Stopping WorkerQueue");
		run = false;
		log.info("Stopped WorkerQueue");
		clear();
	}

	public void run() {
		while (run) {
			if (!isEmpty()) {
				try {
					CustomTrack ct = (CustomTrack) get();
					log.debug("CustomTrack: " + ct.toString());
					String artist = ct.getArtist();
					String song = ct.getTitle();
					artist = tidyUpString(artist);
					song = tidyUpString(song);
					boolean bGetArtistInfo = false;
					if (!previous_artist.equalsIgnoreCase(artist)) {
						bGetArtistInfo = true;
						previous_artist = artist;
					}
					TrackInfo lyrics = getLyrics(artist.trim(), song.trim(), bGetArtistInfo);
					if (lyrics != null) {
						di.setLyrics(lyrics.getLyrics());
						di.setTitle(artist, song);
						if (bGetArtistInfo) {
							di.setArtistInfo(lyrics.getArtistInfo().getBiography());
							di.setNews(lyrics.getArtistInfo().getNews());
							if (ct.getArtwork_url().equalsIgnoreCase("")) {
								di.setArtwork(lyrics.getArtistInfo().getImageURL());
							} else {
								di.setArtwork(ct.getArtwork_url());
							}
						}
					} else {
						di.setArtwork(ct.getArtwork_url());
					}
//					if(isAlbum(ct.getAlbum()))
//						{
//							util.getAlbumInfo(ct.getAlbum());
//						}
				} catch (Exception e) {
					log.error(e.getMessage(), e);
				}
			} else {
				sleep(100);
			}
		}
	}
	
	private boolean isAlbum(String s)
	{
		if(s == null)
			return false;
		if(s.equalsIgnoreCase(""))
			return false;
		return true;
	}

	private TrackInfo getLyrics(String artist, String song, boolean bGetArtistInfo) {
		TrackInfo info = new TrackInfo();
		info.setLyrics("Unable to find Lyrics");
		try {
			String mArtist = artist;
			String mSong = song;
			String first_part = "http://lyrics.wikia.com/";
			String sURL = first_part + mArtist + ":" + mSong;
			if (mArtist.equalsIgnoreCase("") || mSong.equalsIgnoreCase("")) {

				return info;
			}
			sURL = sURL.replace(" ", "_");
			log.debug(sURL);
			// Try Artist, Song first
			String res = makeHTTPQuery(sURL);
			if (res == null) {
				// If that didn't work try Song, Artist
				mArtist = song;
				mSong = artist;
				sURL = first_part + mArtist + ":" + mSong;
				sURL = sURL.replace(" ", "_");
				res = makeHTTPQuery(sURL);
			}

			if (res == null) {
				mArtist = artist;
				mSong = song;
				if (song.contains(".")) {
					String[] splits = song.split("\\.");
					if (splits.length == 2) {
						try {
							Integer.parseInt(splits[0]);
							sURL = first_part + artist.trim() + ":" + splits[1].trim();
							sURL = sURL.replace(" ", "_");
							res = makeHTTPQuery(sURL);
						} catch (Exception ep) {
							log.debug("Could Not Find Lyrics: " + sURL);
						}
					}
				}
			}
			info.setLyrics(res);
			log.debug(res);
			if (bGetArtistInfo) {
				ArtistInfo artist_info = GetArtistInfo(mArtist);
				info.setArtistInfo(artist_info);
			}
			return info;
		} catch (Exception e) {
			log.error(e);
		} finally {
			// in.close();
		}
		return info;
	}

	private ArtistInfo GetArtistInfo(String artist) {
		ArtistInfo info = new ArtistInfo();
		if (artist.equalsIgnoreCase("SOON"))
			return info;
		try {
			info = util.searchArtistByName(artist);
		} catch (EchoNestException e1) {
			log.error(e1);
		}
		return info;
	}

	// private String GetArtistInfo(String artist)
	// {
	// artist = artist.replace(" ", "+");
	// String sURL = "http://www.lastfm.com/music/" + artist + "/+wiki";
	// return makeHTTPQueryArtist(sURL);
	// }

	private String makeHTTPQueryArtist(String sURL) {
		try {
			URL url = new URL(sURL);
			log.debug("ArtistInfo URL: " + sURL);
			URLConnection connection = url.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String s = "";
			String lyrics = "";
			net.htmlparser.jericho.Source source = new net.htmlparser.jericho.Source(in);
			List<Element> elements = source.getAllElements("div");
			for (Element element : elements) {
				if ("wiki".equals(element.getAttributeValue("id"))) {
					Renderer htmlRend = new Renderer(element);
					htmlRend.setIncludeHyperlinkURLs(false);
					lyrics = htmlRend.toString();
					return lyrics;
				}
			}
			in.close();
			// log.debug(lyrics);
		} catch (Exception e) {
			log.debug("Could Not Find Lyrics: " + e.getMessage());

		}
		return null;
	}

	private String makeHTTPQuery(String sURL) {
		try {
			URL url = new URL(sURL);
			URLConnection connection = url.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String lyrics = "";
			net.htmlparser.jericho.Source source = new net.htmlparser.jericho.Source(in);
			List<Element> elements = source.getAllElements("div");
			for (Element element : elements) {
				if ("lyricbox".equals(element.getAttributeValue("class"))) {
					// Get all content
					lyrics = element.getRenderer().toString();
					List<Element> subElements = element.getChildElements();
					for (Element element2 : subElements) {
						if ("rtMatcher".equals(element2.getAttributeValue("class"))) {
							lyrics = lyrics.replace(element2.getRenderer().toString().replaceAll("> ", ">").trim(), "");
							log.debug("### Found Lyrics. URL: " + url);
							return lyrics;
						}
					}
				}
			}
			in.close();
			log.debug(lyrics);
		} catch (Exception e) {
			log.debug("Could Not Find Lyrics: " + e.getMessage());

		}
		return null;
	}

	private String tidyUpString(String s) throws Exception {
		s = s.replace("`", "");
		// s = s.replace("'", "");
		String string = "";
		// if (s.equals(s.toUpperCase())) {
		s = s.toUpperCase();
		String[] splits = s.split(" ");
		for (String word : splits) {
			try {
				word = word.replace(word.substring(1), word.substring(1).toLowerCase());
			} catch (Exception e) {
				// log.debug("Error with Word: " + word);
			}
			string += word + " ";
		}

		// }
		if (string.equals("")) {
			return s;
		}
		string = string.replace("`", "");
		string = string.replace("'", "");
		return string.trim();
	}

}
