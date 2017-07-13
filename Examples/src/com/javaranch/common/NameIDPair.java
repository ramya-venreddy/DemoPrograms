package com.javaranch.common ;

// todo - write javadoc
public class NameIDPair implements java.io.Serializable
{

    private int id ;
    private String name ;

    public NameIDPair( String name , int id )
    {
        this.name = name ;
        this.id = id ;
    }

    public String getName()
    {
        return name ;
    }

    public int getID()
    {
        return id ;
    }

    /** Get just the ID's from an array of NameIDPair objects.
     */
    public static int[] toIntArray( NameIDPair[] pairs )
    {
        int[] a = new int[ pairs.length ];
        for ( int i = 0 ; i < a.length ; i++ )
        {
            a[ i ] = pairs[ i ].id ;
        }
        return a ;
    }

    public static KeyValuePair[] toKeyValuePairArray( NameIDPair[] nip )
    {
        KeyValuePair[] kvp = new KeyValuePair[ nip.length ];
        for( int i = 0 ; i < nip.length ; i++ )
        {
            NameIDPair pair = nip[ i ];
            kvp[ i ] = new KeyValuePair( String.valueOf( pair.id ) , pair.name );
        }
        return kvp ;
    }

}
