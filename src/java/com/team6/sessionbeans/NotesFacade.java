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
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public NotesFacade() {
        super(Notes.class);
    }

    public Notes findById(int id) {
        if (em.createQuery("SELECT c FROM Notes c WHERE c.id = :note_id")
                .setParameter("note_id", id)
                .getResultList().isEmpty()) {
            return null;
        } else {
            return (Notes) (em.createQuery("SELECT c FROM Notes c WHERE c.id = :note_id")
                    .setParameter("note_id", id)
                    .getSingleResult());
        }
    }

    public List<Notes> findNotesByUserId(int userId) {
        if (em.createQuery("SELECT c FROM Notes c WHERE (c.userId.id = :userId AND c.sharedWith = null) OR (c.sharedWith.id = :userId AND c.userId.id != :userId)")
                .setParameter("userId", userId)
                .getResultList().isEmpty()) {
            return null;
        } else {
            return (em.createQuery("SELECT c FROM Notes c WHERE (c.userId.id = :userId AND c.sharedWith = null) OR (c.sharedWith.id = :userId AND c.userId.id != :userId)")
                    .setParameter("userId", userId)
                    .getResultList());
        }
    }

    public Notes findByUserIdAndTitle(int userId, String title) {
        if (em.createQuery("SELECT c FROM Notes c WHERE c.title = :title AND c.userId.id = :userId")
                .setParameter("title", title)
                .setParameter("userId", userId)
                .getResultList().isEmpty()) {
            return null;
        } else {
            return (Notes) (em.createQuery("SELECT c FROM Notes c WHERE c.title = :title AND c.userId.id = :userId")
                    .setParameter("title", title)
                    .setParameter("userId", userId)
                    .getSingleResult());
        }
    }
    
    public Notes findSharedByUserIdAndTitle(int userId, String title) {
        if (em.createQuery("SELECT c FROM Notes c WHERE c.title = :title AND c.sharedWith.id = :userId")
                .setParameter("title", title)
                .setParameter("userId", userId)
                .getResultList().isEmpty()) {
            return null;
        } else {
            return (Notes) (em.createQuery("SELECT c FROM Notes c WHERE c.title = :title AND c.sharedWith.id = :userId")
                    .setParameter("title", title)
                    .setParameter("userId", userId)
                    .getSingleResult());
        }
    }
    
    public List<Notes> findNotesByUserIdAndTitle(int userId, String title) {
        if (em.createQuery("SELECT c FROM Notes c WHERE c.title LIKE :title AND ((c.userId.id = :userId AND c.sharedWith = null) OR (c.sharedWith.id = :userId AND c.userId.id != :userId))")
                .setParameter("title", title)
                .setParameter("userId", userId)
                .getResultList().isEmpty()) {
            return null;
        } else {
            return (em.createQuery("SELECT c FROM Notes c WHERE c.title LIKE :title AND ((c.userId.id = :userId AND c.sharedWith = null) OR (c.sharedWith.id = :userId AND c.userId.id != :userId))")
                    .setParameter("title", title)
                    .setParameter("userId", userId)
                    .getResultList());
        }
    }
    
    public List<Notes> findNotesByUserIdAndDescription(int userId, String description) {
        if (em.createQuery("SELECT c FROM Notes c WHERE c.description LIKE :description AND ((c.userId.id = :userId AND c.sharedWith = null) OR (c.sharedWith.id = :userId AND c.userId.id != :userId))")
                .setParameter("description", description)
                .setParameter("userId", userId)
                .getResultList().isEmpty()) {
            return null;
        } else {
            return (em.createQuery("SELECT c FROM Notes c WHERE c.description LIKE :description AND ((c.userId.id = :userId AND c.sharedWith = null) OR (c.sharedWith.id = :userId AND c.userId.id != :userId))")
                    .setParameter("description", description)
                    .setParameter("userId", userId)
                    .getResultList());
        }
    }
    
    public List<Notes> findNotesByUserIdAndOwner(int userId, String owner) {
        if (em.createQuery("SELECT c FROM Notes c WHERE c.userId.username LIKE :owner AND ((c.userId.id = :userId AND c.sharedWith = null) OR (c.sharedWith.id = :userId AND c.userId.id != :userId))")
                .setParameter("owner", owner)
                .setParameter("userId", userId)
                .getResultList().isEmpty()) {
            return null;
        } else {
            return em.createQuery("SELECT c FROM Notes c WHERE c.userId.username LIKE :owner AND ((c.userId.id = :userId AND c.sharedWith = null) OR (c.sharedWith.id = :userId AND c.userId.id != :userId))")
                    .setParameter("owner", owner)
                    .setParameter("userId", userId)
                    .getResultList();
        }
    }

    public Notes findByTitle(String title) {
        if (em.createQuery("SELECT c FROM Notes c WHERE c.title = :title")
                .setParameter("title", title)
                .getResultList().isEmpty()) {
            return null;
        } else {
            return (Notes) (em.createQuery("SELECT c FROM Notes c WHERE c.title = :title")
                    .setParameter("title", title)
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

    public List<Notes> usernameQuery(String searchString) {
        // Place the % wildcard before and after the search string to search for it anywhere in the State name 
        searchString = "%" + searchString + "%";
        // Conduct the search in a case-insensitive manner and return the results in a list.
        return getEntityManager().createQuery("SELECT c FROM Notes c WHERE c.userId.username LIKE :searchString").setParameter("searchString", searchString).getResultList();
    }

    public List<Notes> allQuery(String searchString) {
        // Place the % wildcard before and after the search string to search for it anywhere in the name 
        searchString = "%" + searchString + "%";
        // Conduct the search in a case-insensitive manner and return the results in a list.
        return getEntityManager().createQuery("SELECT c FROM Notes c WHERE c.title LIKE :searchString OR c.description LIKE :searchString OR c.userId.username LIKE :searchString").setParameter("searchString", searchString).getResultList();
    }
}
