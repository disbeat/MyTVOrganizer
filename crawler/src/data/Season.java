package data;

import java.util.ArrayList;

import ontology.PersistentOntology;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;

public class Season
{
	private String name;
	private ArrayList<Episode> episodes;

	public Season(){}
	
	public ArrayList<Episode> getEpisodes()
	{
		return episodes;
	}

	public void setEpisodes(ArrayList<Episode> episodes)
	{
		this.episodes = episodes;
	}

	public String getName()
	{
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
}
