package ontology;
import java.util.Iterator;

import com.hp.hpl.jena.db.DBConnection;
import com.hp.hpl.jena.db.IDBConnection;
import com.hp.hpl.jena.ontology.DatatypeProperty;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.ObjectProperty;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ModelMaker;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;


public class PersistentOntology {
	
	
	public static String NS = "http://www.mytvorganizer.com/MyTVOrganizer.owl#";
	
	public static DatatypeProperty airDate;
	public static DatatypeProperty airDay;
	public static DatatypeProperty airTime;
	public static DatatypeProperty biography;
	public static DatatypeProperty birthDay;
	public static DatatypeProperty birthPlace;
	public static DatatypeProperty ended;
	public static DatatypeProperty fullRecap;
	public static DatatypeProperty gender;
	public static DatatypeProperty genre;
	public static DatatypeProperty language;
	public static DatatypeProperty link;
	public static DatatypeProperty name;
	public static DatatypeProperty network;
	public static DatatypeProperty number;
	public static DatatypeProperty picture;
	public static DatatypeProperty premiered;
	public static DatatypeProperty recentRole;
	public static DatatypeProperty runtime;
	public static DatatypeProperty status;
	public static DatatypeProperty summary;
	public static DatatypeProperty theme;
	public static DatatypeProperty title;
	public static DatatypeProperty value;
	public static DatatypeProperty voteCount;
	
	public static ObjectProperty season;
	public static ObjectProperty episode;
	
	private static OntModel model;
	
	private static boolean isConnected = false;
	
	public PersistentOntology() {
	}
	
	private static void checkConnection(){
		if (!isConnected)
		{
			Connection c = new Connection();
			c.connect();
			isConnected = true;
		}
	}
	
	public void loadProperties(ModelMaker maker, String modelID){
		
		Model base = maker.createModel( modelID, false );

        OntModel m = ModelFactory.createOntologyModel( getModelSpec( maker ), base );

		
		airDate = m.getDatatypeProperty(NS + "airDate");
		airDay = m.getDatatypeProperty(NS + "airDay");
		name = m.getDatatypeProperty(NS + "name");
		status = m.getDatatypeProperty(NS + "status");
		picture = m.getDatatypeProperty(NS + "picture");
		title = m.getDatatypeProperty(NS + "title");
		number = m.getDatatypeProperty(NS + "number");
		episode = m.getObjectProperty(NS + "episode");
		season = m.getObjectProperty(NS + "season");
	}
	

	public static OntModel getOntModel(){
		
		checkConnection();
		
		return model;
	}

	public void setModel(ModelMaker maker, String source)
	{
        Model base = maker.createModel( source, false );

        // now we plug that base model into an ontology model that also uses
        // the given model maker to create storage for imported models
        model = ModelFactory.createOntologyModel( getModelSpec( maker ), base );
	}
	
	public void loadDB( ModelMaker maker, String source ) {
        // use the model maker to get the base model as a persistent model
        // strict=false, so we get an existing model by that name if it exists
        // or create a new one
        Model base = maker.createModel( source, false );

        // now we plug that base model into an ontology model that also uses
        // the given model maker to create storage for imported models
        model = ModelFactory.createOntologyModel( getModelSpec( maker ), base );

        // now load the source document, which will also load any imports
        model.read( source );
    }

	
    public void listClasses( ModelMaker maker, String modelID ) {
        // use the model maker to get the base model as a persistent model
        // strict=false, so we get an existing model by that name if it exists
        // or create a new one
        Model base = maker.createModel( modelID, false );

        // create an ontology model using the persistent model as base
        model = ModelFactory.createOntologyModel( getModelSpec( maker ), base );
        	
        
        OntClass oc = model.getOntClass(NS + "Episode");
        
        
        DatatypeProperty dp = model.getDatatypeProperty(NS + "fullRecap");
        
        ObjectProperty op = model.getObjectProperty(NS + "cast");
        
       OntClass tvshow = model.getOntClass(NS+"TVShow");
       OntClass cast = model.getOntClass(NS+"Cast");
       
       Individual show =tvshow.createIndividual();
       
       Individual castInd = cast.createIndividual("cast example");
       
       
       show.addProperty(op, castInd);
       
       Statement stmt = show.getProperty(op);
       
       Resource c = (Resource)stmt.getObject();
       
       Individual indiv = c.as(Individual.class);
       
    
        
       ExtendedIterator indivIT = oc.listInstances();
       
       while(indivIT.hasNext()){
    	   
    	   
    	   Individual individual = (Individual)indivIT.next();
    	   
    	   //individual.addProperty(dp, "this is the full recap");
    	   
    	   individual.addProperty(op, individual);
    	   
    	   Statement stment = individual.getProperty(dp);
    	   
    	   System.out.println(stment.getString());
    	  
       }
       /* m.begin();
        Individual individual =m.createIndividual("Segundo Episodio", oc);
        m.commit();
        */
    }

    private void testConnection(IDBConnection conn)
    {
       ExtendedIterator <String>it = conn.getAllModelNames();
       
       while(it.hasNext()){
    	   System.out.println(it.next());
       }
    }


    public ModelMaker getRDBMaker( String dbURL, String dbUser, String dbPw, String dbType, boolean cleanDB ) {
        try {
            // Create database connection
            IDBConnection conn  = new DBConnection( dbURL, dbUser, dbPw, dbType );

            // do we need to clean the database?
            if (cleanDB) {
                conn.cleanDB();
            }

            testConnection(conn);
            
            // Create a model maker object
            return ModelFactory.createModelRDBMaker( conn );
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit( 1 );
        }

        return null;
    }

    public OntModelSpec getModelSpec( ModelMaker maker ) {
        // create a spec for the new ont model that will use no inference over models
        // made by the given maker (which is where we get the persistent models from)
        OntModelSpec spec = new OntModelSpec( OntModelSpec.OWL_MEM );
        spec.setImportModelMaker( maker );

        return spec;
    }


    // Internal implementation methods
    //////////////////////////////////

    //==============================================================================
    // Inner class definitions
    //==============================================================================

}
