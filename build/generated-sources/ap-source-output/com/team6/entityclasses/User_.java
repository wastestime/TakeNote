package com.team6.entityclasses;

import com.team6.entityclasses.Notes;
import com.team6.entityclasses.User;
import com.team6.entityclasses.UserPhoto;
import javax.annotation.Generated;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2017-04-18T13:20:16")
@StaticMetamodel(User.class)
public class User_ { 

    public static volatile SingularAttribute<User, String> firstName;
    public static volatile SingularAttribute<User, String> password;
    public static volatile CollectionAttribute<User, Notes> notesCollection;
    public static volatile SingularAttribute<User, Integer> securityQuestion;
    public static volatile SingularAttribute<User, String> securityAnswer;
    public static volatile CollectionAttribute<User, User> userCollection;
    public static volatile CollectionAttribute<User, User> userCollection1;
    public static volatile SingularAttribute<User, Integer> id;
    public static volatile SingularAttribute<User, String> state;
    public static volatile CollectionAttribute<User, UserPhoto> userPhotoCollection;
    public static volatile SingularAttribute<User, String> email;
    public static volatile SingularAttribute<User, String> username;

}