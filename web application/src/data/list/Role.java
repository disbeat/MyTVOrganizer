package data.list;

import java.io.Serializable;

import ontology.PersistentOntology;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

//TODO persist
public class Role implements Serializable
{
	private Actor playedBy;
	private String roleName;
	
	public Role(){}
	
	public Actor getPlayedBy()
	{
		return playedBy;
	}
	public String getRoleName()
	{
		return roleName;
	}
	public void setPlayedBy(Actor playedBy)
	{
		this.playedBy = playedBy;
	}
	public void setRoleName(String roleName)
	{
		this.roleName = roleName;
	}
	
	public void loadRole(String id)
	{
		Individual ind = PersistentOntology.getOntModel().getIndividual(id);
		if (ind != null)
		{
			Statement statement;
			if((statement = ind.getProperty(PersistentOntology.roleName)) != null)
				this.roleName = statement.getString();
			
			Actor actor;
			if((statement = ind.getProperty(PersistentOntology.playedBy)) != null)
			{
				actor = new Actor();
				actor.addRole(this);
				actor.loadActor(PersistentOntology.NS + statement.getResource().getLocalName());
				playedBy = actor;
			}
		}
	}
}
