package com.javaranch.common ;

import javax.ejb.* ;

/** Inheriting this abstract class provides empty SessionBean methods.

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
public abstract class SessionEJB extends EJB implements SessionBean
{

    protected SessionContext ctx ;

    /** The context is grabbed and stored in ctx for any of your future context needs. <p>

        Should you choose to override this method, please call super.setEntityContext(). <p>
    */
    public void setSessionContext( SessionContext ctx )
    {
        this.ctx = ctx ;
    }

    /** Override this only if you have to. <p>
    */
    public void ejbCreate() throws CreateException
    {
    }

    /** Override this only if you have to. <p>
    */
    public void ejbRemove()
    {
    }

}
