package ontology;

import java.util.ArrayList;
import java.util.List;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

public class Database {

	OntModel model;
	
	public Database() {
		this.model = PersistentOntology.getOntModel();
	}
	
	private ResultSet sparqlSelect(String query) {
    	Query sql = QueryFactory.create(query);
		QueryExecution qexec = QueryExecutionFactory.create(sql, model);
    	return qexec.execSelect(); 	
    }
	
	
	public static boolean hasWord(List<String> in, String word)
	{
		for (int i = 0; i < in.size(); i++){
			if (in.get(i).equalsIgnoreCase(word))
					return true;
		}
		
		return false;
	}
	
	private String filterWords(String[] parts, String var, boolean and)
	{
		String query = "FILTER (";
		for (String part : parts)
			query += " regex(str(?"+var+"), \"^?"+part+"\", \"i\") "+(and?"&&":"||")+" ";
		query = query.substring(0, query.length() - 3);
		query += " ) ";
		
		return query;
	}
	
	public ResultSet searchShow(String showName)
	{
		String[] parts = showName.trim().split(" ");
		String query = new String();
		query += "PREFIX ns: <" + PersistentOntology.NS + "> ";
		query += " select ?showID ?showName ";
		query += " where { ?showID a ns:TVShow ; ns:name ?showName . ";
		query += filterWords(parts, "showName", true) + " } LIMIT 5";
		
		ResultSet rs = this.sparqlSelect(query);
		return rs;
	}
	
	public ResultSet searchEpisode(String episode) {
		String[] episodeParts = episode.trim().split(" ");
		
		Integer num = null;
		try{
			num = Integer.parseInt(episode.trim());
		}catch (NumberFormatException e) {}		
		
		String query = new String();	
		query += "PREFIX ns: <" + PersistentOntology.NS + "> ";
		query += " select ?showID ?showName ?seasonID ?seasonName ?episodeID ?episodeName ?episodeNumber ";
		query += " where { ?showID a ns:TVShow";
		query += " ; ns:name ?showName ; ns:season ?seasonID . ?seasonID a ns:Season ; ns:name ?seasonName ; ns:episode ?episodeID . ?episodeID a ns:Episode ; ";
		
		if (num != null)
			query += " ns:title ?episodeName ; ns:number ?episodeNumber . FILTER (?episodeNumber = "+num+") } LIMIT 5";
		else
		{
			query += " ns:number ?episodeNumber; ns:title ?episodeName . ";
			
			query += filterWords(episodeParts, "episodeName", true);
			
			query += " } LIMIT 5";
		}
				
		ResultSet rs = this.sparqlSelect(query);
		return rs;
	}
	
	public ResultSet searchShowAndSeason(String show, String season)
	{
		String[] showParts = show.trim().split(" ");
		String[] seasonParts = season.trim().split(" ");
		String query = new String();
		
		query += "PREFIX ns: <" + PersistentOntology.NS + "> ";
		query += "select ?showID ?showName ?seasonID ?seasonName ";
		query += "where { ?showID a ns:TVShow; ns:name ?showName . ";
		query += filterWords(showParts, "showName", true);		
		query += " . ?showID ns:season ?seasonID . ?seasonID a ns:Season ; ns:name ?seasonName . ";
		query += filterWords(seasonParts, "seasonName", true);
		query += " } LIMIT 5";

		ResultSet rs = this.sparqlSelect(query);
		return rs;
	}
	

	public ResultSet searchActor(String actor)
	{
		String[] actorParts = actor.trim().split(" ");
		String query = new String();
		
		query += "PREFIX ns: <" + PersistentOntology.NS + "> ";
		query += "select ?actorID ?actorName ";
		query += "where { ?actorID a ns:Actor; ns:name ?actorName . ";
		query += filterWords(actorParts, "actorName", true);		
		query += " } LIMIT 5";

		ResultSet rs = this.sparqlSelect(query);
		return rs;
	}


	public ResultSet searchGenre(String genre)
	{
		String[] genreParts = genre.trim().split(" ");
		String query = new String();
		
		query += "PREFIX ns: <" + PersistentOntology.NS + "> ";
		query += "select ?showID ?showName ?genreName ";
		query += "where { ?showID a ns:TVShow; ns:name ?showName; ns:genre ?genreName . ";
		query += filterWords(genreParts, "genreName", false);		
		query += " } LIMIT 5";

		ResultSet rs = this.sparqlSelect(query);
		return rs;
	}


	public ResultSet searchShowAndGenre(String show, String genre)
	{
		String[] genreParts = genre.trim().split(" ");
		String[] showParts = show.trim().split(" ");
		String query = new String();
		
		query += "PREFIX ns: <" + PersistentOntology.NS + "> ";
		query += "select ?showID ?showName ?genreName ";
		query += " where { ?showID a ns:TVShow; ns:name ?showName";
		query += filterWords(showParts, "showName", true);
		query += " . ?showID ns:genre ?genreName . ";
		query += filterWords(genreParts, "genreName", false);		
		query += " } LIMIT 5";

		ResultSet rs = this.sparqlSelect(query);
		return rs;
	}

	
	public ResultSet searchGenreAndActor(String genre, String actor)
	{
		String[] genreParts = genre.trim().split(" ");
		String[] actorParts = actor.trim().split(" ");
		
		String query = new String();
		
		query += "PREFIX ns: <" + PersistentOntology.NS + "> ";
		query += "select ?showID ?showName ?genreName ?actorName ";
		query += "where { ?showID a ns:TVShow; ns:name ?showName; ns:genre ?genreName . ";
		query += filterWords(genreParts, "genreName", false);		
		query += " . ?showID ns:cast ?cast . ?cast a ns:Cast ; ns:roleplay ?role . ?role a ns:Role ; ns:playedBy ?actorID . ?actorID a ns:Actor ; ns:name ?actorName . " +
		filterWords(actorParts, "actorName", true) +
				" } LIMIT 5";

		ResultSet rs = this.sparqlSelect(query);
		return rs;
	}

	
	public ResultSet searchShowAndEpisode(String show, String episode)
	{
		String[] showParts = show.trim().split(" ");
		String[] episodeParts = episode.trim().split(" ");
		
		Integer num = null;
		try{
			num = Integer.parseInt(episode.trim());
		}catch (NumberFormatException e) {}		
		
		String query = new String();
		query += "PREFIX ns: <" + PersistentOntology.NS + "> ";
		query += " select ?showID ?showName ?seasonID ?seasonName ?episodeID ?episodeName ?episodeNumber";
		query += " where { ?showID a ns:TVShow";
		query += " ; ns:name ?showName . ";
		query += filterWords(showParts, "showName", true);
		query += " . ?showID ns:season ?seasonID . ?seasonID a ns:Season ; ns:name ?seasonName ; ns:episode ?episodeID . ?episodeID a ns:Episode ; ";
		
		if (num != null)
			query += " ns:title ?episodeName ; ns:number ?episodeNumber . FILTER (regex(str(?episodeNumber),\"^"+num+"$\") ) } LIMIT 5";
		else
		{
			query += " ns:number ?episodeNumber; ns:title ?episodeName . ";
			
			query += filterWords(episodeParts, "episodeName", true);
			
			query += " } LIMIT 5";
		}
				
		ResultSet rs = this.sparqlSelect(query);
		return rs;
	}
	
	
	public ResultSet searchShowSeasonAndEpisode(String show, String season, String episode)
	{
		String[] showParts = show.trim().split(" ");
		String[] episodeParts = episode.trim().split(" ");
		
		Integer num = null;
		try{
			num = Integer.parseInt(episode.trim());
		}catch (NumberFormatException e) {}		
		
		String query = new String();
		query += "PREFIX ns: <" + PersistentOntology.NS + "> ";
		query += " select ?showID ?showName ?seasonID ?seasonName ?episodeID ?episodeName ?episodeNumber";
		query += " where { ?showID a ns:TVShow";
		query += " ; ns:name ?showName . ";
		query += filterWords(showParts, "showName", true);
		query += " . ?showID ns:season ?seasonID . ?seasonID a ns:Season ; ns:name ?seasonName . FILTER ( regex( str(?seasonName), \"^"+season.trim()+"$\", \"i\" ) )";
		query += " . ?seasonID ns:episode ?episodeID . ?episodeID a ns:Episode ; ";
		
		if (num != null)
			query += " ns:title ?episodeName ; ns:number ?episodeNumber . FILTER (regex(str(?episodeNumber),\"^"+num+"$\") ) } LIMIT 5";
		else
		{
			query += " ns:number ?episodeNumber; ns:title ?episodeName . ";
			
			query += filterWords(episodeParts, "episodeName", true);
			
			query += " } LIMIT 5";
		}
				
		ResultSet rs = this.sparqlSelect(query);
		return rs;
	}
	
	
	public ResultSet getAllShows(String letter)
	{
		String query = new String();
		query += "PREFIX ns: <" + PersistentOntology.NS + "> ";
		query += " select DISTINCT ?show ?name ";
		query += " where { ?show a ns:TVShow ; ns:name ?name FILTER regex(str(?name), \"^"+letter+"\", \"i\") } " +
				"order by (?name) ";
		ResultSet rs = this.sparqlSelect(query);
		return rs;
	}
	
	
	public ResultSet getAllActors(String letter)
	{
		String query = new String();
		query += "PREFIX ns: <" + PersistentOntology.NS + "> ";
		query += " select DISTINCT ?actor ?name ";
		query += " where { ?actor a ns:Actor ; ns:name ?name FILTER regex(str(?name), \"^"+letter+"\", \"i\") } " +
				"order by (?name) ";
		ResultSet rs = this.sparqlSelect(query);
		return rs;
	}
	
	public ResultSet getRecomendedShows(String currentShowType, String[] actors)
	{
		String query = new String();
		query += "PREFIX ns: <" + PersistentOntology.NS + "> ";
		query += "select DISTINCT ?showID ?showName ";
		query += " where { ?showID a ns:TVShow ; ns:name ?showName ; ns:genre ?genre . " + 
		filterWords(currentShowType.split(" "), "genre", false) +
				 " . ?showID ns:cast ?cast . ?cast a ns:Cast; ns:roleplay ?role . ?role a ns:Role; ns:playedBy ?actor . ?actor a ns:Actor ; ns:name ?actorName .  " + filterWords(actors, "actorName", false)
				+ " } LIMIT 10";
		ResultSet rs = this.sparqlSelect(query);
		return rs;
	}	
	
	
	
	public List<String[]> getShowsOfActor(String uri)
	{
		String query = new String();
		query += "PREFIX ns: <" + PersistentOntology.NS + "> ";
		query += " select ?showID ?showName ?roleN ";
		query += " where { ?showID a ns:TVShow ; ns:name ?showName ; "+
				 " ns:cast ?cast . ?cast a ns:Cast; ns:roleplay ?role . ?role a ns:Role; ns:roleName  ?roleN; ns:playedBy ?actor . ?actor a ns:Actor .  " + filterWords(new String[]{uri}, "actor", true)
				+ " } ";
		ResultSet rs = this.sparqlSelect(query);
		
		List<String[]> result = new ArrayList<String[]>();
		
		while(rs.hasNext())
		{
			QuerySolution solution = rs.next();
			String showID = solution.get("showID").toString();
			String showName = solution.get("showName").toString();
			String roleName = solution.get("roleN").toString();
			
			result.add(new String[]{showID, showName, roleName});
		}
		
		return result;
	}
	
	
	public List<String[]> getActorsOfShow(String uri)
	{
		String query = new String();
		query += "PREFIX ns: <" + PersistentOntology.NS + "> ";
		query += " select ?actor ?roleN ?actorName";
		query += " where { ?showID a ns:TVShow . "+filterWords(new String[]{uri}, "showID", true)+" . ?showID ns:name ?showName ; "+
				 " ns:cast ?cast . ?cast a ns:Cast; ns:roleplay ?role . ?role a ns:Role; ns:roleName  ?roleN; ns:playedBy ?actor . ?actor a ns:Actor ; ns:name ?actorName }";
		ResultSet rs = this.sparqlSelect(query);
		
		List<String[]> result = new ArrayList<String[]>();
		
		while(rs.hasNext())
		{
			QuerySolution solution = rs.next();
			String actorID = solution.get("actor").toString();
			String roleName = solution.get("roleN").toString();
			String actorName = solution.get("actorName").toString();
			
			result.add(new String[]{actorID,  roleName, actorName});
		}
		
		return result;
	}
	
	
	public static void main(String[] args)
	{		
		
//		while (rs.hasNext())
//		{
//			String showID = rs.next().get("tvshow").toString();
//			System.out.println(showID);
//		}
//		
//		
//		rs = d.searchByEpisodeName("Pilot");
//		
//		while (rs.hasNext())
//		{
//			String episodeID = rs.next().get("episode").toString();
//			System.out.println(episodeID);
//		}
//		
		
		
//		rs = d.getAllShows( 0, 10, "a");
		
		
		

		Database d = new Database();
		
		List<String[]> result = d.getShowsOfActor(PersistentOntology.NS + "34");
		
		for(String[] res : result)
			System.out.println(res[0]+ " " + res[1]);
		
		ResultSet rs = d.getRecomendedShows("comedy", new String[]{"steve carell"});
		
		//ResultSet rs = d.searchGenreAndActor("drama comedy", "steve carell");;
		while (rs.hasNext())
		{
			QuerySolution solution = rs.next();
			String showID = solution.get("showID").toString();
			String showName = solution.get("showName").toString();
//			String seasonID = solution.get("seasonID").toString();
//			String seasonName = solution.get("seasonName").toString();
//			String episodeID = solution.get("episodeID").toString();
//			String episodeName = solution.get("episodeName").toString();
//			String episodeNumber = solution.get("episodeNumber").toString();
//			String actorID = solution.get("actorID").toString();
//			String actorName = solution.get("actorName").toString();
//			String genreName = solution.get("genreName").toString();
			
			System.out.println(showID);
			System.out.println(showName);
//			System.out.println(seasonID);
//			System.out.println(seasonName);
//			System.out.println(episodeID);
//			System.out.println(episodeName);
//			System.out.println(episodeNumber);
//			System.out.println(actorID);
//			System.out.println(actorName);
//			System.out.println(genreName);
		}
		
		
		
	}
}
