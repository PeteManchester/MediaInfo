package org.rpi.artist.info;

import java.text.DateFormat;
import java.util.Collection;


import org.apache.log4j.Logger;
import org.rpi.monitor.TestProxy;

//import de.umass.lastfm.Artist;
//import de.umass.lastfm.Caller;
//import de.umass.lastfm.Chart;
//import de.umass.lastfm.User;

public class LastFMUtils {
	
	private static Logger log = Logger.getLogger(TestProxy.class);
	private String key = "003dc9a812e87f5058f53439dd26038e"; //this is the key used in the Last.fm API examples
	private String secret = "5a9a78a8442187172d136a84568309f8";
    private String user = "tst";
	
	private String userAgent = "tst";
	
//	public LastFMUtils()
//	{
//		Caller.getInstance().setUserAgent(userAgent);
//		Caller.getInstance().setDebugMode(true);
//	}
//	
//	public void GetArtistInfo(String artist_name)
//	{
//		String wikiText = "";
//		Artist a = Artist.getInfo(artist_name, null, null, key);
//			wikiText = a != null ? a.getWikiSummary() : "";
//			wikiText = wikiText.replaceAll("<.*?>", "");
//			log.debug(wikiText);
//			//wikiText = StringUtils.unescapeHTML(wikiText, 0);
//	}
//	
//	public void GetChartInfo(String artist_name )
//	{
//		
//	    Chart<Artist> chart = User.getWeeklyArtistChart(user, 10, key);
//	    DateFormat format = DateFormat.getDateInstance();
//	    String from = format.format(chart.getFrom());
//	    String to = format.format(chart.getTo());
//	    System.out.printf("Charts for %s for the week from %s to %s:%n", user, from, to);
//	    Collection<Artist> artists = chart.getEntries();
//	    for (Artist artist : artists) {
//	        System.out.println(artist.getName());
//	    }
//	}

}
