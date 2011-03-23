package data;

import ontology.PersistentOntology;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;

public class Episode
{
	private String title, airDate, number;
	
	// ADDED
	private String videoLink;
	
	public Episode(){}

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
}
