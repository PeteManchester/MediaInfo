package org.rpi.gui;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import org.apache.log4j.Logger;
import org.rpi.config.Config;
import org.rpi.monitor.workqueue.WorkQueue;

import javax.swing.JTextArea;
import javax.swing.JTextPane;

public class DisplayInfo extends JFrame {

	private static Logger log = Logger.getLogger(DisplayInfo.class);
	private JPanel contentPane;
	private JLabel lblArtWork;
	private JLabel txtLyrics;
	private JScrollPane spLyrics;
	private JScrollPane spArt;

	private BufferedImage image = null;
	private JTabbedPane tabbedPane;
	private JTextArea textInfo;
	private JScrollPane spArtistInfo;
	private JTextPane txtNews;
	private JScrollPane spNews;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					DisplayInfo frame = new DisplayInfo();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public DisplayInfo() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 781, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new GridLayout(0, 2, 0, 0));

		spArt = new JScrollPane();
		contentPane.add(spArt);
		spArt.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		spArt.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		lblArtWork = new JLabel("");
		lblArtWork.setHorizontalAlignment(SwingConstants.CENTER);
		spArt.setViewportView(lblArtWork);
		
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		contentPane.add(tabbedPane);

		spLyrics = new JScrollPane();
		tabbedPane.addTab("Lyrics", null, spLyrics, null);

		txtLyrics = new JLabel("");
		txtLyrics.setHorizontalAlignment(SwingConstants.CENTER);
		spLyrics.setViewportView(txtLyrics);
		txtLyrics.setVerticalAlignment(SwingConstants.TOP);
		
		spArtistInfo = new JScrollPane();
		tabbedPane.addTab("Artist Info", null, spArtistInfo, null);
		
		textInfo = new JTextArea();
		spArtistInfo.setViewportView(textInfo);
		textInfo.setWrapStyleWord(true);
		textInfo.setLineWrap(true);
		textInfo.setEditable(false);
		textInfo.setAutoscrolls(true);
		
		spNews = new JScrollPane();
		tabbedPane.addTab("News", null, spNews, null);
		
		txtNews = new JTextPane();
		txtNews.setContentType("text/html");
		txtNews.setEditable(false);
		spNews.setViewportView(txtNews);

		// this.getRootPane().addComponentListener(this);

		this.getRootPane().addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				// This is only called when the user releases the mouse button.
				// System.out.println("componentResized");
				resize();
			}
		});
		this.setVisible(true);
	}

	public void setTitle(String artist, String title) {
		this.setTitle(artist + " " + title + " -- " + Config.mediapalyer_name);
	}

	public void setLyrics(String lyrics) {
		StringBuilder sb = new StringBuilder();
		sb.append("<HTML>");
		sb.append(lyrics.replace("\n", "<BR>"));
		sb.append("</HTML>");
		txtLyrics.setText(sb.toString());
	}
	
	public void setArtistInfo(String artist_info)
	{
//		StringBuilder sb = new StringBuilder();
//		sb.append("<HTML>");
//		String info = artist_info.replace(". ", "\n");
//		info = info.replace("\n", "<BR>");
//		sb.append(info);
//		sb.append("</HTML>");
		textInfo.setText(artist_info);
	}
	
	public void setNews(String news)
	{
		//txtNews.setText(news);
		txtNews.setText(news);
	}
	
	public void setImage(String imageURL) {
		setArtwork(imageURL);
		
	}

	public void setArtwork(String sUrl) {
		try {
			if (sUrl.equalsIgnoreCase("")) {
				lblArtWork.setIcon(null);
				return;
			}
			URL url = new URL(sUrl);
			image = ImageIO.read(url);
			if (image != null)
				resize();
		} catch (Exception e) {
			log.error("Error GetImage: ",e);
			lblArtWork.setIcon(null);
		}
	}

	public void resize() {
		if (image == null)
			return;
		try {
			Dimension dimension = this.getContentPane().getSize();
			int size = (int) dimension.getHeight();
			if (dimension.getHeight() > dimension.getWidth()/2) {
				size = (int) dimension.getWidth()/2;
			}
			BufferedImage bi = new BufferedImage(size, size, BufferedImage.TRANSLUCENT);
			Graphics2D g2d = (Graphics2D) bi.createGraphics();
			// RenderingHints rh = new
			// RenderingHints(RenderingHints.KEY_RENDERING,
			// RenderingHints.VALUE_RENDER_QUALITY)
			g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

			// g2d.addRenderingHints();
			g2d.drawImage(image, 0, 0, size, size, null);
			g2d.dispose();
			lblArtWork.setIcon(new ImageIcon(bi));
		} catch (Exception e) {
			log.error("Error Resize: " , e);
			lblArtWork.setIcon(null);
		}
	}


}
