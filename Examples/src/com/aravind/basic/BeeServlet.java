package com.aravind.basic;

import java.io.* ;
import javax.servlet.http.* ;

public class BeeServlet extends HttpServlet
{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void doGet( HttpServletRequest request , HttpServletResponse response )
    {
        response.setContentType("text/html");
        try
        {
            PrintWriter out = response.getWriter();
            out.println( "a-buzz-buzz ..." );
            out.close();
        }
        catch( Exception e )
        {
            System.out.println( "cannot get writer: " + e );
        }
    }

}

