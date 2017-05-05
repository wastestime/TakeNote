/*
 * Created by Guoxin Sun on 2017.01.24  * 
 * Copyright © 2017 Guoxin Sun. All rights reserved. * 
 */
package com.team6.sessionbeans;

import com.team6.entityclasses.ContactConnections;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author think7z
 */
@Stateless
public class ContactConnectionsFacade extends AbstractFacade<ContactConnections> {

    @PersistenceContext(unitName = "TakeNotePU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ContactConnectionsFacade() {
        super(ContactConnections.class);
    }
    
 
    public List<ContactConnections> findUserContacts(Integer userID) {
        List<ContactConnections> contacts = em.createNamedQuery("ContactConnections.findContactsByUserID")
                .setParameter("userID", userID)
                .getResultList();

        return contacts;
        
    }
    
}
