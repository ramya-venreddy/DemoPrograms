package net.viralpatel.struts.helloworld.action;
 
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
 
import net.viralpatel.struts.helloworld.form.LoginForm;
 
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
 
public class LoginAction extends Action {
 
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
     
        String target = null;
        LoginForm loginForm = (LoginForm)form; 
             
        if(loginForm.getUserName().equals("admin")
                && loginForm.getPassword().equals("admin123")) {
            target = "success";
            request.setAttribute("message", loginForm.getPassword());
        }
        else {
            target = "failure";
        }
         
        return mapping.findForward(target);
    }
}