package data.list;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import ontology.Database;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;


public class SearchManager {

	static String[] VALID_TOKENS= {"show", "season", "episode", "actor", "genre"};
	
	Hashtable <String, String> hash;
	Hashtable <String, String> results;
	Hashtable <String, Integer> ranking;
	
	List <String> tokens;
	List <Integer> positions;
	
	
	List <String> links;
	List <String> names;
	
	String[] parts;
	
	Database database;
	
	
	
	public SearchManager() 
	{		
		tokens = new ArrayList<String>();
		positions = new ArrayList<Integer>();
		
		results = new Hashtable<String, String>();
		ranking = new Hashtable<String, Integer>();
	
		database = new Database();
		
		links = new ArrayList<String>();
		names = new ArrayList<String>();
	}
	
	
	private void search()
	{
		ResultSet rs = null;
		QuerySolution solution = null;
		
		String noPart = hash.get("noToken");
		String showPart = hash.get(VALID_TOKENS[0]);
		String seasonPart = hash.get(VALID_TOKENS[1]);
		String episodePart = hash.get(VALID_TOKENS[2]);
		String actorPart = hash.get(VALID_TOKENS[3]);
		String genrePart = hash.get(VALID_TOKENS[4]);
		
		if (noPart != null && noPart.length() > 0)
		{
			if (showPart == null)
				showPart = noPart;
			if (episodePart == null)
				episodePart = noPart;
			if (actorPart == null)
				actorPart = noPart;
			if (genrePart == null)
				genrePart = noPart;
		}
		
		String showID, showName, seasonID, seasonName, episodeID, episodeName, actorID, actorName, genreName;
		
		if (showPart != null)
		{
			rs = database.searchShow(showPart);
			
			while(rs.hasNext())
			{
				solution = rs.next();
				if (solution != null)
				{
					showID = solution.get("showID").toString();
					showName = solution.get("showName").toString();
					
					try {
						addResult("ShowDetails.jsp?showID="+URLEncoder.encode(showID, "utf8"), "TV SHOW: "+showName, 13 + showPart.split(" ").length);
					} catch (UnsupportedEncodingException e) {	}
					
				}
			}
			
			if (seasonPart != null)
			{
				rs = database.searchShowAndSeason(showPart, seasonPart);
				
				while(rs.hasNext())
				{
					solution = rs.next();
					if (solution != null)
					{
						showID = solution.get("showID").toString();
						showName = solution.get("showName").toString();
						seasonID = solution.get("seasonID").toString();
						seasonName = solution.get("seasonName").toString();
						
						try {
							addResult("SeasonDetails.jsp?seasonID="+URLEncoder.encode(seasonID, "utf8")+
									"&showID="+URLEncoder.encode(showID, "utf8"), "SEASON "+seasonName+" of tv show "+showName, 
									20 + seasonPart.split(" ").length + showPart.split(" ").length);
						} catch (UnsupportedEncodingException e) {	}
						
					}
				}
				
				if (episodePart != null)
				{
					rs = database.searchShowSeasonAndEpisode(showPart, seasonPart, episodePart);
					
					while(rs.hasNext())
					{
						solution = rs.next();
						if (solution != null)
						{
							showID = solution.get("showID").toString();
							showName = solution.get("showName").toString();
							seasonID = solution.get("seasonID").toString();
							seasonName = solution.get("seasonName").toString();
							episodeID = solution.get("episodeID").toString();
							episodeName = solution.get("episodeName").toString();
							
							try {
								addResult("EpisodeDetails.jsp?episodeID="+URLEncoder.encode(episodeID, "utf8")+
									"&seasonID="+URLEncoder.encode(seasonID, "utf8")+"&showID="+URLEncoder.encode(showID, "utf8"), 
									"EPISODE: "+episodeName+" of season "+seasonName+" of the show "+showName, 30 + episodePart.split(" ").length
									+ seasonPart.split(" ").length + showPart.split(" ").length);
							} catch (UnsupportedEncodingException e) {	}
							
						}
					}
				}
				
				if (genrePart != null)
				{
					rs = database.searchShowAndGenre(showPart, genrePart);
					
					while(rs.hasNext())
					{
						solution = rs.next();
						if (solution != null)
						{
							showID = solution.get("showID").toString();
							showName = solution.get("showName").toString();
							genreName = solution.get("genreName").toString();
							
							try {
								addResult("ShowDetails.jsp?showID="+
									URLEncoder.encode(showID, "utf8"), "TV SHOW: "+showName
									+" of genre "+genreName, 10 + genrePart.split(" ").length);
							} catch (UnsupportedEncodingException e) {	}
							
						}
					}
				}
			}
			if (episodePart != null)
			{
				rs = database.searchShowAndEpisode(showPart, episodePart);
				
				while(rs.hasNext())
				{
					solution = rs.next();
					if (solution != null)
					{
						showID = solution.get("showID").toString();
						showName = solution.get("showName").toString();
						seasonID = solution.get("seasonID").toString();
						seasonName = solution.get("seasonName").toString();
						episodeID = solution.get("episodeID").toString();
						episodeName = solution.get("episodeName").toString();
						
						try {
							addResult("EpisodeDetails.jsp?episodeID="+URLEncoder.encode(episodeID, "utf8")+
								"&seasonID="+URLEncoder.encode(seasonID, "utf8")+"&showID="+URLEncoder.encode(showID, "utf8"), 
								"EPISODE: "+episodeName+" of season "+seasonName+" of the show "+showName, 20 + episodePart.split(" ").length
								+ showPart.split(" ").length);
						} catch (UnsupportedEncodingException e) {	}
						
					}
				}
			}			
		}
		
		if (episodePart != null)
		{
			rs = database.searchEpisode(episodePart);
			
			while(rs.hasNext())
			{
				solution = rs.next();
				if (solution != null)
				{
					showID = solution.get("showID").toString();
					showName = solution.get("showName").toString();
					seasonID = solution.get("seasonID").toString();
					seasonName = solution.get("seasonName").toString();
					episodeID = solution.get("episodeID").toString();
					episodeName = solution.get("episodeName").toString();
					
					try {
						addResult("EpisodeDetails.jsp?episodeID="+URLEncoder.encode(episodeID, "utf8")+
							"&seasonID="+URLEncoder.encode(seasonID, "utf8")+"&showID="+URLEncoder.encode(showID, "utf8"), 
							"EPISODE: "+episodeName+" of season "+seasonName+" of the show "+showName, 12 + episodePart.split(" ").length);
					} catch (UnsupportedEncodingException e) {	}
					
				}
			}
		}
		
		if (actorPart != null)
		{
			rs = database.searchActor(actorPart);
			
			while(rs.hasNext())
			{
				solution = rs.next();
				if (solution != null)
				{
					actorID = solution.get("actorID").toString();
					actorName = solution.get("actorName").toString();					
					
					try {
						addResult("ActorDetails.jsp?actorID="+URLEncoder.encode(actorID, "utf8"), 
							"ACTOR: "+actorName, 10 + actorPart.split(" ").length);
					} catch (UnsupportedEncodingException e) {	}
					
				}
			}
			
			if (genrePart != null)
			{
				rs = database.searchGenreAndActor(genrePart, actorPart);
				
				while(rs.hasNext())
				{
					solution = rs.next();
					if (solution != null)
					{
						actorName = solution.get("actorName").toString();
						showID = solution.get("showID").toString();
						showName = solution.get("showName").toString();
						genreName = solution.get("genreName").toString();
						
						try {
							addResult("ShowDetails.jsp?showID="+
								URLEncoder.encode(showID, "utf8"), "TV SHOW: "+showName
								+" of genre "+genreName+" with actor "+actorName, 20 + actorPart.split(" ").length + genrePart.split(" ").length);
						} catch (UnsupportedEncodingException e) {	}
						
					}
				}
			}
		}
		
		if (genrePart != null)
		{
			rs = database.searchGenre(genrePart);
			
			while(rs.hasNext())
			{
				solution = rs.next();
				if (solution != null)
				{
					showID = solution.get("showID").toString();
					showName = solution.get("showName").toString();
					genreName = solution.get("genreName").toString();
					
					try {
						addResult("ShowDetails.jsp?showID="+
							URLEncoder.encode(showID, "utf8"), "TV SHOW: "+showName
							+" of genre "+genreName, 10 + genrePart.split(" ").length);
					} catch (UnsupportedEncodingException e) {	}
					
				}
			}
		} 
		
		System.out.println("SOLUTION CREATED!");
	}
	
	
	private void addResult(String url, String name, Integer rank)
	{
		Integer currentRanking = null;
		if ((currentRanking = ranking.get(url)) != null)
		{
			if (rank > currentRanking)
				ranking.put(url, rank);
		}
		else
		{
			results.put(url, name);
			ranking.put(url, rank);
		}
				
	}
	
	private void addSearchTerm(String token, String data)
	{
		String current = hash.get(token);
		
		if (current != null)
			current += " " + data;
		else
			current = data;
		
		hash.put(token, current);
	}
	
	
	private boolean isPrivateToken(String token)
	{
		for (String t : VALID_TOKENS)
			if (t.equalsIgnoreCase(token))
				return true;
		
		return false;
	}
	
	private void generateSolution()
	{
		hash = new Hashtable<String, String>();
		
		StringBuilder sb = new StringBuilder();
		
		String currentToken = "noToken";
		
		int index;
		
		for (int i = 0; i < parts.length; i++)
		{
			if ((index = positions.indexOf((Integer)i)) != -1)
			{
				if (currentToken != null)
				{
					addSearchTerm(currentToken, sb.toString());					
				}
				currentToken = tokens.get(index);
				
				sb = new StringBuilder();
			}
			else
				sb.append(" ").append(parts[i]);
		}
		
		if (currentToken != null)
		{
			addSearchTerm(currentToken, sb.toString());					
		}
		search();
	}
	
	
	private void recursiveSolution(int pos)
	{
		if(pos == tokens.size())
		{
			generateSolution();
			return;
		}
		
		int tmp = positions.get(pos);
		positions.set(pos, -1);
		
		recursiveSolution(pos+1);
		
		positions.set(pos, tmp);
		
		recursiveSolution(pos+1);
	
		return;
	}
	
	public void seach(String query)
	{
		parts = query.split(" ");
		
		for (int i = 0; i < parts.length; i++)
		{
			if (isPrivateToken(parts[i]))
			{
				tokens.add(parts[i].toLowerCase());
				positions.add(i);
			}
		}
			
		//recursiveSolution(0);
		
		generateSolution();

		Iterator<String> it = ranking.keySet().iterator();
		
		Integer rank;
		String url, name;
		
		while(it.hasNext())
		{
			url = it.next();
			
			rank = ranking.get(url);
			
			name = results.get(url);
			
			int i;
			for (i = 0; i < names.size(); i++)
			{
				if (rank > ranking.get(links.get(i)))
					break;
			}
			links.add(i, url);
			names.add(i, name);
		}
		
	}
	
	
	public List<String> getNames() {
		return names;
	}
	
	public List<String> getLinks() {
		return links;
	}
	
	public static void main(String[] args)
	{
		SearchManager sm = new SearchManager();
		
		sm.seach("show friend episode dog season 7");
		
		List<String> names = sm.getNames();
		List<String> urls = sm.getLinks();
		
		for (int i = 0; i < names.size(); i++)
		{
			System.out.println(names.get(i));
			System.out.println(urls.get(i));
			System.out.println();
		}
	}
}
