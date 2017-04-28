/*
 * Created by Thomas Nguyen on 2017.04.18  * 
 * Copyright © 2017 Thomas Nguyen. All rights reserved. * 
 */
package com.team6.sessionbeans;

import com.team6.entityclasses.Notes;
import com.team6.entityclasses.User;
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

    public List<Notes> findNotesByUserId(int userid) {
        if (em.createQuery("SELECT c FROM Notes c WHERE c.userId.id = :userid")
                .setParameter("userid", userid)
                .getResultList().isEmpty()) {
            return null;
        } else {
            return (em.createQuery("SELECT c FROM Notes c WHERE c.userId.id = :userid")
                    .setParameter("userid", userid)
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
