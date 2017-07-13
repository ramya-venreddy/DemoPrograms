package com.javaranch.db ;

import java.sql.DatabaseMetaData;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import junit.framework.TestCase ;
import com.javaranch.common.Str;

public class TestTableProcessor extends TestCase
{

    private static Properties p ;
    private Connection con ;

    public TestTableProcessor( String name )
    {
        super( name );
    }

    static
    {
        p = new Properties();
        p.setProperty( "Driver" , "com.mysql.jdbc.Driver" );
        p.setProperty( "URL" , "jdbc:mysql://localhost/soup" );
        p.setProperty( "UserName" , "root" );
        //p.setProperty( "UserName" , "" );
        p.setProperty( "Password" , "" );
        p.setProperty( "Package" , "com.javaranch.db.soup" );
        p.setProperty( "Destination" , "/tinkering/src/com/javaranch/db/soup/" );
        p.setProperty( "Facade" , "SoupFacade" );
    }

    static
    {
        try
        {
            String driver = p.getProperty( "Driver" );
            Class.forName( driver ).newInstance();
        }
        catch( Exception e )
        {
            System.out.println( "could not load JDBC driver: " + e );
        }
    }

    public void testGetTableProcessor() throws Exception
    {
        DatabaseMetaData metaData = con.getMetaData();
        ResultSet results = metaData.getTables( null , null , "%" , new String[]{"TABLE"} );
        String tableName = "" ;
        while ( results.next() )
        {
            tableName = results.getString( "TABLE_NAME" );
        }
        String suffix = "" ;
        String idColumnName = p.getProperty( tableName + ".ID" );
        System.out.println( "tableName: " +  tableName );
        System.out.println( "suffix: " +  suffix );
        System.out.println( "idColumnName: " +  idColumnName );

        Jenny j = new Jenny( p );
        Jenny.TableProcessor tp = j.getTableProcessor( metaData , tableName , suffix , p );
        assertEquals( tp.rawTableName , tableName );
    }

    public void test_validAttributeData()
    {

    }

    public void setUp()
    {
        String url = p.getProperty( "URL" );
        String userName = p.getProperty( "UserName" );
        String password = p.getProperty( "Password" );
        try
        {
            if ( Str.usable( userName ))
            {
                con = DriverManager.getConnection( url , userName , password );
            }
            else
            {
                con = DriverManager.getConnection( url );
            }

            assertNotNull ( "null connection", con );
        }
        catch ( SQLException e )
        {
            //System.out.println( "could not get database connection" + e );
            fail("could not get database connection" + e);
        }
    }

    public void tearDown()
    {
        if ( con != null )
        {
            try
            {
                con.close();
            }
            catch ( Exception e )
            {
                System.out.println( "could not close connection: " + e  );
            }
        }

    }

}
