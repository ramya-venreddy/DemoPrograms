package com.javaranch.db ;

import com.javaranch.common.* ;

import java.sql.* ;
import java.util.* ;
import java.io.* ;
import java.security.* ;

/** Jenny the db code generator.
 *
 * This program reads a database and generates java source which provides strongly typed access to the database.
 *
 * In my opinion, there are two major problems with JDBC:
 *
 * <ul>
 *
 *       1) There are slight differences in SQL, drivers, app servers, database access, etc. that make it clear that a
 *          facade is needed.  The facade would provide access to the database in plain java (not SQL).  All of the
 *          implementation specific code is concentrated in one small area instead of spread out all over your application.
 *          The DBFacade class fixes this problem.
 *
 *       2) For any database change, it takes a great deal of discipline to find all of the table names and column
 *          names used throughout your code that now functions differently.  Even the slightest lack of discipline
 *          results in a bug that might not be discovered until run time.  Strong typing eliminates this problem.
 *          This program (Jenny) provides the strong type checking.
 *
 * </ul>
 *
 * For each table and view in a given database, Jenny will create a java source class file.  Each of these classes will provide
 * a collection of general purpose methods for working with the table/view.  They will also provide a list of all the column
 * names, the table/view name and an inner class called Row that can be used to manipulate a single row in the table.
 *
 * Many tables have a primary key that is a non-nullable integer called "tablenameID" or sometimes "ID".  If Jenny
 * finds this, she will add extra methods to the generated source for working with the primary key.
 *
 * Some of my goals:
 *
 * <ul>
 *
 *   <li> <b>Simplicity:</b>  Accessing the database needs to be as simple as possible.  I decided that this meant using
 *        static methods for the table level methods.  All connections, statements, result sets and all other objects
 *        that need closing are generally managed for you so you don't have to worry about it - thus eliminating about
 *        70% of the code required for typical JDBC access.
 *
 *   <li> <b>Unit Testing:</b> I want to auto-generate a Mock class for every table class to facilitate unit testing.
 *        This means that I need to have something I can override.  Since static methods cannot be overridden, I need an
 *        inner class that the static methods use that I can override for my mock classes.  This also means that I need
 *        to hide all of the Row constructors so a mock object can be returned.  This allows unit testing without having
 *        to provide SQL in the unit tests or to have a database server running during the testing.
 *
 *   <li> <b>Flexibility:</b> I want to be able to allow alternate connections, or to make several database calls with
 *        one connection, so every database access method allows me to pass in a connection object.  I also want to
 *        allow multiple ways to get to the same database; support multiple databases on one database server; support
 *        the use of multiple servers being used from one application.
 *
 *   <li> <b>Functional Testing:</b> I want to allow for functional testing (sometimes called integration testing -
 *        kinda like unit testing, but with more than one class and sometimes the actual database) so I allow all
 *        classes to replace the connection source at the facade level.
 *
 *   <li> <b>Clear Division of Labor:</b> I want to work with the idea that the database is managed by a DBA and business
 *        logic is managed by software engineers.  So fancy database stuff is done by the DBA in the database (i.e. joins
 *        are handled within views by the DBA).  In most big software shops, the software engineer will be accessing
 *        data in an existing database that is managed by a DBA that has far more experience with the database than the
 *        engineer.  Anything that cannot be done through the facade probably needs to be done on the database side.
 *
 *   <li> <b>Complexity Reduction:</b>  I want to avoid having complex relationships defined in an XML
 *        document or embedded in the code.  Good engineering is making complicated business logic look SIMPLE!
 *        My experiences with object databases, object to relational mapping, CMP and similar tools is that they promise
 *        simplicity, but when you get right down to it, it becomes horribly complicated!
 *
 *   <li> <b>Self Discipline Not Required!</b>  Many solutions similar to Jenny require human engineers to have the
 *        discipline to make sure that any changes in one place are reflected in other places.  If your table name in
 *        your database does not match the table name in your java code, you have a problem!  With this solution, you
 *        don't need that discipline.  Therefore you have more reliable code.
 *
 * </ul>
 *
 * Each generated class will provide some basic methods such as these:
 *
 * <pre>
 *
 *   Row getRow()
 *   Row getRow( String column , String searchText )
 *   DBResults search( String column , String searchText , String[] dataColumns )
 *   Row[] getRow( String column , String searchText )
 *   Row[] getAllRows()
 *   DBResults getAllRows( String[] dataColumns )
 *   void update( String column , String searchText , Map data )
 *   void delete( String column , String searchText )
 *   void insert( Map data )
 *
 * </pre>
 *
 * If an ID field is found, some methods like these will also be added:
 *
 * <pre>
 *
 *   Row getRow( long id )
 *   void delete( long id )
 *
 * </pre>
 *
 * Every class will have an inner class called Row that can provide strong type checking for every field (column) as
 * well as methods like:
 *
 * <pre>
 *
 *    void update()
 *    void delete()
 *    void insert()
 *
 * </pre>
 *
 * The strong type checking for Row is provided by getters and setters.  Suppose you have a table called Employee.  Jenny
 * will generate a class called EmployeeTable that will contain a Row class that might have the following methods:
 *
 * <pre>
 *
 *    int getEmployeeID()
 *    void setEmployeeID( int employeeID )
 *    String getLastName()
 *    void setLastName( String lastName )
 *
 * </pre>
 *
 * Here's a sample of a business logic method that uses a Jenny generated class.
 *
 * <pre>
 *
 *    // pass in an employee ID and get back the number of tax exemptions that the employee claims
 *    private int getEmployeeExemptions( int employeeID )
 *    {
 *        return EmployeeTable.getRow( employeeID ).getExemptions();
 *    }
 *
 * </pre>
 *
 * This same code using plain JDBC could be 10 to 40 lines long depending on how it would be implemented.  You would need
 * to get a connection, create a statement, build your sql string, execute your statement, wade through the result set,
 * and each of these things need try/catch/finally blocks!  Don't forget to close your connection, statement and result
 * set!
 *
 * <h2>Using Jenny</h2>
 *
 * From the command line:
 *
 * <ul>
 *
 *     Make sure your classpath includes jr.jar and then at the command prompt type:
 *
 * <pre>
 *
 *                 java com.javaranch.db.Jenny db.properties
 *
 * </pre>
 *
 *     Where db.properties is a properties file that describes how Jenny should find your database.  There is a sample
 *     properties file complete with in-line documentation inside jr.jar at /src/com/javaranch/db/jenny.properties.
 *
 * </ul>
 *
 *
 * - - - - - - - - - - - - - - - - - <p>
 *
 * Copyright (c) 2003 Paul Wheaton <p>
 *
 * You are welcome to do whatever you want to with this source file provided
 * that you maintain this comment fragment (between the dashed lines).
 * Modify it, change the package name, change the class name ... personal or business
 * use ...  sell it, share it ... add a copyright for the portions you add ... <p>
 *
 * My goal in giving this away and maintaining the copyright is to hopefully direct
 * developers back to JavaRanch. <p>
 *
 * The original source can be found at <a href="http://www.javaranch.com">JavaRanch</a> <p>
 *
 * - - - - - - - - - - - - - - - - - <p>
 *
 * @author Paul Wheaton
 * @author Marilyn de Queiroz
 *
 */
public class Jenny
{

    private static final String spatialPointColumn = "point";
    private static final String spatialPolygonColumn = "polygon" ;
    private static final String spatialMultiPolygonColumn = "multipolygon" ;

    private String packageLine ;
    private String destinationDirectory ;
    private String mockDirectory ; // null if no mock stuff is wanted
    private String dbFacadeLine ;
    private String facadeName ;
    private boolean usingSpatial = false ;
    private boolean usingOracle = false ;
    private boolean usingSqlServer = false ;

    // exposed for testing purposes only
    Jenny() throws Exception
    {
    }

    Jenny( Properties properties ) throws Exception
    {
        process( properties );
    }

    private static final String oracleSpatialImports =
        "import com.javaranch.db.* ;\n" +
        "import com.javaranch.db.spatial.*;\n" +
        "import com.thoughtworks.xstream.XStream;\n" +
        "import oracle.sql.STRUCT;\n" +
        "import oracle.sql.Datum;\n" +
        "import oracle.sql.ARRAY;";

    // I put this code here instead of in its own file so that compiling Jenny doesn't require Oracle libraries
    private static final String oracleSpatialCode =
            "        private interface SpatialProcessor\n" +
            "        {\n" +
            "            String getXML( Datum[] data ) throws SQLException;\n" +
            "        }\n" +
            "\n" +
            "        public Implementation( String jndiLookup )\n" +
            "        {\n" +
            "            super( jndiLookup );\n" +
            "        }\n" +
            "\n" +
            "        public Implementation( String databaseDriver , String databaseURL )\n" +
            "        {\n" +
            "            super( databaseDriver , databaseURL );\n" +
            "        }\n" +
            "\n" +
            "        public Implementation( String driver , String databaseURL , String databaseName , String databasePassword )\n" +
            "        {\n" +
            "            super( driver , databaseURL , databaseName , databasePassword );\n" +
            "        }\n" +
            "\n" +
            "        private SpatialProcessor pointProcessor = new SpatialProcessor()\n" +
            "        {\n" +
            "            public String getXML( Datum[] data ) throws SQLException\n" +
            "            {\n" +
            "                STRUCT oraclePoint = ( STRUCT ) data[ 2 ];\n" +
            "                Datum[] pointData = oraclePoint.getOracleAttributes();\n" +
            "                double longitude = pointData[ 0 ].doubleValue();\n" +
            "                double latitude = pointData[ 1 ].doubleValue();\n" +
            "                return toXML( new SpatialPoint( latitude , longitude ) );\n" +
            "            }\n" +
            "        };\n" +
            "\n" +
            "        private SpatialProcessor polygonProcessor = new SpatialProcessor()\n" +
            "        {\n" +
            "            public String getXML( Datum[] data ) throws SQLException\n" +
            "            {\n" +
            "                ARRAY oracleArray = ( ARRAY ) data[ 4 ];\n" +
            "                Datum[] pointData = oracleArray.getOracleArray();\n" +
            "                int numPoints = pointData.length / 3;\n" +
            "                SpatialPoint[] points = new SpatialPoint[ numPoints ];\n" +
            "                for ( int i = 0 ; i < numPoints ; i++ )\n" +
            "                {\n" +
            "                    int index = i * 3;\n" +
            "                    double longitude = pointData[ index ].doubleValue();\n" +
            "                    double latitude = pointData[ index + 1 ].doubleValue();\n" +
            "                    points[ i ] = new SpatialPoint( latitude , longitude );\n" +
            "                }\n" +
            "                return toXML( new SpatialPolygon( points ) );\n" +
            "            }\n" +
            "        };\n" +
            "\n" +
            "        private SpatialProcessor multiPolygonProcessor = new SpatialProcessor()\n" +
            "        {\n" +
            "            public String getXML( Datum[] data ) throws SQLException\n" +
            "            {\n" +
            "                ARRAY oracleArray = ( ARRAY ) data[ 3 ];\n" +
            "                Datum[] polyData = oracleArray.getOracleArray();\n" +
            "                int numPolys = polyData.length / 3;\n" +
            "                SpatialPolygon[] polys = new SpatialPolygon[ numPolys ];\n" +
            "\n" +
            "                oracleArray = ( ARRAY ) data[ 4 ];\n" +
            "                Datum[] pointData = oracleArray.getOracleArray();\n" +
            "                int pointDataPos = 0 ;\n" +
            "\n" +
            "                for ( int polyCount = 0 ; polyCount < numPolys ; polyCount++ )\n" +
            "                {\n" +
            "                    int polyLength ;\n" +
            "                    if ( polyCount == numPolys - 1 ) // last poly\n" +
            "                    {\n" +
            "                        polyLength = ( pointData.length - pointDataPos ) / 3 ;\n" +
            "                    }\n" +
            "                    else\n" +
            "                    {\n" +
            "                        int nextPolyStart = polyData[ ( polyCount + 1 ) * 3 ].intValue() - 1 ;\n" +
            "                        polyLength = ( nextPolyStart - pointDataPos ) / 3 ;\n" +
            "                    }\n" +
            "\n" +
            "                    SpatialPoint[] points = new SpatialPoint[ polyLength ];\n" +
            "                    for ( int i = 0 ; i < polyLength ; i++ )\n" +
            "                    {\n" +
            "                        double longitude = pointData[ pointDataPos ].doubleValue();\n" +
            "                        double latitude = pointData[ pointDataPos + 1 ].doubleValue();\n" +
            "                        points[ i ] = new SpatialPoint( latitude , longitude );\n" +
            "                        pointDataPos += 3 ;\n" +
            "                    }\n" +
            "                    polys[ polyCount ] = new SpatialPolygon( points );\n" +
            "                }\n" +
            "                return toXML( polys );" +
            "            }\n" +
            "        };\n" +
            "\n" +
            "        private final SpatialProcessor[] spatialProcessors =\n" +
            "                {null, pointProcessor, null, polygonProcessor, null, null, null, multiPolygonProcessor, null, null};\n" +
            "\n" +
            "        protected String getSpatialXML( Object geometryObject ) throws SQLException\n" +
            "        {\n" +
            "            STRUCT spatialObject = ( STRUCT ) geometryObject;\n" +
            "            Datum[] data = spatialObject.getOracleAttributes();\n" +
            "            int spatialType = data[ 0 ].intValue() % 10;\n" +
            "            return spatialProcessors[ spatialType ].getXML( data );\n" +
            "        }" +
            "\n" +
            "        public Object fromXML( String xml )\n" +
            "        {\n" +
            "            return xstream.fromXML( xml );\n" +
            "        }\n" +
            "\n" +
            "        public String toXML( Object obj )\n" +
            "        {\n" +
            "            return xstream.toXML( obj );\n" +
            "        }\n" ;

    // exposed for testing purposes only
    private void process( Properties properties ) throws Exception
    {
        String driver = properties.getProperty( "Driver" );
        if ( ! Str.usable( driver ) )
        {
            throw new InvalidParameterException( "Driver property not found in given property file" );
        }
        Class.forName( driver ).newInstance();

        String url = properties.getProperty( "URL" );
        if ( ! Str.usable( url ) )
        {
            throw new InvalidParameterException( "URL property not found in given property file" );
        }

        validateProperties( properties );

        // we now have all of the required info

        Files.deleteAllFilesInDir( destinationDirectory );

        String userName = properties.getProperty( "UserName" );
        String password = properties.getProperty( "Password" );
        String facadeJNDI = properties.getProperty( "FacadeJNDI" );
        usingSpatial = "true".equals( properties.getProperty( "UseSpatial" ) );
        String brand = properties.getProperty( "Brand" );
        usingOracle = "Oracle".equals( brand );
        usingSqlServer = "SqlServer".equals( brand );
        boolean doNotUseHighLowPattern = "false".equals( properties.getProperty( "UseHighLowPattern" ) );

        String facadeParameter ;

        if ( Str.usable( facadeJNDI ) )
        {
            facadeParameter = '"' + facadeJNDI + '"';
        }
        else
        {
            String facadeDriver = properties.getProperty( "FacadeDriver" );
            String facadeURL ;
            String facadeUserName ;
            String facadePassword ;
            if ( Str.usable( facadeDriver ) )
            {
                facadeURL = properties.getProperty( "FacadeURL" );
                facadeUserName = properties.getProperty( "FacadeUserName" );
                facadePassword = properties.getProperty( "FacadePassword" );
            }
            else
            {
                facadeDriver = driver ;
                facadeURL = url ;
                facadeUserName = userName ;
                facadePassword = password ;
            }

            facadeParameter = '"' + facadeDriver + "\" , \"" + facadeURL + '"';

            if ( Str.usable( facadeUserName ) )
            {
                facadeParameter += " , \"" + facadeUserName + "\" , \"" + facadePassword + '"';
            }
        }

        // create our new database facade.
        TextFileOut out = new TextFileOut( destinationDirectory + facadeName + ".java" );
        out.println( packageLine );
        out.println();
        out.println( "import com.javaranch.db.* ;" );
        out.println( "import java.sql.* ;" );
        out.println( "import java.util.Map ;" );
        if ( usingSpatial )
        {
            out.println( oracleSpatialImports );
        }
        out.println();
        out.println( "public class " + facadeName + "\n{\n" );

        out.println( "    private static Implementation instance = new Implementation();\n" );

        //todo make all implementations use inner Implementation

        //todo make mock of this facade

        if ( usingSpatial )
        {
            out.println( "    private static XStream xstream = new XStream();\n" );

            out.println( "    public static class Implementation extends OracleSpatialDBFacade" );
            out.println( "    {" );
            out.println( "" );
            out.println( "        Implementation()" );
            out.println( "        {" );
            out.println( "            super(" + facadeParameter + ");" );
            if ( doNotUseHighLowPattern )
            {
                out.println( "            doNotUseHighLowPattern();" );
            }
            out.println( "        }" );
            out.println( "" );
            out.println( oracleSpatialCode );
            out.println( "" );
            out.println( "    }" );
            out.println( "" );
            out.println( "    public static Implementation getInstance()" );
            out.println( "    {" );
        }
        else
        {
            if ( usingOracle )
            {
                out.println( "    public static class Implementation extends OracleFacade" );
            }
            else
            {
                out.println( "    public static class Implementation extends DBFacade" );
            }
            out.println( "    {" );
            out.println( "" );
            out.println( "        Implementation()" );
            out.println( "        {" );
            out.println( "            super(" + facadeParameter + ");" );
            if ( doNotUseHighLowPattern )
            {
                out.println( "            doNotUseHighLowPattern();" );
            }
            out.println( "        }" );
            out.println( "" );
            out.println( "    }" );
            out.println( "" );
            out.println( "    public static Implementation getInstance()" );
            out.println( "    {" );
        }

        out.println( "        return instance ;" );
        out.println( "    }\n" );

        out.println( "    /** Force all methods that use this class to access the database a different way." );
        out.println( "     *" );
        out.println( "     * If you want to do unit testing on classes that use this class, you can pass in a MockDBFacade." );
        out.println( "     *" );
        out.println( "     * If you want to do functional (integration) testing with an active database and access it a different" );
        out.println( "     * way than this class is set up, you can pass in an instance of DBFacade that uses that way." );
        out.println( "     *" );
        out.println( "     * Suppose these classes are all generated for use within EJB, but you want to write a utility that uses these" );
        out.println( "     * same classes outside of the EJB container, you can pass in a different DBFacade." );
        out.println( "     */" );

        out.println( "    public static void setInstance( Implementation facade )" );
        out.println( "    {" );
        out.println( "        instance = facade ;" );
        out.println( "    }\n" );

        if ( usingSqlServer )
        {
            out.println( "    protected boolean useNoLockHint()" );
            out.println( "    {" );
            out.println( "        return true ;" );
            out.println( "    }\n" );
        }

        out.println( "    private " + facadeName + "()" );
        out.println( "    {" );
        out.println( "    }\n" );

        printFacadeMethods( out );
        out.close();

        dbFacadeLine = "            super( " + facadeName + ".getInstance() , tableName );"; // todo - only used once

        mockDirectory = Str.trim( properties.getProperty( "MockDestination" ) );
        if ( Str.usable( mockDirectory ) )
        {
            File file = new File( mockDirectory );
            file.mkdirs();

            if ( file.exists() && file.isDirectory() )  //todo - now done in Files
            {
                Files.deleteAllFilesInDir( mockDirectory );
            }
            else
            {
                System.err.println( "Your mock directory is invalid" );
            }
        }

        Connection con ;
        if ( Str.usable( userName ) )
        {
            con = DriverManager.getConnection( url , userName , password );
        }
        else
        {
            con = DriverManager.getConnection( url );
        }

        boolean filterBySchema = usingOracle && Str.usable( userName );  //todo - postgres schema

        Set tableNames = new HashSet();
        DatabaseMetaData metaData = con.getMetaData();
        ResultSet results = metaData.getTables( null , null , "%" , new String[]{"TABLE"} );
        while ( results.next() )
        {
            String tableName = results.getString( "TABLE_NAME" );
            tableNames.add( tableName );
            if ( tableName.indexOf( '$' ) == -1 )
            {
                boolean goodTable = true ;
                if ( filterBySchema )
                {
                    goodTable = userName.equalsIgnoreCase( results.getString( "TABLE_SCHEM" ) );
                }

                if ( goodTable )
                {
                    processTable( metaData , tableName , "Table" , properties );
                }
            }
        }

        // FYI: "It is planned to implement views in MySQL Server around version 5.0."
        results = metaData.getTables( null , null , "%" , new String[]{"VIEW"} );
        while ( results.next() )
        {
            String tableName = results.getString( "TABLE_NAME" );
            if ( ! tableNames.contains( tableName ) )
            {

                boolean goodView = true ;
                if ( filterBySchema )
                {
                    goodView = userName.equalsIgnoreCase( results.getString( "TABLE_SCHEM" ) );
                }

                if ( goodView )
                {
                    //System.out.println( tableName );
                    processTable( metaData , tableName , "View" , properties );
                }
            }
        }
        con.close();
    }

    private void validateProperties( Properties properties )
    {
        String givenPackage = properties.getProperty( "Package" );
        if ( ! Str.usable( givenPackage ) )
        {
            throw new InvalidParameterException( "Package property not found in given property file" );
        }
        packageLine = "package " + givenPackage + " ;";

        destinationDirectory = properties.getProperty( "Destination" );
        if ( ! Str.usable( destinationDirectory ) )
        {
            throw new InvalidParameterException( "Destination property not found in given property file" );
        }
        new File( destinationDirectory ).mkdirs();

        facadeName = properties.getProperty( "Facade" );
        if ( ! Str.usable( facadeName ) )
        {
            throw new InvalidParameterException( "Facade property not found in given property file" );
        }
    }


    private static String spatialImpGetRows =
            "        public Row[] getRows( String columnName , SpatialPolygon searchArea ) throws SQLException\n" +
            "        {\n" +
            "            return rowArray( this.search( columnName ,  searchArea , allColumns ) );\n" +
            "        }\n" +
            "\n" +
            "        public Row[] getRows( Connection con , String columnName , SpatialPolygon searchArea ) throws SQLException\n" +
            "        {\n" +
            "            return rowArray( this.search( con , columnName ,  searchArea , allColumns ) );\n" +
            "        }\n" +
            "\n" +
            "        public Row[] getRows( String columnName , SpatialPolygon[] searchArea ) throws SQLException\n" +
            "        {\n" +
            "            return rowArray( this.search( columnName ,  searchArea , allColumns ) );\n" +
            "        }\n" +
            "\n" +
            "        public Row[] getRows( Connection con , String columnName , SpatialPolygon[] searchArea ) throws SQLException\n" +
            "        {\n" +
            "            return rowArray( this.search( con , columnName ,  searchArea , allColumns ) );\n" +
            "        }\n";

    private static String spatialGetRows_knownSpatialColumn =
            "    public static Row[] getRows( SpatialPolygon searchArea ) throws SQLException\n" +
            "    {\n" +
            "        return imp.getRows( searchArea );\n" +
            "    }\n" +
            "\n" +
            "    public static Row[] getRows( Connection con , SpatialPolygon searchArea ) throws SQLException\n" +
            "    {\n" +
            "        return imp.getRows( con , searchArea );\n" +
            "    }\n" +
            "\n" +
            "    public static Row[] getRows( SpatialPolygon[] searchArea ) throws SQLException\n" +
            "    {\n" +
            "        return imp.getRows( searchArea );\n" +
            "    }\n" +
            "\n" +
            "    public static Row[] getRows( Connection con , SpatialPolygon[] searchArea ) throws SQLException\n" +
            "    {\n" +
            "        return imp.getRows( con , searchArea );\n" +
            "    }\n";

    private static String spatialGetRows =
            "    public static Row[] getRows( String columnName , SpatialPolygon searchArea ) throws SQLException\n" +
            "    {\n" +
            "        return imp.getRows( columnName , searchArea );\n" +
            "    }\n" +
            "\n" +
            "    public static Row[] getRows( Connection con , String columnName , SpatialPolygon searchArea ) throws SQLException\n" +
            "    {\n" +
            "        return imp.getRows( con , columnName , searchArea );\n" +
            "    }\n" +
            "\n" +
            "    public static Row[] getRows( String columnName , SpatialPolygon[] searchArea ) throws SQLException\n" +
            "    {\n" +
            "        return imp.getRows( columnName , searchArea );\n" +
            "    }\n" +
            "\n" +
            "    public static Row[] getRows( Connection con , String columnName , SpatialPolygon[] searchArea ) throws SQLException\n" +
            "    {\n" +
            "        return imp.getRows( con , columnName , searchArea );\n" +
            "    }\n";

    private void printFacadeMethods( TextFileOut out ) throws IOException
    {
        out.println( "    /** Get a database connection." );
        out.println( "     *" );
        out.println( "     * Gets a connection from the same place all of the methods here get a connection if" );
        out.println( "     * you don't provide one.  Of course, if you open it, you gotta close it!" );
        out.println( "     */" );
        out.println( "    public static Connection getConnection() throws Exception" );
        out.println( "    {" );
        out.println( "        return instance.getConnection();" );
        out.println( "    }\n" );


        out.println( "    /** Establish a connection to the database to see if any exceptions might be generated." );
        out.println( "     *" );
        out.println( "     * Call this method in your startup code to see if everything is configured and working correctly." );
        out.println( "     *" );
        out.println( "     * @throws Exception The types of exceptions thrown here can change depending the type of database access." );
        out.println( "     */" );
        out.println( "    public static void testConnection() throws Exception" );
        out.println( "    {" );
        out.println( "        instance.testConnection();" );
        out.println( "    }\n" );


        out.println( "    /** Sometimes you just gotta do it in SQL." );
        out.println( "     *" );
        out.println( "     * This method will at least handle the statements and result sets for you." );
        out.println( "     *" );
        out.println( "     * @param sql A complete SQL statement" );
        out.println( "     */" );
        out.println( "    public static DBResults executeQuery( Connection con , String sql ) throws SQLException" );
        out.println( "    {" );
        out.println( "        return instance.executeQuery( con , sql );" );
        out.println( "    }\n" );


        out.println( "    /** Sometimes you just gotta do it in SQL." );
        out.println( "     *" );
        out.println( "     * This method will at least handle the statements and result sets for you." );
        out.println( "     *" );
        out.println( "     * @param sql A complete SQL statement" );
        out.println( "     */" );
        out.println( "    public static DBResults executeQuery( String sql ) throws SQLException" );
        out.println( "    {" );
        out.println( "        return instance.executeQuery( sql );" );
        out.println( "    }\n" );


        out.println( "    /** Sometimes you just gotta do it in SQL." );
        out.println( "     *" );
        out.println( "     * This method will at least handle the statements for you." );
        out.println( "     *" );
        out.println( "     * @param sql A complete SQL statement" );
        out.println( "     */" );
        out.println( "    public static void executeUpdate( Connection con , String sql ) throws SQLException" );
        out.println( "    {" );
        out.println( "        instance.executeUpdate( con , sql );" );
        out.println( "    }\n" );


        out.println( "    /** Sometimes you just gotta do it in SQL." );
        out.println( "     *" );
        out.println( "     * This method will at least handle the statements for you." );
        out.println( "     *" );
        out.println( "     * @param sql A complete SQL statement" );
        out.println( "     */" );
        out.println( "    public static void executeUpdate( String sql ) throws SQLException" );
        out.println( "    {" );
        out.println( "        instance.executeUpdate( sql );" );
        out.println( "    }\n" );


        out.println( "    /** Use the DBQuery object to do a search." );
        out.println( "     *" );
        out.println( "     *   @param con A database connection. <p>" );
        out.println( "     *   @param q A predescribed query - see DBQuery for details. <p>" );
        out.println( "     *   @return contains zero objects if none are found." );
        out.println( "     */" );
        out.println( "    public static DBResults search( Connection con , DBQuery q ) throws SQLException" );
        out.println( "    {" );
        out.println( "        return instance.search( con , q );" );
        out.println( "    }\n" );

        out.println( "    /** Find all the data in a table that matches up to this SQL where clause." );
        out.println( "     *" );
        out.println( "     *  NOTE!  Use this method only if there is no alternative!  The purpose of these classes" );
        out.println( "     *  is to provide a non-SQL facade to the relational database because some databases" );
        out.println( "     *  are different than others.  If your whereClause SQL fragment uses any SQL that is" );
        out.println( "     *  proprietary to the database you are currently using, there is a good chance that it" );
        out.println( "     *  will not work when the software is ported! <p>" );
        out.println( "     *" );
        out.println( "     *  @param whereClause The SQL to use in selecting the rows - do not say \"WHERE\". <p>" );
        out.println( "     *  @param dataColumns The column names that have the data you want. <p>" );
        out.println( "     *  @return contains zero objects if none are found." );
        out.println( "     */" );
        out.println( "    public static DBResults search( Connection con , String table , String whereClause , String[] dataColumns ) throws SQLException" );
        out.println( "    {" );
        out.println( "        return instance.search( con , table , whereClause , dataColumns );" );
        out.println( "    }\n" );

        out.println( "    /** Find all the data in a table that matches up to this SQL where clause." );
        out.println( "     *" );
        out.println( "     *  NOTE!  Use this method only if there is no alternative!  The purpose of these classes" );
        out.println( "     *  is to provide a non-SQL facade to the relational database because some databases" );
        out.println( "     *  are different than others.  If your whereClause SQL fragment uses any SQL that is" );
        out.println( "     *  proprietary to the database you are currently using, there is a good chance that it" );
        out.println( "     *  will not work when the software is ported! <p>" );
        out.println( "     *" );
        out.println( "     *  @param whereClause The SQL to use in selecting the rows - do not say \"WHERE\". <p>" );
        out.println( "     *  @param dataColumns The column names that have the data you want. <p>" );
        out.println( "     *  @return contains zero objects if none are found." );
        out.println( "     */" );
        out.println( "    public static DBResults search( String table , String whereClause , String[] dataColumns ) throws SQLException" );
        out.println( "    {" );
        out.println( "        return instance.search( table , whereClause , dataColumns );" );
        out.println( "    }\n" );

        out.println( "    /** Find all the data in a table where the text in a column exactly matches a string. <p>" );
        out.println( "     *" );
        out.println( "     * @param dataColumns The searchColumn names that have the data you want. <p>" );
        out.println( "     * @return contains zero objects if none are found." );
        out.println( "     */" );
        out.println( "    public static DBResults search( Connection con , String table , String searchColumn , String searchText , String[] dataColumns ) throws SQLException" );
        out.println( "    {" );
        out.println( "        return instance.search( con , table , searchColumn , searchText , dataColumns );" );
        out.println( "    }\n" );

        out.println( "    /** Find all the data in a table where the text in a column exactly matches one of these strings. <p>" );
        out.println( "     *" );
        out.println( "     * @param dataColumns The searchColumn names that have the data you want. <p>" );
        out.println( "     * @return contains zero objects if none are found." );
        out.println( "     */" );
        out.println( "    public static DBResults search( Connection con , String table , String searchColumn , String[] searchText , String[] dataColumns ) throws SQLException" );
        out.println( "    {" );
        out.println( "        return instance.search( con , table , searchColumn , searchText , dataColumns );" );
        out.println( "    }\n" );

        out.println( "    /** Use the DBQuery object to do a search." );
        out.println( "     *" );
        out.println( "     * @return contains zero objects if none are found." );
        out.println( "     */" );
        out.println( "    public static DBResults search( DBQuery q ) throws SQLException" );
        out.println( "    {" );
        out.println( "        return instance.search( q );" );
        out.println( "    }\n" );

        out.println( "    /** Find all the data in a table where the text in a column exactly matches a string." );
        out.println( "     *" );
        out.println( "     * @param dataColumns The searchColumn names that have the data you want. <p>" );
        out.println( "     * @return contains zero objects if none are found." );
        out.println( "     */" );
        out.println( "    public static DBResults search( String table , String searchColumn , String searchText , String[] dataColumns ) throws SQLException" );
        out.println( "    {" );
        out.println( "        return instance.search( table , searchColumn , searchText , dataColumns );" );
        out.println( "    }\n" );

        out.println( "    /** Find all the data in a table where the text in a column exactly matches one of these strings." );
        out.println( "     *" );
        out.println( "     * @param dataColumns The searchColumn names that have the data you want. <p>" );
        out.println( "     * @return contains zero objects if none are found." );
        out.println( "     */" );
        out.println( "    public static DBResults search( String table , String searchColumn , String searchText[] , String[] dataColumns ) throws SQLException" );
        out.println( "    {" );
        out.println( "        return instance.search( table , searchColumn , searchText , dataColumns );" );
        out.println( "    }\n" );

        out.println( "    /** Update all rows where the searchText matches the text found in column." );
        out.println( "     *" );
        out.println( "     * @param data A key-value collection of searchColumn names (key) and data (value). If a searchColumn name starts with \">\", that searchColumn will be processed as a stream (good for big Strings). <p>" );
        out.println( "     */" );
        out.println( "    public static void update( String table , String searchColumn , String searchText , Map data ) throws SQLException" );
        out.println( "    {" );
        out.println( "        instance.update( table , searchColumn , searchText , data );" );
        out.println( "    }\n" );

        out.println( "    /** Update all rows where the searchText matches the text found in column." );
        out.println( "     *" );
        out.println( "     * @param data A key-value collection of searchColumn names (key) and data (value). If a searchColumn name starts with \">\", that searchColumn will be processed as a stream (good for big Strings). <p>" );
        out.println( "     */" );
        out.println( "    public static void update( Connection con , String table , String column , String searchText , Map data ) throws SQLException" );
        out.println( "    {" );
        out.println( "        instance.update( con , table , column , searchText , data );" );
        out.println( "    }\n" );

        out.println( "    /** Update all rows where the text in a searchColumn exactly matches one of these strings. <p>" );
        out.println( "     *" );
        out.println( "     * @param data A key-value collection of searchColumn names (key) and data (value). If a searchColumn name starts with \">\", that searchColumn will be processed as a stream (good for big Strings). <p>" );
        out.println( "     */" );
        out.println( "    public static void update( String table , String searchColumn , String[] searchText , Map data ) throws SQLException" );
        out.println( "    {" );
        out.println( "        instance.update( table , searchColumn , searchText , data );" );
        out.println( "    }\n" );

        out.println( "    /** Update all rows where the text in a searchColumn exactly matches one of these strings. <p>" );
        out.println( "     *" );
        out.println( "     * @param data A key-value collection of searchColumn names (key) and data (value). If a searchColumn name starts with \">\", that searchColumn will be processed as a stream (good for big Strings). <p>" );
        out.println( "     */" );
        out.println( "    public static void update( Connection con , String table , String searchColumn , String[] searchText , Map data ) throws SQLException" );
        out.println( "    {" );
        out.println( "        instance.update( con , table , searchColumn , searchText , data );" );
        out.println( "    }\n" );

        out.println( "    /** Update all rows matching this where clause." );
        out.println( "     *" );
        out.println( "     *  NOTE!  Use this method only if there is no alternative!  The purpose of these classes" );
        out.println( "     *  is to provide a non-SQL facade to the relational database because some databases" );
        out.println( "     *  are different than others.  If your whereClause SQL fragment uses any SQL that is" );
        out.println( "     *  proprietary to the database you are currently using, there is a good chance that it" );
        out.println( "     *  will not work when the software is ported! <p>" );
        out.println( "     *" );
        out.println( "     * @param whereClause The SQL to use in selecting the rows - do not say \"WHERE\"." );
        out.println( "     * @param data A key-value collection of searchColumn names (key) and data (value). If a searchColumn name starts with \">\", that searchColumn will be processed as a stream (good for big Strings). <p>" );
        out.println( "     */" );
        out.println( "    public static void update( String table , String whereClause , Map data ) throws SQLException" );
        out.println( "    {" );
        out.println( "        instance.update( table , whereClause , data );" );
        out.println( "    }\n" );

        out.println( "    /** Update all rows matching this where clause." );
        out.println( "     *" );
        out.println( "     *  NOTE!  Use this method only if there is no alternative!  The purpose of these classes" );
        out.println( "     *  is to provide a non-SQL facade to the relational database because some databases" );
        out.println( "     *  are different than others.  If your whereClause SQL fragment uses any SQL that is" );
        out.println( "     *  proprietary to the database you are currently using, there is a good chance that it" );
        out.println( "     *  will not work when the software is ported! <p>" );
        out.println( "     *" );
        out.println( "     * @param whereClause The SQL to use in selecting the rows - do not say \"WHERE\"." );
        out.println( "     * @param data A key-value collection of searchColumn names (key) and data (value). If a searchColumn name starts with \">\", that searchColumn will be processed as a stream (good for big Strings). <p>" );
        out.println( "     */" );
        out.println( "    public static void update( Connection con , String table , String whereClause , Map data ) throws SQLException" );
        out.println( "    {" );
        out.println( "        instance.update( con , table , whereClause , data );" );
        out.println( "    }\n" );

        out.println( "    /** Delete all rows where the searchText matches the text found in column." );
        out.println( "     */" );
        out.println( "    public static void delete( Connection con , String table , String searchColumn , String searchText ) throws SQLException" );
        out.println( "    {" );
        out.println( "        instance.delete( con , table , searchColumn , searchText );" );
        out.println( "    }\n" );

        out.println( "    /** Delete all rows where the searchText matches the text found in column." );
        out.println( "     */" );
        out.println( "    public static void delete( String table , String searchColumn , String searchText ) throws SQLException" );
        out.println( "    {" );
        out.println( "        instance.delete( table , searchColumn , searchText );" );
        out.println( "    }\n" );

        out.println( "    /** Delete all rows metching the query." );
        out.println( "     */" );
        out.println( "    public static void delete( Connection con , DBQuery q ) throws SQLException" );
        out.println( "    {" );
        out.println( "        instance.delete( con , q );" );
        out.println( "    }\n" );

        out.println( "    /** Delete all rows metching the query." );
        out.println( "     */" );
        out.println( "    public static void delete( DBQuery q ) throws SQLException" );
        out.println( "    {" );
        out.println( "        instance.delete( q );" );
        out.println( "    }\n" );

        out.println( "    /** Create a new row in the table and populate the fields with the provided data." );
        out.println( "     *" );
        out.println( "     * @param data A key-value collection of searchColumn names (key) and data (value). If a searchColumn name starts with \">\", that searchColumn will be processed as a stream (good for big Strings). <p>" );
        out.println( "     */" );
        out.println( "    public static void insert( Connection con , String table , Map data ) throws SQLException" );
        out.println( "    {" );
        out.println( "        instance.insert( con , table , data );" );
        out.println( "    }\n" );

        out.println( "    /** Create a new row in the table and populate the fields with the provided data." );
        out.println( "     *" );
        out.println( "     * @param data A key-value collection of searchColumn names (key) and data (value). If a searchColumn name starts with \">\", that searchColumn will be processed as a stream (good for big Strings). <p>" );
        out.println( "     */" );
        out.println( "    public static void insert( String table , Map data ) throws SQLException" );
        out.println( "    {" );
        out.println( "        instance.insert( table , data );" );
        out.println( "    }\n" );

        out.println( "    public static long insertAndGetID( String table , Map data ) throws SQLException" );
        out.println( "    {" );
        out.println( "        return instance.insertAndGetID( table , data );" );
        out.println( "    }\n" );

        out.println( "    public static long insertAndGetID( Connection con , String table , Map data ) throws SQLException" );
        out.println( "    {" );
        out.println( "        return instance.insertAndGetID( con , table , data );" );
        out.println( "    }\n" );

        out.println( "    public static long insertAndGetID( String table , Map data , String idColumnName ) throws SQLException" );
        out.println( "    {" );
        out.println( "        return instance.insertAndGetID( table , data , idColumnName );" );
        out.println( "    }\n" );

        out.println( "    public static long insertAndGetID( Connection con , String table , Map data , String idColumnName ) throws SQLException" );
        out.println( "    {" );
        out.println( "        return instance.insertAndGetID( con , table , data , idColumnName );" );
        out.println( "    }\n" );

        if ( usingSpatial )
        {
            out.println( "    public static Object fromXML( String xml )" );
            out.println( "    {" );
            out.println( "        return xstream.fromXML( xml );" );
            out.println( "    }\n" );

            out.println( "    public static String toXML( Object obj )" );
            out.println( "    {" );
            out.println( "        return xstream.toXML( obj );" );
            out.println( "    }\n" );
        }

        out.println( "}" );
    }

    static class Identifier
    {
        String minor ;  // always starts with a lower case letter
        String major ;  // always starts with an upper case letter

        Identifier( String s )
        {
            minor = s ;
            major = s ;
        }
    }

    private static boolean validAttributeData( String name )
    {
        boolean returnVal = false ;
        if ( Str.usable( name ) )
        {
            Str s = new Str( name );
            int capCount = s.countRange( 'A' , 'Z' );
            int lowerCount = s.countRange( 'a' , 'z' );
            int digitCount = s.countDigits();
            int underscores = s.charCount( '_' );
            if ( capCount + lowerCount + digitCount + underscores == s.length() )
            {
                char firstChar = Character.toLowerCase( s.get( 0 ) );
                if ( Numbers.inRange( firstChar, 'a', 'z' ) )
                {
                    returnVal = true;
                }
                else
                {
                    System.out.println( "attribute data '" + name + "' does not start with a letter" );
                }
            }
            else
            {
                System.out.println( "attribute data '" + name + "' contains characters that Jenny doesn't like" );
            }
        }
        else
        {
            System.out.println( "could not work with attribute data - null or empty" );
        }

        return returnVal ;
    }

    // pass in a table name and get back a proper attribute name
    // exposed for unit testing purposes
    static Identifier attributeName( String name )
    {
        Identifier returnVal = new Identifier( name );
        if ( validAttributeData( name ) )
        {
            Str s = new Str( name );
            if ( s.countRange( 'a' , 'z' ) == 0 )
            {
                // all caps, underscores and numbers
                s.toLower();
                boolean done = false;
                while ( ! done )
                {
                    int pos = s.indexOf( '_' );
                    if ( pos == -1 )
                    {
                        done = true;
                    }
                    else
                    {
                        s.delete( pos , 1 );
                        s.toUpper( pos );
                        if ( ( pos == ( s.length() - 2 ) ) && ( s.get( pos ) == 'I' ) && ( s.get( pos + 1 ) == 'd' ) )
                        {
                            s.set( pos + 1 , 'D' );
                        }
                    }
                }
                returnVal.minor = s.toString();
                s.toUpper( 0 );
                returnVal.major = s.toString();
            }
            else
            {
                // some form of mixed case

                // If the first part is an uppercase acronym, the acronym needs to be converted to all lower case.
                // First, let's find the next lower case letter.
                if ( ( s.length() > 1 ) && s.isUpper( 0 ) && s.isUpper( 1 ) )
                {
                    // the first part is an acronym and the acronym is in all upper case
                    returnVal.major = s.toString();


                    // we need to shrink everything up to the character before the first lower case character.
                    boolean done = false;
                    int i = 2;
                    while ( ! done )
                    {
                        if ( s.isLower( i ) )
                        {
                            done = true;
                        }
                        else
                        {
                            s.toLower( i - 1 );
                            i++;
                        }
                    }
                }
                else
                {
                    s.toUpper( 0 );
                    returnVal.major = s.toString();
                }
                s.toLower( 0 );
                returnVal.minor = s.toString();
            }
            s.toLower();
            if ( s.equals( "id" ) )
            {
                returnVal.major = "ID";
                returnVal.minor = "id";
            }
        }
        return returnVal ;
    }

    // exposed for unit testing purposes
    class TableProcessor
    {

        private Map columnProcessors = new HashMap();

        private Identifier tableIdentifier ;
        private String fullTableName ;  // includes the word "Table" or "View" at the end
        String rawTableName ; // exposed for unit testing purposes
        private List columnNames = new ArrayList();
        private List attributes = new ArrayList();
        private List gettersAndSetters = new ArrayList();
        private StringBuffer readerColumns = new StringBuffer();
        private List readers = new ArrayList();
        private List writers = new ArrayList();

        private TextFileOut out ;
        private String tableID = null;
        private String tableIDAttribute = null;
        private boolean importTimestamp = false;
//        private boolean importSpatialPoint = false ;
//        private boolean importSpatialPolygon = false ;
        //private boolean importStr = false;
        private String idColumnName = null;
        private int spatialColumnCount = 0 ;
        private String lastSpatialColumn = null ;

//        TableProcessor( DatabaseMetaData metaData , String tableName , String suffix , String idColumnName ) throws Exception
        TableProcessor( DatabaseMetaData metaData , String tableName , String suffix , Properties properties ) throws Exception
        {
            idColumnName = properties.getProperty( tableName + ".ID" ) ;
            setUpColumnProcessors();
            rawTableName = tableName ;
            tableIdentifier = attributeName( tableName );
            if ( tableIdentifier.major.endsWith( suffix ) )
            {
                tableIdentifier = attributeName( tableName.substring( 0 , tableName.length() - suffix.length() ) );
            }
            fullTableName = tableIdentifier.major + suffix ;

            ResultSet rs = metaData.getColumns( null , null , tableName , null );
            System.out.println( "Creating class " + fullTableName );
            while ( rs.next() )
            {
                String columnName = rs.getString( "COLUMN_NAME" );
                int dataType = rs.getInt( "DATA_TYPE" );
                Object processorKey = new Integer( dataType );

                if ( dataType == java.sql.Types.OTHER )
                {
                    // this could be an oracle spatial type!
                    String spatialType = properties.getProperty( tableName + '.' + columnName );
                    if ( Str.usable( spatialType ) )
                    {
                        processorKey = spatialType ;
                        spatialColumnCount++;
                        lastSpatialColumn = attributeName( columnName ).minor + "ColumnName" ;
                    }
                }

                int nullable = rs.getInt( "NULLABLE" );

                ColumnProcessor processor = (ColumnProcessor)columnProcessors.get( processorKey );
                if ( processor == null )
                {
                    processor = stringProcessor ;
                }
                processor.process( columnName , nullable == DatabaseMetaData.columnNullable );
            }

            out = new TextFileOut( destinationDirectory + fullTableName + ".java" );

            printHeader();
            printImplementationClass();
            printRowClass();
            printConvenienceMethods();

            out.println();
            out.println();

            out.println( "}" );
            out.close();

            if ( Str.usable( mockDirectory ) )
            {
                out = new TextFileOut( mockDirectory + "Mock" + fullTableName + ".java" );
                printMockHeader();
                printMockRowClass();
                printMockConvenienceMethods();
                out.println( "}" );
                out.close();
            }
        }

        private abstract class ColumnProcessor
        {
            abstract void process( String columnName , boolean nullable );
        }

        private abstract class NullableObject extends ColumnProcessor
        {

//            String convertRowAttributeToString( String rowVariableName )
//            {
//                return rowVariableName + ".toString()";
//            }

            void populateLists( String columnName , boolean nullable , String type , String toTypeMethod )
            {
                Identifier attributeName = attributeName( columnName );

                columnNames.add( "    public static final String " + attributeName.minor + "ColumnName = \"" + columnName + "\";" );
                attributes.add( "        private " + type + ' ' + attributeName.minor + " ;" );

                StringBuffer buffy = new StringBuffer();
                buffy.append( "        public " + type + " get" + attributeName.major + "()\n" );
                buffy.append( "        {\n" );
                buffy.append( "            return " + attributeName.minor + " ;\n" );
                buffy.append( "        }\n\n" );
                buffy.append( "        public void set" + attributeName.major + "( " + type + ' ' + attributeName.minor + " )\n" );
                buffy.append( "        {\n" );
                buffy.append( "            this." + attributeName.minor + " = " + attributeName.minor + " ;\n" );
                buffy.append( "        }\n\n" );
                gettersAndSetters.add( buffy.toString() );

                readerColumns.append( attributeName.minor + "ColumnName , " );
                String fromString = "data[" + readers.size() + ']';
                if ( toTypeMethod != null )
                {
                    fromString = toTypeMethod + "( " + fromString + " )";
                }
                readers.add( "                this." + attributeName.minor + " = " + fromString + ';' );

                String writerValue = "this." + attributeName.minor ;
                if ( ! ( type.equals( "String" ) || type.startsWith( "SpatialPo" ) ) )
                {
                    writerValue += " == null ? null : " + writerValue + ".toString()" ;
                }
                writers.add( "            data.put( " + attributeName.minor + "ColumnName , " + writerValue + " );" );
            }

        }

        private ColumnProcessor stringProcessor = new NullableObject()
        {

            public void process( String columnName , boolean nullable )
            {
                populateLists( columnName , nullable , "String" , null );
            }

        };

        private ColumnProcessor timestampProcessor = new NullableObject()
        {
            public void process( String columnName , boolean nullable )
            {
                importTimestamp = true;
                //importStr = true;
                populateLists( columnName , nullable , "Timestamp" , "Str.toTimestamp" );
            }
        };

//        private abstract class SpatialColumnProcessor extends NullableObject
//        {
//            String convertRowAttributeToString( String rowVariableName )
//            {
//                return "xstream.toXML( " + rowVariableName + " )";
//            }
//        }

        private ColumnProcessor spatialPointProcessor = new NullableObject()
        {
            public void process( String columnName , boolean nullable )
            {
//                importSpatialPoint = true;
                populateLists( columnName , nullable , "SpatialPoint" , "(SpatialPoint)" + facadeName + ".fromXML" );
            }
        };

        private ColumnProcessor spatialPolygonProcessor = new NullableObject()
        {
            public void process( String columnName , boolean nullable )
            {
//                importSpatialPolygon = true;
                populateLists( columnName , nullable , "SpatialPolygon" , "(SpatialPolygon)" + facadeName + ".fromXML" );
            }
        };

        private ColumnProcessor spatialMultiPolygonProcessor = new NullableObject()
        {
            public void process( String columnName , boolean nullable )
            {
//                importSpatialPolygon = true;
                populateLists( columnName , nullable , "SpatialPolygon[]" , "(SpatialPolygon[])" + facadeName + ".fromXML" );
            }
        };

        private abstract class NullablePrimitive extends ColumnProcessor
        {

            void populateLists( String columnName , boolean nullable , String type , String wrapper ,
                                String defaultValue , String toTypeMethod )
            {
                Identifier attributeName = attributeName( columnName );
                String x = attributeName.minor ;
                String X = attributeName.major ;
                String xNull = x + "Null";

                columnNames.add( "    public static final String " + x + "ColumnName = \"" + columnName + "\";" );
                attributes.add( "        private " + type + ' ' + x + " ;" );
                if ( nullable )
                {
                    attributes.add( "        private boolean " + xNull + " = true ;" );
                }

                StringBuffer buffy = new StringBuffer();
                buffy.append( "        public " + type + " get" + X + "()\n" );
                buffy.append( "        {\n" );
                buffy.append( "            return " + x + " ;\n" );
                buffy.append( "        }\n\n" );
                buffy.append( "        public void set" + X + "( " + type + ' ' + x + " )\n" );
                buffy.append( "        {\n" );
                buffy.append( "            this." + x + " = " + x + " ;\n" );
                if ( nullable )
                {
                    buffy.append( "            " + xNull + " = false ;\n" );
                }
                buffy.append( "        }\n\n" );
                if ( nullable )
                {
                    buffy.append( "        public void set" + X + "( " + wrapper + ' ' + x + " )\n" );
                    buffy.append( "        {\n" );
                    buffy.append( "            " + xNull + " = ( " + x + " == null );\n" );
                    buffy.append( "            if ( " + xNull + " )\n" );
                    buffy.append( "            {\n" );
                    buffy.append( "                this." + x + " = " + defaultValue + " ;\n" );
                    buffy.append( "            }\n" );
                    buffy.append( "            else\n" );
                    buffy.append( "            {\n" );
                    buffy.append( "                this." + x + " = " + x + '.' + type + "Value() ;\n" );
                    buffy.append( "            }\n" );
                    buffy.append( "        }\n\n" );

                    buffy.append( "        public boolean is" + X + "Null()\n" );
                    buffy.append( "        {\n" );
                    buffy.append( "            return " + xNull + " ;\n" );
                    buffy.append( "        }\n\n" );

                    buffy.append( "        public void set" + X + "Null( boolean " + xNull + " )\n" );
                    buffy.append( "        {\n" );
                    buffy.append( "            this." + xNull + " = " + xNull + " ;\n" );
                    buffy.append( "            if ( " + xNull + " )\n" );
                    buffy.append( "            {\n" );
                    buffy.append( "                " + x + " = " + defaultValue + " ;\n" );
                    buffy.append( "            }\n" );
                    buffy.append( "        }\n\n" );
                }
                gettersAndSetters.add( buffy.toString() );

                readerColumns.append( attributeName.minor + "ColumnName , " );

                if ( nullable )
                {
                    String firstLine = "                this." + xNull + " = ( data[" + readers.size() + "] == null );\n";
                    String getValue = toTypeMethod + "( data[" + readers.size() + "] )";
                    String secondLine = "                this." + x + " = " + xNull + " ? " + defaultValue + " : " + getValue + ';';
                    readers.add( firstLine + secondLine );
                    writers.add( "            data.put( " + attributeName.minor + "ColumnName , this." + xNull + " ? null : String.valueOf( this." + x + " ) );" );
                }
                else
                {
                    readers.add( "                this." + x + " =  " + toTypeMethod + "( data[" + readers.size() + "] );" );

                    String toStringPrefix = "String.valueOf( ";
                    String toStringPostfix = " )";
                    if ( type.equals( "boolean" ) )
                    {
                        toStringPrefix = "";
                        toStringPostfix = " ? \"1\" : \"0\"";
                    }
                    String writerValue = toStringPrefix + " this." + x + toStringPostfix ;
                    writers.add( "            data.put( " + attributeName.minor + "ColumnName , " + writerValue + " );" );
                }
            }

        }

        private ColumnProcessor intProcessor = new NullablePrimitive()
        {
            public void process( String columnName , boolean nullable )
            {
                boolean isGivenID = columnName.equalsIgnoreCase( idColumnName );
                boolean isID = columnName.toLowerCase().equals( "id" );
                boolean isTableNameID = columnName.equalsIgnoreCase( tableIdentifier.minor + "id" );
                boolean isTableName_ID = columnName.equalsIgnoreCase( tableIdentifier.minor + "_id" );
                if ( isGivenID || isID || isTableNameID || isTableName_ID )
                {
                    if ( tableID == null )
                    {
                        tableID = columnName ;
                        tableIDAttribute = attributeName( columnName ).minor ;
                    }
                }
                //importStr = true;
                populateLists( columnName , nullable , "int" , "Integer" , "0" , "Str.toInt" );
            }
        };

        private ColumnProcessor longProcessor = new NullablePrimitive()
        {
            public void process( String columnName , boolean nullable )
            {
                //importStr = true;
                populateLists( columnName , nullable , "long" , "Long" , "0" , "Str.toLong" );
            }
        };

        private ColumnProcessor doubleProcessor = new NullablePrimitive()
        {
            public void process( String columnName , boolean nullable )
            {
                //importStr = true;
                populateLists( columnName , nullable , "double" , "Double" , "0.0" , "Str.toDouble" );
            }
        };

        private ColumnProcessor booleanProcessor = new NullablePrimitive()
        {
            public void process( String columnName , boolean nullable )
            {
                //importStr = true;
                populateLists( columnName , nullable , "boolean" , "Boolean" , "false" , "Str.toBoolean" );
            }
        };

        private void setUpColumnProcessors()
        {
            columnProcessors.put( new Integer( java.sql.Types.INTEGER ) , intProcessor );
            columnProcessors.put( new Integer( java.sql.Types.TINYINT ) , intProcessor );
            columnProcessors.put( new Integer( java.sql.Types.SMALLINT ) , intProcessor );
            columnProcessors.put( new Integer( java.sql.Types.BIGINT ) , longProcessor );
            columnProcessors.put( new Integer( java.sql.Types.DOUBLE ) , doubleProcessor );
            columnProcessors.put( new Integer( java.sql.Types.REAL ) , doubleProcessor );
            if ( usingOracle )
            {
                columnProcessors.put( new Integer( java.sql.Types.DECIMAL ) , intProcessor );
                columnProcessors.put( new Integer( java.sql.Types.NUMERIC ) , intProcessor );
            }
            else
            {
                columnProcessors.put( new Integer( java.sql.Types.DECIMAL ) , doubleProcessor );
                columnProcessors.put( new Integer( java.sql.Types.NUMERIC ) , doubleProcessor );
            }
            columnProcessors.put( new Integer( java.sql.Types.TIMESTAMP ) , timestampProcessor );
            columnProcessors.put( new Integer( java.sql.Types.BIT ) , booleanProcessor );
            columnProcessors.put( new Integer( java.sql.Types.BINARY ) , booleanProcessor );
            columnProcessors.put( new Integer( java.sql.Types.CHAR ) , stringProcessor );
            columnProcessors.put( new Integer( java.sql.Types.LONGVARCHAR ) , stringProcessor );
            columnProcessors.put( new Integer( java.sql.Types.VARCHAR ) , stringProcessor );
            columnProcessors.put( new Integer( java.sql.Types.DATE ) , stringProcessor );
            columnProcessors.put( new Integer( java.sql.Types.TIME ) , stringProcessor );

            columnProcessors.put( spatialPointColumn , spatialPointProcessor );
            columnProcessors.put( spatialPolygonColumn , spatialPolygonProcessor );
            columnProcessors.put( spatialMultiPolygonColumn , spatialMultiPolygonProcessor );
        }

        private void printHeader() throws Exception
        {
            out.println( packageLine );
            out.println();
            out.println( "import java.util.Map ;" );
            out.println( "import java.util.HashMap ;" );
            out.println( "import java.sql.Connection ;" );
            out.println( "import java.sql.SQLException ;" );
            if ( importTimestamp )
            {
                out.println( "import java.sql.Timestamp ;" );
            }
            out.println( "import com.javaranch.common.Str ;" );
//            if ( importSpatialPoint )
//            {
//                out.println( "import com.javaranch.db.spatial.SpatialPoint ;" );
//            }
//            if ( importSpatialPolygon )
//            {
//                out.println( "import com.javaranch.db.spatial.SpatialPolygon ;" );
//            }
            out.println( "import com.javaranch.db.DBResults ;" );
            if ( usingSpatial )
            {
                out.println( "import com.javaranch.db.spatial.* ;" );
            }
            else
            {
                out.println( "import com.javaranch.db.TableFacade ;" );
            }
            out.println();
            out.println( "/** Strongly typed access to the database table \"" + rawTableName + "\"." );
            out.println( " *" );
            out.println( " * This source file was automatically generated by \"Jenny the db code generator\"" );
            out.println( " * based on information found in the database.  Do not modify this file!" );
            out.println( " *" );
            out.println( " * For more information on Jenny, see http://www.javaranch.com/jenny.jsp" );
            out.println( " *" );
            out.println( " *" );
            out.println( " * Most of the methods are static so you don't need to instantiate a copy of this class " );
            out.println( " * to do your work.  The primary access methods are:" );
            out.println( " * <ul>" );
            out.println( " *" );
            out.println( " *     <b>getRow()/getRows()/getAllRows()</b><br>" );
            out.println( " *     <b>search() </b><i>like getRows(), but you can specify which columns you want back</i><br>" );
            out.println( " *     <b>update()</b><br>" );
            out.println( " *     <b>delete()</b><br>" );
            out.println( " *     <b>insert()</b><br>" );
            out.println( " *" );
            out.println( " * </ul>" );
            out.println( " *" );
            out.println( " * These methods all have the option of passing in a connection as the first parameter." );
            out.println( " * Usually you won't use a connection directly, but sometimes it's useful." );
            out.println( " *" );
            out.println( " * The getRows() methods all return an array of Row objects or a single Row object.  The" );
            out.println( " * row object is easy to work with and provides strong type checking.  If your table has" );
            out.println( " * a lot of columns, and your search will return a lot of rows, you might want to consider" );
            out.println( " * using a search() method instead.  You lose some of your strong type checking, but " );
            out.println( " * you might go a lot easier on memory.  In these cases, you will want to make sure you" );
            out.println( " * use the column name constants found at the top of this class." );
            out.println( " *" );
            out.println( " */" );
            out.println( "public class " + fullTableName );
            out.println( "{" );
            out.println();
            out.println( "    private static Implementation imp = new Implementation();" );
            out.println();
            out.println( "    public static final String tableName = \"" + rawTableName + "\";" );
            out.println();

            // write out all of the column names
            for ( int i = 0 ; i < columnNames.size() ; i++ )
            {
                out.println( (String)columnNames.get( i ) );
            }
            out.println();

            // the array of column names
            out.println( "    private static String[] allColumns =" );
            out.println( "    {" );
            out.println( "        " + readerColumns );
            out.println( "    };" );
            out.println();
            out.println( "    /** You probably want to use the static methods for most of your access, but once in a while you might need to" );
            out.println( "     *  pass an instance object to a method that knows how to work with these sorts of tables." );
            out.println( "     */" );
            out.println( "    public static Implementation getInstance()" );
            out.println( "    {" );
            out.println( "        return imp ;" );
            out.println( "    }" );
            out.println();
            out.println( "    /** For use by unit testing, although you could provide your own implementation here if" );
            out.println( "     *  you wanted to." );
            out.println( "     *  " );
            out.println( "     *  To use this in your unit testing, create an instance of Mock" + fullTableName + " and pass" );
            out.println( "     *  it in here.  Then set your mock return values, call the method you are testing and examine" );
            out.println( "     *  the mock values that are now set!" );
            out.println( "     */" );
            out.println( "    public static void setInstance( " + fullTableName + ".Implementation instance  )" );
            out.println( "    {" );
            out.println( "        imp = instance ;" );
            out.println( "    }" );
            out.println();
        }


        // todo - add getQuery() method that would return a query pre-populated with the table/view name and a search
        // todo   for the ID column (if one is specified)

        private void printImplementationClass() throws Exception
        {
            out.println( "    /** Exposed for unit testing purposes only! */" );
            if ( usingSpatial )
            {
                out.println( "    static class Implementation extends SpatialTableFacade" );
            }
            else
            {
                out.println( "    static class Implementation extends TableFacade" );
            }
            out.println( "    {" );
            out.println();
            out.println( "        /** Exposed for unit testing purposes only! */" );
            out.println( "        Implementation()" );
            out.println( "        {" );
            out.println( dbFacadeLine );
            out.println( "        }" );
            out.println();
            out.println( "        // convert a DBResults object to an array of Row objects." );
            out.println( "        // requires that all of the columns be represented in the DBResults object and in the right order" );
            out.println( "        private static Row[] rowArray( DBResults r )" );
            out.println( "        {" );
            out.println( "            Row[] rows = new Row[ r.size() ];" );
            out.println( "            for( int i = 0 ; i < rows.length ; i++ )" );
            out.println( "            {" );
            out.println( "                rows[ i ] = new Row( r.getRow( i ) );" );
            out.println( "            }" );
            out.println( "            return rows ;" );
            out.println( "        }" );
            out.println();
            out.println( "        /** Instantiate an empty Row object */" );
            out.println( "        public Row getRow()" );
            out.println( "        {" );
            out.println( "            // if you are wondering about why this method is so lame - it's for unit testing!" );
            out.println( "            // The idea is that during unit testing, a different test object will be returned here." );
            out.println( "            // To learn more about unit testing with Jenny generated code, visit " +
                         "<a href=\"http://www.javaranch.com/jenny.jsp\">www.javaranch.com/jenny.jsp</a>" );
            out.println( "            return new Row();" );
            out.println( "        }\n" );

            if ( tableID != null )
            {
                //todo - make sure all use of the primary key table name uses the declared constant and not a string literal

                out.println( "        /** Instantiate a Row object and fill its content based on a search for the ID. " );
                out.println( "         *" );
                out.println( "         * Return null if not found.  Return first item if more than one found." );
                out.println( "         */" );
                out.println( "        public Row getRow( Connection con , int " + tableIDAttribute + " ) throws SQLException" );
                out.println( "        {" );
                out.println( "            Row row = new Row( this.search( con , \"" + tableID + "\" , String.valueOf( " + tableIDAttribute + " ) , allColumns ) );" );
                out.println( "            return row.dataLoadedFromDatabase() ? row : null ;" );
                out.println( "        }\n" );

                out.println( "        /** Instantiate a Row object and fill its content based on a search for the ID." );
                out.println( "         *" );
                out.println( "         * Return null if not found." );
                out.println( "         */" );
                out.println( "        public Row getRow( long " + tableIDAttribute + " ) throws SQLException" );
                out.println( "        {" );
                out.println( "            Row row = new Row( this.search( \"" + tableID + "\" , String.valueOf( " + tableIDAttribute + " ) , allColumns ) );" );
                out.println( "            return row.dataLoadedFromDatabase() ? row : null ;" );
                out.println( "        }\n" );
            }

            out.println( "        /** Instantiate a Row object and fill its content based on a search" );
            out.println( "         *" );
            out.println( "         * Return null if not found." );
            out.println( "         */" );
            out.println( "        public Row getRow( Connection con , String column , String searchText ) throws SQLException" );
            out.println( "        {" );
            out.println( "            Row row = new Row( this.search( con , column , searchText , allColumns ) );" );
            out.println( "            return row.dataLoadedFromDatabase() ? row : null ;" );
            out.println( "        }\n" );

            out.println( "        /** Instantiate a Row object and fill its content based on a search" );
            out.println( "         *" );
            out.println( "         * Return null if not found." );
            out.println( "         */" );
            out.println( "        public Row getRow( String column , String searchText ) throws SQLException" );
            out.println( "        {" );
            out.println( "            Row row = new Row( this.search( column , searchText , allColumns ) );" );
            out.println( "            return row.dataLoadedFromDatabase() ? row : null ;" );
            out.println( "        }\n" );

            out.println( "        /** Return an array of length zero if nothing found */" );
            out.println( "        public Row[] getRows( Connection con , String column , String searchText ) throws SQLException" );
            out.println( "        {" );
            out.println( "            return rowArray( this.search( con , column , searchText , allColumns ) );" );
            out.println( "        }\n" );

            out.println( "        /** Return an array of length zero if nothing found */" );
            out.println( "        public Row[] getRows( String column , String searchText ) throws SQLException" );
            out.println( "        {" );
            out.println( "            return rowArray( this.search( column , searchText , allColumns ) );" );
            out.println( "        }\n" );

            out.println( "        /** Return an array of length zero if nothing found */" );
            out.println( "        public Row[] getRows( Connection con , String column , String[] searchText ) throws SQLException" );
            out.println( "        {" );
            out.println( "            return rowArray( this.search( con , column , searchText , allColumns ) );" );
            out.println( "        }\n" );

            out.println( "        /** Return an array of length zero if nothing found */" );
            out.println( "        public Row[] getRows( String column , String[] searchText ) throws SQLException" );
            out.println( "        {" );
            out.println( "            return rowArray( this.search( column , searchText , allColumns ) );" );
            out.println( "        }\n" );

            out.println( "        /** Return an array of length zero if nothing found */" );
            out.println( "        public Row[] getRows( Connection con , String whereClause ) throws SQLException" );
            out.println( "        {" );
            out.println( "            return rowArray( this.search( con , whereClause , allColumns ) );" );
            out.println( "        }\n" );

            out.println( "        /** Return an array of length zero if nothing found */" );
            out.println( "        public Row[] getRows( String whereClause ) throws SQLException" );
            out.println( "        {" );
            out.println( "            return rowArray( this.search( whereClause , allColumns ) );" );
            out.println( "        }\n" );

            if ( spatialColumnCount == 1 )
            {
                out.println( "        public Row[] getRows( SpatialPolygon searchArea ) throws SQLException" );
                out.println( "        {" );
                out.println( "            return rowArray( this.search( " + lastSpatialColumn + " ,  searchArea , allColumns ) );" );
                out.println( "        }\n" );

                out.println( "        public Row[] getRows( Connection con , SpatialPolygon searchArea ) throws SQLException" );
                out.println( "        {" );
                out.println( "            return rowArray( this.search( con , " + lastSpatialColumn + " ,  searchArea , allColumns ) );" );
                out.println( "        }\n" );

                out.println( "        public Row[] getRows( SpatialPolygon[] searchArea ) throws SQLException" );
                out.println( "        {" );
                out.println( "            return rowArray( this.search( " + lastSpatialColumn + " ,  searchArea , allColumns ) );" );
                out.println( "        }" );

                out.println( "        public Row[] getRows( Connection con , SpatialPolygon[] searchArea ) throws SQLException" );
                out.println( "        {" );
                out.println( "            return rowArray( this.search( con , " + lastSpatialColumn + " ,  searchArea , allColumns ) );" );
                out.println( "        }\n" );
            }

            if ( spatialColumnCount > 0 )
            {
                out.println( spatialImpGetRows );
            }

            out.println( "        /** Return an array of length zero if nothing found */" );
            out.println( "        public Row[] getAllRows( Connection con ) throws SQLException" );
            out.println( "        {" );
            out.println( "            return rowArray( this.search( con , allColumns ) );" );
            out.println( "        }\n" );

            out.println( "        /** Return an array of length zero if nothing found */" );
            out.println( "        public Row[] getAllRows() throws SQLException" );
            out.println( "        {" );
            out.println( "            return rowArray( this.search( allColumns ) );" );
            out.println( "        }\n" );

            if ( tableID != null )
            {
                out.println( "        public void update( Connection con , int " + tableIDAttribute + " , Map data ) throws SQLException" );
                out.println( "        {" );
                out.println( "            this.update( con , \"" + tableID + "\" , String.valueOf( " + tableIDAttribute + " ) , data );" );
                out.println( "        }\n" );

                out.println( "        public void update( int " + tableIDAttribute + " , Map data ) throws SQLException" );
                out.println( "        {" );
                out.println( "            this.update( \"" + tableID + "\" , String.valueOf( " + tableIDAttribute + " ) , data );" );
                out.println( "        }\n" );

                out.println( "        public void delete( Connection con , long " + tableIDAttribute + " ) throws SQLException" );
                out.println( "        {" );
                out.println( "            this.delete( con , \"" + tableID + "\" , String.valueOf( " + tableIDAttribute + " ) );" );
                out.println( "        }\n" );

                out.println( "        public void delete( long " + tableIDAttribute + " ) throws SQLException" );
                out.println( "        {" );
                out.println( "            this.delete( \"" + tableID + "\" , String.valueOf( " + tableIDAttribute + " ) );" );
                out.println( "        }\n" );

                out.println( "        public long insertAndGetID( Connection con , Map data ) throws SQLException" );
                out.println( "        {" );
                out.println( "            return this.insertAndGetID( con , data , \"" + tableID + "\" );" );
                out.println( "        }\n" );

                out.println( "        public long insertAndGetID( Map data ) throws SQLException" );
                out.println( "        {" );
                out.println( "            return this.insertAndGetID( data , \"" + tableID + "\" );" );
                out.println( "        }\n" );
            }
            out.println();
            out.println( "    }" );
            out.println();
        }

        private void printRowClass() throws Exception
        {
            out.println( "    public static class Row" );
            out.println( "    {" );
            out.println();
            out.println( "        private boolean dataLoadedFromDatabase = false ;" );
            out.println();

            // write out the "Row" attributes
            for ( int i = 0 ; i < attributes.size() ; i++ )
            {
                out.println( (String)attributes.get( i ) );
            }
            out.println();

            out.println( "        /** for internal use only!   If you need a row object, use getRow(). */" );
            out.println( "        Row()" );
            out.println( "        {" );
            out.println( "        }" );
            out.println();

            out.println( "        private Row( String[] data )" );
            out.println( "        {" );
            out.println( "            if ( data != null )" );
            out.println( "            {" );
            for ( int i = 0 ; i < readers.size() ; i++ )
            {
                out.println( (String)readers.get( i ) );
            }
            out.println( "                dataLoadedFromDatabase = true ;" );
            out.println( "            }" );
            out.println( "        }" );
            out.println();

            out.println( "        private Row( DBResults results )" );
            out.println( "        {" );
            out.println( "            this( results.getRow(0) );" );
            out.println( "        }" );
            out.println();

            // write out the "Row" getters and setters
            for ( int i = 0 ; i < gettersAndSetters.size() ; i++ )
            {
                out.println( (String)gettersAndSetters.get( i ) );
            }

            out.println();
            out.println( "        " );
            out.println( "        private boolean dataLoadedFromDatabase()" );
            out.println( "        {" );
            out.println( "            return dataLoadedFromDatabase ;" );
            out.println( "        }" );
            out.println();

            out.println( "        private Map buildDataMap()" );
            out.println( "        {" );
            out.println( "            Map data = new HashMap();" );
            for ( int i = 0 ; i < writers.size() ; i++ )
            {
                out.println( (String)writers.get( i ) );
            }
            out.println( "            return data ;" );
            out.println( "        }" );
            out.println();


            out.println( "        /** update a row object based on a search */" );
            out.println( "        public void update( Connection con , String column , String searchText ) throws SQLException" );
            out.println( "        {" );
            out.println( "            imp.update( con , column , searchText , buildDataMap() );" );
            out.println( "        }" );
            out.println();

            out.println( "        /** update a row object based on a search */" );
            out.println( "        public void update( String column , String searchText ) throws SQLException" );
            out.println( "        {" );
            out.println( "            imp.update( column , searchText , buildDataMap() );" );
            out.println( "        }" );
            out.println();

            if ( tableID == null )
            {
                out.println( "        /** create a new row.*/" );
                out.println( "        public void insert( Connection con ) throws SQLException" );
                out.println( "        {" );
                out.println( "            imp.insert( con , buildDataMap() );" );
                out.println( "        }\n" );

                out.println( "        /** create a new row.*/" );
                out.println( "        public void insert() throws SQLException" );
                out.println( "        {" );
                out.println( "            imp.insert( buildDataMap() );" );
                out.println( "        }\n" );
            }
            else
            {
                out.println( "        /** update a row object based on the id */" );
                out.println( "        public void update( Connection con ) throws SQLException" );
                out.println( "        {" );
                out.println( "            imp.update( con , " + tableIDAttribute + " , buildDataMap() );" );
                out.println( "        }" );
                out.println();

                out.println( "        /** update a row object based on the id */" );
                out.println( "        public void update() throws SQLException" );
                out.println( "        {" );
                out.println( "            imp.update( " + tableIDAttribute + " , buildDataMap() );" );
                out.println( "        }" );
                out.println();

                out.println( "        /** create a new row complete with a new ID.\n" );
                out.println( "            The current ID is ignored.  The new ID is placed in the row.\n" );
                out.println( "            @return the new row ID " );
                out.println( "        */" );
                out.println( "        public long insert( Connection con ) throws SQLException" );
                out.println( "        {" );
                out.println( "            return imp.insertAndGetID( con , buildDataMap() );" );
                out.println( "        }\n" );

                out.println( "        /** create a new row complete with a new ID.\n" );
                out.println( "            The current ID is ignored.  The new ID is placed in the row.\n" );
                out.println( "            @return the new row ID " );
                out.println( "        */" );
                out.println( "        public long insert() throws SQLException" );
                out.println( "        {" );
                out.println( "            return imp.insertAndGetID( buildDataMap() );" );
                out.println( "        }\n" );

                out.println( "        /** delete a row object based on the id */" );
                out.println( "        public void delete( Connection con ) throws SQLException" );
                out.println( "        {" );
                out.println( "            imp.delete( con , " + tableIDAttribute + " );" );
                out.println( "        }" );
                out.println();

                out.println( "        /** delete a row object based on the id */" );
                out.println( "        public void delete() throws SQLException" );
                out.println( "        {" );
                out.println( "            imp.delete( " + tableIDAttribute + " );" );
                out.println( "        }" );
                out.println();

            }

            out.println();
            out.println( "    }" );
            out.println();
        }

        private void printConvenienceMethods() throws Exception
        {

            out.println( "    /** Return an empty row object */" );
            out.println( "    public static Row getRow()" );
            out.println( "    {" );
            out.println( "        return imp.getRow();" );
            out.println( "    }\n" );

            if ( tableID != null )
            {
                out.println( "    /** Instantiate a Row object and fill its content based on a search for the ID. " );
                out.println( "     *" );
                out.println( "     * Return null if not found." );
                out.println( "     */" );
                out.println( "    public static Row getRow( Connection con , int " + tableIDAttribute + " ) throws SQLException" );
                out.println( "    {" );
                out.println( "        return imp.getRow( con , " + tableIDAttribute + " );" );
                out.println( "    }\n" );

                out.println( "    /** Instantiate a Row object and fill its content based on a search for the ID. " );
                out.println( "     *" );
                out.println( "     * Return null if not found." );
                out.println( "     */" );
                out.println( "    public static Row getRow( long " + tableIDAttribute + " ) throws SQLException" );
                out.println( "    {" );
                out.println( "        return imp.getRow( " + tableIDAttribute + " );" );
                out.println( "    }\n" );
            }

            out.println( "    /** Instantiate a Row object and fill its content based on a search" );
            out.println( "     *" );
            out.println( "     * Return null if not found." );
            out.println( "     */" );
            out.println( "    public static Row getRow( Connection con , String column , String searchText ) throws SQLException" );
            out.println( "    {" );
            out.println( "        return imp.getRow( con , column , searchText );" );
            out.println( "    }\n" );

            out.println( "    /** Instantiate a Row object and fill its content based on a search" );
            out.println( "     *" );
            out.println( "     * Return null if not found." );
            out.println( "     */" );
            out.println( "    public static Row getRow( String column , String searchText ) throws SQLException" );
            out.println( "    {" );
            out.println( "        return imp.getRow( column , searchText );" );
            out.println( "    }\n" );

            out.println( "    /** Return an array of length zero if nothing found */" );
            out.println( "    public static Row[] getRows( Connection con , String column , String searchText ) throws SQLException" );
            out.println( "    {" );
            out.println( "        return imp.getRows( con , column , searchText );" );
            out.println( "    }\n" );

            out.println( "    /** Return an array of length zero if nothing found */" );
            out.println( "    public static Row[] getRows( String column , String searchText ) throws SQLException" );
            out.println( "    {" );
            out.println( "        return imp.getRows( column , searchText );" );
            out.println( "    }\n" );


            out.println( "    /** Return an array of length zero if nothing found */" );
            out.println( "    public static Row[] getRows( Connection con , String column , String[] searchText ) throws SQLException" );
            out.println( "    {" );
            out.println( "        return imp.getRows( con , column , searchText );" );
            out.println( "    }\n" );

            out.println( "    /** Return an array of length zero if nothing found */" );
            out.println( "    public static Row[] getRows( String column , String[] searchText ) throws SQLException" );
            out.println( "    {" );
            out.println( "        return imp.getRows( column , searchText );" );
            out.println( "    }\n" );


            out.println( "    /** Return an array of length zero if nothing found */" );
            out.println( "    public static Row[] getRows( Connection con , String column , int searchValue ) throws SQLException" );
            out.println( "    {" );
            out.println( "        return imp.getRows( con , column , String.valueOf( searchValue ) );" );
            out.println( "    }\n" );

            out.println( "    /** Return an array of length zero if nothing found */" );
            out.println( "    public static Row[] getRows( String column , int searchValue ) throws SQLException" );
            out.println( "    {" );
            out.println( "        return imp.getRows( column , String.valueOf( searchValue ) );" );
            out.println( "    }\n" );


            out.println( "    /** Return an array of length zero if nothing found */" );
            out.println( "    public static Row[] getRows( Connection con , String column , int[] searchValues ) throws SQLException" );
            out.println( "    {" );
            out.println( "        return imp.getRows( con , column , Str.toStringArray( searchValues ) );" );
            out.println( "    }\n" );

            out.println( "    /** Return an array of length zero if nothing found */" );
            out.println( "    public static Row[] getRows( String column , int[] searchValues ) throws SQLException" );
            out.println( "    {" );
            out.println( "        return imp.getRows( column , Str.toStringArray( searchValues ) );" );
            out.println( "    }\n" );


            out.println( "    /** Return an array of length zero if nothing found */" );
            out.println( "    public static Row[] getRows( Connection con , String whereClause ) throws SQLException" );
            out.println( "    {" );
            out.println( "        return imp.getRows( con , whereClause );" );
            out.println( "    }\n" );

            out.println( "    /** Return an array of length zero if nothing found */" );
            out.println( "    public static Row[] getRows( String whereClause ) throws SQLException" );
            out.println( "    {" );
            out.println( "        return imp.getRows( whereClause );" );
            out.println( "    }\n" );

            if ( spatialColumnCount == 1 )
            {
                out.println( spatialGetRows_knownSpatialColumn );
            }

            if ( spatialColumnCount > 0 )
            {
                out.println( spatialGetRows );
            }

            out.println( "    /** Return an array of length zero if nothing found */" );
            out.println( "    public static Row[] getAllRows( Connection con ) throws SQLException" );
            out.println( "    {" );
            out.println( "        return imp.getAllRows( con );" );
            out.println( "    }\n" );

            out.println( "    /** Return an array of length zero if nothing found */" );
            out.println( "    public static Row[] getAllRows() throws SQLException" );
            out.println( "    {" );
            out.println( "        return imp.getAllRows();" );
            out.println( "    }\n" );


            out.println( "    public static DBResults search( Connection con , String column , String searchText , String[] dataColumns ) throws SQLException" );
            out.println( "    {" );
            out.println( "        return imp.search( con , column , searchText , dataColumns );" );
            out.println( "    }\n" );

            out.println( "    public static DBResults search( String column , String searchText , String[] dataColumns ) throws SQLException" );
            out.println( "    {" );
            out.println( "        return imp.search( column , searchText , dataColumns );" );
            out.println( "    }\n" );

            out.println( "    public static DBResults search( Connection con , String column , String[] searchText , String[] dataColumns ) throws SQLException" );
            out.println( "    {" );
            out.println( "        return imp.search( con , column , searchText , dataColumns );" );
            out.println( "    }\n" );

            out.println( "    public static DBResults search( String column , String searchText[] , String[] dataColumns ) throws SQLException" );
            out.println( "    {" );
            out.println( "        return imp.search( column , searchText , dataColumns );" );
            out.println( "    }\n" );

            out.println( "    public static DBResults search( Connection con , String column , int searchValue , String[] dataColumns ) throws SQLException" );
            out.println( "    {" );
            out.println( "        return imp.search( con , column , searchValue , dataColumns );" );
            out.println( "    }\n" );

            out.println( "    public static DBResults search( String column , int searchValue , String[] dataColumns ) throws SQLException" );
            out.println( "    {" );
            out.println( "        return imp.search( column , searchValue , dataColumns );" );
            out.println( "    }\n" );

            out.println( "    public static DBResults search( Connection con , String column , int[] searchValues , String[] dataColumns ) throws SQLException" );
            out.println( "    {" );
            out.println( "        return imp.search( con , column , searchValues , dataColumns );" );
            out.println( "    }\n" );

            out.println( "    public static DBResults search( String column , int[] searchValues , String[] dataColumns ) throws SQLException" );
            out.println( "    {" );
            out.println( "        return imp.search( column , searchValues , dataColumns );" );
            out.println( "    }\n" );

            out.println( "    public static DBResults search( Connection con , String whereClause , String[] dataColumns ) throws SQLException" );
            out.println( "    {" );
            out.println( "        return imp.search( con , whereClause , dataColumns );" );
            out.println( "    }\n" );

            out.println( "    public static DBResults search( String whereClause , String[] dataColumns ) throws SQLException" );
            out.println( "    {" );
            out.println( "        return imp.search( whereClause , dataColumns );" );
            out.println( "    }\n" );

            out.println( "    public static DBResults search( Connection con , String[] dataColumns ) throws SQLException" );
            out.println( "    {" );
            out.println( "        return imp.search( con , dataColumns );" );
            out.println( "    }\n" );

            out.println( "    public static DBResults search( String[] dataColumns ) throws SQLException" );
            out.println( "    {" );
            out.println( "        return imp.search( dataColumns );" );
            out.println( "    }\n" );


            out.println( "    public static void update( Connection con , String column , String searchText , Map data ) throws SQLException" );
            out.println( "    {" );
            out.println( "        imp.update( con , column , searchText , data );" );
            out.println( "    }\n" );

            out.println( "    public static void update( String column , String searchText , Map data ) throws SQLException" );
            out.println( "    {" );
            out.println( "        imp.update( column , searchText , data );" );
            out.println( "    }\n" );


            if ( tableID != null )
            {
                out.println( "    public static void delete( Connection con , long " + tableIDAttribute + " ) throws SQLException" );
                out.println( "    {" );
                out.println( "        imp.delete( con , " + tableIDAttribute + " );" );
                out.println( "    }\n" );

                out.println( "    public static void delete( long " + tableIDAttribute + " ) throws SQLException" );
                out.println( "    {" );
                out.println( "        imp.delete( " + tableIDAttribute + " );" );
                out.println( "    }\n" );
            }

            out.println( "    public static void delete( Connection con , String column , String searchText ) throws SQLException" );
            out.println( "    {" );
            out.println( "        imp.delete( con , column , searchText );" );
            out.println( "    }\n" );

            out.println( "    public static void delete( String column , String searchText ) throws SQLException" );
            out.println( "    {" );
            out.println( "        imp.delete( column , searchText );" );
            out.println( "    }\n" );

            if ( tableID == null )
            {
                out.println( "    public static void insert( Connection con , Map data ) throws SQLException" );
                out.println( "    {" );
                out.println( "        imp.insert( con , data );" );
                out.println( "    }\n" );

                out.println( "    public static void insert( Map data ) throws SQLException" );
                out.println( "    {" );
                out.println( "        imp.insert( data );" );
                out.println( "    }\n" );
            }
            else
            {
                out.println( "    public static long insert( Connection con , Map data ) throws SQLException" );
                out.println( "    {" );
                out.println( "        return imp.insertAndGetID( con , data );" );
                out.println( "    }\n" );

                out.println( "    public static long insert( Map data ) throws SQLException" );
                out.println( "    {" );
                out.println( "        return imp.insertAndGetID( data );" );
                out.println( "    }\n" );
            }


        }

        private void printMockHeader() throws Exception
        {
            out.println( packageLine );
            out.println();
            out.println( "import java.util.* ;" );
            out.println( "import java.sql.* ;" );
            out.println( "import com.javaranch.db.* ;" );
            out.println();
            out.println( "public class Mock" + fullTableName + " extends " + fullTableName + ".Implementation" );
            out.println( "{" );
            out.println();
        }

        private void printMockRowClass() throws Exception
        {
            out.println( "    public static class Row extends " + fullTableName + ".Row" );
            out.println( "    {" );
            out.println();

            out.println( "        public Connection update_con = null ;" );
            out.println( "        public String update_column = null ;" );
            out.println( "        public String update_searchText = null ;" );
            out.println( "        public int update_calls = 0 ;" );
            out.println( "        " );
            out.println( "        public void update( Connection con , String column , String searchText ) throws SQLException" );
            out.println( "        {" );
            out.println( "            update_con = con ;" );
            out.println( "            update_column = column ;" );
            out.println( "            update_searchText = searchText ;" );
            out.println( "            update_calls++;" );
            out.println( "        }" );
            out.println();
            out.println( "        public void update( String column , String searchText ) throws SQLException" );
            out.println( "        {" );
            out.println( "            update_column = column ;" );
            out.println( "            update_searchText = searchText ;" );
            out.println( "            update_calls++;" );
            out.println( "        }\n" );

            if ( tableID != null )
            {
                out.println( "        public void update( Connection con ) throws SQLException" );
                out.println( "        {" );
                out.println( "            update_con = con ;" );
                out.println( "            update_calls++;" );
                out.println( "        }" );
                out.println();

                out.println( "        public void update() throws SQLException" );
                out.println( "        {" );
                out.println( "            update_calls++;" );
                out.println( "        }" );
                out.println();

                out.println( "        public Connection insert_con = null ;" );
                out.println( "        public int insert_calls = 0 ;" );
                out.println( "        public long insert_return = 0 ;\n" );

                out.println( "        public long insert( Connection con ) throws SQLException" );
                out.println( "        {" );
                out.println( "            insert_con = con ;" );
                out.println( "            insert_calls++;" );
                out.println( "            return insert_return ;" );
                out.println( "        }\n" );

                out.println( "        public long insert() throws SQLException" );
                out.println( "        {" );
                out.println( "            insert_calls++;" );
                out.println( "            return insert_return ;" );
                out.println( "        }\n" );
            }
            else
            {
                out.println( "        public Connection insert_con = null ;" );
                out.println( "        public int insert_calls = 0 ;\n" );

                out.println( "        public void insert( Connection con ) throws SQLException" );
                out.println( "        {" );
                out.println( "            insert_con = con ;" );
                out.println( "            insert_calls++;" );
                out.println( "        }\n" );

                out.println( "        public void insert() throws SQLException" );
                out.println( "        {" );
                out.println( "            insert_calls++;" );
                out.println( "        }\n" );

            }

            out.println( "    }" );
            out.println();
        }

        private void printMockReturns( String returnType , String  calls , String singleReturn ) throws Exception
        {
            String returnList = singleReturn + 's';
            out.println( "        " + returnType + " returnVal = " + singleReturn + " ;" );
            out.println( "        if ( ( returnVal == null ) && ( " + calls + " < " + returnList + ".size() ) )" );
            out.println( "        {" );
            out.println( "            returnVal = (" + returnType + ')' + returnList + ".get( " + calls + " );" );
            out.println( "        }" );

            out.println( "        " + calls + "++;" );
            out.println( "        return returnVal ;" );

        }

        private void printMockConvenienceMethods() throws Exception
        {
            out.println( "    public Connection getRow_con = null ;" );
            out.println( "    public String getRow_column = null ;" );
            out.println( "    public String getRow_searchText = null ;" );
            if ( tableID != null )
            {
                out.println( "    public long getRow_" + tableIDAttribute + " = 0 ;" );
            }
            out.println( "    public int getRow_calls = 0 ;" );
            out.println( "    public " + fullTableName + ".Row getRow_return = null ;" );
            out.println( "    public List getRow_returns = new ArrayList(); // use this instead of getRow_return for multiple calls" );
            out.println();

            if ( tableID != null )
            {

                out.println( "    public " + fullTableName + ".Row getRow( Connection con , long " + tableIDAttribute + " )" );
                out.println( "    {" );
                out.println( "        getRow_con = con ;" );
                out.println( "        getRow_" + tableIDAttribute + " = " + tableIDAttribute + " ;" );
                printMockReturns( fullTableName + ".Row" , "getRow_calls" , "getRow_return" );
                out.println( "    }\n" );

                out.println( "    public " + fullTableName + ".Row getRow( long " + tableIDAttribute + " )" );
                out.println( "    {" );
                out.println( "        getRow_" + tableIDAttribute + " = " + tableIDAttribute + " ;" );
                printMockReturns( fullTableName + ".Row" , "getRow_calls" , "getRow_return" );
                out.println( "    }\n" );
            }

            out.println( "    public " + fullTableName + ".Row getRow()" );
            out.println( "    {" );
            printMockReturns( fullTableName + ".Row" , "getRow_calls" , "getRow_return" );
            out.println( "    }\n" );

            out.println( "    public " + fullTableName + ".Row getRow( Connection con , String column , String searchText ) throws SQLException" );
            out.println( "    {" );
            out.println( "        getRow_con = con ;" );
            out.println( "        getRow_column = column ;" );
            out.println( "        getRow_searchText = searchText ;" );
            printMockReturns( fullTableName + ".Row" , "getRow_calls" , "getRow_return" );
            out.println( "    }\n" );

            out.println( "    public " + fullTableName + ".Row getRow( String column , String searchText ) throws SQLException" );
            out.println( "    {" );
            out.println( "        getRow_column = column ;" );
            out.println( "        getRow_searchText = searchText ;" );
            printMockReturns( fullTableName + ".Row" , "getRow_calls" , "getRow_return" );
            out.println( "    }\n" );

            out.println( "    public Connection getRows_con = null ;" );
            out.println( "    public String getRows_column = null ;" );
            out.println( "    public Object getRows_searchText = null ; // could be a String or String[]" );
            out.println( "    public String getRows_whereClause = null ;" );
            if ( tableID != null )
            {
                out.println( "    public long getRows_" + tableIDAttribute + " = 0 ;" );
            }
            out.println( "    public int getRows_calls = 0 ;" );
            out.println( "    public " + fullTableName + ".Row[] getRows_return = null ;" );
            out.println( "    public List getRows_returns = new ArrayList(); // use this instead of getRows_return for multiple calls\n" );

            out.println( "    public " + fullTableName + ".Row[] getRows( Connection con , String column , String searchText ) throws SQLException" );
            out.println( "    {" );
            out.println( "        getRows_con = con ;" );
            out.println( "        getRows_column = column ;" );
            out.println( "        getRows_searchText = searchText ;" );
            printMockReturns( fullTableName + ".Row[]" , "getRows_calls" , "getRows_return" );
            out.println( "    }\n" );

            out.println( "    public " + fullTableName + ".Row[] getRows( String column , String searchText ) throws SQLException" );
            out.println( "    {" );
            out.println( "        getRows_column = column ;" );
            out.println( "        getRows_searchText = searchText ;" );
            printMockReturns( fullTableName + ".Row[]" , "getRows_calls" , "getRows_return" );
            out.println( "    }\n" );

            out.println( "    public " + fullTableName + ".Row[] getRows( Connection con , String column , String[] searchText ) throws SQLException" );
            out.println( "    {" );
            out.println( "        getRows_con = con ; " );
            out.println( "        getRows_column = column ; " );
            out.println( "        getRows_searchText = searchText ; " );
            printMockReturns( fullTableName + ".Row[]" , "getRows_calls" , "getRows_return" );
            out.println( "    }\n" );

            out.println( "    public " + fullTableName + ".Row[] getRows( String column , String[] searchText ) throws SQLException" );
            out.println( "    {" );
            out.println( "        getRows_column = column ;" );
            out.println( "        getRows_searchText = searchText ;" );
            printMockReturns( fullTableName + ".Row[]" , "getRows_calls" , "getRows_return" );
            out.println( "    }\n" );

            out.println( "    public " + fullTableName + ".Row[] getRows( Connection con , String whereClause ) throws SQLException" );
            out.println( "    {" );
            out.println( "        getRows_con = con ;" );
            out.println( "        getRows_whereClause = whereClause ;" );
            printMockReturns( fullTableName + ".Row[]" , "getRows_calls" , "getRows_return" );
            out.println( "    }\n" );

            out.println( "    public " + fullTableName + ".Row[] getRows( String whereClause ) throws SQLException" );
            out.println( "    {" );
            out.println( "        getRows_whereClause = whereClause ;" );
            printMockReturns( fullTableName + ".Row[]" , "getRows_calls" , "getRows_return" );
            out.println( "    }\n" );

            out.println( "    public Connection getAllRows_con = null ;" );
            out.println( "    public int getAllRows_calls = 0 ;" );
            out.println( "    public " + fullTableName + ".Row[] getAllRows_return = null ;" );
            out.println( "    public List getAllRows_returns = new ArrayList(); // use this instead of getAllRows_return for multiple calls\n" );

            out.println( "    public " + fullTableName + ".Row[] getAllRows( Connection con ) throws SQLException" );
            out.println( "    {" );
            out.println( "        getAllRows_con = con ;" );
            printMockReturns( fullTableName + ".Row[]" , "getAllRows_calls" , "getAllRows_return" );
            out.println( "    }\n" );

            out.println( "    public " + fullTableName + ".Row[] getAllRows() throws SQLException" );
            out.println( "    {" );
            printMockReturns( fullTableName + ".Row[]" , "getAllRows_calls" , "getAllRows_return" );
            out.println( "    }\n" );

            out.println( "    public Connection search_con = null ;" );
            out.println( "    public String search_column = null ;" );
            out.println( "    public Object search_searchText = null ; // could be a String or String[]" );
            out.println( "    public String[] search_dataColumns = null ;" );
            out.println( "    public String search_whereClause = null ;" );
            out.println( "    public int search_calls = 0 ;" );
            out.println( "    public DBResults search_return = null ;" );
            out.println( "    public List search_returns = new ArrayList(); // use this instead of search_return for multiple calls\n" );

            out.println( "    public DBResults search( Connection con , String column , String searchText , String[] dataColumns ) throws SQLException" );
            out.println( "    {" );
            out.println( "        search_con = con ;" );
            out.println( "        search_column = column ;" );
            out.println( "        search_searchText = searchText ;" );
            out.println( "        search_dataColumns = dataColumns ;" );
            printMockReturns( "DBResults" , "search_calls" , "search_return" );
            out.println( "    }\n" );

            out.println( "    public DBResults search( String column , String searchText , String[] dataColumns ) throws SQLException" );
            out.println( "    {" );
            out.println( "        search_column = column ;" );
            out.println( "        search_searchText = searchText ;" );
            out.println( "        search_dataColumns = dataColumns ;" );
            printMockReturns( "DBResults" , "search_calls" , "search_return" );
            out.println( "    }\n" );

            out.println( "    public DBResults search( Connection con , String column , String[] searchText , String[] dataColumns ) throws SQLException" );
            out.println( "    {" );
            out.println( "        search_con = con ;" );
            out.println( "        search_column = column ;" );
            out.println( "        search_searchText = searchText ;" );
            out.println( "        search_dataColumns = dataColumns ;" );
            printMockReturns( "DBResults" , "search_calls" , "search_return" );
            out.println( "    }\n" );

            out.println( "    public DBResults search( String column , String searchText[] , String[] dataColumns ) throws SQLException" );
            out.println( "    {" );
            out.println( "        search_column = column ;" );
            out.println( "        search_searchText = searchText ;" );
            out.println( "        search_dataColumns = dataColumns ;" );
            printMockReturns( "DBResults" , "search_calls" , "search_return" );
            out.println( "    }\n" );

            out.println( "    public DBResults search( Connection con , String whereClause , String[] dataColumns ) throws SQLException" );
            out.println( "    {" );
            out.println( "        search_con = con ;" );
            out.println( "        search_whereClause = whereClause ;" );
            out.println( "        search_dataColumns = dataColumns ;" );
            printMockReturns( "DBResults" , "search_calls" , "search_return" );
            out.println( "    }\n" );

            out.println( "    public DBResults search( String whereClause , String[] dataColumns ) throws SQLException" );
            out.println( "    {" );
            out.println( "        search_whereClause = whereClause ;" );
            out.println( "        search_dataColumns = dataColumns ;" );
            printMockReturns( "DBResults" , "search_calls" , "search_return" );
            out.println( "    }\n" );

            out.println( "    public DBResults search( Connection con , String[] dataColumns ) throws SQLException" );
            out.println( "    {" );
            out.println( "        search_con = con ;" );
            out.println( "        search_dataColumns = dataColumns ;" );
            printMockReturns( "DBResults" , "search_calls" , "search_return" );
            out.println( "    }\n" );

            out.println( "    public DBResults search( String[] dataColumns ) throws SQLException" );
            out.println( "    {" );
            out.println( "        search_dataColumns = dataColumns ;" );
            printMockReturns( "DBResults" , "search_calls" , "search_return" );
            out.println( "    }\n" );

            out.println( "    public Connection update_con = null ;" );
            out.println( "    public String update_column = null ;" );
            out.println( "    public Object update_searchText = null ; // could be a String or String[]" );
            out.println( "    public Map update_data = null ;" );
            out.println( "    public int update_calls = 0 ;\n" );

            out.println( "    public void update( Connection con , String column , String searchText , Map data ) throws SQLException" );
            out.println( "    {" );
            out.println( "        update_con = con ;" );
            out.println( "        update_column = column ;" );
            out.println( "        update_searchText = searchText ;" );
            out.println( "        update_data = data ;" );
            out.println( "        update_calls++;" );
            out.println( "    }\n" );

            out.println( "    public void update( String column , String searchText , Map data ) throws SQLException" );
            out.println( "    {" );
            out.println( "        update_column = column ;" );
            out.println( "        update_searchText = searchText ;" );
            out.println( "        update_data = data ;" );
            out.println( "        update_calls++;" );
            out.println( "    }\n" );

            out.println( "    public Connection delete_con = null ;" );
            out.println( "    public long delete_" + tableIDAttribute + " = 0 ;" );
            out.println( "    public String delete_column = null ;" );
            out.println( "    public Object delete_searchText = null ; // could be a String or String[]" );
            out.println( "    public int delete_calls = 0 ;\n" );

            if ( tableID != null )
            {
                out.println( "    public void delete( Connection con , long " + tableIDAttribute + " ) throws SQLException" );
                out.println( "    {" );
                out.println( "        delete_con = con ;" );
                out.println( "        delete_" + tableIDAttribute + " = " + tableIDAttribute + " ;" );
                out.println( "        delete_calls++;" );
                out.println( "    }\n" );

                out.println( "    public void delete( long " + tableIDAttribute + " ) throws SQLException" );
                out.println( "    {" );
                out.println( "        delete_" + tableIDAttribute + " = " + tableIDAttribute + " ;" );
                out.println( "        delete_calls++;" );
                out.println( "    }\n" );
            }

            out.println( "    public void delete( Connection con , String column , String searchText ) throws SQLException" );
            out.println( "    {" );
            out.println( "        delete_con = con ;" );
            out.println( "        delete_column = column ;" );
            out.println( "        delete_searchText = searchText ;" );
            out.println( "        delete_calls++;" );
            out.println( "    }\n" );

            out.println( "    public void delete( String column , String searchText ) throws SQLException" );
            out.println( "    {" );
            out.println( "        delete_column = column ;" );
            out.println( "        delete_searchText = searchText ;" );
            out.println( "        delete_calls++;" );
            out.println( "    }\n" );

            if ( tableID == null )
            {
                out.println( "    public Connection insert_con = null ;" );
                out.println( "    public Map insert_data = null ;" );
                out.println( "    public List insert_data_list = new ArrayList();" );
                out.println( "    public int insert_calls = 0 ;\n" );

                out.println( "    public void insert( Connection con , Map data ) throws SQLException" );
                out.println( "    {" );
                out.println( "        insert_con = con ;" );
                out.println( "        insert_data = data ;" );
                out.println( "        insert_data_list.add( data );" );
                out.println( "        insert_calls++;" );
                out.println( "    }\n" );

                out.println( "    public void insert( Map data ) throws SQLException" );
                out.println( "    {" );
                out.println( "        insert_data = data ;" );
                out.println( "        insert_data_list.add( data );" );
                out.println( "        insert_calls++;" );
                out.println( "    }\n" );
            }
            else
            {
                out.println( "    public Connection insertAndGetID_con = null ;" );
                out.println( "    public Map insertAndGetID_data = null ;" );
                out.println( "    public int insertAndGetID_calls = 0 ;" );
                out.println( "    public long insertAndGetID_return = 0 ;\n" );

                out.println( "    public long insertAndGetID( Connection con , Map data ) throws SQLException" );
                out.println( "    {" );
                out.println( "        insertAndGetID_con = con ;" );
                out.println( "        insertAndGetID_data = data ;" );
                out.println( "        insertAndGetID_calls++ ;" );
                out.println( "        return insertAndGetID_return ;" );
                out.println( "    }\n" );

                out.println( "    public long insertAndGetID( Map data ) throws SQLException" );
                out.println( "    {" );
                out.println( "        insertAndGetID_data = data ;" );
                out.println( "        insertAndGetID_calls++ ;" );
                out.println( "        return insertAndGetID_return ;" );
                out.println( "    }\n" );
            }

        }

    }

    private void processTable( DatabaseMetaData metaData , String tableName , String suffix , Properties properties ) throws Exception
    {
        new TableProcessor( metaData , tableName , suffix , properties );
    }

    // exposed for unit testing purposes
    public TableProcessor getTableProcessor( DatabaseMetaData metaData , String tableName , String suffix , Properties properties ) throws Exception
    {
        return new TableProcessor( metaData , tableName , suffix , properties );
    }

    public static void main( String[] args ) throws Exception
    {
        if ( args.length > 0 )
        {
            Properties p = new Properties();
            String propertiesFile = null ;
            List commandLineProperties = new ArrayList();
            for ( int i = 0 ; i < args.length ; i++ )
            {
                String arg = args[ i ];
                if ( arg.indexOf( '=' ) == -1 )
                {
                    propertiesFile = arg ;
                }
                else
                {
                    commandLineProperties.add( arg );
                }
            }
            if ( propertiesFile != null )
            {
                p.load( new FileInputStream( propertiesFile ) );
            }
            for ( int i = 0 ; i < commandLineProperties.size() ; i++ )
            {
                String s = (String)commandLineProperties.get( i );
                int pos = s.indexOf( '=' );
                String key = s.substring( 0 , pos );
                String value = s.substring( pos + 1 );
                p.setProperty( key , value );
            }
            if ( p.size() < 3 )
            {
                throw new Exception( "your properties file seems to be missing some stuff" );
            }
            new Jenny( p );
        }
        else
        {
            System.out.println( "usage: com.javaranch.db.Jenny [individual properties] <propertiesfile>" );
        }
    }

}
