package com.javaranch.common;

import javax.servlet.http.* ;

public class ServletData
{

    private HttpSession session ;
    private HttpServletRequest request ;
    private HttpServletResponse response ;

    /** Exposed for unit testing purposes only! */
    ServletData(){}

    public ServletData( HttpServletRequest request , HttpServletResponse response , HttpSession session )
    {
        this.request = request ;
        this.response = response ;
        this.session = session;
    }

    public ServletData( HttpServletRequest request , HttpServletResponse response )
    {
        this.request = request ;
        this.response = response ;
        this.session = request.getSession(false);
    }

    /** Store an object in the session.
     */
    public void set( String key , Object obj )
    {
        if ( obj == null )
        {
            remove( key );
        }
        else if ( session != null )
        {
            session.setAttribute( key , obj );
        }
    }

    /** Get an object from the session.
     *
     * If the session does not exist or if the key does not exist, null is returned.
     */
    public Object get( String key )
    {
        Object returnVal = null ;
        if ( session != null )
        {
            returnVal = session.getAttribute( key );
        }
        return returnVal ;
    }

    /** Same as get(), only with a "toString()" performed on the returned object.
     */
    public String getString( String key )
    {
        String returnVal = null ;
        Object obj = get( key );
        if ( obj != null )
        {
            returnVal = obj.toString();
        }
        return returnVal ;
    }

    /** Store an integer in the session.
     */
    public void set( String key , int i )
    {
        set( key , new Integer( i ) );
    }

    /** The complementary method to "set(key,int)".
     */
    public int getInt( String key )
    {
        int returnVal = 0 ;
        Integer i = (Integer)get( key );
        if ( i != null )
        {
            returnVal = i.intValue();
        }
        return returnVal ;
    }

    /** Is the key used in the session.
     */
    public boolean exists( String key )
    {
        return ( get( key ) != null );
    }

    /** Remove an object from the session.
     */
    public void remove( String key )
    {
        if ( session != null )
        {
            session.removeAttribute( key );
        }
    }

    /** Get the j2ee session.
     */
    public HttpSession getSession()
    {
        return session ;
    }

    /** Get the j2ee request.
     */
    public HttpServletRequest getRequest()
    {
        return request;
    }

    /** Get the j2ee response.
     */
    public HttpServletResponse getResponse()
    {
        return response;
    }

    /** Same as getRequest.getParameter().
     */
    public String getParameter( String parameterName )
    {
        return request.getParameter( parameterName );
    }

    /** Same as getRequest.getParameterValues().
     */
    public String[] getParameterValues( String parameterName )
    {
        return request.getParameterValues( parameterName );
    }

    /** Same as getRequest.getAttribute().
     */
    public Object getAttribute( String attributeName )
    {
        return request.getAttribute( attributeName );
    }

    /** Store an object in the request.
     */
    public void setAttribute( String key , Object obj )
    {
            request.setAttribute( key , obj );
    }

}
