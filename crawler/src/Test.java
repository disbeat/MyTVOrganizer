import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ontology.PersistentOntology;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

import data.Episode;
import data.Season;
import data.Show;
import dataGathering.TVDotComCrawler;

public class Test
{
	public static final int MINIMUM_VOTES = 1000;
	
	/**
	 * Creates a list of shows by reading the given XML file.
	 * @param fileName
	 * @return
	 * @throws JDOMException
	 * @throws IOException
	 */
	public static List<Show> createShowsFromXml(String fileName) throws JDOMException, IOException
	{
		List<Show> allShows = new ArrayList<Show>();
		Show show;
		SAXBuilder builder = new SAXBuilder();
		Document d = builder.build(new File(fileName));
		Element root = d.getRootElement();
		Element newElement;
		String value;
		
		// percorre todos os shows
		List<Element> children = (List<Element>) root.getChildren();
		for(Iterator<Element> i = children.iterator(); i.hasNext();)
		{
			newElement = (Element) i.next();
			show = new Show();
			
			// name
			value = newElement.getChildText("name");
			if(value != null) 
				show.setName(value);
			// show link
			value = newElement.getChildText("showLink");
			if(value != null) 
				show.setShowLink(value);
			// start date
			value = newElement.getChildText("startDate");
			if(value != null) 
				show.setDate(value);
			// tv.com rating
			value = newElement.getChildText("tvDotComRating");
			if(value != null) 
				show.setTvDotComRating(value);
			// tv.com votes
			value = newElement.getChildText("tvDotComVotes");
			if(value != null) 
				show.setTvDotComVotes(Integer.parseInt(value));
			
			System.out.println(newElement.getChildText("name"));
//			System.out.println(newElement.getChildText("showLink"));
//			System.out.println(newElement.getChildText("startDate"));
			System.out.println(newElement.getChildText("tvDotComRating"));
			System.out.println(newElement.getChildText("tvDotComVotes"));
			
			allShows.add(show);
		}
		return allShows;
	}
	
	public static Document createDocumentFromShows(List<Show> allShows)
	{
		Document document = new Document();
		Element root = new Element("shows");
		Element showElement, seasonElement, episodeElement;
		for (Show show : allShows)
		{
			if(show.getTvDotComVotes() < MINIMUM_VOTES)
				continue;
			
			showElement = new Element("show");
			showElement.addContent(new Element("name").setText(show.getName()));
			showElement.addContent(new Element("showLink").setText(show.getShowLink()));
			showElement.addContent(new Element("startDate").setText(show.getDate()));
			showElement.addContent(new Element("tvDotComRating").setText(show.getTvDotComRating()));
			showElement.addContent(new Element("tvDotComVotes").setText("" + show.getTvDotComVotes()));
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
			
			if(show.getSeasons() == null)
			{
				root.addContent(showElement);
				System.out.println("Show name:" + show.getName());
				continue;
			}
			
			for(Season season : show.getSeasons())
			{
				seasonElement = new Element("season");
				seasonElement.addContent(new Element("no").setText(season.getName()));
				for(Episode episode : season.getEpisodes())
				{
					episodeElement = new Element("episode");
					episodeElement.addContent(new Element("epnum").setText(episode.getNumber()));
					episodeElement.addContent(new Element("airdate").setText(episode.getAirDate()));
					//episodeElement.addContent(new Element("link").setText(episode.getNumber()));
					episodeElement.addContent(new Element("title").setText(episode.getTitle()));
				}
				showElement.addContent(seasonElement);
			}
			
			
			root.addContent(showElement);
		}
		
		document.setRootElement(root);
		return document;
	}
	
	
	public static void main(String[] args) throws IOException, JDOMException
	{		
		String lostURL = "http://www.tv.com/chuck/show/68724/summary.html?flag=1&tag=page_nav;subtabs;summary";
		//"http://www.tv.com/lost/show/24313/summary.html?flag=1&tag=page_nav;subtabs;summary";
		TVDotComCrawler c = new TVDotComCrawler();
		String frontPage = c.getPage(lostURL);
		//System.out.println(frontPage);
//		String CAST_PATTERN = "<a href=\"(.*?)\">Cast and Crew</a>";
//		String castURL = c.getMatch(CAST_PATTERN, frontPage, 1, 1);
		String castURL = lostURL.replace("summary.html", "cast.html");
		//System.out.println("\n\n" + castURL + "\n\n");
		// more than one cast page pattern
		/*<ul class="TAB_LINKS">
		<li class="first">
		<a class="selected" href="http://www.tv.com/lost/show/24313/cast.html?tag=Stars;paginator;1&pg_celebs=0">1</a>
		</li>
		<li>
		<a href="http://www.tv.com/lost/show/24313/cast.html?tag=Stars;paginator;2&pg_celebs=1">2</a>
		</li>
		</ul>*/
		String castPage = c.getPage(castURL);
		System.out.println(castPage);
		String PAGES_PATTERN = "<ul class=\"TAB_LINKS\">.*?<a href=\"(.*?)\".*?</li>";
		String otherPageURL = c.getMatch(PAGES_PATTERN, castPage, 2, 1);
		System.out.println(otherPageURL);
		
		
		
		
		
		//List<Show> allShows = createShowsFromXml("newUpdatedShows.xml");
		//System.out.println("\n\n\n" + allShows.size() + "\n\n");
//		Document result = createDocumentFromShows(allShows);
//		writeToXmlFile(result, "newUpdatedShows.xml");
		

		/*
		long start = System.currentTimeMillis();
		// ########## get ALL tv shows from XML file ##########
		ArrayList<Show> allShows = getShowsFromXml();
		long reading = System.currentTimeMillis();
		System.out.println("Reading: " + (reading - start));
		TVRageCrawler crawler = new TVRageCrawler();
		int count = 12122;
		
		// ##################################################################################
		List<Show> shows = new LinkedList<Show>();
		char startingWith = 't';
		String name;
		
		// HACK!!!!!!!!!!!!!!!!
		boolean add = false;
		for(Show show : allShows)
		{
			if(add)
				shows.add(show);
			else
			{
				if(show.getName().compareToIgnoreCase("TV Revolution") == 0)
				{
					add = true;
					System.out.println("YUPI!!!!!!!!!!");
				}
			}
		}
		
		while(true)
		{
			//shows = getShowsStartingBy(startingWith, allShows);
			System.out.println("Shows starting with letter: " + startingWith);
			// actualiza os shows com info do TVRage
			for(Show show : shows)
			{	
				name = show.getName().trim().toLowerCase();
				if(name.length() < 1 || name.charAt(0) != startingWith)
					continue;
				
//				try
//				{
					try
					{
						//crawler.addSeasonsInfo(show);
						Document d = crawler.writeAllToFileTest(show);
						if(d != null)
						{
							writeToXmlFile(d, "allTvRageData\\" + count + ".xml");
							count++;
						}
					} catch(Exception e) {
						e.printStackTrace();
					}
					
//				} catch (JDOMException e){
//					//e.printStackTrace();
//					System.out.println("JDOM Exception! Show: " + show.getName());
//				}
			}
			// escreve para ficheiro
			//writeToXmlFile(new Document().setRootElement(getRootElement(shows)), "showsFromTvDotComStartingWith_" + startingWith + ".xml");
			if(startingWith == 'z')
				break;
			startingWith++;
		}
		// ##################################################################################
		// COMMENT 1
		long scraping = System.currentTimeMillis();
		System.out.println("Scraping:" + (scraping - reading));
		// escreve para ficheiro
		writeToXmlFile(new Document().setRootElement(getRootElement(allShows)), "allShowsComplete.xml");
		long writing = System.currentTimeMillis();
		System.out.println("Writing:" + (writing - scraping));
		// grava no jena
		for(Show show : allShows)
			show.persist();
		long saving = System.currentTimeMillis();
		System.out.println("Saving:" + (saving - writing));
		// mostra dados do jena
		//showShows();
		// END OF COMMENT 1
		*/
	}	
	
	private static void filterShowsByVotes() throws JDOMException, IOException
	{
		List<Show> allShows = createShowsFromXml("allShowsTvDotCom.xml");
		TVDotComCrawler crawler = new TVDotComCrawler();
		long start = System.currentTimeMillis();
		crawler.addVotesInfo(allShows, 150, MINIMUM_VOTES);
		System.out.println("Total time: " + (System.currentTimeMillis() - start));
		Document result = createDocumentFromShows(allShows);
		writeToXmlFile(result, "updatedShows.xml");
	}
	
	private static void doIt()
	{
		int fileCount = 1;
		try
		{
			while(true)
			{
				SAXBuilder builder = new SAXBuilder();
				File inputFile = new File("allTvRageData\\" + fileCount + ".xml");
				Document d = builder.build(new FileInputStream(inputFile));
				Element root = d.getRootElement();
				fileCount++;
			}
		} 
		catch(IOException e) {
			e.printStackTrace();
		} 
		catch (JDOMException e) {
			e.printStackTrace();
		}
		
	}
	
	public static List<Show> getShowsStartingBy(char c, List<Show> allShows)
	{
		List<Show> shows = new LinkedList<Show>();
		String name;
		for(Show s : allShows)
		{
			name = s.getName().trim().toLowerCase();
			if(name.length() < 1)
			{
				System.out.println("WTF???? => " + name);
				continue;
			}
			if(name.charAt(0) == c)
				shows.add(s);
		}
		return shows;
	}
	
	public static void showShows()
	{
		OntModel model = PersistentOntology.getOntModel();
		model.write(System.out, "N-TRIPLES");
		OntClass show = model.getOntClass(PersistentOntology.NS + "TVShow");
		
		ExtendedIterator it = show.listInstances();
		
		while (it.hasNext())
		{
			System.out.println("TVSHOW:");
			Individual ind = (Individual)it.next();
			Statement p = ind.getProperty(PersistentOntology.name);
			if (p != null)
				System.out.println(" - name: " + ind.getProperty(PersistentOntology.name).getString());
			
			p = ind.getProperty(PersistentOntology.status);
			if (p != null)
				System.out.println(" - status: " + ind.getProperty(PersistentOntology.status).getString());
			
			p = ind.getProperty(PersistentOntology.airDay);
			if (p != null)
				System.out.println(" - dates: " + p.getString());
			System.out.println();
			
			
		}
	}

	public static void writeToXmlFile(Document doc, String fileName)
	{
		XMLOutputter outputter = new XMLOutputter();
		try
		{
			File outputFile = new File(fileName);
			FileOutputStream out = new FileOutputStream(outputFile);
			outputter.output(doc, out);
			out.close();
		} catch (IOException e)
		{
			System.err.println(e);
		}
	}
	
	public static void scrapAndSaveTvDotComShows() throws IOException
	{
		// ########## get ALL tv shows from TV.com ##########
		TVDotComCrawler crawler = new TVDotComCrawler();
		ArrayList<Show> allShows = crawler.scrapAllShows(MINIMUM_VOTES);
		// System.out.println(allShows.size());
		writeToXmlFile(createDocumentFromShows(allShows), "allShowsTvDotCom.xml");
	}
}
