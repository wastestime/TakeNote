package com.team6.jsfclasses;

import com.team6.entityclasses.Activity;
import com.team6.entityclasses.ContactConnections;
import com.team6.entityclasses.User;
import com.team6.jsfclasses.util.JsfUtil;
import com.team6.jsfclasses.util.JsfUtil.PersistAction;
import com.team6.sessionbeans.ActivityFacade;
import com.team6.sessionbeans.ContactConnectionsFacade;
import com.team6.sessionbeans.UserFacade;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import org.primefaces.model.timeline.TimelineEvent;
import org.primefaces.model.timeline.TimelineModel;

@Named("userController")
@SessionScoped
public class UserController implements Serializable {

    @EJB
    private UserFacade userFacade;

    public ContactConnectionsFacade getContactConnectionsFacade() {
        return contactConnectionsFacade;
    }
    @EJB
    private ContactConnectionsFacade contactConnectionsFacade;
    @EJB
    private ActivityFacade activityFacade;

  
    
    @EJB
    private com.team6.sessionbeans.UserFacade ejbFacade;
    private List<User> items = null;
    private User selected;
    private String searchQuery;
    private String statusMessage;
    //private String activityTitle;

    
    private List<User> friends; // DONT FORGET TO ADD THSI INTO DATABASE
    private List<Activity> activities;

    public UserController() {
        this.searchQuery=null;
        //this.activityTitle = null;
        this.friends = new ArrayList();
        this.activities = new ArrayList();
        // READ FROM DATABASE

    }

    public UserFacade getEjbFacade() {
        return ejbFacade;
    }

    public User getSelected() {
        return selected;
    }

    public void setSelected(User selected) {
        this.selected = selected;
    }

    public UserFacade getUserFacade() {
        return this.userFacade;
    }

    public ActivityFacade getActivityFacade() {
        return activityFacade;
    }

    public void setActivityFacade(ActivityFacade activityFacade) {
        this.activityFacade = activityFacade;
    }
    /*public String getActivityTitle() {
        return activityTitle;
    }

    public void setActivityTitle(String activityTitle) {
        this.activityTitle = activityTitle;
    }
    */
    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private UserFacade getFacade() {
        return ejbFacade;
    }

    public User prepareCreate() {
        selected = new User();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("UserCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("UserUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("UserDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<User> getItems() {
        if (items == null) {
            items = getFacade().findAll();
        }
        return items;
    }

    private void persist(PersistAction persistAction, String successMessage) {
        if (selected != null) {
            setEmbeddableKeys();
            try {
                if (persistAction != PersistAction.DELETE) {
                    getFacade().edit(selected);
                } else {
                    getFacade().remove(selected);
                }
                JsfUtil.addSuccessMessage(successMessage);
            } catch (EJBException ex) {
                String msg = "";
                Throwable cause = ex.getCause();
                if (cause != null) {
                    msg = cause.getLocalizedMessage();
                }
                if (msg.length() > 0) {
                    JsfUtil.addErrorMessage(msg);
                } else {
                    JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
                }
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            }
        }
    }

    public User getUser(java.lang.Integer id) {
        return getFacade().find(id);
    }

    public List<User> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<User> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = User.class)
    public static class UserControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            UserController controller = (UserController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "userController");
            return controller.getUser(getKey(value));
        }

        java.lang.Integer getKey(String value) {
            java.lang.Integer key;
            key = Integer.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Integer value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof User) {
                User o = (User) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), User.class.getName()});
                return null;
            }
        }

    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    public String getSearchQuery() {
        return this.searchQuery;
    }
    
    public boolean contains(User user) {
        String user_name = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("username");
        User currUser = getUserFacade().findByUsername(user_name);
        List<ContactConnections> contacts = getContactConnectionsFacade().findUserContacts(currUser.getId());
        boolean flag = false;
        System.out.printf("!!!!!! user id to add = %d\n", user.getId());
        for (ContactConnections c : contacts) {
            System.out.printf("********* user_id = %d, contact = %d\n", c.getUserId().getId(), c.getContactUid().getId());
            if (c.getContactUid().getId().equals(user.getId())) {
                flag = true;
                System.out.printf("==duplicate addding is not allowed!\n");
                break;
            }
        }
        return flag;
    }

    public void addFriend() {
        if (this.searchQuery != null) {
            //System.out.printf("=======%s  ========\n", this.searchQuery);
            User user = getUserFacade().findByUsername(this.searchQuery);
            if (user == null) {
                statusMessage = "Invalid username as input!";
                FacesMessage message = new FacesMessage(statusMessage);
                FacesContext.getCurrentInstance().addMessage(null, message);
                return;
            }
            if (!contains(user)) {
                
                String user_name = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("username");
                User currUser = getUserFacade().findByUsername(user_name);
                if (user.getId().equals(currUser.getId())) {
                    statusMessage = "You can't as yourself as friend!";
                    FacesMessage message = new FacesMessage(statusMessage);
                    FacesContext.getCurrentInstance().addMessage(null, message);
                    return;
                }
                //this.friends.add(user);
                System.out.printf("+++++the selected user is : %s...", currUser.getUsername());
                ContactConnections cc = new ContactConnections(currUser, user);
                getContactConnectionsFacade().create(cc);
                System.out.printf("After add size =%d  ========\n", this.friends.size());
                // by setting friends to null, we foce the get friends method 
                // above to retrieve all the users' contacts again
                //this.friends = null;
                addActivity("Add Friend");

                statusMessage = "You added a new friend!";
                FacesMessage message = new FacesMessage(statusMessage);
                FacesContext.getCurrentInstance().addMessage(null, message);
            } else {
                statusMessage = "You were already friends!";
                FacesMessage message = new FacesMessage(statusMessage);
                FacesContext.getCurrentInstance().addMessage(null, message);
            }
            searchQuery=null;

        }
    }
    public void removeConnection() {
        String user_name = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("username");
        User currUser = getUserFacade().findByUsername(user_name);
        List<ContactConnections> contacts = getContactConnectionsFacade().findUserContacts(currUser.getId());
        for (ContactConnections c : contacts) {
            if (c.getContactUid().getId().equals(this.selected.getId())) {
                getContactConnectionsFacade().remove(c);
                addActivity("Remove Friend");
                break;
            }
        }
    }
    
    public void refreshList() {
        this.friends.clear();
    }
    public List<User> fetchContacts() {
        if (this.friends.isEmpty()) {
            //read from data base
            String user_name = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("username");
            User currUser = getUserFacade().findByUsername(user_name);
            List<ContactConnections> contacts = getContactConnectionsFacade().findUserContacts(currUser.getId());
            
            contacts.forEach((c) -> {
                this.friends.add(getUserFacade().findByUsername(c.getContactUid().getUsername()));
            });
        }
        return this.friends;
    }
    
    public void addActivity(String title) {
        // came in should be "edit" "create" "delete"
        Activity a = new Activity();
        System.out.println("wwwwwwwww in =========addddActivity");
        // prepare the activity
        a.setTitle(title);
        
        String user_name = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("username");
        User currUser = getUserFacade().findByUsername(user_name);
        a.setUserId(currUser);
        
        a.setTimeCreated(new Date());
        
        if(title.toLowerCase().contains("edit")) {
            a.setImagePath("/resources/images/activityImages/edit.png");
            getActivityFacade().create(a);
        }
        else if (title.toLowerCase().contains("create account")) {
            a.setImagePath("/resources/images/activityImages/create_account.png");
            getActivityFacade().create(a);
        }
        else if (title.toLowerCase().contains("create")) {
            a.setImagePath("/resources/images/activityImages/create.png");
            getActivityFacade().create(a);
        }
        else if (title.toLowerCase().contains("delete")){
            a.setImagePath("/resources/images/activityImages/delete.png");
            getActivityFacade().create(a);
        }
        else if (title.toLowerCase().contains("share")){
            
            a.setImagePath("/resources/images/activityImages/share.png");
            getActivityFacade().create(a);
        }
        else if (title.toLowerCase().contains("add friend")){
            
            a.setImagePath("/resources/images/activityImages/friend.png");
            getActivityFacade().create(a);
        }
        else if (title.toLowerCase().contains("remove friend")){
            a.setImagePath("/resources/images/activityImages/unfriend.png");
            getActivityFacade().create(a);
        }
        else if (title.toLowerCase().contains("change photo")){
            a.setImagePath("/resources/images/activityImages/photo.png");
            getActivityFacade().create(a);
        }
        else if (title.toLowerCase().contains("password")){
            a.setImagePath("/resources/images/activityImages/password.png");
            getActivityFacade().create(a);
        }
        else if (title.toLowerCase().contains("upload textfile")){
            a.setImagePath("/resources/images/activityImages/upload.png");
            getActivityFacade().create(a);
        }
        
        
        // create in the database
        
    }
    
    public TimelineModel fetchEvents() {
        //this.activities.clear();
        TimelineModel events = new TimelineModel();
        //if (this.activities.isEmpty()) {
            //read from data base
            String user_name = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("username");
            User currUser = getUserFacade().findByUsername(user_name);
            
            List<Activity> temps = getActivityFacade().findUserActivities(currUser.getId());
            
            temps.forEach((a) -> {
                events.add(new TimelineEvent(a, a.getTimeCreated()));
            });

        //}
        return events;
    
    }
    
    
}
