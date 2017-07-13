package com.javaranch.common ;

import javax.ejb.* ;

/** This class provides empty EntityBean methods. <p>


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
public abstract class EntityEJB extends EJB implements EntityBean
{


    protected EntityContext ctx ;

    /** Implemented for you - you can access the context as "ctx". <p>

        If you override this method, you must set ctx appropriately or other methods in this class won't work correctly. <p>
    */
    public void setEntityContext( EntityContext ctx )
    {
        this.ctx = ctx ;
    }

    /** Implemented for you - "ctx" is set to null. <p>
    */
    public void unsetEntityContext()
    {
        ctx = null ;
    }

}
