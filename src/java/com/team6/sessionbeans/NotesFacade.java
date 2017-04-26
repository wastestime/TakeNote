/*
 * Created by Thomas Nguyen on 2017.04.18  * 
 * Copyright © 2017 Thomas Nguyen. All rights reserved. * 
 */
package com.team6.sessionbeans;

import com.team6.entityclasses.Notes;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Thomas
 */
@Stateless
public class NotesFacade extends AbstractFacade<Notes> {


    @PersistenceContext(unitName = "TakeNotePU")
    private EntityManager em;    // 'em' holds the object reference to the instantiated EntityManager object.

    // @Override annotation indicates that the super class AbstractFacade's getEntityManager() method is overridden.
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }


    public NotesFacade() {
        super(Notes.class); // Invokes super's AbstractFacade constructor method by passing
        // Notes.class, which is the object reference of the Notes class.
    }
    
    public Notes findById(int id) {
        if (em.createQuery("SELECT c FROM Notes c WHERE c.id = :id")
                .setParameter("note_id", id)
                .getResultList().isEmpty()) {
            return null;
        } else {
            return (Notes) (em.createQuery("SELECT c FROM Notes c WHERE c.username = :username")
                    .setParameter("note_id", id)
                    .getSingleResult());
        }
    }

    
    public List<Notes> titleQuery(String searchString) {
        // Place the % wildcard before and after the search string to search for it anywhere in the notes name 
        searchString = "%" + searchString + "%";
        // Conduct the search in a case-insensitive manner and return the results in a list.
        return getEntityManager().createQuery("SELECT c FROM Notes c WHERE c.title LIKE :searchString").setParameter("searchString", searchString).getResultList();
    }

   
    public List<Notes> descriptionQuery(String searchString) {
        // Place the % wildcard before and after the search string to search for it anywhere in the description name 
        searchString = "%" + searchString + "%";
        // Conduct the search in a case-insensitive manner and return the results in a list.
        return getEntityManager().createQuery("SELECT c FROM Notes c WHERE c.description LIKE :searchString").setParameter("searchString", searchString).getResultList();
    }

    
    public List<Notes> userIdQuery(String searchString) {
        // Place the % wildcard before and after the search string to search for it anywhere in the State name 
        searchString = "%" + searchString + "%";
        // Conduct the search in a case-insensitive manner and return the results in a list.
        return getEntityManager().createQuery("SELECT c FROM Notes c WHERE c.userId LIKE :searchString").setParameter("searchString", searchString).getResultList();
    }

    
    public List<Notes> allQuery(String searchString) {
        // Place the % wildcard before and after the search string to search for it anywhere in the name 
        searchString = "%" + searchString + "%";
        // Conduct the search in a case-insensitive manner and return the results in a list.
        return getEntityManager().createQuery("SELECT c FROM Notes c WHERE c.title LIKE :searchString OR c.description LIKE :searchString OR c.userId LIKE :searchString").setParameter("searchString", searchString).getResultList();
    }
}