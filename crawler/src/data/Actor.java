package data;

import java.util.List;

import ontology.PersistentOntology;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;

//TODOS ATRIBUTOS OK

public class Actor
{
	private String biography, birthDay, birthPlace, gender, name, recentRole;
	private List<Role> roles;

	public Actor(){}
	
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
	public String getRecentRole()
	{
		return recentRole;
	}
	public void setRoles(List<Role> roles)
	{
		this.roles = roles;
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
	public void setRecentRole(String recentRole)
	{
		this.recentRole = recentRole;
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
		if (this.recentRole != null)
			individual.addProperty(PersistentOntology.recentRole, this.recentRole);
		
		//TODO: ROLES....
	}
}
