package com.javaranch.common ;

/** Abstract base class for EntityEJB and SessionEJB. <p>

    This class is intended for use inside an EJB server only! <p>

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
public abstract class EJB
{

    /** Override this method only if you need to. <p>
    */
    public void ejbActivate(){}

    /** Override this method only if you need to. <p>
    */
    public void ejbPassivate(){}

}

