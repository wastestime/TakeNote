package com.team6.jsfclasses;

import com.itextpdf.text.Document;
import com.itextpdf.text.html.simpleparser.HTMLWorker;
import com.itextpdf.text.pdf.PdfWriter;
import com.team6.entityclasses.Notes;
import com.team6.entityclasses.User;
import com.team6.entityclasses.UserFile;
import com.team6.jsfclasses.util.JsfUtil;
import com.team6.jsfclasses.util.JsfUtil.PersistAction;
import com.team6.managers.Constants;
import com.team6.managers.NotificationManager;
import com.team6.sessionbeans.NotesFacade;
import com.team6.sessionbeans.UserFacade;
import com.team6.sessionbeans.UserFileFacade;
import java.io.FileOutputStream;
import java.io.IOException;

import java.io.Serializable;
import java.io.StringReader;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;

@Named("notesController")
@SessionScoped
public class NotesController implements Serializable {

    /*
    Using the @Inject annotation, the compiler is directed to store the object reference of the
    UserFileController CDI-named bean into the instance variable userFileController at runtime.
    With this injection, the instance variables and instance methods of the UserFileController
    class can be accessed in this CDI-named bean. The following imports are required for the injection:
    
        import com.mycompany.jsfclasses.UserFileController;
        import javax.inject.Inject;
     */
    @EJB
    private UserFacade userFacade;

    @EJB
    private UserFileFacade userFileFacade;

    @EJB
    private com.team6.sessionbeans.NotesFacade notesFacade;

    @Inject
    private UserController userController;

    @Inject
    private NotificationManager notificationManager;

    public UserController getUserController() {
        return userController;
    }

    // The list of items corresponding to the current user.
    private List<Notes> items = null;
    
    // The list of items corresponding to the current user that meet the given search parameters.
    private List<Notes> searchItems = null;
    
    // The currently selected note.
    private Notes selected;
    
    // The note currently being edited by the Editor.
    private Notes editorSelected;
    
    // The String that is used to search through the items.
    private String searchString;
    
    // The specific field of the Notes class that the searchString correponds to.
    private String searchField;
    
    // The User object corresponding to the user that this note was shared with.
    private User toShareWith;
    
    // The path to the PDF file corresponding to the currently selected note.
    private String pdfPath;

    // A boolean value that is used to indicate whether or not the current note has been initialized.
    private boolean isInitialized = false;

    /**
     * The default constructor.
     */
    public NotesController() {
    }

    @PostConstruct
    public void init() {
        items = null;
    }

    /**
     * Generates a PDF file with the contents of the current note.
     */
    public void getPDF() {
        
        // Check if the user has selected a note.
        if (selected != null) {
            
            // Get the name and location of the file that we are going to create.
            pdfPath = Constants.FILES_ABSOLUTE_PATH + selected.getTitle() + ".pdf";

            try {
                
                // Create a new PDF and initialize the parameters to those of the current note.
                Document document = new Document();
                PdfWriter.getInstance(document, new FileOutputStream(pdfPath));
                document.open();

                document.addTitle(selected.getTitle());
                document.addSubject(selected.getDescription());
                document.addKeywords(selected.getDescription());
                document.addAuthor(selected.getUserId().getFirstName() + " " + selected.getUserId().getLastName());
                document.addCreator(selected.getUserId().getFirstName() + " " + selected.getUserId().getLastName());
                document.addCreationDate();

                // Convert the content of the current note from HTML to PDf.
                HTMLWorker worker = new HTMLWorker(document);
                worker.parse(new StringReader(selected.getContent()));
                document.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public Notes getSelected() {
        return selected;
    }

    public void setSelected(Notes selected) {
        this.selected = selected;

        getPDF();
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private NotesFacade getFacade() {
        return notesFacade;
    }

    public Notes prepareCreate() {
        editorSelected = new Notes();
        initializeEmbeddableKey();
        return editorSelected;
    }

    public Notes prepareEdit() {
        editorSelected = getNotesFacade().findById(selected.getId());
        userController.addActivity("Edit Note");

        return editorSelected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("NotesCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("NotesUpdated"));
    }

    public String getEditorNote() {
        String user_name = (String) FacesContext.getCurrentInstance()
                .getExternalContext().getSessionMap().get("username");

        int user_id = (int) FacesContext.getCurrentInstance()
                .getExternalContext().getSessionMap().get("user_id");
        System.out.println(user_id);

        Notes existNote = getNotesFacade().findByUserIdAndTitle(user_id, editorSelected.getTitle());
        if (existNote != null) {
            editorSelected = existNote;
        }
        if (editorSelected == null || editorSelected.getContent() == null) {
            return "Please Start typing!";
        }
        return editorSelected.getContent();
    }

    public String getCurrentTitle() {
        if (selected == null) {
            return "==title==";
        }
        return selected.getTitle();
    }

    public String getEditorTitle() {
        if (editorSelected == null) {
            return "==title==";
        }
        return editorSelected.getTitle();
    }

    public String getCurrentDescription() {

        if (selected == null) {
            return "==description==";
        }

        return selected.getDescription();
    }

    public String getCurrentNote() {
        if (selected == null || selected.getContent() == null) {
            return "Please Start typing!";
        }
        return selected.getContent();
    }

    public String getCurrentUsername() {

        if (selected == null) {
            return "==username==";
        }

        return selected.getUserId().getUsername();
    }

    public String getCurrentCreatedTime() {

        if (selected == null) {
            return "==createdTime==";
        }

        return selected.getCreatedTime().toString();
    }

    public String getCurrentModifiedTime() {

        if (selected == null) {
            return "==modifiedTime==";
        }

        return selected.getModifiedTime().toString();
    }

    public void destroy() {
        userController.addActivity("Delete Note");
        editorSelected = selected;
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("NotesDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            editorSelected = null;
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Notes> getItems() {

        String user_name = (String) FacesContext.getCurrentInstance()
                .getExternalContext().getSessionMap().get("username");

        User user = getUserFacade().findByUsername(user_name);
        if (user != null && items == null) {
            int user_id = user.getId();
            items = getFacade().findNotesByUserId(user_id);
            return items;
        }

        return items;
    }

    public void shareNote() {
        userController.addActivity("Share Note");
        String user_name = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("username");
        User currUser = getUserFacade().findByUsername(user_name);
        notificationManager.sendNotificationToUser(this.toShareWith.getEmail(), "Someone shared you a note", currUser.getUsername() + " shared you a note \" " + selected.getTitle() + " \" on "
                + "jupiter.cs.vt.edu/TakeNote" + " !"
        );
        Notes exists = getNotesFacade().findSharedByUserIdAndTitle(toShareWith.getId(), selected.getTitle());

        if (exists == null) {
            Notes newNote = new Notes();
            newNote.setTitle(selected.getTitle());
            newNote.setDescription(selected.getDescription());
            newNote.setCreatedTime(selected.getCreatedTime());
            newNote.setModifiedTime(selected.getModifiedTime());

            newNote.setUserFileCollection(selected.getUserFileCollection());
            newNote.setContent(selected.getContent());
            newNote.setUserId(selected.getUserId());
            newNote.setSharedWith(toShareWith);

            editorSelected = newNote;

            create();
        } else {
            exists.setContent(selected.getContent());
            editorSelected = exists;
            update();
        }

    }

    public UserFileFacade getUserFileFacade() {
        return userFileFacade;
    }

    public void setUserFileFacade(UserFileFacade userFileFacade) {
        this.userFileFacade = userFileFacade;
    }

    public UserFacade getUserFacade() {
        return userFacade;
    }

    /**
     * We create a new note and set up all the parameters for editorselected
     * when the editor does not open a existed note.
     *
     * @throws IOException
     */
    public void initialize() throws IOException {
        items = null;
        String user_name = (String) FacesContext.getCurrentInstance()
                .getExternalContext().getSessionMap().get("username");

        int user_id = (int) FacesContext.getCurrentInstance()
                .getExternalContext().getSessionMap().get("user_id");
        System.out.println(user_id);

        Notes existNote = getNotesFacade().findByUserIdAndTitle(user_id, editorSelected.getTitle());
        if (existNote != null) {
            editorSelected = existNote;
        } else {
            User user = getUserFacade().findByUsername(user_name);
            editorSelected.setUserId(user);
            if (editorSelected.getContent() == null) {
                editorSelected.setContent(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
                        .get("content"));
            }
            Date currDate = new Date();
            editorSelected.setCreatedTime(currDate);
            editorSelected.setModifiedTime(currDate);
            userController.addActivity("Create Note");

            notesFacade.create(editorSelected);
            isInitialized = true;
        }
        initializeSessionMap();
    }

    public Notes getEditorSelected() {
        return editorSelected;
    }

    public void setEditorSelected(Notes editorSelected) {
        this.editorSelected = editorSelected;
    }

    public int getNumAttatchments(Notes currNote) {

        if (currNote.getSharedWith() == null) {
            return currNote.getNumAttatchments();
        }

        List<UserFile> items = null;

        if (currNote == null) {
            return 0;
        }

        // Obtain the signed-in user's username
        String username = (String) currNote.getUserId().getUsername();
        String noteTitle = currNote.getTitle();
        Notes note = getNotesFacade().findByUserIdAndTitle(currNote.getUserId().getId(), noteTitle);
        // Obtain the id (primary key in the database) of the signedInUser object
        Integer userId = note.getUserId().getId();
        Integer noteId = note.getId();
        items = getUserFileFacade().findUserFilesByNoteId(noteId);

        return items.size();
    }

    /**
     * Update the note in the database and update the shared copy.
     *
     */
    public void save() {
        int user_id = (int) FacesContext.getCurrentInstance()
                .getExternalContext().getSessionMap().get("user_id");
        Notes existNote = getNotesFacade().findByUserIdAndTitle(user_id, editorSelected.getTitle());
        editorSelected = existNote;
        Date currDate = new Date();
        editorSelected.setModifiedTime(currDate);

//        The RequestParameter map will catch the Json object generated by Javascript and  parsed by <p:remoteCommand>.
        editorSelected.setContent(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
                .get("content"));
        update();
        List<Notes> shared = getNotesFacade().findAllSharedNotes(existNote.getUserId().getId(), editorSelected.getTitle());
        if (shared != null) {
            for (Notes thisNote : shared) {
                thisNote.setContent(existNote.getContent());
                editorSelected = thisNote;
                update();
            }
        }
    }

    public boolean isIsInitialized() {
        return isInitialized;
    }

    public void setIsInitialized(boolean isInitialized) {
        this.isInitialized = isInitialized;
    }

    private void persist(PersistAction persistAction, String successMessage) {
        if (editorSelected != null) {
            setEmbeddableKeys();
            try {
                if (persistAction != PersistAction.DELETE) {
                    getFacade().edit(editorSelected);
                } else {
                    getFacade().remove(editorSelected);
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

    public void initializeSessionMap() {

        String user_name = (String) FacesContext.getCurrentInstance()
                .getExternalContext().getSessionMap().get("username");

        int user_id = (int) FacesContext.getCurrentInstance()
                .getExternalContext().getSessionMap().get("user_id");

        Notes existNote = getNotesFacade().findByUserIdAndTitle(user_id, editorSelected.getTitle());

        // Put the User's object reference into session map variable user
        FacesContext.getCurrentInstance().getExternalContext().
                getSessionMap().put("title", existNote.getTitle());

        // Put the User's database primary key into session map variable user_id
        FacesContext.getCurrentInstance().getExternalContext().
                getSessionMap().put("note_id", existNote.getId());
    }

    public Notes getNotes(java.lang.Integer id) {
        return getFacade().find(id);
    }

    public List<Notes> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Notes> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    public List<Notes> getSearchItems() {

        int user_id = (int) FacesContext.getCurrentInstance()
                .getExternalContext().getSessionMap().get("user_id");

        searchItems = getNotesFacade().findNotesByUserId(user_id);

        switch (searchField) {
            case "title":

                searchItems = getNotesFacade().findNotesByUserIdAndTitle(user_id, searchString);
                break;
            case "description":

                searchItems = getNotesFacade().findNotesByUserIdAndDescription(user_id, searchString);
                break;
            case "username":

                searchItems = getNotesFacade().findNotesByUserIdAndOwner(user_id, searchString);
                break;
            default:

                searchItems = getNotesFacade().findNotesByUserId(user_id);
        }

        return searchItems;
    }

    public void search(ActionEvent actionEvent) throws IOException {
        FacesContext.getCurrentInstance().getExternalContext().redirect("SearchResults.xhtml");
    }

    @FacesConverter(forClass = Notes.class)
    public static class NotesControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            NotesController controller = (NotesController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "notesController");
            return controller.getNotes(getKey(value));
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
            if (object instanceof Notes) {
                Notes o = (Notes) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Notes.class.getName()});
                return null;
            }
        }

    }

    public NotesFacade getNotesFacade() {
        return notesFacade;
    }

    public void setNotesFacade(NotesFacade notesFacade) {
        this.notesFacade = notesFacade;
    }

    public String getSearchString() {
        return searchString;
    }

    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }

    public String getSearchField() {
        return searchField;
    }

    public void setSearchField(String searchField) {
        this.searchField = searchField;
    }

    public User getToShareWith() {
        return toShareWith;
    }

    public void setToShareWith(User toShareWith) {
        this.toShareWith = toShareWith;
    }

    public String getPDFPath() {
        return pdfPath;
    }

    public boolean checkSameUser() {

        String user_name = (String) FacesContext.getCurrentInstance()
                .getExternalContext().getSessionMap().get("username");

        User user = getUserFacade().findByUsername(user_name);

        return (selected != null) && (user != null) && selected.getUserId().equals(user);
    }

    public void refresh() {
        items = null;
        System.out.print("REFRESH ACTIVATED");
    }
}
