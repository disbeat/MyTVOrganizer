package ontology;
import com.hp.hpl.jena.ontology.OntDocumentManager;
import com.hp.hpl.jena.rdf.model.ModelMaker;


public class Connection{

	public static final String ONTOLOGY = "urn:x-hp-jena:MyTVOrganizer";
	
	public static final String DB_URL = "jdbc:mysql://localhost/jena";
    public static final String DB_USER = "root";
    public static final String DB_PASSWD = "admin";
    public static final String DB = "MySQL";
    public static final String DB_DRIVER = "com.mysql.jdbc.Driver";


    // database connection parameters, with defaults
    private static String s_dbURL = DB_URL;
    private static String s_dbUser = DB_USER;
    private static String s_dbPw = DB_PASSWD;
    private static String s_dbType = DB;
    private static String s_dbDriver = DB_DRIVER;

    // if true, reload the data
    public static boolean s_reload = false;

    // source URL to load data from; if null, use default
    private static String s_source;


    public void connect() {
          // check for default sources
        if (s_source == null) {
            s_source = getDefaultSource();
        }

        // create the helper class we use to handle the persistent ontologies
        PersistentOntology po = new PersistentOntology();

        // ensure the JDBC driver class is loaded
        try {
            Class.forName( s_dbDriver );
        }
        catch (Exception e) {
            System.err.println( "Failed to load the driver for the database: " + e.getMessage() );
            System.err.println( "Have you got the CLASSPATH set correctly?" );
        }

        // are we re-loading the data this time?
        if (s_reload) {

            // we pass cleanDB=true to clear out existing models
            // NOTE: this will remove ALL Jena models from the named persistent store, so
            // use with care if you have existing data stored
            ModelMaker maker = po.getRDBMaker( s_dbURL, s_dbUser, s_dbPw, s_dbType, true );

            // now load the source data into the newly cleaned db
            po.loadDB( maker, s_source );
        }

        // now we list the classes in the database, to show that the persistence worked
        ModelMaker maker = po.getRDBMaker( s_dbURL, s_dbUser, s_dbPw, s_dbType, false );
        
        po.setModel( maker, s_source );
        po.loadProperties(maker, s_source);
        
    }


	    /**
     * Answer the default source document, and set up the document manager
     * so that we can find it on the file system
     *
     * @return The URI of the default source document
     */
    private static String getDefaultSource() {
        // use the ont doc mgr to map from a generic URN to a local source file
        OntDocumentManager.getInstance().addAltEntry( ONTOLOGY, "file:MyTVOrganizer.owl" );

        return ONTOLOGY;
    }
	
    
    public static void main(String[] args) {
		Connection c = new Connection();
		c.s_reload = true;
		c.connect();
	}

   }
