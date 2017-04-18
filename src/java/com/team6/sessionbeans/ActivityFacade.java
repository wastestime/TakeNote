/*
 * Created by Thomas Nguyen on 2017.04.18  * 
 * Copyright © 2017 Thomas Nguyen. All rights reserved. * 
 */
package com.team6.sessionbeans;

import com.team6.entityclasses.Activity;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Thomas
 */
@Stateless
public class ActivityFacade extends AbstractFacade<Activity> {

    @PersistenceContext(unitName = "TakeNotePU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ActivityFacade() {
        super(Activity.class);
    }
    
}
