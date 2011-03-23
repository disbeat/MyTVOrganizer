package data.list;

import java.util.LinkedList;
import java.util.List;

import ontology.Database;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;


public class ActorLister {

	List<Actor> actors;
	
	public ActorLister() {
		
		actors = new LinkedList<Actor>();
	}
	
	public void loadActors(String letter) {
		if (letter == null)
			letter = "a";
		
		Database d = new Database();
		
		ResultSet rs = d.getAllActors(letter);
		
		Actor actor = null;
		while (rs.hasNext())
		{
			QuerySolution solution = rs.next();
			
			String uri = solution.get("actor").toString();
			
			actor = new Actor();
			actor.loadActor(uri);
			
			actors.add(actor);
		}
	}
	
	
	public List<Actor> getActors() {
		return actors;
	}
	
	public Actor getActor(int i) {
		return actors.get(i);
	}
	
	public int size() {
		return actors.size();
	}
	
	
}
