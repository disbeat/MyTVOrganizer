package data;

import java.util.ArrayList;
import java.util.List;

import ontology.PersistentOntology;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;

public class Show
{
	/*
	 * <Show> <name>Buffy the Vampire Slayer</name> <totalseasons>7</totalseasons> <showid>2930</showid> <showlink>http://tvrage.com/Buffy_The_Vampire_Slayer</showlink> <started>Mar/10/1997</started> <ended>May/20/2003</ended> <image>http://images.tvrage.com/shows/3/2930.jpg</image> <origin_country>US</origin_country> <status>Canceled/Ended</status> <classification>Scripted</classification> <genres><genre>Action</genre><genre >Adventure</genre><genre>Comedy</genre>
	 * <genre>Drama</genre><genre>Mystery</genre><genre>Sci-Fi</genre></genres> <runtime>60</runtime> <network country="US">UPN</network> <airtime>20:00</airtime> <airday>Tuesday</airday>
	 */

	private int showId, tvDotComVotes;
	private String name, showLink, date, started, image, status, tvDotComRating;
	private ArrayList<Season> seasons;
	
	// ADDED STUFF
	private Cast cast;
	private Rating rating;
	private String airDay, airTime, classification;
	private List<String> genres;
	
	public Show(){}
	
	public void setGenres(List<String> genres)
	{
		this.genres = genres;
	}
	public void setClassification(String classification)
	{
		this.classification = classification;
	}
	public void setAirTime(String airTime)
	{
		this.airTime = airTime;
	}
	public List<String> getGenres()
	{
		return genres;
	}
	public void setAirDay(String airDay)
	{
		this.airDay = airDay;
	}
	public String getAirTime()
	{
		return airTime;
	}
	public String getAirDay()
	{
		return airDay;
	}
	public String getClassification()
	{
		return classification;
	}
	
	public float getIntegerRating()
	{
		float rating;
		try {
			
			rating = Float.parseFloat(tvDotComRating);
		} catch(Exception e){
			//e.printStackTrace();
			rating = 0;
		}
		return rating;
	}
	public void setRating(Rating rating)
	{
		this.rating = rating;
	}
	public Rating getRating()
	{
		return rating;
	}
	
	public Cast getCast()
	{
		return cast;
	}

	public void setCast(Cast cast)
	{
		this.cast = cast;
	}
	public String getImage()
	{
		return image;
	}

	public String getName()
	{
		return name;
	}

	public ArrayList<Season> getSeasons()
	{
		return seasons;
	}

	public int getShowId()
	{
		return showId;
	}

	public String getShowLink()
	{
		return showLink;
	}

	public String getStarted()
	{
		return started;
	}

	public String getStatus()
	{
		return status;
	}
	
	public int getTvDotComVotes()
	{
		return tvDotComVotes;
	}
	
	public void setTvDotComVotes(int tvDotComVotes)
	{
		this.tvDotComVotes = tvDotComVotes;
	}

	public void setImage(String image)
	{
		this.image = image;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public void setSeasons(ArrayList<Season> seasons)
	{
		this.seasons = seasons;
	}

	public void setShowId(int showId)
	{
		this.showId = showId;
	}

	public void setShowLink(String showLink)
	{
		this.showLink = showLink;
	}

	public void setStarted(String started)
	{
		this.started = started;
	}

	public void setStatus(String status)
	{
		this.status = status;
	}

	public String getDate()
	{
		return date;
	}

	public String getTvDotComRating()
	{
		return tvDotComRating;
	}

	public void setDate(String date)
	{
		this.date = date;
	}

	public void setTvDotComRating(String tvDotComRating)
	{
		this.tvDotComRating = tvDotComRating;
	}

	public void persist()
	{
		OntModel model = PersistentOntology.getOntModel();

		OntClass c = model.getOntClass(PersistentOntology.NS + "TVShow");
		OntClass s = model.getOntClass(PersistentOntology.NS + "Season");

		// TO DO: Adicionar ID
		Individual individual = c.createIndividual();

		individual.addProperty(PersistentOntology.name, this.name);
		if (this.image != null)
			individual.addProperty(PersistentOntology.picture, this.image);
		if (this.date != null)
			individual.addProperty(PersistentOntology.airDay, this.date);
		if (this.status != null)
			individual.addProperty(PersistentOntology.status, this.status);

		 for (Season season: seasons)
		 {
			 Individual id = s.createIndividual();
			 individual.addProperty(PersistentOntology.season, id);
			 season.persist(id, model);
		 }

	}
}
