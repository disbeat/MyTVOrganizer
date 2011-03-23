package dataGathering;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import data.list.Episode;
import data.list.Season;
import data.list.Show;

public class TVRageCrawler
{
	// + QUERY
	private static final String TV_RAGE_SEARCH = "http://services.tvrage.com/feeds/search.php?show=";
	// + SHOWID
	private static final String TV_RAGE_INFO = "http://services.tvrage.com/feeds/full_show_info.php?sid=";
	
	private Document currentDocument;
	
	public TVRageCrawler(){}
	
	public Document writeAllToFileTest(Show show) throws IOException, JDOMException
	{
		Document d = null;
		String nome = show.getName();
		int showId = searchId(nome);
		// se existe no tvRage
		if(showId != -1)
		{
			//System.out.println("ID: " + showId);
			show.setSeasons(getSeasons(showId));
			
			String page = TV_RAGE_INFO + showId;
			SAXBuilder builder = new SAXBuilder();
			d = builder.build(new URL(page));	
		}
		return d;
	}
	
	public void completeShowInfo(List<Show> allShows) throws IOException, JDOMException
	{
		for(Show show : allShows)
			completeShowInfo(show);
	}
	
	public void completeShowInfo(Show show) throws IOException, JDOMException
	{
		String nome = show.getName();
		System.out.println(nome);
		int showId = searchId(nome);
		// se existe no tvRage
		if(showId != -1)
		{
			//System.out.println("ID: " + showId);
			show.setShowId(showId);
			String page = TV_RAGE_INFO + showId;
			SAXBuilder builder = new SAXBuilder();
			currentDocument = builder.build(new URL(page));
			Element root = currentDocument.getRootElement();
			show.setAirDay(root.getChildText("airday"));
			show.setAirTime(root.getChildText("airtime"));
			show.setClassification((root.getChildText("classification")));
			show.setImage((root.getChildText("image")));
			show.setStarted(root.getChildText("started"));
			show.setStatus(root.getChildText("status"));
			Element genres = root.getChild("genres");
			if(genres != null)
			{
				List<Element> list = genres.getChildren();
				if(list != null)
				{
					List<String> toAdd = new ArrayList<String>();
					Iterator<Element> it = list.iterator();
					while(it.hasNext())
					{
						Element e = it.next();
						toAdd.add(e.getText());
					}
					show.setGenres(toAdd);
				}
			}
			
			show.setSeasons(getSeasons(showId));
		}
		else
			System.out.println("Ups!!!");
	}
	
	public void addSeasonsInfo(Show show) throws IOException, JDOMException
	{
		show.setSeasons(getSeasons(show.getShowId()));
	}
	
//	public void addSeasonsInfo(Show show) throws IOException, JDOMException
//	{
//		String nome = show.getName();
//		int showId = searchId(nome);
//		// se existe no tvRage
//		if(showId != -1)
//		{
//			//System.out.println("ID: " + showId);
//			show.setSeasons(getSeasons(showId));
//		}
//	}
	
	private int searchId(String name) throws IOException, JDOMException
	{
		String page = TV_RAGE_SEARCH + name;
		SAXBuilder builder = new SAXBuilder();
		currentDocument = builder.build(new URL(page));
		Element root = currentDocument.getRootElement();
		if(root.getText().equals("0"))
		{
			System.out.println("No results for show '" + name + "'");
			return -1;
		}
		else
		{
			Element firstShow = root.getChild("show");
			return Integer.parseInt(firstShow.getChildText("showid"));
		}
	}
	
	private ArrayList<Season> getSeasons(int showId) throws MalformedURLException, JDOMException, IOException
	{
		ArrayList<Season> seasons = new ArrayList<Season>();
//		String page = TV_RAGE_INFO + showId;
//		SAXBuilder builder = new SAXBuilder();
//		Document d = builder.build(new URL(page));
		Element root = currentDocument.getRootElement();
		Element episodeListElement = root.getChild("Episodelist");
		if(episodeListElement == null)
			return seasons;
		List seasonList = episodeListElement.getChildren();
		Iterator it = seasonList.iterator();
		// para todas as seasons
		while(it.hasNext())
		{
			/*<Season no="0"> 
			<episode>
				<epnum>0</epnum>
				<seasonnum>00</seasonnum>
				<prodnum>4V79</prodnum>
				<airdate>0000-00-00</airdate>
				<link>http://www.tvrage.com/Buffy_The_Vampire_Slayer/episodes/329033</link>
				<title>Unaired Pilot</title>
			</episode> 
			</Season>*/
			Element seasonElement = (Element) it.next();
			Season season = new Season();
			season.setName(seasonElement.getAttributeValue("no"));
			ArrayList<Episode> episodes = new ArrayList<Episode>();
			List episodeList = seasonElement.getChildren();
			Iterator episodesIterator = episodeList.iterator();
			// para todos os episodios
			while(episodesIterator.hasNext())
			{
				Element episodeElement = (Element) episodesIterator.next();
				Episode episode = new Episode();
				episode.setTitle(episodeElement.getChildText("title"));
				episode.setNumber(episodeElement.getChildText("epnum"));
				episode.setAirDate(episodeElement.getChildText("airdate"));
				episodes.add(episode);
				
				//System.out.println("Episode name: " + episode.getTitle());
			}
			season.setEpisodes(episodes);
			seasons.add(season);
		}
		
		return seasons;
	}
}
