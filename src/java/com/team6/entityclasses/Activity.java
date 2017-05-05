/*
 * Created by Guoxin Sun on 2017.05.05  * 
 * Copyright © 2017 Guoxin Sun. All rights reserved. * 
 */
package com.team6.entityclasses;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author think7z
 */
@Entity
@Table(name = "Activity")
@XmlRootElement

//queries that will be used 
@NamedQueries({
    @NamedQuery(name = "Activity.findActivitiesByUserID", query = "SELECT a FROM Activity a WHERE a.userId.id = :userID")
    , @NamedQuery(name = "Activity.findAll", query = "SELECT a FROM Activity a")
    , @NamedQuery(name = "Activity.findById", query = "SELECT a FROM Activity a WHERE a.id = :id")})

public class Activity implements Serializable {
//  time the activity was created
    @Basic(optional = false)
    @NotNull
    @Column(name = "timeCreated")
    @Temporal(TemporalType.TIMESTAMP)
    private Date timeCreated;
//  the image path for that kind of activity
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 140)
    @Column(name = "imagePath")
    private String imagePath;
//  the title of the activity
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 64)
    @Column(name = "title")
    private String title;
   
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
   
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @ManyToOne
    private User userId;

    //Constructor
    public Activity() {
    }
    
    public Activity(String title, String imagepath) {
        this.title = title;
        this.imagePath = imagepath;
    }
    
    public Activity(Integer id) {
        this.id = id;
    }

    // geters and setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getUserId() {
        return userId;
    }

    public void setUserId(User userId) {
        this.userId = userId;
    }
    
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public Date getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Date timeCreated) {
        this.timeCreated = timeCreated;
    }
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Activity)) {
            return false;
        }
        Activity other = (Activity) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.team6.entityclasses.Activity[ id=" + id + " ]";
    }
  
}
