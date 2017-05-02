/*
 * Created by Luke Mazzu on 2017.05.02  * 
 * Copyright © 2017 Luke Mazzu. All rights reserved. * 
 */
package com.team6.managers;

import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import com.team6.util.EmailSender;
import java.io.Serializable;
import javax.mail.MessagingException;
/**
 *
 * @author Luke
 */
@Named(value = "notificationManager")
@SessionScoped
public class NotificationManager implements Serializable {
    private final EmailSender emailSender = new EmailSender();
   
    
    public boolean sendNotificationToSignedInUser(String title, String body) {
        emailSender.setEmailTo((String)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("email"));
        emailSender.setEmailSubject(title);
        emailSender.setEmailBody(body);
        
        try {
        emailSender.sendEmail();
        }
        catch(MessagingException e) {
            System.err.print("Error occured when sending email notification to signed in user: " + e);
            return false;
        }

        return true;
    }
    
        public boolean sendNotificationToUser(String emailTo, String title, String body) {
        emailSender.setEmailTo(emailTo);
        emailSender.setEmailSubject(title);
        emailSender.setEmailBody(body);
        
        try {
        emailSender.sendEmail();
        }
        catch(MessagingException e) {
            System.err.print("Error occured when sending email notification to signed in user: " + e);
            return false;
        }

        return true;
    }
    
}
