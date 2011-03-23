import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import ontology.PersistentOntology;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

import data.list.Actor;
import data.list.Cast;
import data.list.Episode;
import data.list.Role;
import data.list.Season;
import data.list.Show;
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
			
//			System.out.println(newElement.getChildText("name"));
//			System.out.println(newElement.getChildText("showLink"));
//			System.out.println(newElement.getChildText("startDate"));
//			System.out.println(newElement.getChildText("tvDotComRating"));
//			System.out.println(newElement.getChildText("tvDotComVotes"));
			
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
//			if(show.getTvDotComVotes() < MINIMUM_VOTES)
//				continue;
			
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
	
	private static void writeToObjectFile(List<Show> allShows, String fileName) throws IOException
	{
		FileOutputStream fileOutputStream = new FileOutputStream(fileName); 
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
        objectOutputStream.writeObject(allShows);
        objectOutputStream.flush();
        objectOutputStream.close();
	}
	
	private static List<Show> readFromObjectFile(String fileName) throws IOException, ClassNotFoundException
	{
		List<Show> allShows = new ArrayList<Show>();
		FileInputStream fileInputStream = new FileInputStream(fileName); 
        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
        allShows = (List<Show>) objectInputStream.readObject();
        objectInputStream.close();
        return allShows;
	}
	
	private static void printAllShows(List<Show> allShows)
	{
		Show show;
		Cast cast;
		List<Role> roles, actorRoles;
		Actor actor;
		Scanner scanner = new Scanner(System.in);
		
		for(int i=0; i < allShows.size(); i++)
		{
			show = allShows.get(i);
			
//			System.out.println("show.getName() : '" + show.getName() + "'");
//			System.out.println("show.getShowId(): '" + show.getShowId() + "'");
//			System.out.println("show.getAirDay(): '" + show.getAirDay() + "'");
//			System.out.println("show.getAirTime(): '" + show.getAirTime() + "'");
//			System.out.println("show.getClassification(): '" + show.getClassification() + "'");
//			System.out.println("show.getDate(): '" + show.getDate() + "'");
//			System.out.println("show.getImage(): '" + show.getImage() + "'");
//			System.out.println("show.getShowId(): '" + show.getShowLink() + "'");
//			System.out.println("show.getStarted(): '" + show.getStarted() + "'");
//			System.out.println("show.getStatus(): '" + show.getStatus() + "'");
//			System.out.println("show.getTvDotComRating(): '" + show.getTvDotComRating() + "'");
//			System.out.println("show.getTvDotComVotes(): '" + show.getTvDotComVotes() + "'");
////			if(show.getRating() == null)
////				System.out.println("NULL");
//			for(String genre : show.getGenres())
//				System.out.println("Genre: '" + genre + "'");
//			scanner.nextLine();
			
			cast = show.getCast();
			if(cast == null)
			{
				System.out.println("No cast!!!");
				scanner.nextLine();
				continue;
			}
			
			roles = cast.getRoles();
			if(roles == null)
			{
				System.out.println("No roles in cast!!!");
				scanner.nextLine();
				continue;
			}
			
			for(Role role : roles)
			{
				//System.out.println("\nRole name: '" + role.getRoleName() + "'");
				actor = role.getPlayedBy();
				if(actor == null)
				{
					System.out.println("Keanu Reeves Exception (NULL actor)!!!");
					scanner.nextLine();
					continue;
				}
				
				//System.out.println("Name: " + actor.getName());
				//System.out.println("ID: " + actor.getId());
				//System.out.println("Gender: " + actor.getGender());
				//System.out.println("Birthday: " + actor.getBirthDay());
				//System.out.println("Birthplace: " + actor.getBirthPlace());
				//System.out.println("Bio: " + actor.getBiography());
				
				actorRoles = actor.getRoles();
				if(actorRoles == null)
				{
					System.out.println("No actor roles!!!");
					scanner.nextLine();
					continue;
				}
				
//				System.out.println("All roles for actor:");
//				for(Role actorRole : actorRoles)
//					System.out.println("\tRole name: '" + actorRole.getRoleName() + "'");
			}

			//System.out.println("\n");
//			scanner.nextLine();
		}
	}
	
	public static void saveToDB(List<Show> allShows)
	{
		for(Show show : allShows)
		{
			persistShow(show);
		}
	}
	
	private static void persistShow(Show show)
	{
		OntModel ontologyModel = PersistentOntology.getOntModel();
		OntClass tvShowClass = ontologyModel.getOntClass(PersistentOntology.NS + "TVShow");
		Individual tvShowIndividual = tvShowClass.createIndividual(PersistentOntology.NS + "Show_" + show.getShowId());
		
		tvShowIndividual.addProperty(PersistentOntology.name, show.getName());
		if (show.getImage() != null)
			tvShowIndividual.addProperty(PersistentOntology.picture, show.getImage());
		if (show.getAirDay() != null)
			tvShowIndividual.addProperty(PersistentOntology.airDay, show.getAirDay());
		if (show.getStatus() != null)
			tvShowIndividual.addProperty(PersistentOntology.status, show.getStatus());
		
		// NEW STUFF
//		tvShowIndividual.addProperty(PersistentOntology.tvDotComVotes, "" + show.getTvDotComVotes());
//		if (show.getTvDotComRating() != null)
//			tvShowIndividual.addProperty(PersistentOntology.tvDotComRating, show.getTvDotComRating());
		if (show.getStarted() != null)
			tvShowIndividual.addProperty(PersistentOntology.started, show.getStarted());
		if (show.getAirTime() != null)
			tvShowIndividual.addProperty(PersistentOntology.airTime, show.getAirTime());
		if (show.getClassification() != null)
			tvShowIndividual.addProperty(PersistentOntology.classification, show.getClassification());
		if(show.getGenres() != null)
			for(String genre : show.getGenres())
				if(genre != null)
					tvShowIndividual.addProperty(PersistentOntology.genre, genre);
		// create Cast
		OntClass castClass = ontologyModel.getOntClass(PersistentOntology.NS + "Cast");
		Individual castIndividual = castClass.createIndividual(tvShowIndividual.getURI() + "_Cast");
		tvShowIndividual.addProperty(PersistentOntology.cast, castIndividual);
		// create Roles
		Cast cast = show.getCast();
		for(Role role : cast.getRoles())
			if(role != null)
			{
				OntClass roleClass = ontologyModel.getOntClass(PersistentOntology.NS + "Role");
				Individual roleIndividual = roleClass.createIndividual(tvShowIndividual.getURI() + "_Cast_Role_" + role.getRoleName().replace(' ', '_'));
				roleIndividual.addProperty(PersistentOntology.roleName, role.getRoleName());
				castIndividual.addProperty(PersistentOntology.role, roleIndividual);
				Actor actor = role.getPlayedBy();
				OntClass actorClass = ontologyModel.getOntClass(PersistentOntology.NS + "Actor");
				Individual actorIndividual = PersistentOntology.getOntModel().getIndividual(PersistentOntology.NS + actor.getId());
				if(actorIndividual == null)
				{
					actorIndividual = actorClass.createIndividual(PersistentOntology.NS + actor.getId());
					if(actor.getBiography() != null)
						actorIndividual.addProperty(PersistentOntology.biography, actor.getBiography());
					if(actor.getBirthDay() != null)
						actorIndividual.addProperty(PersistentOntology.birthDay, actor.getBirthDay());
					if(actor.getBirthPlace() != null)
						actorIndividual.addProperty(PersistentOntology.birthPlace, actor.getBirthPlace());
					if(actor.getGender() != null)
						actorIndividual.addProperty(PersistentOntology.gender, actor.getGender());
					if(actor.getName() != null)
						actorIndividual.addProperty(PersistentOntology.name, actor.getName());

//					//private List<Role> roles;
//					for(Role actorRole : actor.getRoles())
//					{
//						Individual actorRoleIndividual = PersistentOntology.getOntModel().getIndividual(PersistentOntology.NS + actor.getId());
//						if(actorRoleIndividual == null)
//						{
//							
//						}
//					}
				}
				OntProperty propertyPlayedBy = ontologyModel.getOntProperty(PersistentOntology.NS + "playedBy");
				OntProperty propertyPlays = ontologyModel.getOntProperty(PersistentOntology.NS + "plays");
				propertyPlayedBy.setInverseOf(propertyPlays);
				roleIndividual.addProperty(propertyPlayedBy, actorIndividual);
			}
		
		
		 OntClass seasonClass = ontologyModel.getOntClass(PersistentOntology.NS + "Season");
		 for (Season season: show.getSeasons())
		 {
			 Individual seasonIndividual = seasonClass.createIndividual(PersistentOntology.NS + "Show_" + show.getShowId() + "_Season_" + season.getName());
			 tvShowIndividual.addProperty(PersistentOntology.season, seasonIndividual);
			 persistSeason(season, seasonIndividual, ontologyModel);
		 }
	}
	
	// ALL OK
	public static void persistSeason(Season season, Individual seasonIndividual, OntModel ontologyModel)
	{
		OntClass episodeClass = ontologyModel.getOntClass(PersistentOntology.NS + "Episode");
		if(season.getName() != null)
			seasonIndividual.addProperty(PersistentOntology.name, season.getName());
		
		for(Episode episode: season.getEpisodes())
		{
			Individual episodeIndividual = episodeClass.createIndividual(seasonIndividual.getURI() + "_Episode_" + episode.getNumber());
			seasonIndividual.addProperty(PersistentOntology.episode, episodeIndividual);
			persistEpisode(episode, episodeIndividual);
		}	
	}
	
	public static void persistEpisode(Episode episode, Individual episodeIndividual)
	{
		if(episode.getTitle() != null)
			episodeIndividual.addProperty(PersistentOntology.title, episode.getTitle());
		if(episode.getNumber() != null)
			episodeIndividual.addProperty(PersistentOntology.number, episode.getNumber());
		if(episode.getAirDate() != null)
			episodeIndividual.addProperty(PersistentOntology.airDate, episode.getAirDate());
		// NEW STUFF
		if(episode.getVideoLink() != null)
			episodeIndividual.addProperty(PersistentOntology.video, episode.getVideoLink());
	}

	public static void loadEpisode(String episodeID)
	{
//		Individual individual = PersistentOntology.getOntModel().getIndividual(episodeID);
//		this.episodeID = episodeID;
//		if (individual != null) {
//			Statement tmp;
//			if ((tmp = individual.getProperty(PersistentOntology.title)) != null)
//				title = tmp.getString();
//			if ((tmp = individual.getProperty(PersistentOntology.airDate)) != null)
//				airDate = tmp.getString();
//			if ((tmp = individual.getProperty(PersistentOntology.number)) != null)
//				number = tmp.getString();
//		}
	}


	private static void loadShow(String showID)
	{
//		Individual ind = PersistentOntology.getOntModel().getIndividual(showID);
//		if (ind != null)
//		{
//			Statement tmp;
//			if ((tmp = ind.getProperty(PersistentOntology.name)) != null)
//				show.getname = tmp.getString();
//			if ((tmp = ind.getProperty(PersistentOntology.airDay)) != null)
//				show.getdate = tmp.getString();
//			if ((tmp = ind.getProperty(PersistentOntology.premiered)) != null)
//				show.getstarted = tmp.getString();
////			if ((tmp = ind.getProperty(PersistentOntology.ended)) != null)
////				show.getended = tmp.getString();
//			
//			URI = ind.getURI();
//			StmtIterator stmtIT = ind.listProperties(PersistentOntology.season);
//			
//			Season newSeason;
//			while (stmtIT.hasNext())
//			{
//				Statement statement = stmtIT.next();
//				
//				newSeason = new Season();
//				
//				newSeason.loadSeason(PersistentOntology.NS + statement.getResource().getLocalName());
//				
//				show.getseasons.add(newSeason);
//				
//				System.out.println("SEASON "+ statement.getResource().getLocalName());
//			}
//			
//			System.out.println("NAME FROM ONTOLOGY: " + show.getgetName);
//		}
//		else
//		{
//			System.out.println("SHOW NOT FOUND!");
//		}
	}

	
	public static void main(String[] args) throws IOException, JDOMException, ClassNotFoundException
	{		
		// ################################ 1
		//List<Show> allShows = createShowsFromXml("newUpdatedShows.xml");
//		
		List<Show> allShows = readFromObjectFile("allShowsObjectCompleted");
		ontology.Connection.s_reload = true;
		saveToDB(allShows);
//		System.out.println("#Shows: " + allShows.size());
		
//		writeToObjectFile(allShows, "allShowsObjectCompleted");
		
		
//		TVRageCrawler tvRageCrawler = new TVRageCrawler();
//		tvRageCrawler.completeShowInfo(allShows);
		//SideReelCrawler crawler = new SideReelCrawler();
		//crawler.addEpisodesVideoLinks(allShows);
		//crawler.multi(allShows, 150);
		//printAllShows(allShows);
		

		
//		Document result = createDocumentFromShows(allShows);
//		writeToXmlFile(result, "newUpdatedShows.xml");
		// END 1

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
