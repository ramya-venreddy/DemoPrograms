package com.javaranch.common;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class ActionServlet extends LogServlet
{

    private Map actionMap = new HashMap();
    private ActionHandler defaultActionHandler = null;

    public interface ActionHandler
    {
        public void doAction( ServletData servletData ) throws Exception;
    }

    public abstract void init() throws ServletException;

    /**
     * Override init and call this method for every action to be implemented.
     *
     * @param actionKey The text that will be sent in the servlet parameter to kick off this action.
     */
    public void addActionHandler( String actionKey , ActionHandler actionHandler )
    {
        if ( defaultActionHandler == null )
        {
            defaultActionHandler = actionHandler;
        }
        actionMap.put( actionKey , actionHandler );
    }

    private void processAction( ServletData servletData ) throws Exception
    {
        String parameter = servletData.getParameter( "action" );
        ActionHandler action = (ActionHandler)actionMap.get( parameter );
        if ( action == null )
        {
            if ( defaultActionHandler == null )
            {
                System.out.println( "no action defined in actionMap" );
            }
            else
            {
                defaultActionHandler.doAction( servletData );
            }
        }
        else
        {
            action.doAction( servletData );
        }
    }

    /**
     * Load a servlet, or web page, or JSP, or whatever.
     */
    public void forward( String url , ServletData servletData )
    {
        String message = "";
        try
        {
            RequestDispatcher dispatcher = getServletContext().getRequestDispatcher( url );
            if ( dispatcher == null )
            {
                dispatcher = servletData.getRequest().getRequestDispatcher( url );
                if ( dispatcher == null )
                {
                    throw new Exception( "dispatcher is null. " +
                            "The most frequent cause of this problem is a bad url" );
                }
            }
            dispatcher.forward( servletData.getRequest() , servletData.getResponse() );
        }
        catch ( Exception e )
        {
            message = "problem loading URL '" + url + "':" + e;
            System.out.println( message );
            throw new SkipException( message );
        }
    }

    /**
     * Load a servlet, or web page, or JSP, or whatever.
     */
    public static void redirect( String url , ServletData servletData )
    {
        String message = "";
        try
        {
            HttpServletResponse response = servletData.getResponse();
            if ( response == null )
            {
                throw new Exception( "response is null. " +
                        "The most frequent cause of this problem is a bad url" );
            }
            response.sendRedirect( url );
        }
        catch ( Exception e )
        {
            message = "problem loading URL '" + url + "':" + e;
            System.out.println( message );
            throw new SkipException( message );
        }
    }

    public final void doPost( HttpServletRequest request , HttpServletResponse response )
    {
        trackDoPost();
        ServletData servletData = new ServletData( request , response );
        try
        {
            processAction( servletData );
        }
        catch ( SkipException e )
        {
        }
        catch ( Exception e )
        {
            String s = e.toString();
            if ( ( s != null ) && ( !s.startsWith( "redirect - " ) ) && ( !s.startsWith( "forward - " ) ) )
            {
                System.out.println( "exception in doPost(): " + s );
                e.printStackTrace( );
            }
        }
    }

    private static class SkipException extends RuntimeException
    {
        SkipException()
        {
            super( "skipping rest of code" );
        }

        SkipException( String message )
        {
            super( "skipping rest of code" + message );
        }
    }

    public final void doGet( HttpServletRequest request , HttpServletResponse response )
    {
        trackDoGet();
        if ( !showStatus( request , response ) )
        {
            ServletData servletData = new ServletData( request , response );
            try
            {
                processAction( servletData );
            }
            catch ( SkipException e )
            {
            }
            catch ( Exception e )
            {
                System.out.println( "exception in doGet():" + e );
            }
        }
    }

}
