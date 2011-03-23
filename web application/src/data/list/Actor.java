package data.list;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ontology.PersistentOntology;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

//TODOS ATRIBUTOS OK

public class Actor implements Serializable
{
	private int id;
	private String biography, birthDay, birthPlace, gender, name;
	private List<Role> roles;
	
	private String uri;

	public Actor(){ roles = new ArrayList<Role>();}
	
	public String getUri()
	{
		return uri;
	}
	
	public int getId()
	{
		return id;
	}
	public void setId(int id)
	{
		this.id = id;
	}
	
	public List<Role> getRoles()
	{
		return roles;
	}
	public String getBiography()
	{
		return biography;
	}
	public String getBirthDay()
	{
		return birthDay;
	}
	public String getBirthPlace()
	{
		return birthPlace;
	}
	public String getGender()
	{
		return gender;
	}
	public String getName()
	{
		return name;
	}
	public synchronized void addRole(Role role)
	{
		roles.add(role);
	}
	public void setBiography(String biography)
	{
		this.biography = biography;
	}
	public void setBirthDay(String birthDay)
	{
		this.birthDay = birthDay;
	}
	public void setBirthPlace(String birthPlace)
	{
		this.birthPlace = birthPlace;
	}
	public void setGender(String gender)
	{
		this.gender = gender;
	}
	public void setName(String name)
	{
		this.name = name;
	}
	
	public void persist()
	{
		OntModel model = PersistentOntology.getOntModel();

		OntClass c = model.getOntClass(PersistentOntology.NS + "Actor");

		Individual individual = c.createIndividual(PersistentOntology.NS +"Actor_" + System.currentTimeMillis());

		individual.addProperty(PersistentOntology.name, this.name);
		if (this.biography != null)
			individual.addProperty(PersistentOntology.biography, this.biography);
		if (this.birthDay != null)
			individual.addProperty(PersistentOntology.birthDay, this.birthDay);
		if (this.birthPlace != null)
			individual.addProperty(PersistentOntology.birthPlace, this.birthPlace);
		if (this.gender != null)
			individual.addProperty(PersistentOntology.gender, this.gender);
		
		//TODO: ROLES....
	}
	
	public void loadActor(String id)
	{
		Individual ind = PersistentOntology.getOntModel().getIndividual(id);
		if (ind != null)
		{
			uri = id;
			
			Statement statement;
			if((statement = ind.getProperty(PersistentOntology.biography)) != null)
				this.biography = statement.getString();
			if((statement = ind.getProperty(PersistentOntology.birthDay)) != null)
				this.birthDay = statement.getString();
			if((statement = ind.getProperty(PersistentOntology.birthPlace)) != null)
				this.birthPlace = statement.getString();
			if((statement = ind.getProperty(PersistentOntology.gender)) != null)
				this.gender = statement.getString();
			if((statement = ind.getProperty(PersistentOntology.name)) != null)
				this.name = statement.getString();
			
			StmtIterator stmtIT = ind.listProperties(PersistentOntology.plays);
			Role role;
			while (stmtIT.hasNext())
			{
				statement = stmtIT.next();
				role = new Role();
				Individual roleInd = PersistentOntology.getOntModel().getIndividual(PersistentOntology.NS + statement.getResource().getLocalName());
				if((statement = roleInd.getProperty(PersistentOntology.roleName)) != null)
					role.setRoleName(statement.getString());
				if((statement = roleInd.getProperty(PersistentOntology.playedBy)) != null)
					role.setPlayedBy(this);
				addRole(role);
			}
		}
	}
}
