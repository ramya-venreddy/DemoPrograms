package com.javaranch.db ;

//import java.util.Properties;

import junit.framework.TestCase ;

public class TestJenny extends TestCase
{

    /*
    private static Properties p ;

    public TestJenny( String name )
    {
        super( name );
    }

    static
    {
        p = new Properties();
        p.setProperty( "Driver" , "com.mysql.jdbc.Driver" );
        p.setProperty( "URL" , "jdbc:mysql://localhost/soup" );
        p.setProperty( "UserName" , "" );
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
    */

    public void testAttributeName() throws Exception
    {
        assertEquals( "video" , Jenny.attributeName( "VIDEO" ).minor );
        assertEquals( "Video" , Jenny.attributeName( "VIDEO" ).major );
        assertEquals( "videoStore" , Jenny.attributeName( "VIDEO_STORE" ).minor );
        assertEquals( "VideoStore" , Jenny.attributeName( "VIDEO_STORE" ).major );
        assertEquals( "videoStoreDay" , Jenny.attributeName( "VIDEO_STORE_DAY" ).minor );
        assertEquals( "VideoStoreDay" , Jenny.attributeName( "VIDEO_STORE_DAY" ).major );
        assertEquals( "videoStore" , Jenny.attributeName( "videoStore" ).minor );
        assertEquals( "VideoStore" , Jenny.attributeName( "videoStore" ).major );
        assertEquals( "videoStore" , Jenny.attributeName( "VideoStore" ).minor );
        assertEquals( "VideoStore" , Jenny.attributeName( "VideoStore" ).major );
        assertEquals( "videoStoreDay" , Jenny.attributeName( "VideoStoreDay" ).minor );
        assertEquals( "VideoStoreDay" , Jenny.attributeName( "VideoStoreDay" ).major );
        assertEquals( "address1" , Jenny.attributeName( "address1" ).minor );
        assertEquals( "Address1" , Jenny.attributeName( "address1" ).major );
        assertEquals( "address1" , Jenny.attributeName( "Address1" ).minor );
        assertEquals( "Address1" , Jenny.attributeName( "Address1" ).major );
        assertEquals( "address1" , Jenny.attributeName( "ADDRESS_1" ).minor );
        assertEquals( "Address1" , Jenny.attributeName( "ADDRESS_1" ).major );
        assertEquals( "videoID" , Jenny.attributeName( "VideoID" ).minor );
        assertEquals( "VideoID" , Jenny.attributeName( "VideoID" ).major );
        assertEquals( "videoID" , Jenny.attributeName( "VIDEO_ID" ).minor );
        assertEquals( "VideoID" , Jenny.attributeName( "VIDEO_ID" ).major );
        assertEquals( "id" , Jenny.attributeName( "ID" ).minor );
        assertEquals( "ID" , Jenny.attributeName( "ID" ).major );
        assertEquals( "id" , Jenny.attributeName( "id" ).minor );
        assertEquals( "ID" , Jenny.attributeName( "id" ).major );
        assertEquals( "videoAd" , Jenny.attributeName( "VIDEO_AD" ).minor );
        assertEquals( "VideoAd" , Jenny.attributeName( "VIDEO_AD" ).major );
        assertEquals( "videoIdea" , Jenny.attributeName( "VIDEO_IDEA" ).minor );
        assertEquals( "VideoIdea" , Jenny.attributeName( "VIDEO_IDEA" ).major );
        assertEquals( "stupid" , Jenny.attributeName( "STUPID" ).minor ); // trailing "ID"
        assertEquals( "Stupid" , Jenny.attributeName( "STUPID" ).major ); // trailing "ID"
        assertEquals( "hmsBounty" , Jenny.attributeName( "HMSBounty" ).minor );
        assertEquals( "HMSBounty" , Jenny.attributeName( "HMSBounty" ).major );
        assertEquals( "aBillion" , Jenny.attributeName( "aBillion" ).minor );
        assertEquals( "ABillion" , Jenny.attributeName( "aBillion" ).major );
        assertEquals( "aBillion" , Jenny.attributeName( "ABillion" ).minor );
        assertEquals( "ABillion" , Jenny.attributeName( "ABillion" ).major );
        assertEquals( "a" , Jenny.attributeName( "A" ).minor );
        assertEquals( "A" , Jenny.attributeName( "A" ).major );
        assertEquals( "a" , Jenny.attributeName( "a" ).minor );
        assertEquals( "A" , Jenny.attributeName( "a" ).major );
    }

}
