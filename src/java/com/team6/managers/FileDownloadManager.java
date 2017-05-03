/*
 * Created by Thomas Nguyen on 2017.04.19  * 
 * Copyright © 2017 Thomas Nguyen. All rights reserved. * 
 */
package com.team6.managers;

// UserFile class's instance methods are accessed
import com.team6.entityclasses.UserFile;
import com.team6.jsfclasses.NotesController;

// These two are needed for CDI @Inject injection
import com.team6.jsfclasses.UserFileController;
import javax.inject.Inject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Serializable;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.ServletContext;

// These two are needed for PrimeFaces file download
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

@Named(value = "fileDownloadManager")
@RequestScoped


public class FileDownloadManager implements Serializable {

    @Inject
    private UserFileController userFileController;
    
    @Inject
    private NotesController noteController;

    /*
    StreamedContent and DefaultStreamedContent classes are imported from
    org.primefaces.model.StreamedContent above.
     */
    private StreamedContent file;
    
    private StreamedContent pdf;

    /*
    ==================
    Constructor Method
    ==================
     */
    public FileDownloadManager() {
    }

    /*
    =============
    Getter Method
    =============
     */
    public StreamedContent getFile(UserFile fileToDownload) throws FileNotFoundException {

        //UserFile fileToDownload = userFileController.getSelected();

        String nameOfFileToDownload = fileToDownload.getFilename();
        String absolutePathOfFileToDownload = fileToDownload.getFilePath();
        String contentMimeType = FacesContext.getCurrentInstance().getExternalContext().getMimeType(absolutePathOfFileToDownload);

        /*
        System.out.println prints under the GlassFish tab of the Output window.
        Print out intermediate values to effectively debug your application logic.
        
        System.out.println("*** Name of the file to download = " + nameOfFileToDownload + " ***\n");
        System.out.println("*** Path of the file to download = " + absolutePathOfFileToDownload + " ***\n");
        System.out.println("*** MIME Type of the file to download = " + contentMimeType + " ***");
         */
        InputStream stream = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().
                getContext()).getResourceAsStream(absolutePathOfFileToDownload);

        // Obtain the filename without the prefix "userId_" inserted to associate the file to a user
        String downloadedFileName = nameOfFileToDownload.substring(nameOfFileToDownload.indexOf("_") + 1);

        // FileInputStream must be used here since our files are stored in a directory external to our application
        file = new DefaultStreamedContent(new FileInputStream(absolutePathOfFileToDownload), contentMimeType, downloadedFileName);

        return file;
    }
    
    public StreamedContent getpdf() throws FileNotFoundException {

        if (noteController.getSelected() != null)
        {
            String nameOfFileToDownload = noteController.getSelected().getTitle() + ".pdf";
            String absolutePathOfFileToDownload = Constants.FILES_ABSOLUTE_PATH + nameOfFileToDownload;
            String contentMimeType = FacesContext.getCurrentInstance().getExternalContext().getMimeType(absolutePathOfFileToDownload);

            /*
            System.out.println prints under the GlassFish tab of the Output window.
            Print out intermediate values to effectively debug your application logic.

            System.out.println("*** Name of the file to download = " + nameOfFileToDownload + " ***\n");
            System.out.println("*** Path of the file to download = " + absolutePathOfFileToDownload + " ***\n");
            System.out.println("*** MIME Type of the file to download = " + contentMimeType + " ***");
             */
            InputStream stream = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().
                    getContext()).getResourceAsStream(absolutePathOfFileToDownload);

            // Obtain the filename without the prefix "userId_" inserted to associate the file to a user
            String downloadedFileName = nameOfFileToDownload.substring(nameOfFileToDownload.indexOf("_") + 1);

            // FileInputStream must be used here since our files are stored in a directory external to our application
            pdf = new DefaultStreamedContent(new FileInputStream(absolutePathOfFileToDownload), contentMimeType, downloadedFileName);

            return pdf;
        }
        
        return null;
    }

}
