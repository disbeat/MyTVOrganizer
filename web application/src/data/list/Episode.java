package data.list;

import java.io.Serializable;

import ontology.PersistentOntology;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Statement;

public class Episode implements Serializable
{
	private String title, airDate, number;
	
	// ADDED
	private String videoLink;
	private String episodeID;
	
	public Episode(){}
	
	public String getEpisodeID()
	{
		return episodeID;
	}

	public String getVideoLink()
	{
		return videoLink;
	}
	public void setVideoLink(String videoLink)
	{
		this.videoLink = videoLink;
	}
	
	public String getAirDate()
	{
		return airDate;
	}

	public String getNumber()
	{
		return number;
	}

	public String getTitle()
	{
		return title;
	}

	public void setAirDate(String airDate)
	{
		this.airDate = airDate;
	}

	public void setNumber(String number)
	{
		this.number = number;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}
	
	public void persist(Individual individual, OntModel model)
	{
		if (this.title != null)
			individual.addProperty(PersistentOntology.title, this.title);
		if (this.number != null)
			individual.addProperty(PersistentOntology.number, this.number);
		if (this.airDate != null)
			individual.addProperty(PersistentOntology.airDate, this.airDate);
	}
	
	public void loadEpisode(String episodeID)
	{
		Individual individual = PersistentOntology.getOntModel().getIndividual(episodeID);
		this.episodeID = episodeID;
		if (individual != null)
		{
			Statement tmp;
			if ((tmp = individual.getProperty(PersistentOntology.title)) != null)
				title = tmp.getString();
			if ((tmp = individual.getProperty(PersistentOntology.airDate)) != null)
				airDate = tmp.getString();
			if ((tmp = individual.getProperty(PersistentOntology.number)) != null)
				number = tmp.getString();
			if ((tmp = individual.getProperty(PersistentOntology.video)) != null)
				videoLink = tmp.getString();
		}
	}
}
