package com.team6.jsfclasses;

import com.team6.entityclasses.Notes;
import com.team6.entityclasses.User;
import com.team6.entityclasses.UserFile;
import com.team6.jsfclasses.util.JsfUtil;
import com.team6.jsfclasses.util.JsfUtil.PersistAction;
import com.team6.managers.Constants;
import com.team6.sessionbeans.NotesFacade;
import com.team6.sessionbeans.UserFacade;
import com.team6.sessionbeans.UserFileFacade;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;

@Named("userFileController")
@SessionScoped
public class UserFileController implements Serializable {

    /*
    ===============================
    Instance Variables (Properties)
    ===============================

    The instance variable 'userFacade' is annotated with the @EJB annotation.
    The @EJB annotation directs the EJB Container (of the GlassFish AS) to inject (store) the object reference
    of the UserFacade object, after it is instantiated at runtime, into the instance variable 'userFacade'.
     */
    @EJB
    private UserFacade userFacade;

    @EJB
    private NotesFacade notesFacade;

    @Inject
    private NotesController notesController;
    /*
    The instance variable 'userFileFacade' is annotated with the @EJB annotation.
    The @EJB annotation directs the EJB Container (of the GlassFish AS) to inject (store) the object reference 
    of the UserFileFacade object, after it is instantiated at runtime, into the instance variable 'userFileFacade'.
     */
    @EJB
    private com.team6.sessionbeans.UserFileFacade userFileFacade;
    private List<UserFile> items = null;
    private UserFile selected;

    /*
    cleanedFileNameHashMap<KEY, VALUE>
        KEY   = Integer fileId
        VALUE = String cleanedFileNameForSelected
     */
    HashMap<Integer, String> cleanedFileNameHashMap = null;

    // Message to show when file type cannot be processed
    private String fileTypeMessage = "";

    // Selected row number in p:dataTable in UserFiles.xhtml
    private String selectedRowNumber = "0";

    public UserFileController() {
    }

    /*
    =========================
    Getter and Setter Methods
    =========================
     */
    public UserFacade getUserFacade() {
        return userFacade;
    }

    public UserFileFacade getUserFileFacade() {
        return userFileFacade;
    }

    public UserFile getSelected() {
        return selected;
    }

    public void setSelected(UserFile selected) {
        this.selected = selected;
    }

    public String getSelectedRowNumber() {
        return selectedRowNumber;
    }

    public void setSelectedRowNumber(String selectedRowNumber) {
        this.selectedRowNumber = selectedRowNumber;
    }

    public String getFileTypeMessage() {
        return fileTypeMessage;
    }

    public void setFileTypeMessage(String fileTypeMessage) {
        this.fileTypeMessage = fileTypeMessage;
    }

    public NotesFacade getNotesFacade() {
        return notesFacade;
    }

    public void setNotesFacade(NotesFacade notesFacade) {
        this.notesFacade = notesFacade;
    }

    public NotesController getNotesController() {
        return notesController;
    }

    public void setNotesController(NotesController notesController) {
        this.notesController = notesController;
    }

    public HashMap<Integer, String> getCleanedFileNameHashMap() {
        return cleanedFileNameHashMap;
    }

    public void setCleanedFileNameHashMap(HashMap<Integer, String> cleanedFileNameHashMap) {
        this.cleanedFileNameHashMap = cleanedFileNameHashMap;
    }

    public void downloadAttachment(UserFile currItem) {
        System.out.println("download Attachment" + currItem.getFilename());
    }

    public List<UserFile> getItems() {
        System.out.println("get Items In User File Controller");
        items = null;
//        if (!getNotesController().isIsInitialized()) {
//            System.out.println("Get attachment before editor initialize");
//            return items;
//        }
//        if (items == null) {

        // Obtain the signed-in user's username
        String usernameOfSignedInUser = (String) FacesContext.getCurrentInstance()
                .getExternalContext().getSessionMap().get("username");

        // Obtain the object reference of the signed-in user
        User signedInUser = getUserFacade().findByUsername(usernameOfSignedInUser);
//            String noteTitle = (String) FacesContext.getCurrentInstance()
//                        .getExternalContext().getSessionMap().get("title");
        if (notesController.getEditorSelected() == null) {
            return null;
        }
        String noteTitle = notesController.getEditorSelected().getTitle();

        Notes note = getNotesFacade().findByUserIdAndTitle(signedInUser.getId(), noteTitle);

        if (note == null) {
            return null;
        }
        // Obtain the id (primary key in the database) of the signedInUser object
        Integer userId = signedInUser.getId();
        Integer noteId = note.getId();
        // Obtain only those files from the database that belong to the signed-in user
        items = getUserFileFacade().findUserFilesByNoteId(noteId);

        // Instantiate a new hash map object
        cleanedFileNameHashMap = new HashMap<>();

        /*
            cleanedFileNameHashMap<KEY, VALUE>
                KEY   = Integer fileId
                VALUE = String cleanedFileNameForSelected
         */
        for (int i = 0; i < items.size(); i++) {

            // Obtain the filename stored in CloudStorage/FileStorage as 'userId_filename'
            String storedFileName = items.get(i).getFilename();

            // Remove the "userId_" (e.g., "4_") prefix in the stored filename
            String cleanedFileName = storedFileName.substring(storedFileName.indexOf("_") + 1);

            // Obtain the file id
            Integer fileId = items.get(i).getId();

            // Create an entry in the hash map as a key-value pair
            cleanedFileNameHashMap.put(fileId, cleanedFileName);
        }
//        }

        return items;
    }

    public Collection<UserFile> getSharedItems() {
        System.out.println("get Items In User File Controller");
        items = null;
//        if (!getNotesController().isIsInitialized()) {
//            System.out.println("Get attachment before editor initialize");
//            return items;
//        }
//        if (items == null) {
        if (notesController.getSelected() == null) {
            return null;
        }
        // Obtain the signed-in user's username
        String username = (String) notesController.getSelected().getUserId().getUsername();
//
//        // Obtain the object reference of the signed-in user
//        User signedInUser = getUserFacade().getUser();

        String noteTitle = notesController.getSelected().getTitle();
//        String noteTitle = notesController.getEditorSelected().getTitle();
//
        Notes note = getNotesFacade().findByUserIdAndTitle(notesController.getSelected().getUserId().getId(), noteTitle);

        //    return notesController.getSelected().getUserFileCollection();
//        for (UserFile aFile : notesController.getSelected().getUserFileCollection()) {
//            items.add(aFile);
//        }
//        if (note == null) {
//            return null;
//        }
//        // Obtain the id (primary key in the database) of the signedInUser object
        Integer userId = note.getUserId().getId();
        Integer noteId = note.getId();
//
//        System.out.println("share note id" + note.getId() + "share note user=======================" + note.getUserId().getUsername());
//        // Obtain only those files from the database that belong to the signed-in user
        items = getUserFileFacade().findUserFilesByNoteId(noteId);
//
//        // Instantiate a new hash map object
        cleanedFileNameHashMap = new HashMap<>();

        /*
            cleanedFileNameHashMap<KEY, VALUE>
                KEY   = Integer fileId
                VALUE = String cleanedFileNameForSelected
         */
        for (int i = 0; i < items.size(); i++) {

            // Obtain the filename stored in CloudStorage/FileStorage as 'userId_filename'
            String storedFileName = items.get(i).getFilename();

            // Remove the "userId_" (e.g., "4_") prefix in the stored filename
            String cleanedFileName = storedFileName.substring(storedFileName.indexOf("_") + 1);

            // Obtain the file id
            Integer fileId = items.get(i).getId();

            // Create an entry in the hash map as a key-value pair
            cleanedFileNameHashMap.put(fileId, cleanedFileName);
        }

        return items;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private UserFileFacade getFacade() {
        return userFileFacade;
    }

    public UserFile prepareCreate() {
        selected = new UserFile();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("UserFileCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("UserFileUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("UserFileDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    private void persist(PersistAction persistAction, String successMessage) {
        if (selected != null) {
            setEmbeddableKeys();
            try {
                if (persistAction != PersistAction.DELETE) {
                    getUserFileFacade().edit(selected);
                } else {
                    getUserFileFacade().remove(selected);
                }
                JsfUtil.addSuccessMessage(successMessage);
            } catch (EJBException ex) {
                String msg = "";
                Throwable cause = ex.getCause();
                if (cause != null) {
                    msg = cause.getLocalizedMessage();
                }
                if (msg.length() > 0) {
                    JsfUtil.addErrorMessage(msg);
                } else {
                    JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
                }
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            }
        }
    }

    public UserFile getUserFile(java.lang.Integer id) {
        return getFacade().find(id);
    }

    public List<UserFile> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<UserFile> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = UserFile.class)
    public static class UserFileControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            UserFileController controller = (UserFileController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "userFileController");
            return controller.getUserFile(getKey(value));
        }

        java.lang.Integer getKey(String value) {
            java.lang.Integer key;
            key = Integer.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Integer value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof UserFile) {
                UserFile o = (UserFile) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), UserFile.class.getName()});
                return null;
            }
        }

    }

    /*
    =========================
    Delete Selected User File
    =========================
     */
    public String deleteSelectedUserFile(UserFile item) {

        UserFile userFileToDelete = item;

        FacesMessage resultMsg;

        // This sets the necessary flag to ensure the messages are preserved.
        FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);

        if (userFileToDelete == null) {
            resultMsg = new FacesMessage("You do not have a file to delete!");
        } else {
            try {
                // Delete the file from CloudStorage/FileStorage
                Files.deleteIfExists(Paths.get(userFileToDelete.getFilePath()));

                // Delete the user file record from the CloudDriveDB database
                getUserFileFacade().remove(userFileToDelete);
                // UserFileFacade inherits the remove() method from AbstractFacade

                resultMsg = new FacesMessage("Selected file is successfully deleted!");

                // See method below
                refreshFileList();

            } catch (IOException e) {
                resultMsg = new FacesMessage("Something went wrong while deleting the user file! See: " + e.getMessage());
                FacesContext.getCurrentInstance().addMessage(null, resultMsg);
            }
        }

        FacesContext.getCurrentInstance().addMessage(null, resultMsg);

        Notes selected = notesController.getEditorSelected();
        Collection<UserFile> attachments = selected.getUserFileCollection();
        attachments.remove(item);
        selected.setUserFileCollection(attachments);
        notesController.setEditorSelected(selected);

        notesController.update();

        return "UserFiles?faces-redirect=true";
    }

    /*
    ========================
    Refresh User's File List
    ========================
     */
    public void refreshFileList() {
        /*
        By setting the items to null, we force the getItems
        method above to retrieve all of the user's files again.
         */
        items = null;
    }

    /*
    ===============================
    Return Image File given File Id
    ===============================
     */
    /**
     *
     * @param fileId database primary key value for a user file
     * @return the file if it is an image file; otherwise return a blank image
     */
    public String imageFile(Integer fileId) {

        // Obtain the object reference of the UserFile whose primary key = fileId
        UserFile userFile = getUserFileFacade().getUserFile(fileId);

        // Obtain the userFile's filename as it is stored in the CloudDrive DB database
        String userFileName = userFile.getFilename();

        // Extract the file extension from the filename
        String fileExtension = userFileName.substring(userFileName.lastIndexOf(".") + 1);

        // Convert file extension to uppercase
        String fileExtensionInCaps = fileExtension.toUpperCase();

        switch (fileExtensionInCaps) {
            case "JPG":
            case "JPEG":
            case "PNG":
            case "GIF":
                // File is an acceptable image type. Return the image file's relative path.
                return Constants.FILES_RELATIVE_PATH + userFileName;
            default:
                /*
                The file is not an image file. Return noPreviewImage.png, which is a
                blank transparent image of size 36x36 px, from the resources folder.
                 */
                return "resources/images/noPreviewImage.png";
        }
    }

    /*
    =====================================
    Return Cleaned Filename given File Id
    =====================================
     */
    public String cleanedFilenameForFileId(Integer fileId) {
        /*
        cleanedFileNameHashMap<KEY, VALUE>
            KEY   = Integer fileId
            VALUE = String cleanedFileNameForSelected
         */

        // Obtain the cleaned filename for the given fileId
        String cleanedFileName = cleanedFileNameHashMap.get(fileId);

        return cleanedFileName;
    }

    /*
    =========================================
    Return Cleaned Filename for Selected File
    =========================================
     */
    // This method is called from UserFiles.xhtml by passing the fileId as a parameter.
    public String cleanedFileNameForSelected() {

        Integer fileId = selected.getId();
        /*
        cleanedFileNameHashMap<KEY, VALUE>
            KEY   = Integer fileId
            VALUE = String cleanedFileNameForSelected
         */

        // Obtain the cleaned filename for the given fileId
        String cleanedFileName = cleanedFileNameHashMap.get(fileId);

        return cleanedFileName;
    }

    /*
    ====================================
    Return Selected File's Relative Path
    ====================================
     */
    public String selectedFileRelativePath() {
        return Constants.FILES_RELATIVE_PATH + selected.getFilename();
    }

    /*
    =============================================
    Return True if Selected File is an Image File
    =============================================
     */
    public boolean isImage() {

        fileTypeMessage = "";

        switch (extensionOfSelectedFileInCaps()) {
            case "JPG":
            case "JPEG":
            case "PNG":
            case "GIF":
                // Selected file is an image file
                return true;
            default:
                return false;
        }
    }

    /*
    ========================================
    Return True if Selected File is Viewable
    ========================================
     */
    public boolean isViewable() {

        switch (extensionOfSelectedFileInCaps()) {
            case "CSS":
            case "CSV":
            case "HTML":
            case "JAVA":
            case "PDF":
            case "SQL":
            case "TXT":
                // Selected file is viewable
                fileTypeMessage = "";
                return true;
            default:
                fileTypeMessage = "Unable to display the selected file!";
                return false;
        }
    }

    /*
    =================================================
    Return True if Selected File is an MP4 Video File
    =================================================
     */
    public boolean isMP4Video() {

        return extensionOfSelectedFileInCaps().equals("MP4");
    }

    /*
    ========================================================
    Return Extension of the Selected File in Capital Letters
    ========================================================
     */
    public String extensionOfSelectedFileInCaps() {

        // Obtain the selected filename
        String userFileName = selected.getFilename();

        // Extract the file extension from the filename
        String fileExtension = userFileName.substring(userFileName.lastIndexOf(".") + 1);

        // Convert file extension to be in capital letters
        String fileExtensionInCaps = fileExtension.toUpperCase();

        return fileExtensionInCaps;
    }

}
