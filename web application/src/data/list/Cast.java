package data.list;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ontology.PersistentOntology;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

// TODO persist
public class Cast implements Serializable
{
	private List<Role> roles;
	
	public Cast(){ roles = new ArrayList<Role>(); }
	
	public List<Role> getRoles()
	{
		return roles;
	}
	public void addRole(Role role)
	{
		roles.add(role);
	}	
	
	public void loadCast(String id)
	{
		Individual ind = PersistentOntology.getOntModel().getIndividual(id);
		if (ind != null)
		{
			Statement statement;
			StmtIterator stmtIT = ind.listProperties(PersistentOntology.role);
			Role role;
			while (stmtIT.hasNext())
			{
				statement = stmtIT.next();
				role = new Role();
				role.loadRole(PersistentOntology.NS + statement.getResource().getLocalName());
				addRole(role);
			}
		}
	}
}
