/*
 * Created by Thomas Nguyen on 2017.03.28  * 
 * Copyright © 2017 Thomas Nguyen. All rights reserved. * 
 */
package com.team6.managers;

import com.team6.entityclasses.User;
import com.team6.jsfclasses.UserController;
import com.team6.sessionbeans.UserFacade;
import java.io.Serializable;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;
import javax.inject.Inject;
import javax.inject.Named;


@Named(value = "passwordResetManager")
@SessionScoped
/**
 *
 * @author Balci
 */
public class PasswordResetManager implements Serializable {

    // Instance Variables (Properties)
    private String username;
    private String message = "";
    private String answer;
    private String password;

    /*
    The @EJB annotation implies that the EJB container will perform an injection of the object
    reference of the CustomerFacade object into customerFacade when it is created at runtime.
     */
    @EJB
    private UserFacade userFacade;
    
    @Inject
    private UserController userController;
    /*
    =========================
    Getter and Setter Methods
    =========================
     */
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /*
    ================
    Instance Methods
    ================
     */
    // Process the submitted username
    public String usernameSubmit() {

        // Obtain the object reference of the Customer object with username
        User customer = userFacade.findByUsername(username);

        if (customer == null) {
            message = "Entered username does not exist!";

            // Redirect to show the EnterUsername page
            return "RecoverPassword?faces-redirect=true";
        } else {
            // Entered username exists
            message = "";

            // Redirect to show the SecurityQuestion page
            return "SecurityQuestion?faces-redirect=true";
        }
    }

    // Process the submitted answer to the security question
    public String securityAnswerSubmit() {

        // Obtain the object reference of the Customer object with username
        User customer = userFacade.findByUsername(username);

        String actualSecurityAnswer = customer.getSecurityAnswer();
        String enteredSecurityAnswer = getAnswer();

        if (actualSecurityAnswer.equals(enteredSecurityAnswer)) {
            // Answer to the security question is correct
            message = "";

            // Redirect to show the ResetPassword page
            return "ResetPassword?faces-redirect=true";
        } else {
            // Answer to the security question is wrong
            message = "Security question answer is incorrect!";

            // Redirect to show the SecurityQuestion page
            return "SecurityQuestion?faces-redirect=true";
        }
    }

    // Return the security question selected by the Customer object with username
    public String getSecurityQuestion() {

        // Obtain the object reference of the Customer object with username
        User customer = userFacade.findByUsername(username);

        // Obtain the number of the security question selected by the customer
        int questionNumber = customer.getSecurityQuestion();

        // Return the security question corresponding to the question number
        return Constants.QUESTIONS[questionNumber];
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
            message = "Password and Confirm Password must match!";
        } else {
            message = "";
        }
    }

    public String resetPassword() {

        if (message == null || message.isEmpty()) {

            // Obtain the object reference of the Customer object with username
            User customer = userFacade.findByUsername(username);

            try {
                // Reset Customer object's password
                customer.setPassword(password);

                // Update the database
                userFacade.edit(customer);

                // Initialize the instance variables
                username = message = answer = password = "";

            } catch (EJBException e) {
                message = "Something went wrong while resetting your password, please try again!";
                
                // Redirect to show the ResetPassword page
                return "ResetPassword?faces-redirect=true";
            }
            userController.addActivity("Change Password");
            // Redirect to show the index (Home) page
            return "index?faces-redirect=true";
            
        } else {
            // Redirect to show the ResetPassword page
            return "ResetPassword?faces-redirect=true";
        }
    }

}
