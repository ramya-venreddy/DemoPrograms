package com.javaranch.common;

import javax.servlet.http.* ;
import java.util.ArrayList;
import java.util.List;

/** Used for unit testing your servlets! */
public class MockServletData extends ServletData
{

    public String set_key = null ;
    public Object set_obj = null ;
    public int set_calls = 0 ;
    public void set( String key , Object obj )
    {
        set_key = key ;
        set_obj = obj ;
        set_calls++;
    }

    public String get_key = null ;
    public int get_calls = 0 ;
    public Object get_return = null ;
    public List get_returns = new ArrayList();
    public Object get( String key )
    {
        get_key = key ;
        Object returnVal = get_return ;
        if ( ( returnVal == null ) && ( get_calls < get_returns.size() ) )
        {
            returnVal = get_returns.get( get_calls );
        }
        get_calls++;
        return returnVal ;
    }

    public String getString_key = null ;
    public int getString_calls = 0 ;
    public String getString_return = null ;
    public List getString_returns = new ArrayList();
    public String getString( String key )
    {
        getString_key = key ;
        String returnVal = getString_return ;
        if ( ( returnVal == null ) && ( getString_calls < getString_returns.size() ) )
        {
            returnVal = (String)getString_returns.get( getString_calls );
        }
        getString_calls++;
        return returnVal ;
    }

    public int set_i = 0 ;
    public void set( String key , int i )
    {
        set_key = key ;
        set_i = i ;
        set_calls++;
    }

    public String getInt_key = null ;
    public int getInt_calls = 0 ;
    public int getInt_return = 0 ;
    public int[] getInt_returns = null ;
    public int getInt( String key )
    {
        getInt_key = key ;
        int returnVal = getInt_return ;
        if ( ( getInt_returns != null ) && ( getInt_calls < getInt_returns.length ) )
        {
            returnVal = getInt_returns[ getInt_calls ];
        }
        getInt_calls++;
        return returnVal ;
    }

    public int exists_calls = 0 ;
    public String exists_key = null ;
    public boolean exists_return = false ;
    public boolean exists( String key )
    {
        exists_key = key ;
        exists_calls++ ;
        return exists_return ;
    }

    public String remove_key = null ;
    public int remove_calls = 0 ;
    public void remove( String key )
    {
        remove_key = key ;
        remove_calls++;
    }

    public int getSession_calls = 0 ;
    public HttpSession getSession_return = null ;
    public HttpSession getSession()
    {
        getSession_calls++;
        return getSession_return ;
    }

    public int getRequest_calls = 0 ;
    public HttpServletRequest getRequest_return = null ;
    public HttpServletRequest getRequest()
    {
        getRequest_calls++;
        return getRequest_return ;
    }

    public int getResponse_calls = 0 ;
    public HttpServletResponse getResponse_return = null ;
    public HttpServletResponse getResponse()
    {
        getResponse_calls++;
        return getResponse_return ;
    }

    public String getParameter_parameterName = null ;
    public int getParameter_calls = 0 ;
    public String getParameter_return = null ;
    public String[] getParameter_returns = null ;
    public String getParameter( String parameterName )
    {
        getParameter_parameterName = parameterName ;
        String returnVal = getParameter_return ;
        if ( ( getParameter_returns != null ) && ( getParameter_calls < getParameter_returns.length ) )
        {
            returnVal = getParameter_returns[ getParameter_calls ];
        }
        getParameter_calls++;
        return returnVal ;
    }

    public String getParameterValues_key = null ;
    public int getParameterValues_calls = 0 ;
    public String[] getParameterValues_return = null ;
    public List getParameterValues_returns = new ArrayList();
    public String[] getParameterValues( String parameterName )
    {
        getParameterValues_key = parameterName ;
        String[] returnVal = getParameterValues_return ;
        if ( ( returnVal == null ) && ( getParameterValues_calls < getParameterValues_returns.size() ) )
        {
            returnVal = (String[])getParameterValues_returns.get( getParameterValues_calls );
        }
        getParameterValues_calls++;
        return returnVal ;
    }

}

