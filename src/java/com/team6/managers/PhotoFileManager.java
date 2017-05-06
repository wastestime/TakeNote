/*
 * Created by Thomas Nguyen on 2017.01.28  * 
 * Copyright © 2017 Take Note. All rights reserved. * 
 */
package com.team6.managers;

import com.team6.entityclasses.User;
import com.team6.entityclasses.UserPhoto;
import com.team6.jsfclasses.UserController;
import com.team6.managers.Constants;
import com.team6.sessionbeans.UserFacade;
import com.team6.sessionbeans.UserPhotoFacade;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.inject.Named;
import org.imgscalr.Scalr;
import org.primefaces.model.UploadedFile;

@Named(value = "photoFileManager")
@SessionScoped
/**
 *
 * @author Balci
 */
public class PhotoFileManager implements Serializable {

    /*
    ===============================
    Instance Variables (Properties)
    ===============================
     */
    private UploadedFile file;
    private String message = "";
    FacesMessage resultMsg;

    /*
    The instance variable 'userFacade' is annotated with the @EJB annotation.
    The @EJB annotation directs the EJB Container (of the GlassFish AS) to inject (store) the object reference
    of the UserFacade object, after it is instantiated at runtime, into the instance variable 'userFacade'.
     */
    @EJB
    private UserFacade userFacade;

    /*
    The instance variable 'userPhotoFacade' is annotated with the @EJB annotation.
    The @EJB annotation directs the EJB Container (of the GlassFish AS) to inject (store) the object reference 
    of the UserPhotoFacade object, after it is instantiated at runtime, into the instance variable 'userPhotoFacade'.
     */
    @EJB
    private UserPhotoFacade userPhotoFacade;

    @Inject
    private UserController userController;
    /*
    =========================
    Getter and Setter Methods
    =========================
     */
    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }

    // Return the message
    public String getMessage() {
        return message;
    }

    // Set the message
    public void setMessage(String message) {
        this.message = message;
    }

    public UserFacade getUserFacade() {
        return userFacade;
    }

    public UserPhotoFacade getUserPhotoFacade() {
        return userPhotoFacade;
    }

    //****************  Instance Methods  ********************
    /*
    We display two types of error messages:
    (1) we store a message in an instance variable and pass it to a JSF page to display it.
        <>  Under this approach, the displayed error message must be 
            cleared with the method below.
    (2) we use FacesMessage resultMsg;
        <>  Under this approach, the error message is displayed with 
            <p:growl id="growl" life="5000" /> and fades away after 5 seconds.
    
    ====================
    Clear Error Messages
    ====================
     */
    public String clearErrorMessage() {
        message = "";
        return "ChangePhoto?faces-redirect=true";
    }

    /*
    ========================
    Handle User Photo Upload
    ========================
     */
    public String upload() {

        // Check if a file is selected
        if (file.getSize() == 0) {
            message = "You need to choose a file first!";
            return "";
        }

        /*
        MIME (Multipurpose Internet Mail Extensions) is a way of identifying files on
        the Internet according to their nature and format. 

        A "Content-type" is simply a header defined in many protocols, such as HTTP, that 
        makes use of MIME types to specify the nature of the file currently being handled.

        Some MIME file types: (See http://www.freeformatter.com/mime-types-list.html)

            JPEG Image      = image/jpeg or image/jpg
            PNG image       = image/png
            GIF image       = image/gif
            Plain text file = text/plain
            HTML file       = text/html
            JSON file       = application/json
         */
        // Obtain the uploaded file's MIME file type
        String mimeFileType = file.getContentType();

        if (mimeFileType.startsWith("image/")) {
            // The uploaded file is an image file
            /*
            The subSequence() method returns the portion of the mimeFileType string from the 6th
            position to the last character. Note that it starts with "image/" which has 6 characters at
            positions 0,1,2,3,4,5. Therefore, we start the subsequence at position 6 to obtain the file extension.
             */
            String fileExtension = mimeFileType.subSequence(6, mimeFileType.length()).toString();

            String fileExtensionInCaps = fileExtension.toUpperCase();

            switch (fileExtensionInCaps) {
                case "JPG":
                case "JPEG":
                case "PNG":
                case "GIF":
                    // File is an acceptable image type
                    break;
                default:
                    message = "Selected file type is not a JPG, JPEG, PNG, or GIF!";
                    return "";
            }
        } else {
            message = "Selected file to upload must be an image file of type JPG, JPEG, PNG or GIF!";
            return "";
        }

        storePhotoFile(file);
        message = "";
        userController.addActivity("Change Photo");
        // Redirect to show the Profile page
        return "Profile?faces-redirect=true";
    }

    /*
    ========================
    Cancel Photo File Upload
    ========================
     */
    public String cancel() {
        message = "";
        return "Profile?faces-redirect=true";
    }

    /*
    ================================
    Store Uploaded User's Photo File
    ================================
    
    Store the uploaded photo file and its thumbnail version and create a database record 
     */
    public FacesMessage storePhotoFile(UploadedFile file) {
        try {
            // This sets the necessary flag to ensure the messages are preserved.
            FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);

            // Delete signed-in user's uploaded photo file, its thumbnail file, tmp_file, and its database record.
            deletePhoto();

            /*
            InputStream is an abstract class, which is the superclass of all classes representing an input stream of bytes.
            Convert the uploaded file into an input stream of bytes.
             */
            InputStream inputStream = file.getInputstream();

            // Write the uploaded file's input stream of bytes into the file named TEMP_FILE = "tmp_file"
            File tempFile = inputStreamToFile(inputStream, Constants.TEMP_FILE);

            // Close the input stream and release any system resources associated with it
            inputStream.close();

            // Obtain the username of the logged-in user
            String user_name = (String) FacesContext.getCurrentInstance()
                    .getExternalContext().getSessionMap().get("username");

            // Obtain the object reference of the logged-in User object
            User user = getUserFacade().findByUsername(user_name);

            // Obtain the uploaded file's MIME file type
            String mimeFileType = file.getContentType();

            // If it is an image file, obtain its file extension; otherwise, set png as the file extension anyway.
            String fileExtension = mimeFileType.startsWith("image/") ? mimeFileType.subSequence(6, mimeFileType.length()).toString() : "png";

            /*
            Obtain the list of Photo objects that belong to the User whose
            database primary key is user.getId()
             */
            List<UserPhoto> photoList = getUserPhotoFacade().findUserPhotosByUserID(user.getId());

            if (!photoList.isEmpty()) {
                // Remove the photo from the database
                getUserPhotoFacade().remove(photoList.get(0));
            }

            // Construct a new Photo object with file extension and user's object reference
            UserPhoto newPhoto = new UserPhoto(fileExtension, user);

            // Create a record for the new Photo object in the database
            getUserPhotoFacade().create(newPhoto);

            // Obtain the object reference of the first Photo object of the
            // user whose primary key is user.getId()
            UserPhoto photo = getUserPhotoFacade().findUserPhotosByUserID(user.getId()).get(0);

            // Reconvert the uploaded file into an input stream of bytes.
            inputStream = file.getInputstream();

            // Write the uploaded file's input stream of bytes under the photo object's
            // filename using the inputStreamToFile method given below
            File uploadedFile = inputStreamToFile(inputStream, photo.getPhotoFilename());

            // Create and save the thumbnail version of the uploaded file
            saveThumbnail(uploadedFile, photo);

            // Compose the result message
            resultMsg = new FacesMessage("User's photo file is successfully uploaded!");

            // Return the result message
            return resultMsg;

        } catch (IOException e) {
            resultMsg = new FacesMessage("Something went wrong while deleting the user file! See: " + e.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, resultMsg);
        }

        // Compose the result message
        resultMsg = new FacesMessage("Upload Failed! There was a problem reading the image file. Please try again with a new photo file!");

        return resultMsg;
    }

    /*
    =====================================================
    Write a given data stream into a file with given name
    =====================================================
     */
    /**
     * @param inputStream of bytes to be written into file with name targetFilename
     * @param targetFilename
     * @return the created file targetFile
     * @throws IOException
     */
    private File inputStreamToFile(InputStream inputStream, String targetFilename) throws IOException {

        // This sets the necessary flag to ensure the messages are preserved.
        FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);

        File targetFile = null;

        try {
            /*
            inputStream.available() returns an estimate of the number of bytes that can be read from
            the inputStream without blocking by the next invocation of a method for this input stream.
            A memory buffer of bytes is created with the size of estimated number of bytes.
             */
            byte[] buffer = new byte[inputStream.available()];

            // Read the bytes of data from the inputStream into the created memory buffer. 
            inputStream.read(buffer);

            // Create a new empty file with the given name targetFilename in CloudStorage/PhotoStorage
            targetFile = new File(Constants.PHOTOS_ABSOLUTE_PATH, targetFilename);

            // A file OutputStream is an output stream for writing data to a file
            OutputStream outStream;

            /*
            FileOutputStream is intended for writing streams of raw bytes such as image data.
            Create a new FileOutputStream for writing to the empty targetFile
             */
            outStream = new FileOutputStream(targetFile);

            // Create the targetFile in CloudStorage/PhotoStorage with the inputStream given
            outStream.write(buffer);

            // Close the output stream and release any system resources associated with it. 
            outStream.close();

        } catch (IOException e) {
            resultMsg = new FacesMessage("Something went wrong in input stream to file! See: " + e.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, resultMsg);
        }

        // Return the created targetFile
        return targetFile;
    }

    /*
    ============================================
    Store Signed-In User's Thumbnail Photo Image
    ============================================

    When user uploads a photo, a thumbnail (small) version of the photo image
    is created in this method by using the Scalr.resize method provided in the
    imgscalr (Java Image Scaling Library) imported as imgscalr-lib-4.2.jar
     */
    private void saveThumbnail(File inputFile, UserPhoto inputPhoto) {

        // This sets the necessary flag to ensure the messages are preserved.
        FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);

        try {
            // Buffer the photo image from the uploaded inputFile
            BufferedImage uploadedPhoto = ImageIO.read(inputFile);

            /*
            The thumbnail photo image size is set to 200x200px in Constants.java as follows:
            public static final Integer THUMBNAIL_SIZE = 200;

            If the user uploads a large photo file, we will scale it down to THUMBNAIL_SIZE
            and use it so that the size is reasonable for performance reasons.

            The photo image scaling is properly done by using the imgscalr-lib-4.2.jar file.

            The thumbnail file is named as "userId_thumbnail.fileExtension", 
            e.g., 5_thumbnail.jpg for user with id 5.
             */
            // Scale the uploaded photo image to the THUMBNAIL_SIZE using imgscalr.
            BufferedImage thumbnailPhoto = Scalr.resize(uploadedPhoto, Constants.THUMBNAIL_SIZE);

            // Create the thumbnail photo file in CloudStorage/PhotoStorage
            File thumbnailPhotoFile = new File(Constants.PHOTOS_ABSOLUTE_PATH, inputPhoto.getThumbnailFileName());

            // Write the thumbnailPhoto into thumbnailPhotoFile with the file extension
            ImageIO.write(thumbnailPhoto, inputPhoto.getExtension(), thumbnailPhotoFile);

        } catch (IOException e) {
            resultMsg = new FacesMessage("Something went wrong while saving the thumbnail file! See: " + e.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, resultMsg);
        }
    }

    /*
    =============================
    Delete Signed-In User's Photo
    =============================
     */
    public void deletePhoto() {

        // Obtain the signed-in user's username
        String usernameOfSignedInUser = (String) FacesContext.getCurrentInstance()
                .getExternalContext().getSessionMap().get("username");

        // Obtain the object reference of the signed-in user
        User signedInUser = getUserFacade().findByUsername(usernameOfSignedInUser);

        // Obtain the id (primary key in the database) of the signedInUser object
        Integer userId = signedInUser.getId();

        /*
        Obtain the list of Photo file objects that belong to the signed-in user whose
        database primary key is userId. The list will contain only one photo or nothing.
         */
        List<UserPhoto> photoList = getUserPhotoFacade().findUserPhotosByUserID(userId);

        if (!photoList.isEmpty()) {

            // Obtain the object reference of the first Photo object in the list
            UserPhoto photo = photoList.get(0);

            try {
                // Delete the photo file from CloudStorage/PhotoStorage
                Files.deleteIfExists(Paths.get(photo.getPhotoFilePath()));

                // Delete the thumbnail file from CloudStorage/PhotoStorage
                Files.deleteIfExists(Paths.get(photo.getThumbnailFilePath()));

                // Delete the temporary file from CloudStorage/PhotoStorage
                Files.deleteIfExists(Paths.get(photo.getTemporaryFilePath()));

                // Delete the photo file record from the TakeNotesDB database
                getUserPhotoFacade().remove(photo);
                // UserPhotoFacade inherits the remove() method from AbstractFacade

            } catch (IOException e) {
                resultMsg = new FacesMessage("Something went wrong while deleting the photo file! See: " + e.getMessage());
                FacesContext.getCurrentInstance().addMessage(null, resultMsg);
            }
        }

    }
}
