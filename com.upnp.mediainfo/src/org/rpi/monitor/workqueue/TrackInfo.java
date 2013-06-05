package org.rpi.monitor.workqueue;

import org.rpi.artist.info.ArtistInfo;

public class TrackInfo {
	
	private String lyrics = "";
	private ArtistInfo artistInfo = new ArtistInfo();
	/**
	 * @return the lyrics
	 */
	public String getLyrics() {
		if(lyrics ==null )
			return "";
		return lyrics;
	}
	/**
	 * @param lyrics the lyrics to set
	 */
	public void setLyrics(String lyrics) {
		this.lyrics = lyrics;
	}
	/**
	 * @return the artistInfo
	 */
	public ArtistInfo getArtistInfo() {
		return artistInfo;
	}
	/**
	 * @param artistInfo the artistInfo to set
	 */
	public void setArtistInfo(ArtistInfo artistInfo) {
		this.artistInfo = artistInfo;
	}
}
