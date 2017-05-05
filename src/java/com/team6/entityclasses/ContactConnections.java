/*
 * Created by Guoxin Sun on 2017.05.05  * 
 * Copyright © 2017 Guoxin Sun. All rights reserved. * 
 */
package com.team6.entityclasses;

import java.io.Serializable;
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
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author think7z
 */
@Entity
@Table(name = "ContactConnections")
@XmlRootElement
// queries that will be used
@NamedQueries({
    @NamedQuery(name = "ContactConnections.findContactsByUserID", query = "SELECT c FROM ContactConnections c WHERE c.userId.id = :userID")
    , @NamedQuery(name = "ContactConnections.findAll", query = "SELECT c FROM ContactConnections c")
    , @NamedQuery(name = "ContactConnections.findById", query = "SELECT c FROM ContactConnections c WHERE c.id = :id")})
public class ContactConnections implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @ManyToOne
    private User userId;
    @JoinColumn(name = "contact_uid", referencedColumnName = "id")
    @ManyToOne
    private User contactUid;
    // Constructors
    public ContactConnections() {
    }
    
    public ContactConnections(User one, User two) {
        this.userId = one;
        this.contactUid = two;
    }

    public ContactConnections(Integer id) {
        this.id = id;
    }
    // Getters and Setters
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

    public User getContactUid() {
        return contactUid;
    }

    public void setContactUid(User contactUid) {
        this.contactUid = contactUid;
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
        if (!(object instanceof ContactConnections)) {
            return false;
        }
        ContactConnections other = (ContactConnections) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.team6.entityclasses.ContactConnections[ id=" + id + " ]";
    }
    
}
