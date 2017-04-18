package com.team6.entityclasses;

import com.team6.entityclasses.Activity;
import com.team6.entityclasses.User;
import com.team6.entityclasses.UserFile;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2017-04-18T13:20:16")
@StaticMetamodel(Notes.class)
public class Notes_ { 

    public static volatile SingularAttribute<Notes, Date> modifiedTime;
    public static volatile CollectionAttribute<Notes, UserFile> userFileCollection;
    public static volatile SingularAttribute<Notes, Date> createdTime;
    public static volatile SingularAttribute<Notes, Integer> id;
    public static volatile CollectionAttribute<Notes, Activity> activityCollection;
    public static volatile SingularAttribute<Notes, String> title;
    public static volatile SingularAttribute<Notes, User> userId;
    public static volatile SingularAttribute<Notes, String> content;

}