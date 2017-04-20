/*
 * Created by Guoxin Sun on 2017.04.03  * 
 * Copyright © 2017 Guoxin Sun. All rights reserved. * 
 */
package com.team6.validators;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

@FacesValidator("passwordValidator")
/**
 *
 * @author Balci
 */
public class PasswordValidator implements Validator {

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {

        // Type cast the inputted "value" to enteredPassword of type String
        String enteredPassword = (String) value;

        if (enteredPassword == null || enteredPassword.isEmpty()) {
            // Do not take any action. 
            // The required="true" in the XHTML file will catch this and produce an error message.
            return;
        }

        /*
        A whitespace is a character such as space " ", newline "\n", tab "\t", return "\r"
        First, check to see if the entered password contains a whitespace
         */
        int pwdLength = enteredPassword.length();
        for (int i = 0; i < pwdLength; i++) {
            if (Character.isWhitespace(enteredPassword.charAt(i))) {

                throw new ValidatorException(new FacesMessage("Password cannot contain a whitespace!"));
            }
        }

        /* REGular EXpression (regex) for validating the strength of enteredPassword
        
            ^                   start of regex
            (?=.{8,32})         minimum 8 and maximum 32 characters long  
            (?=.*[!@#$%^&*()])  contain at least one special character 
                                (all special characters of the number keys from 1 to 0 on the keyboard)
            (?=.*[A-Z])         contain at least one uppercase letter
            (?=.*[a-z])         contain at least one lowercase letter
            (?=.*[0-9])         contain at least one digit from 0 to 9.
            $                   end of regex
         */
        // REGular EXpression (regex) to validate the strength of enteredPassword
        String regex = "^(?=.{8,32})(?=.*[!@#$%^&*()])(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9]).*$";

        if (!enteredPassword.matches(regex)) {
            throw new ValidatorException(new FacesMessage("The password must be minimum 8 and maximum 32 "
                    + "characters long, contain at least one special character, "
                    + "contain at least one uppercase letter, "
                    + "contain at least one lowercase letter, "
                    + "and contain at least one digit 0 to 9."));
        }
    }

}
