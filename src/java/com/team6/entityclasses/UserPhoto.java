/*
 * Created by Thomas Nguyen on 2017.04.18  * 
 * Copyright © 2017 Thomas Nguyen. All rights reserved. * 
 */
package com.team6.entityclasses;

import com.team6.managers.Constants;
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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Balci
 */
// The @Entity annotation designates this class as a JPA Entity POJO class representing the Photo table in the PizzaHutDB database.
@Entity

// Name of the CloudDriveDB database table represented
@Table(name = "UserPhoto")

@XmlRootElement
@NamedQueries({
    /* The following query is added. The others are auto generated */
    @NamedQuery(name = "UserPhoto.findUserPhotosByUserID", query = "SELECT p FROM UserPhoto p WHERE p.userId.id = :userId")
    , @NamedQuery(name = "UserPhoto.findAll", query = "SELECT u FROM UserPhoto u")
    , @NamedQuery(name = "UserPhoto.findById", query = "SELECT u FROM UserPhoto u WHERE u.id = :id")
    , @NamedQuery(name = "UserPhoto.findByExtension", query = "SELECT u FROM UserPhoto u WHERE u.extension = :extension")
})

public class UserPhoto implements Serializable {

    /*
    ========================================================
    Instance variables representing the attributes (columns)
    of the UserPhoto table in the CloudDriveDB database.
    ========================================================
     */
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 5)
    @Column(name = "extension")
    private String extension;

    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @ManyToOne
    private User userId;

    /*
    ====================================================================
    Class constructors for instantiating a UserPhoto entity object to
    represent a row in the UserPhoto table in the CloudDriveDB database.
    ====================================================================
     */
    public UserPhoto() {
    }

    public UserPhoto(Integer id) {
        this.id = id;
    }

    public UserPhoto(Integer id, String extension) {
        this.id = id;
        this.extension = extension;
    }

    // This method is added to the generated code
    public UserPhoto(String fileExtension, User id) {
        this.extension = fileExtension;
        userId = id;
    }

    /*
    ======================================================
    Getter and Setter methods for the attributes (columns)
    of the UserPhoto table in the CloudDriveDB database.
    ======================================================
     */
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public User getUserId() {
        return userId;
    }

    public void setUserId(User userId) {
        this.userId = userId;
    }

    /*
    ================
    Instance Methods
    ================
     */
    /**
     * @return Generates and returns a hash code value for the object with id
     */
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    /**
     * Checks if the UserPhoto object identified by 'object' is the same as the UserPhoto object identified by 'id'
     *
     * @param object The UserPhoto object identified by 'object'
     * @return True if the UserPhoto 'object' and 'id' are the same; otherwise, return False
     */
    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof UserPhoto)) {
            return false;
        }
        UserPhoto other = (UserPhoto) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    /**
     * @return the String representation of a UserPhoto id
     */
    @Override
    public String toString() {
        return id.toString();
    }

    /*
    =====================================================
    The following methods are added to the generated code
    =====================================================
     
    User's photo image file is named as "userId.fileExtension", e.g., 5.jpg for user with id 5.
    Since the user can have only one photo, this makes sense.
     */
    public String getPhotoFilename() {
        return getUserId() + "." + getExtension();
    }

    /*
    The thumbnail photo image size is set to 200x200px in Constants.java as follows:
    public static final Integer THUMBNAIL_SIZE = 200;
    
    If the user uploads a large photo file, we will scale it down to THUMBNAIL_SIZE
    and use it so that the size is reasonable for performance reasons.
    
    The photo image scaling is properly done by using the imgscalr-lib-4.2.jar file.
    
    The thumbnail file is named as "userId_thumbnail.fileExtension", 
    e.g., 5_thumbnail.jpg for user with id 5.
     */
    public String getThumbnailFileName() {
        return getUserId() + "_thumbnail." + getExtension();
    }

    public String getPhotoFilePath() {
        return Constants.PHOTOS_ABSOLUTE_PATH + getPhotoFilename();
    }

    public String getThumbnailFilePath() {
        return Constants.PHOTOS_ABSOLUTE_PATH + getThumbnailFileName();
    }
    
    public String getTemporaryFilePath() {
        return Constants.PHOTOS_ABSOLUTE_PATH + "tmp_file";
    }
    
    /*
    Customer's photo thumbnail image file is named as 
    the primary key (id) of the customer's photo_thumbnail.file extension name.
    Example: 38_thumbnail.jpeg
     */
    public String getThumbnailName() {
        return getId() + "_thumbnail." + getExtension();
    }

}
