/*
 * Created by Thomas Nguyen on 2017.03.28  * 
 * Copyright © 2017 Thomas Nguyen. All rights reserved. * 
 */
package com.team6.managers;

import com.team6.entityclasses.Notes;
import com.team6.entityclasses.User;
import com.team6.entityclasses.UserFile;
import com.team6.entityclasses.UserPhoto;
import com.team6.jsfclasses.UserController;
import com.team6.sessionbeans.UserFacade;
import com.team6.sessionbeans.UserFileFacade;
import com.team6.sessionbeans.UserPhotoFacade;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;
import javax.inject.Inject;
import javax.inject.Named;

/*
---------------------------------------------------------------------------
JSF Managed Beans annotated with @ManagedBean from javax.faces.bean
is in the process of being deprecated in favor of CDI Beans. Therefore, 
you should use @Named from javax.inject package to designate a managed
bean as a JSF controller class. Contexts and Dependency Injection (CDI) 
beans annotated with @Named is the preferred approach, because CDI 
enables Java EE-wide dependency injection. CDI bean is a bean managed
by the CDI container. 

Within JSF XHTML pages, this bean will be referenced by using the name
'accountManager'. Actually, the default name is the class name starting
with a lower case letter and value = 'accountManager' is optional;
However, we spell it out to make our code more readable and understandable.
---------------------------------------------------------------------------
 */
@Named(value = "accountManager")

/*
The @SessionScoped annotation preserves the values of the AccountManager
object's instance variables across multiple HTTP request-response cycles
as long as the user's established HTTP session is alive.
 */
@SessionScoped

/**
 *
 * @author Balci
 */

/*
--------------------------------------------------------------------------
Marking the AccountManager class as "implements Serializable" implies that
instances of the class can be automatically serialized and deserialized. 

Serialization is the process of converting a class instance (object)
from memory into a suitable format for storage in a file or memory buffer, 
or for transmission across a network connection link.

Deserialization is the process of recreating a class instance (object)
in memory from the format under which it was stored.
--------------------------------------------------------------------------
 */
public class AccountManager implements Serializable {

    /*
    ===============================
    Instance Variables (Properties)
    ===============================
     */
    private String username;
    private String password;
    private String newPassword;

    private String firstName;
    private String lastName;

    private String state;

    private int securityQuestion;
    private String securityAnswer;

    private String email;

    private final String[] listOfStates = Constants.STATES;
    private Map<String, Object> security_questions;
    private String statusMessage;

    private User selected;

    
    
    /*
    The instance variable 'userFacade' is annotated with the @EJB annotation.
    The @EJB annotation directs the EJB Container (of the GlassFish AS) to inject (store) the object reference
    of the UserFacade object, after it is instantiated at runtime, into the instance variable 'userFacade'.
     */
    @EJB
    private UserFacade userFacade;
   

    /*
    The instance variable 'userFileFacade' is annotated with the @EJB annotation.
    The @EJB annotation directs the EJB Container (of the GlassFish AS) to inject (store) the object reference 
    of the UserFileFacade object, after it is instantiated at runtime, into the instance variable 'userFileFacade'.
     */
    @EJB
    private UserFileFacade userFileFacade;

    /*
    The instance variable 'userPhotoFacade' is annotated with the @EJB annotation.
    The @EJB annotation directs the EJB Container (of the GlassFish AS) to inject (store) the object reference 
    of the UserPhotoFacade object, after it is instantiated at runtime, into the instance variable 'userPhotoFacade'.
     */
    @EJB
    private UserPhotoFacade userPhotoFacade;

    @Inject
    private UserController userController;
    // Constructor method instantiating an instance of AccountManager
    public AccountManager() {
    }

    /*
    =========================
    Getter and Setter Methods
    =========================
     */
    public String[] getListOfStates() {
        return listOfStates;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getSecurityQuestion() {
        return securityQuestion;
    }

    public void setSecurityQuestion(int securityQuestion) {
        this.securityQuestion = securityQuestion;
    }

    public String getSecurityAnswer() {
        return securityAnswer;
    }

    public void setSecurityAnswer(String securityAnswer) {
        this.securityAnswer = securityAnswer;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserFacade getUserFacade() {
        return userFacade;
    }

    public UserFileFacade getUserFileFacade() {
        return userFileFacade;
    }

    public UserPhotoFacade getUserPhotoFacade() {
        return userPhotoFacade;
    }

    /*
    private Map<String, Object> security_questions;
        String      int
        ---------   ---
        question1,  0
        question2,  1
        question3,  2
            :
    When the user selects a security question, its number (int) is stored; not its String.
    Later, given the number (int), the security question String is retrieved.
     */
    public Map<String, Object> getSecurity_questions() {

        if (security_questions == null) {
            /*
            Difference between HashMap and LinkedHashMap:
            HashMap stores key-value pairings in no particular order. 
                Values are retrieved based on their corresponding Keys.
            LinkedHashMap stores and retrieves key-value pairings
                in the order they were put into the map.
             */
            security_questions = new LinkedHashMap<>();

            for (int i = 0; i < Constants.QUESTIONS.length; i++) {
                security_questions.put(Constants.QUESTIONS[i], i);
            }
        }
        return security_questions;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public User getSelected() {

        if (selected == null) {
            /*
            user_id (database primary key) was put into the SessionMap
            in the initializeSessionMap() method below or in LoginManager.
             */
            int userPrimaryKey = (int) FacesContext.getCurrentInstance().
                    getExternalContext().getSessionMap().get("user_id");
            /*
            Given the primary key, obtain the object reference of the User
            object and store it into the instance variable selected.
             */
            selected = getUserFacade().find(userPrimaryKey);
        }
        // Return the object reference of the selected User object
        return selected;
    }

    public void setSelected(User selectedUser) {
        this.selected = selectedUser;
    }

    /*
    ================
    Instance Methods
    ================
     */
    // Return True if a user is logged in; otherwise, return False
    public boolean isLoggedIn() {
        return FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("username") != null;
    }

    /*
    Create a new new user account. Return "" if an error occurs; otherwise,
    upon successful account creation, redirect to show the SignIn page.
     */
    public String createAccount() {
        
        // First, check if the entered username is already being used

        // Obtain the object reference of a User object with username
        User aUser = getUserFacade().findByUsername(username);

        if (aUser != null) {
            // A user already exists with the username entered
            username = "";
            statusMessage = "Username already exists! Please select a different one!";
            return "";
        }
        
        // The entered username is available

        if (statusMessage == null || statusMessage.isEmpty()) {
            try {
                // Instantiate a new User object
                User newUser = new User();

                /*
                Set the properties of the newly created newUser object with the values
                entered by the user in the AccountCreationForm in CreateAccount.xhtml
                */
                newUser.setFirstName(firstName);
                newUser.setLastName(lastName);
                newUser.setState(state);
                newUser.setSecurityQuestion(securityQuestion);
                newUser.setSecurityAnswer(securityAnswer);
                newUser.setEmail(email);
                newUser.setUsername(username);
                newUser.setPassword(password);
                
                getUserFacade().create(newUser);

            } catch (EJBException e) {
                username = "";
                statusMessage = "Something went wrong while creating user's account! See: " + e.getMessage();
                return "";
            }
            // Initialize the session map for the newly created User object
            initializeSessionMap();
            //FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
            userController.addActivity("Create Account");
           
            /*
            The Profile page cannot be shown since the new User has not signed in yet.
            Therefore, we show the Sign In page for the new User to sign in first.
             */
            return "SignIn.xhtml?faces-redirect=true";
        }
        return "";
    }

    /*
    Update the signed-in user's account profile. Return "" if an error occurs;
    otherwise, upon successful account update, redirect to show the Profile page.
     */
    public String updateAccount() {

        if (statusMessage == null || statusMessage.isEmpty()) {

            // Obtain the signed-in user's username
            String user_name = (String) FacesContext.getCurrentInstance().
                    getExternalContext().getSessionMap().get("username");

            // Obtain the object reference of the signed-in user
            User editUser = getUserFacade().findByUsername(user_name);

            try {
                /*
                Set the signed-in user's properties to the values entered by
                the user in the EditAccountProfileForm in EditAccount.xhtml.
                */
                editUser.setFirstName(this.selected.getFirstName());
                editUser.setLastName(this.selected.getLastName());
                editUser.setState(this.selected.getState());
                editUser.setEmail(this.selected.getEmail());

                // It is optional for the user to change his/her password
                String new_Password = getNewPassword();

                if (new_Password == null || new_Password.isEmpty()) {
                    // Do nothing. The user does not want to change the password.
                } else {
                    editUser.setPassword(new_Password);
                    // Password changed successfully!
                    // Password was first validated by invoking the validatePasswordChange method below.
                }

                // Store the changes in the CloudDriveDB database
                getUserFacade().edit(editUser);
                userController.addActivity("Edit Profile");

            } catch (EJBException e) {
                username = "";
                statusMessage = "Something went wrong while editing user's profile! See: " + e.getMessage();
                return "";
            }
            // Account update is completed, redirect to show the Profile page.
            return "Profile.xhtml?faces-redirect=true";
        }
        return "";
    }

    /*
    Delete the signed-in user's account. Return "" if an error occurs; otherwise,
    upon successful account deletion, redirect to show the index (home) page.
     */
    public String deleteAccount() {

        if (statusMessage == null || statusMessage.isEmpty()) {

            // Obtain the database primary key of the signed-in User object
            int user_id = (int) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("user_id");

            try {
                // Delete all of the photo files associated with the signed-in user whose primary key is user_id
                //deleteAllUserPhotos(user_id);

                // Delete all of the user files associated with the signed-in user whose primary key is user_id
                //deleteAllUserFiles(user_id);

                // Delete the User entity, whose primary key is user_id, from the CloudDriveDB database
                getUserFacade().deleteUser(user_id);

                statusMessage = "Your account is successfully deleted!";

            } catch (EJBException e) {
                username = "";
                statusMessage = "Something went wrong while deleting user's account! See: " + e.getMessage();
                return "";
            }

            logout();
            return "index.xhtml?faces-redirect=true";
        }
        return "";
    }

    // Validate if the entered password matches the entered confirm password
    public void validateInformation(ComponentSystemEvent event) {

        /*
        FacesContext contains all of the per-request state information related to the processing of
        a single JavaServer Faces request, and the rendering of the corresponding response.
        It is passed to, and potentially modified by, each phase of the request processing lifecycle.
         */
        FacesContext fc = FacesContext.getCurrentInstance();

        /*
        UIComponent is the base class for all user interface components in JavaServer Faces. 
        The set of UIComponent instances associated with a particular request and response are organized into
        a component tree under a UIViewRoot that represents the entire content of the request or response.
         */
        // Obtain the UIComponent instances associated with the event
        UIComponent components = event.getComponent();

        /*
        UIInput is a kind of UIComponent for the user to enter a value in.
         */
        // Obtain the object reference of the UIInput field with id="password" on the UI
        UIInput uiInputPassword = (UIInput) components.findComponent("password");

        // Obtain the password entered in the UIInput field with id="password" on the UI
        String entered_password = uiInputPassword.getLocalValue()
                == null ? "" : uiInputPassword.getLocalValue().toString();

        // Obtain the object reference of the UIInput field with id="confirmPassword" on the UI
        UIInput uiInputConfirmPassword = (UIInput) components.findComponent("confirmPassword");

        // Obtain the confirm password entered in the UIInput field with id="confirmPassword" on the UI
        String entered_confirm_password = uiInputConfirmPassword.getLocalValue()
                == null ? "" : uiInputConfirmPassword.getLocalValue().toString();

        if (entered_password.isEmpty() || entered_confirm_password.isEmpty()) {
            // Do not take any action. 
            // The required="true" in the XHTML file will catch this and produce an error message.
            return;
        }

        if (!entered_password.equals(entered_confirm_password)) {
            statusMessage = "Password and Confirm Password must match!";
        } else {
            statusMessage = "";
        }
    }

    // Validate the new password and new confirm password
    public void validatePasswordChange(ComponentSystemEvent event) {
        /*
        FacesContext contains all of the per-request state information related to the processing of
        a single JavaServer Faces request, and the rendering of the corresponding response.
        It is passed to, and potentially modified by, each phase of the request processing lifecycle.
         */
        FacesContext fc = FacesContext.getCurrentInstance();

        /*
        UIComponent is the base class for all user interface components in JavaServer Faces. 
        The set of UIComponent instances associated with a particular request and response are organized into
        a component tree under a UIViewRoot that represents the entire content of the request or response.
         */
        // Obtain the UIComponent instances associated with the event
        UIComponent components = event.getComponent();

        /*
        UIInput is a kind of UIComponent for the user to enter a value in.
         */
        // Obtain the object reference of the UIInput field with id="newPassword" on the UI
        UIInput uiInputPassword = (UIInput) components.findComponent("newPassword");

        // Obtain the new password entered in the UIInput field with id="newPassword" on the UI
        String new_Password = uiInputPassword.getLocalValue()
                == null ? "" : uiInputPassword.getLocalValue().toString();

        // Obtain the object reference of the UIInput field with id="newConfirmPassword" on the UI
        UIInput uiInputConfirmPassword = (UIInput) components.findComponent("newConfirmPassword");

        // Obtain the new confirm password entered in the UIInput field with id="newConfirmPassword" on the UI
        String new_ConfirmPassword = uiInputConfirmPassword.getLocalValue()
                == null ? "" : uiInputConfirmPassword.getLocalValue().toString();

        // It is optional for the user to change his/her password
        if (new_Password.isEmpty() || new_ConfirmPassword.isEmpty()) {
            // Do nothing. The user does not want to change the password.
            return;
        }

        if (!new_Password.equals(new_ConfirmPassword)) {
            statusMessage = "New Password and New Confirm Password must match!";
        } else {
            /*
            REGular EXpression (regex) for validating password strength:
            (?=.{8,31})        ==> Validate the password to be minimum 8 and maximum 31 characters long. 
            (?=.*[!@#$%^&*()]) ==> Validate the password to contain at least one special character. 
                                   (all special characters of the number keys from 1 to 0 on the keyboard)
            (?=.*[A-Z])        ==> Validate the password to contain at least one uppercase letter. 
            (?=.*[a-z])        ==> Validate the password to contain at least one lowercase letter. 
            (?=.*[0-9])        ==> Validate the password to contain at least one number from 0 to 9.
             */
            String regex = "^(?=.{8,31})(?=.*[!@#$%^&*()])(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9]).*$";

            if (!new_Password.matches(regex)) {
                statusMessage = "The password must be minimum 8 "
                        + "characters long, contain at least one special character, "
                        + "contain at least one uppercase letter, "
                        + "contain at least one lowercase letter, "
                        + "and contain at least one number 0 to 9.";
            } else {
                statusMessage = "";
            }
        }
    }

    // Validate if the entered password and confirm password are correct
    public void validateUserPassword(ComponentSystemEvent event) {
        /*
        FacesContext contains all of the per-request state information related to the processing of
        a single JavaServer Faces request, and the rendering of the corresponding response.
        It is passed to, and potentially modified by, each phase of the request processing lifecycle.
         */
        FacesContext fc = FacesContext.getCurrentInstance();

        /*
        UIComponent is the base class for all user interface components in JavaServer Faces. 
        The set of UIComponent instances associated with a particular request and response are organized into
        a component tree under a UIViewRoot that represents the entire content of the request or response.
         */
        // Obtain the UIComponent instances associated with the event
        UIComponent components = event.getComponent();

        /*
        UIInput is a kind of UIComponent for the user to enter a value in.
         */
        // Obtain the object reference of the UIInput field with id="password" on the UI
        UIInput uiInputPassword = (UIInput) components.findComponent("password");

        // Obtain the password entered in the UIInput field with id="password" on the UI
        String entered_password = uiInputPassword.getLocalValue()
                == null ? "" : uiInputPassword.getLocalValue().toString();

        // Obtain the object reference of the UIInput field with id="confirmPassword" on the UI
        UIInput uiInputConfirmPassword = (UIInput) components.findComponent("confirmPassword");

        // Obtain the confirm password entered in the UIInput field with id="confirmPassword" on the UI
        String entered_confirm_password = uiInputConfirmPassword.getLocalValue()
                == null ? "" : uiInputConfirmPassword.getLocalValue().toString();

        if (entered_password.isEmpty() || entered_confirm_password.isEmpty()) {
            // Do not take any action. 
            // The required="true" in the XHTML file will catch this and produce an error message.
            return;
        }

        if (!entered_password.equals(entered_confirm_password)) {
            statusMessage = "Password and Confirm Password must match!";
        } else {
            // Obtain the logged-in User's username
            String user_name = (String) FacesContext.getCurrentInstance().
                    getExternalContext().getSessionMap().get("username");

            // Obtain the object reference of the signed-in User object
            User user = getUserFacade().findByUsername(user_name);

            if (entered_password.equals(user.getPassword())) {
                // entered password = signed-in user's password
                statusMessage = "";
            } else {
                statusMessage = "Incorrect Password!";
            }
        }
    }

    // Initialize the session map for the User object
    public void initializeSessionMap() {

        // Obtain the object reference of the User object
        User user = getUserFacade().findByUsername(getUsername());

        // Put the User's object reference into session map variable user
        FacesContext.getCurrentInstance().getExternalContext().
                getSessionMap().put("user", user);

        // Put the User's database primary key into session map variable user_id
        FacesContext.getCurrentInstance().getExternalContext().
                getSessionMap().put("user_id", user.getId());
    }

    /*
    UIComponent is the base class for all user interface components in JavaServer Faces. 
    The set of UIComponent instances associated with a particular request and response are organized into
    a component tree under a UIViewRoot that represents the entire content of the request or response.
     
    @param components: UIComponent instances associated with the current request and response
    @return True if entered password is correct; otherwise, return False
     */
    private boolean correctPasswordEntered(UIComponent components) {

        // Obtain the object reference of the UIInput field with id="verifyPassword" on the UI
        UIInput uiInputVerifyPassword = (UIInput) components.findComponent("verifyPassword");

        // Obtain the verify password entered in the UIInput field with id="verifyPassword" on the UI
        String verifyPassword = uiInputVerifyPassword.getLocalValue()
                == null ? "" : uiInputVerifyPassword.getLocalValue().toString();

        if (verifyPassword.isEmpty()) {
            statusMessage = "Please enter a password!";
            return false;

        } else if (verifyPassword.equals(password)) {
            // Correct password is entered
            return true;

        } else {
            statusMessage = "Invalid password entered!";
            return false;
        }
    }

    // Show the Home page
    public String showHomePage() {
        return "index?faces-redirect=true";
    }

    // Show the Profile page
    public String showProfile() {
        return "Profile?faces-redirect=true";
    }

    public String logout() {

        // Clear the logged-in User's session map
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().clear();

        // Reset the logged-in User's properties
        username = password = "";
        firstName = lastName = "";
        state = "";
        securityQuestion = 0;
        securityAnswer = "";
        email = statusMessage = "";

        // Invalidate the logged-in User's session
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();

        // Redirect to show the index (Home) page
        return "index.xhtml?faces-redirect=true";
    }

    public String userPhoto() {

        // Obtain the signed-in user's username
        String usernameOfSignedInUser = (String) FacesContext.getCurrentInstance()
                .getExternalContext().getSessionMap().get("username");

        // Obtain the object reference of the signed-in user
        User signedInUser = getUserFacade().findByUsername(usernameOfSignedInUser);

        // Obtain the id (primary key in the database) of the signedInUser object
        Integer userId = signedInUser.getId();

        List<UserPhoto> photoList = getUserPhotoFacade().findUserPhotosByUserID(userId);

        if (photoList.isEmpty()) {
            /*
            No user photo exists. Return defaultUserPhoto.png 
            in CloudStorage/PhotoStorage.
            */
            return Constants.DEFAULT_PHOTO_RELATIVE_PATH;
        }

        /*
        photoList.get(0) returns the object reference of the first Photo object in the list.
        getThumbnailFileName() message is sent to that Photo object to retrieve its
        thumbnail image file name, e.g., 5_thumbnail.jpeg
         */
        String thumbnailFileName = photoList.get(0).getThumbnailFileName();

        /*
        In glassfish-web.xml file, we designated the '/CloudStorage/' directory as the
        Alternate Document Root with the following statement:
        
        <property name="alternatedocroot_1" value="from=/CloudStorage/* dir=/Users/Balci" />
        
        in Constants.java file, we defined the relative photo file path as
        
        public static final String PHOTOS_RELATIVE_PATH = "CloudStorage/PhotoStorage/";
        
        Thus, JSF knows that 'CloudStorage/' is the document root directory.
        */
        System.out.println("=======" +thumbnailFileName);
        String relativePhotoFilePath = Constants.PHOTOS_RELATIVE_PATH + thumbnailFileName;
        
        return relativePhotoFilePath;
    }

    /*
    Delete both uploaded and thumbnail photo files that belong to the User
    object whose database primary key is userId
     */
    public void deleteAllUserPhotos(int userId) {

        /*
        Obtain the list of Photo objects that belong to the User whose
        database primary key is userId.
         */
        List<UserPhoto> photoList = getUserPhotoFacade().findUserPhotosByUserID(userId);

        if (!photoList.isEmpty()) {

            // Obtain the object reference of the first Photo object in the list.
            UserPhoto photo = photoList.get(0);
            try {
                /*
                 Delete the user's photo if it exists.
                 getFilePath() is given in UserPhoto.java file.
                 */
                Files.deleteIfExists(Paths.get(photo.getPhotoFilePath()));

                /*
                 Delete the user's thumbnail image if it exists.
                 getThumbnailFilePath() is given in UserPhoto.java file.
                 */
                Files.deleteIfExists(Paths.get(photo.getThumbnailFilePath()));

                // Delete the temporary file if it exists
                Files.deleteIfExists(Paths.get(Constants.PHOTOS_ABSOLUTE_PATH + "tmp_file"));

                // Remove the user's photo's record from the CloudDriveDB database
                getUserPhotoFacade().remove(photo);

            } catch (IOException e) {
                statusMessage = "Something went wrong while deleting user's photo! See: " + e.getMessage();
            }
        }
    }

    /*
    Delete all of the files that belong to the User
    whose object reference is userId
     */
    public void deleteAllUserFiles(int userId) {

        // Obtain a list of files that belongs to the user with userId
        List<UserFile> userFilesList = getUserFileFacade().findUserFilesByUserID(userId);

        if (!userFilesList.isEmpty()) {

            for (int i = 0; i < userFilesList.size(); i++) {

                // Obtain the object reference of the ith user file
                UserFile userFile = userFilesList.get(i);

                try {
                    /*
                    Delete the user file if it exists.
                    getFilePath() is given in UserFile.java file.
                     */
                    Files.deleteIfExists(Paths.get(userFile.getFilePath()));

                    // Remove the user's file record from the CloudDriveDB database
                    getUserFileFacade().remove(userFile);

                } catch (IOException e) {
                    statusMessage = "Something went wrong while deleting user files! See: " + e.getMessage();
                }
            }
        }
    }

    public void setUserPhotoFacade(UserPhotoFacade userPhotoFacade) {
        this.userPhotoFacade = userPhotoFacade;
    }
    // EL in Profile.xhtml invokes this method to obtain the constant value
    public String photoStorageDirectoryName() {
        return Constants.PHOTOS_ABSOLUTE_PATH;
    }
}
