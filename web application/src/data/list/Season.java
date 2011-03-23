package data.list;

import java.io.Serializable;
import java.util.ArrayList;

import ontology.PersistentOntology;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

public class Season implements Serializable
{
	private String name;
	private ArrayList<Episode> episodes;
	private String seasonID;

	public Season(){ episodes = new ArrayList<Episode>(); }
	
	public ArrayList<Episode> getEpisodes()
	{
		return episodes;
	}

	public void setEpisodes(ArrayList<Episode> episodes)
	{
		this.episodes = episodes;
	}
	
	public String  getSeasonID()
	{
		return seasonID;
	}
	
	public void addEpisode(Episode episode)
	{
		episodes.add(episode);
	}

	public String getName()
	{
		if (name == null)
			return "not available";
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}
	
	public void persist(Individual individual, OntModel model)
	{
		OntClass e = model.getOntClass(PersistentOntology.NS + "Episode");
		
		if (this.name != null)
			individual.addProperty(PersistentOntology.name, this.name);
		
		for (Episode episode: episodes)
		{
			Individual ind = e.createIndividual();
			individual.addProperty(PersistentOntology.episode, ind);
			episode.persist(ind, model);
		}	
	}
	
	public void loadSeason(String id)
	{
		this.seasonID = id;
		Individual ind = PersistentOntology.getOntModel().getIndividual(id);
		if (ind != null)
		{
			Statement tmp;
			if ((tmp = ind.getProperty(PersistentOntology.name)) != null)
				this.name = tmp.getString();
			
			StmtIterator stmtIT = ind.listProperties(PersistentOntology.episode);	
			Episode newEpisode;
			while (stmtIT.hasNext())
			{
				Statement statement = stmtIT.next();
				newEpisode = new Episode();
				newEpisode.loadEpisode(PersistentOntology.NS + statement.getResource().getLocalName());
				addEpisode(newEpisode);
			}
		}
	}
}
