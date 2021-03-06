/*
 * Created by Thomas Nguyen on 2017.04.18  * 
 * Copyright © 2017 Thomas Nguyen. All rights reserved. * 
 */
package com.team6.sessionbeans;

import com.team6.entityclasses.Activity;
import java.util.List;
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
    // find a user's all activities
    public List<Activity> findUserActivities(Integer userID) {
        List<Activity> activities = em.createNamedQuery("Activity.findActivitiesByUserID")
                .setParameter("userID", userID)
                .getResultList();

        return activities;     
    }
}
