package org.rpi.monitor.workqueue;

public class CustomTrack {
	
	private String title = null;
	private String artist = null;
	private String artwork_url = null;
	private String album = null;
	/**
	 * @return the title
	 */
	public String getTitle() {
		return title.trim();
	}
	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	/**
	 * @return the artist
	 */
	public String getArtist() {
		return artist.trim();
	}
	/**
	 * @param artist the artist to set
	 */
	public void setArtist(String artist) {
		this.artist = artist;
	}
	/**
	 * @return the artwork_url
	 */
	public String getArtwork_url() {
		return artwork_url;
	}
	/**
	 * @param artwork_url the artwork_url to set
	 */
	public void setArtwork_url(String artwork_url) {
		this.artwork_url = artwork_url;
	}
	
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("\r\n");
		sb.append("Artist: " + artist);
		sb.append("\r\n");
		sb.append("Title: " + title);
		sb.append("\r\n");
		sb.append("Album: " + album);
		sb.append("\r\n");
		sb.append("Artwork URL: " + artwork_url);
		return sb.toString();
	}
	
	public String getAlbum()
	{
		return album.trim();
	}
	public void setAlbum(String album) {
		this.album = album;		
	}

}
