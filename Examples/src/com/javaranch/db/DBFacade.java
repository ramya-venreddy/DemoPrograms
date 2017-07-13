package com.javaranch.db ;

import com.javaranch.common.*;

import java.util.* ;
import java.io.* ;
import java.security.InvalidParameterException;
import java.sql.*;
import javax.naming.* ;
import javax.sql.DataSource;

/** Abstract out all or most of SQL and JDBC to centralize database access, reduce code redundancy and facilitate
 *  unit testing.
 *
 * You will probably want to create only one of these for each database you will access.
 *
 * You will probably use the Jenny generated classes to access your database, but you can use your DBFacade class if
 * you want to do something interesting like a JOIN that is not part of a database VIEW.
 *
 * Although I think that all joins should be done from the database layer and presented as a view, some folks
 * really insist on doing a join from within the java end of things.  So here's an example:
 *
 * <pre>
 *
 *    String fullNameColumn = EmployeeTable.lastNameColumnName + ', ' + EmployeeTable.firstNameColumnName + " as fullName" ;
 *    String[] columns = { EmployeeTable.employeeIDColumnName , fullNameColumn , AddressTable.cityColumnName };
 *    String tableName = "Employee,Address" ;
 *    return dbFacade.search( tableName , "Address.addressID" , "Employee.addressID" , columns );
 *
 * </pre>
 *
 * So, pass in a comma separated list of table names for the tables involved.  Use SQL to define your new, joined
 * columns.  Use the where clause builders or build your own to specify the search parameters.
 *
 * @author Paul Wheaton
 */
public class DBFacade
{

    private boolean initialized = false ;
    private boolean useHighLowPattern = true ;

    // Used for when JNDI was provided in the constructor.  Null if not being used.
    private String jndiLookup = null ;
    private DataSource dataSource = null ;

    // Used for when a Driver and URL was provided in the constructor.  Null if not being used.
    private String databaseDriver = null ;
    private String databaseURL = null ;
    private String databaseName = null ;
    private String databasePassword = null ;

    /** Instantiate a DBFacade object where all database connections will be based on a JNDI lookup.
     *
     * For a variety of architectural reasons, exceptions occurring here will be cached and regurgitated when you try to
     * get a connection.  If you want to test to see if the exception happened here, use the getStartupException() method.
     *
     * @param jndiLookup the JNDI url, i.e. "java:comp/env/jdbc/DataSource"
     */
    public DBFacade( String jndiLookup )
    {
        if ( ! Str.usable( jndiLookup ) )
        {
            throw new InvalidParameterException( "jndiLookup is not allowed to be empty" );
        }
        this.jndiLookup = jndiLookup ;
    }

    /** Instantiate a DBFacade object where all database connections will be based on loading a databaseDriver and accessing
     *  a database URL.
     *
     * For a variety of architectural reasons, exceptions occurring here will be cached and regurgitated when you try to
     * get a connection.  If you want to test to see if the exception happened here, use the getStartupException() method.
     *
     * @param databaseDriver The name of a class that will be loaded. i.e. "com.mysql.jdbc.Driver" or "com.inet.tds.TdsDriver"
     * @param databaseURL Where the database can be found. i.e. "jdbc:databaseDriver://hostname/databasename" or "jdbc:mysql://localhost/soup"
     */
    public DBFacade( String databaseDriver , String databaseURL )
    {
        if ( ! Str.usable( databaseDriver ) )
        {
            throw new InvalidParameterException( "databaseDriver is not allowed to be empty" );
        }
        if ( ! Str.usable( databaseURL ) )
        {
            throw new InvalidParameterException( "databaseURL is not allowed to be empty" );
        }
        this.databaseDriver = databaseDriver ;
        this.databaseURL = databaseURL ;
    }

    /** Instantiate a DBFacade object where all database connections will be based on loading a driver and accessing
     *  a database URL.
     *
     * For a variety of architectural reasons, exceptions occurring here will be cached and regurgitated when you try to
     * get a connection.  If you want to test to see if the exception happened here, use the getStartupException() method.
     *
     * @param driver The name of a class that will be loaded. i.e. "com.mysql.jdbc.Driver" or "com.inet.tds.TdsDriver"
     * @param databaseURL Where the database can be found. i.e. "jdbc:driver://hostname/databasename" or "jdbc:mysql://localhost/soup"
     * @param databaseName The user name that has access to the database.
     * @param databasePassword The password for the user that has access to the database.
     */
    public DBFacade( String driver , String databaseURL , String databaseName , String databasePassword )
    {
        this( driver , databaseURL );
        this.databaseName = databaseName ;
        this.databasePassword = databasePassword ;
    }

    /** Exposed for unit testing purposes only! */
    DBFacade(){}

    private void initialize() throws Exception
    {
        if ( ! initialized )
        {
            if ( jndiLookup != null )
            {
                if ( dataSource == null )
                {
                    Context jndi = new InitialContext();
                    dataSource = (DataSource)jndi.lookup( jndiLookup );
                    if ( dataSource == null )
                    {
                        throw new Exception( "JNDI lookup returned a null DataSource" );
                    }
                }
            }
            else if ( databaseDriver != null )
            {
                Class.forName( databaseDriver ).newInstance();
            }
            else
            {
                throw new Exception( "insufficient information to connect to the database" );
            }
            initialized = true ;
        }
    }

    /** Calling this method will make sure that all inserts with a unique ID will use a database vendor specific way
     *  of generating unique ID's.
     */
    public void doNotUseHighLowPattern()
    {
        useHighLowPattern = false ;
    }

    protected boolean useNoLockHint()
    {
        return false ;
    }

    /** Return a database connection.
     *
     *  Gets a connection from the same place all of the methods here get a connection if
     *  you don't provide one.  Of course, if you open it, you gotta close it!
     */
    public Connection getConnection() throws Exception
    {
        initialize();
        Connection con = null ;
        if ( dataSource == null )
        {
            if ( databaseURL != null )
            {
                if ( databaseName == null )
                {
                    con = DriverManager.getConnection( databaseURL );
                }
                else
                {
                    con = DriverManager.getConnection( databaseURL , databaseName , databasePassword );
                }
            }
        }
        else
        {
            con = dataSource.getConnection();
        }
        return con ;
    }

    /** Establish a connection to the database to see if any exceptions might be generated.
     *
     * Call this method in your startup code to see if everything is configured and working correctly.
     *
     * @throws Exception The types of exceptions thrown here can change depending the type of database access.
     */
    public void testConnection() throws Exception
    {
        Connection con = getConnection();
        if ( con != null )
        {
            con.close();
        }
    }

    /** Sometimes you just gotta do it in SQL.
     *
     * This method will at least handle the statements and result sets for you.
     *
     * @param con A database connection.
     * @param sql A complete SQL statement
     * @return contains zero objects if none are found.
     */
    public DBResults executeQuery( Connection con , String sql ) throws SQLException
    {
        if ( con == null )
        {
            throw new InvalidParameterException( "parameter con is not allowed to be null" );
        }
        if ( ! Str.usable( sql ) )
        {
            throw new InvalidParameterException( "parameter sql must contain usable SQL!" );
        }
        DBResults data = new DBResults();
        Statement statement = con.createStatement();
        try
        {
            ResultSet rs = statement.executeQuery( sql );
            int numberOfColumns = rs.getMetaData().getColumnCount();
            while ( rs.next() )
            {
                String[] s = new String[ numberOfColumns ];
                for ( int i = 0 ; i < s.length ; i++ )
                {
                    s[ i ] = rs.getString( i + 1 );  // todo spatial stuff happens here
                }
                data.add( s );
            }
        }
        catch ( Exception e )
        {
            throw new SQLException( "executeQuery() execute fail [" + sql + "]: " + e );
        }
        finally
        {
            statement.close();
        }
        return data ;
    }

    /** Sometimes you just gotta do it in SQL.
     *
     * This method will at least handle the statements and result sets for you.
     *
     * @param sql A complete SQL statement
     * @return contains zero objects if none are found.
     */
    public DBResults executeQuery( String sql ) throws SQLException
    {
        DBResults data = new DBResults();
        try
        {
            Connection con = getConnection();
            try
            {
                data = executeQuery( con , sql );
            }
            catch ( SQLException e )
            {
                throw e ;  // this is to avoid the exception message below
            }
            catch ( Exception e )
            {
                throw new SQLException( "executeQuery() fail: " + e );
            }
            finally
            {
                con.close();
            }
        }
        catch ( SQLException e )
        {
            throw e ;  // this is to avoid the exception message below
        }
        catch( Exception e )
        {
            throw new SQLException( "executeQuery() connection fail: " + e );
        }
        return data ;
    }


    /** Sometimes you just gotta do it in SQL.
     *
     * This method will at least handle the statements for you.
     *
     * @param con A database connection.
     * @param sql A complete SQL statement
     */
    public void executeUpdate( Connection con , String sql ) throws SQLException
    {
        if ( con == null )
        {
            throw new InvalidParameterException( "parameter con is not allowed to be null" );
        }
        if ( ! Str.usable( sql ) )
        {
            throw new InvalidParameterException( "parameter sql must contain usable SQL!" );
        }
        Statement statement = con.createStatement();
        try
        {
            statement.executeUpdate( sql );
        }
        catch ( Exception e )
        {
            throw new SQLException( "executeQuery() execute fail [" + sql + "]: " + e );
        }
        finally
        {
            statement.close();
        }
    }

    /** Sometimes you just gotta do it in SQL.
     *
     * This method will at least handle the statements for you.
     *
     * @param sql A complete SQL statement
     */
    public void executeUpdate( String sql ) throws SQLException
    {
        try
        {
            Connection con = getConnection();
            try
            {
                executeUpdate( con , sql );
            }
            catch ( SQLException e )
            {
                throw e ;  // this is to avoid the exception message below
            }
            catch ( Exception e )
            {
                throw new SQLException( "executeUpdate() fail: " + e );
            }
            finally
            {
                con.close();
            }
        }
        catch ( SQLException e )
        {
            throw e ;  // this is to avoid the exception message below
        }
        catch( Exception e )
        {
            throw new SQLException( "executeUpdate() connection fail: " + e );
        }
    }

    /** Use the DBQuery object to do a search.
     *
     *   @param con A database connection. <p>
     *   @param q A predescribed query - see DBQuery for details. <p>
     *   @return contains zero objects if none are found.
     */
    public DBResults search( Connection con , DBQuery q ) throws SQLException
    {
        String sql = q.getSQL();
        return executeQuery( con , sql );
    }

    /** Find all the data in a table that matches up to this SQL where clause.
     *
     *  NOTE!  Use this method only if there is no alternative!  The purpose of these classes
     *  is to provide a non-SQL facade to the relational database because some databases
     *  are different than others.  If your whereClause SQL fragment uses any SQL that is
     *  proprietary to the database you are currently using, there is a good chance that it
     *  will not work when the software is ported! <p>
     *
     *  @param whereClause The SQL to use in selecting the rows - do not say "WHERE". <p>
     *  @param dataColumns The column names that have the data you want. <p>
     *  @return contains zero objects if none are found.
     */
    public DBResults search( Connection con , String table , String whereClause , String[] dataColumns ) throws SQLException
    {
        DBQuery q = new DBQuery( this , table );
        q.setWhereClause( whereClause );
        q.setDataColumns( dataColumns );
        return search( con , q );
    }

    /** Find all the data in a table that matches up to this SQL where clause.
     *
     *  NOTE!  Use this method only if there is no alternative!  The purpose of these classes
     *  is to provide a non-SQL facade to the relational database because some databases
     *  are different than others.  If your whereClause SQL fragment uses any SQL that is
     *  proprietary to the database you are currently using, there is a good chance that it
     *  will not work when the software is ported! <p>
     *
     *  @param whereClause The SQL to use in selecting the rows - do not say "WHERE". <p>
     *  @param dataColumns The column names that have the data you want. <p>
     *  @return contains zero objects if none are found.
     */
    public DBResults search( String table , String whereClause , String[] dataColumns ) throws SQLException
    {
        DBResults data = new DBResults();
        try
        {
            Connection con = getConnection();
            try
            {
                data = search( con , table , whereClause , dataColumns );
            }
            catch ( SQLException e )
            {
                throw e ;  // this is to avoid the exception message below
            }
            catch ( Exception e )
            {
                throw new SQLException( "search() create fail: " + e );
            }
            finally
            {
                con.close();
            }
        }
        catch ( SQLException e )
        {
            throw e ;  // this is to avoid the exception message below
        }
        catch( Exception e )
        {
            throw new SQLException( "search() connection fail: " + e );
        }
        return data ;
    }


    /** Find all the data in a table where the text in a column exactly matches a string. <p>
     *
     * @param dataColumns The searchColumn names that have the data you want. <p>
     * @return contains zero objects if none are found.
     */
    public DBResults search( Connection con , String table , String searchColumn , String searchText , String[] dataColumns ) throws SQLException
    {
        DBQuery q = new DBQuery( this , table , searchColumn , searchText , dataColumns );

        return search( con , q );
    }

    /** Find all the data in a table where the text in a column exactly matches one of these strings. <p>
     *
     * @param dataColumns The searchColumn names that have the data you want. <p>
     * @return contains zero objects if none are found.
     */
    public DBResults search( Connection con , String table , String searchColumn , String[] searchText , String[] dataColumns ) throws SQLException
    {
        DBQuery q = new DBQuery( this , table , searchColumn , searchText , dataColumns );

        return search( con , q );
    }

    /** Find all the data in a table where the text in a column exactly matches a string. <p>
     *
     * @param dataColumns The searchColumn names that have the data you want. <p>
     * @return contains zero objects if none are found.
     */
    public DBResults search( Connection con , String table , String searchColumn , int searchValue , String[] dataColumns ) throws SQLException
    {
        DBQuery q = new DBQuery( this , table , searchColumn , searchValue , dataColumns );

        return search( con , q );
    }

    /** Find all the data in a table where the text in a column exactly matches one of these strings. <p>
     *
     * @param dataColumns The searchColumn names that have the data you want. <p>
     * @return contains zero objects if none are found.
     */
    public DBResults search( Connection con , String table , String searchColumn , int[] searchValues , String[] dataColumns ) throws SQLException
    {
        DBQuery q = new DBQuery( this , table , searchColumn , searchValues , dataColumns );

        return search( con , q );
    }

    /** Use the DBQuery object to do a search.
     *
     * @return contains zero objects if none are found.
     */
    public DBResults search( DBQuery q ) throws SQLException
    {
        if ( q == null )
        {
            throw new InvalidParameterException( "parameter q is not allowed to be null" );
        }
        DBResults data = new DBResults();
        try
        {
            Connection con = getConnection();
            try
            {
                data = search( con , q );
            }
            catch ( SQLException e )
            {
                throw e ;  // this is to avoid the exception message below
            }
            catch ( Exception e )
            {
                throw new SQLException( "search() create fail: " + e );
            }
            finally
            {
                con.close();
            }
        }
        catch ( SQLException e )
        {
            throw e ;  // this is to avoid the exception message below
        }
        catch( Exception e )
        {
            throw new SQLException( "search() connection fail: " + e );
        }
        return data ;
    }


    /** Find all the data in a table where the text in a column exactly matches a string.
     *
     * @param dataColumns The searchColumn names that have the data you want. <p>
     * @return contains zero objects if none are found.
     */
    public DBResults search( String table , String searchColumn , String searchText , String[] dataColumns ) throws SQLException
    {
        DBQuery q = new DBQuery( this , table , searchColumn , searchText , dataColumns );
        DBResults r = search( q );
        return r ;

        //return search( new DBQuery( table , searchColumn , searchText , dataColumns ) );
    }

    /** Find all the data in a table where the text in a column exactly matches one of these strings.
     *
     * @param dataColumns The searchColumn names that have the data you want. <p>
     * @return contains zero objects if none are found.
    */
    public DBResults search( String table , String searchColumn , String[] searchText , String[] dataColumns ) throws SQLException
    {
        return search( new DBQuery( this , table , searchColumn , searchText , dataColumns ) );
    }

    /** Find all the data in a table where the text in a column exactly matches a string.
     *
     * @param dataColumns The searchColumn names that have the data you want. <p>
     * @return contains zero objects if none are found.
     */
    public DBResults search( String table , String searchColumn , int searchValue , String[] dataColumns ) throws SQLException
    {
        DBQuery q = new DBQuery( this , table , searchColumn , searchValue , dataColumns );
        return search( q );

        //return search( new DBQuery( table , searchColumn , searchText , dataColumns ) );
    }

    /** Find all the data in a table where the text in a column exactly matches one of these strings.
     *
     * @param dataColumns The searchColumn names that have the data you want. <p>
     * @return contains zero objects if none are found.
    */
    public DBResults search( String table , String searchColumn , int[] searchValues , String[] dataColumns ) throws SQLException
    {
        return search( new DBQuery( this , table , searchColumn , searchValues , dataColumns ) );
    }

    /** Update all rows where the searchText matches the text found in column.
     *
     * @param data A key-value collection of searchColumn names (key) and data (value). If a searchColumn name starts with ">", that searchColumn will be processed as a stream (good for big Strings). <p>
     */
    public void update( String table , String searchColumn , String searchText , Map data ) throws SQLException
    {
        update( table , searchColumn + '=' + normalizeSearchText( searchText ) , data );
    }

    /** Update all rows where the searchText matches the text found in searchColumn.
     *
     * @param data A key-value collection of searchColumn names (key) and data (value). If a searchColumn name starts with ">", that searchColumn will be processed as a stream (good for big Strings). <p>
     */
    public void update( Connection con , String table , String searchColumn , String searchText , Map data ) throws SQLException
    {
        update( con , table , searchColumn + '=' + normalizeSearchText( searchText ) , data );
    }

    /** Update all rows where the text in a searchColumn exactly matches one of these strings. <p>
     *
     * @param data A key-value collection of searchColumn names (key) and data (value). If a searchColumn name starts with ">", that searchColumn will be processed as a stream (good for big Strings). <p>
     */
    public void update( String table , String searchColumn , String[] searchText , Map data ) throws SQLException
    {
        update( table , DBQuery.buildWhereClause( searchColumn , searchText ) , data );
    }

    /** Update all rows where the text in a searchColumn exactly matches one of these strings. <p>
     *
     * @param data A key-value collection of searchColumn names (key) and data (value). If a searchColumn name starts with ">", that searchColumn will be processed as a stream (good for big Strings). <p>
     */
    public void update( Connection con , String table , String searchColumn , String[] searchText , Map data ) throws SQLException
    {
        update( con , table , DBQuery.buildWhereClause( searchColumn , searchText ) , data );
    }

    /** Update all rows matching this where clause.
     *
     *  NOTE!  Use this method only if there is no alternative!  The purpose of these classes
     *  is to provide a non-SQL facade to the relational database because some databases
     *  are different than others.  If your whereClause SQL fragment uses any SQL that is
     *  proprietary to the database you are currently using, there is a good chance that it
     *  will not work when the software is ported! <p>
     *
     * @param whereClause The SQL to use in selecting the rows - do not say "WHERE".
     * @param data A key-value collection of searchColumn names (key) and data (value). If a searchColumn name starts with ">", that searchColumn will be processed as a stream (good for big Strings). <p>
     */
    public void update( String table , String whereClause , Map data ) throws SQLException
    {
        try
        {
            Connection con = getConnection();
            try
            {
                update( con , table , whereClause , data );
            }
            catch ( SQLException e )
            {
                throw e ;  // this is to avoid the exception message below
            }
            catch ( Exception e )
            {
                throw new SQLException( "update() create fail: " + e );
            }
            finally
            {
                con.close();
            }
        }
        catch ( SQLException e )
        {
            throw e ;  // this is to avoid the exception message below
        }
        catch( Exception e )
        {
            throw new SQLException( "update() connection fail: " + e );
        }
    }

    /** Update all rows matching this where clause.
     *
     *  NOTE!  Use this method only if there is no alternative!  The purpose of these classes
     *  is to provide a non-SQL facade to the relational database because some databases
     *  are different than others.  If your whereClause SQL fragment uses any SQL that is
     *  proprietary to the database you are currently using, there is a good chance that it
     *  will not work when the software is ported! <p>
     *
     * @param whereClause The SQL to use in selecting the rows - do not say "WHERE".
     * @param data A key-value collection of searchColumn names (key) and data (value). If a searchColumn name starts with ">", that searchColumn will be processed as a stream (good for big Strings). <p>
     */
    public void update( Connection con , String table , String whereClause , Map data ) throws SQLException
    {
        Str sql = new Str( "UPDATE " + table + " SET " );
        List streamData = new ArrayList();
        Iterator it = data.entrySet().iterator();
        while (it.hasNext())
        {
            Map.Entry entry = (Map.Entry)it.next();
            String dataColumn = (String)entry.getKey();
            Object obj = entry.getValue();
            String dataValue = null ;
            if ( obj != null )
            {
                dataValue = obj.toString();
            }
            if ( dataColumn.charAt( 0 ) == '>' )
            {
                // insert data via a stream
                dataColumn = dataColumn.substring( 1 , dataColumn.length() ); // delete first char
                sql.append( dataColumn );
                sql.append( "=?," );
                streamData.add( dataValue );
            }
            else if ( dataColumn.charAt( 0 ) == '+' )
            {
                // insert data without normalizing it
                dataColumn = dataColumn.substring( 1 , dataColumn.length() ); // delete first char
                sql.append( dataColumn );
                sql.append( "=" );
                sql.append( dataValue );
                sql.append( "," );
            }
            else
            {
                // this data can be placed directly in the SQL
                sql.append( dataColumn );
                sql.append( "=" );
                if ( dataValue == null )
                {
                    sql.append( "NULL" );
                }
                else
                {
                    sql.append( normalizeSearchText( dataValue ) );
                }
                sql.append( "," );
            }
        }

        sql.deleteLast( 1 );

        sql.append( " WHERE " );
        sql.append( whereClause );

        PreparedStatement statement = con.prepareStatement( sql.toString() );

        for( int i = 0 ; i < streamData.size() ; i++ )
        {
            String s = (String)streamData.get( i );
            // todo - see if there is a solution for the deprecated code
            // yes, this is deprecated, but at the time this was put in other solutions did not work
            // because the only drivers for weblogic/oracle combinations would only work this way.
            statement.setAsciiStream( i + 1 , new StringBufferInputStream( s ) , s.length() );
        }
        try
        {
            statement.executeUpdate();
        }
        catch ( Exception e )
        {
            throw new SQLException( "update() execute fail (" + sql + "): " + e );
        }
        finally
        {
            statement.close();
        }
    }

    /** Converts a string to be used for SQL.
     *
     * If the string is null, "NULL" is returned.  Otherwise, single quotes are slapped on both ends of the string.
     *
     * If the string contains any single quotes, they are escaped.
     *
     */
    public static String normalizeSearchText( String searchText )
    {
        String returnVal = "NULL";
        if ( searchText != null )
        {
            Str s = new Str( searchText );
            s.replace( '\'' , "''" );
            returnVal = "'" + s.toString() + "'" ;
        }
        return returnVal ;
    }

    /** Delete all rows matching the search portion of the DBQuery object.
     */
    public void delete( Connection con , DBQuery q ) throws SQLException
    {
        Statement statement = con.createStatement();
        String sql = "DELETE FROM " + q.getTable() + q.getWhereClause() ;
        try
        {
            statement.executeUpdate( sql );
        }
        catch( Exception e )
        {
            throw new SQLException( "delete() execute fail: " + e );
        }
        finally
        {
            statement.close();
        }
    }

    public void delete( DBQuery q ) throws SQLException
    {
        try
        {
            Connection con = getConnection();
            try
            {
                delete( con , q );
            }
            catch ( SQLException e )
            {
                throw e ;  // this is to avoid the exception message below
            }
            catch ( Exception e )
            {
                throw new SQLException( "delete() create fail: " + e );
            }
            finally
            {
                con.close();
            }
        }
        catch ( SQLException e )
        {
            throw e ;  // this is to avoid the exception message below
        }
        catch( Exception e )
        {
            throw new SQLException( "delete() connection fail: " + e );
        }
    }

    /** Delete all rows where the searchText matches the text found in column.
     */
    public void delete( Connection con , String table , String searchColumn , String searchText ) throws SQLException
    {
        delete( con , new DBQuery( this , table , searchColumn , searchText ) );
    }

    /** Delete all rows where the searchText matches the text found in column.
     */
    public void delete( String table , String searchColumn , String searchText ) throws SQLException
    {
        delete( new DBQuery( this , table , searchColumn , searchText ) );
    }

    /** Override this method if you need to provide a database dependent way of providing unique ID's.
     *
     * If you override this method, you would most likely want to override the processPreparedStatementForInsert() also.
     *
     * For Oracle, you would want to add to the sql and return a CallableStatement.
     *
     * @param sql The insert statement that will be used.
     */
    protected PreparedStatement getPreparedStatementForInsert( Connection con , String sql , String idColumnName ) throws Exception
    {
        return con.prepareStatement( sql );

        // for Oracle:
        // return con.prepareCall( "BEGIN " + sql + " returning " + idColumnName + " INTO ?; END;" );
    }

    /** Override this method if you need to provide a database dependent way of providing unique ID's.
     *
     * If you override this method, you would most likely want to override the processPreparedStatementForInsert() also.
     *
     * @param statement The statement object from getPreparedStatementForInsert(), ready to perform executeUpdate();
     * @param streamDataCount The number of stream objects that have been fed into the statement
     * @return the ID
     */
    protected String processPreparedStatementForInsert( PreparedStatement statement , int streamDataCount ) throws Exception
    {
        statement.executeUpdate();
        return null ;

        // for Oracle:
        //      CallableStatement callableStatement = (CallableStatement)statement ;
        //      int outParameterNumber = streamDataCount + 1 ;
        //      callableStatement.registerOutParameter( outParameterNumber , Types.INTEGER );
        //      statement.executeUpdate();
        //      return callableStatement.getString( outParameterNumber );
    }

    /** For internal use only. */
    protected String insertAndPossiblyGetNewID( Connection con , String table , Map data , String idColumnName ) throws SQLException
    {
        // If idColumnName is null, a preprared statement is used and a simple insert is performed.
        // Otherwise, the getPreparedStatementForInsert() and processPreparedStatementForInsert() methods are used
        // to do a database specific way of doing an insert and get the new ID back.
        String returnVal = null ;
        try
        {
            Str columns = new Str( 200 );
            Str values = new Str( 200 );
            List streamData = new ArrayList();
            Iterator it = data.entrySet().iterator();
            while (it.hasNext())
            {
                Map.Entry entry = (Map.Entry)it.next();
                String dataColumn = (String)entry.getKey();
                Object obj = entry.getValue();
                String dataValue = null;
                if ( obj != null )
                {
                    dataValue = obj.toString();
                }
                if ( dataColumn.charAt( 0 ) == '>' )
                {
                    // we need to mash this data in via a stream
                    dataColumn = dataColumn.substring( 1 , dataColumn.length() );
                    values.append( "?," );
                    streamData.add( dataValue );
                }
                else if ( dataColumn.charAt(0) == '+' )
                {
                    dataColumn = dataColumn.substring( 1 , dataColumn.length() );
                    values.append( dataValue );
                    values.append( "," );
                }
                else
                {
                    // this data can be placed directly in the SQL
                    if ( dataValue == null )
                    {
                        values.append( "NULL" );
                    }
                    else
                    {
                        values.append( normalizeSearchText( dataValue ) );
                    }
                    values.append( "," );
                }
                columns.append( dataColumn );
                columns.append( ',' );
            }

            columns.deleteLast( 1 );
            values.deleteLast( 1 );

            String sql = "INSERT INTO " + table + " (" + columns + ") VALUES (" + values + ")" ;
            boolean processReturnClause = Str.usable( idColumnName );
            PreparedStatement statement ;
            if ( processReturnClause )
            {
                statement = getPreparedStatementForInsert( con , sql , idColumnName );
            }
            else
            {
                statement = con.prepareStatement( sql );
            }
            for( int i = 0 ; i < streamData.size() ; i++ )
            {
                String s = (String)streamData.get( i );
                // todo - can we get away with something other than deprecated code now?
                // yes, this is deprecated, but at the time this was put in other solutions did not work
                // because the only drivers for weblogic/oracle combinations would only work this way.
                statement.setAsciiStream( i + 1 , new StringBufferInputStream( s ) , s.length() );
            }
            try
            {
                if ( processReturnClause )
                {
                    returnVal = processPreparedStatementForInsert( statement , streamData.size() );
                }
                else
                {
                    statement.executeUpdate();
                }
            }
            catch ( Exception e )
            {
                System.out.println( "\nSQL=" + sql );
                e.printStackTrace();
                throw new SQLException( "insert() insert fail: " + e );
            }
            finally
            {
                statement.close();
            }
        }
        catch ( SQLException e )
        {
            throw e ;  // this is to avoid the exception message below
        }
        catch ( Exception e )
        {
            e.printStackTrace();
            throw new SQLException( "insert() create fail: " + e );
        }
        return returnVal ;
    }

    private String insertAndPossiblyGetNewID( String table , Map data , String idColumnName ) throws SQLException
    {
        String returnVal = null ;
        try
        {
            Connection con = getConnection();
            try
            {
                returnVal = insertAndPossiblyGetNewID( con , table , data , idColumnName );
            }
            finally
            {
                con.close();
            }
        }
        catch ( SQLException e )
        {
            throw e ;  // this is to avoid the exception message below
        }
        catch( Exception e )
        {
            throw new SQLException( "insert() connection fail: " + e );
        }
        return returnVal ;
    }

    /** Create a new row in the table and populate the fields with the provided data.
     *
     * @param data A key-value collection of searchColumn names (key) and data (value). If a searchColumn name starts with ">", that searchColumn will be processed as a stream (good for big Strings). <p>
     */
    public void insert( Connection con , String table , Map data ) throws SQLException
    {
        insertAndPossiblyGetNewID( con , table , data , null );
    }

    /** Create a new row in the table and populate the fields with the provided data.
     *
     * @param data A key-value collection of searchColumn names (key) and data (value). If a searchColumn name starts with ">", that searchColumn will be processed as a stream (good for big Strings). <p>
     */
    public void insert( String table , Map data ) throws SQLException
    {
        insertAndPossiblyGetNewID( table , data , null );
    }



    ///////////////////////////////////////////////////////////////////////////////
    //
    // Unique ID stuff
    //
    // Yes, this violates two elements of "bean law" (if you are using this within an EJB environment):
    //
    //     1) all static variables must be final.
    //
    //            Technically, I am following the law.  Of course, the intent is that I won't change
    //            anything because I might not be aware that my application could be running
    //            in more than one VM.  I am aware of the other VM's - the database transaction stuff
    //            will protect me in this respect.
    //
    //     2) No synchronization within beans.
    //
    //            This is because the EJB server will handle all synchronization for me.  I'm not sure
    //            what will happen if I use synchronization, but I need it here, so I'm running the risk.
    //
    //
    //

    private class Counter
    {

        private String tableName ;
        private long skip ;
        private long high ;
        private long low ;

        Counter( String tableName ) throws SQLException
        {
            this.tableName = tableName ;
            getHigh();
            low = 0 ;
        }

        private final String[] columns = { "counter" , "skip" };

        private void getHigh() throws SQLException
        {
            try
            {
                Connection con = getConnection();
                try
                {
                    // read current counter

                    // todo - test for no table.  If no table, create table
                    // CREATE TABLE IF NOT EXISTS tbl_name [(create_definition,...)] [table_options]
                    // An error occurs if there is no current database or if the table already exists.

                    // todo - test for no row.  If no row, create row
                    // SELECT COUNT(*) FROM tbl_name;
                    // if COUNT == 0; INSERT ...

                    String[] rowData = search( con , "tableIDCounters" , "tableName" , tableName , columns ).getRow(0);
                    long tempHigh = Long.parseLong( rowData[0] );
                    tempHigh++ ;
                    long tempSkip = Long.parseLong( rowData[1] );

                    // write updated counter
                    Map map = new HashMap();
                    map.put( "counter" , String.valueOf( tempHigh ) );
                    update( con , "tableIDCounters" , "tableName" , tableName , map );

                    // if we reached this point, there were no exceptions

                    high = tempHigh * tempSkip ;
                    skip = tempSkip ;
                }
                catch ( Exception e )
                {
                    throw new SQLException( "unique identifier algorithm could not retrieve/update counter: " + e );
                }
                finally
                {
                    con.close();
                }
            }
            catch ( SQLException e )
            {
                throw e ;  // this is to avoid the exception message below
            }
            catch( Exception e )
            {
                throw new SQLException( "unique identifier algorithm connection fail: " + e );
            }
        }

        synchronized long getNextID() throws SQLException
        {
            if ( low >= skip )
            {
                getHigh();
                low = 0 ;
            }
            long returnVal = high + low ;
            low++ ;
            return returnVal ;
        }

    }


    private final Map counters = new HashMap();

    private final Object mutex = new Object();

    private long getNextID( String tableName ) throws SQLException
    {
        Counter c = null;
        c = (Counter) counters.get( tableName );
        if ( c == null )
        {
            // this block of code needs to be synchronized so that I don't accidentally try
            // to create two of the same thing at the same time.
            synchronized ( mutex )
            {
                if ( c == null )
                {
                    c = new Counter( tableName );
                    counters.put( tableName , c );
                }
            }
        }
        return c.getNextID();
    }

    //
    //
    //
    // End of unique ID stuff.
    //
    ///////////////////////////////////////////////////////////////////////////////



    /** Create a new row with a unique ID. <p>

        Requires that a table exists called "TableIDCounters" complete with a row representing
        the table you specified in getPKTable(). "TableIDCounters" must have three columns: "tableName", "counter" and "skip".
        The name in the "tableName" row must be an exact match to the table string you provide as the first parameter.
        Make sure that before you call this method for the first time, that you initialize the "counter" field for
        the respective row to be zero.  "skip" represents how often the counter should go to the database for a new
        "high" value (more on the high/low algorithm below).  If you're not sure what skip value to use, use 100.
        Higher numbers will give higher performance, but will use up your numbers faster. <p>

        This method uses a high/low algorithm to insure unique numbers despite the complexity of a distributed system.
        With a skip value of 100, the algorithm will retrieve the high value from the database.  If the high value
        is, say, 22, the number 2200 will be returned.  On the next request, 2201 will be returned without a database call.
        Subsequent calls will return 2202 through 2209.  The next request will force another database call which may return
        27 (other servers in the server group may also be using this algorithm) - so the value 2700 is returned. <p>

        The ID column name is assumed to be "ID".

        @param data A key-value collection of column names (key) and data (value). If a column name starts with ">",
                    that column will be processed as a stream (good for big Strings). <p>

        @return The new ID. <p>
    */
    public long insertAndGetID( String table , Map data ) throws SQLException
    {
        return insertAndGetID( table, data , "ID" );
    }

    public long insertAndGetID( Connection con , String table , Map data ) throws SQLException
    {
        return insertAndGetID( con , table, data , "ID" );
    }

    public long insertAndGetID( String table , Map data , String idColumnName ) throws SQLException
    {
        long id = 0 ;
        if ( useHighLowPattern )
        {
            id = getNextID( table );
            data.put( idColumnName , String.valueOf( id ) );
            insert( table , data );
        }
        else
        {
            data.remove( idColumnName );
            data.put( '+' + idColumnName , table + "_seq.nextval" );
            String s = insertAndPossiblyGetNewID( table , data , idColumnName );
            id = Str.toLong( s );
        }
        return id ;
    }

    public long insertAndGetID( Connection con , String table , Map data , String idColumnName ) throws SQLException
    {
        long id = 0 ;
        if ( useHighLowPattern )
        {
            id = getNextID( table );
            data.put( idColumnName , String.valueOf( id ) );
            insert( con , table , data );
        }
        else
        {
            data.remove( idColumnName );
            data.put( '+' + idColumnName , table + "_seq.nextval" );
            String s = insertAndPossiblyGetNewID( con , table , data , idColumnName );
            id = Str.toLong( s );
        }
        return id ;
    }


}

