package com.javaranch.common ;

import java.util.* ;
import javax.naming.*;
import javax.rmi.* ;

/** An object the represents a client side access to a server. <p>

    - - - - - - - - - - - - - - - - - <p>

    Copyright (c) 1998-2004 Paul Wheaton <p>

    You are welcome to do whatever you want to with this source file provided
    that you maintain this comment fragment (between the dashed lines).
    Modify it, change the package name, change the class name ... personal or business
    use ...  sell it, share it ... add a copyright for the portions you add ... <p>

    My goal in giving this away and maintaining the copyright is to hopefully direct
    developers back to JavaRanch. <p>

    The original source can be found at <a href=http://www.javaranch.com>JavaRanch</a> <p>

    - - - - - - - - - - - - - - - - - <p>

    @author Paul Wheaton
*/

public class EJBClient
{

    private String initial = null ;
    private String url = null ;
    private boolean narrow = false ;
    private boolean formalLookup = false ;

    private Context context = null ;

    /** Works for most needs
     */
    public EJBClient()
    {
    }

    /**
     * @param initial used to create a new initial context under "java.naming.factory.initial".  example: "allaire.ejipt.ContextFactory"
     * @param url used to create a new initial context under "java.naming.provider.url". example: "ejipt://127.0.0.1:2323"
     * @param narrow need to do narrowing?
     * @param formalLookup prepend "java:comp/env/" on the home name?
     */
    public EJBClient( String initial , String url , boolean narrow , boolean formalLookup )
    {
        this.initial = initial ;
        this.url = url ;
        this.narrow = narrow ;
        this.formalLookup = formalLookup ;
    }

    /** One context is kept on hand to serve up homes for many bean requests. <p>
    */
    public Object getHome( String homeName , Class homeClass ) throws NamingException
    {
        if ( context == null )
        {
            if ( url == null )
            {
                context = new InitialContext();
            }
            else
            {
                Hashtable ejbProperties = new Hashtable();
                ejbProperties.put( "java.naming.factory.initial" , initial );
                ejbProperties.put( "java.naming.provider.url" , url );
                context = new InitialContext( ejbProperties );
            }
        }

        if ( formalLookup )
        {
            homeName = "java:comp/env/" + homeName ;
        }

        Object obj = context.lookup( homeName );

        if ( narrow )
        {
            obj = PortableRemoteObject.narrow( obj , homeClass );
        }

        return obj ;
    }

}
