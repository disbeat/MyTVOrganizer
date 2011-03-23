package data.list;

import java.util.LinkedList;
import java.util.List;

import ontology.Database;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;


public class ShowLister {

	List<Show> shows;
	
	public ShowLister() {
		
		shows = new LinkedList<Show>();
	}
	
	public void loadShows(String letter) {
		if (letter == null)
			letter = "a";
		
		Database d = new Database();
		
		ResultSet rs = d.getAllShows(letter);
		
		Show show = null;
		while (rs.hasNext())
		{
			QuerySolution solution = rs.next();
			
			String uri = solution.get("show").toString();
			
			show = new Show();
			show.loadShow(uri);
			
			shows.add(show);
		}
	}
	
	
	public List<Show> getShows() {
		return shows;
	}
	
	public Show getShow(int i) {
		return shows.get(i);
	}
	
	public int size() {
		return shows.size();
	}
	
	
}
