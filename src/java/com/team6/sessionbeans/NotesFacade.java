/*
 * Created by Thomas Nguyen on 2017.04.18  * 
 * Copyright © 2017 Thomas Nguyen. All rights reserved. * 
 */
package com.team6.sessionbeans;

import com.team6.entityclasses.Notes;
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
}
