package com.team6.jsfclasses;
import com.itextpdf.text.Document;
import com.itextpdf.text.html.simpleparser.HTMLWorker;
import com.itextpdf.text.pdf.PdfWriter;
import com.team6.entityclasses.Notes;
import com.team6.entityclasses.User;
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
    
    private List<Notes> items = null;
    private List<Notes> searchItems = null;
    private Notes selected;
    private Notes editorSelected;
    private String searchString;
    private String searchField;
    private User toShareWith;
    private String pdfPath;

    
    private boolean isInitialized = false;

    public NotesController() {
        items = new LinkedList<>();
    }

    public void getPDF() {

//        File folder = new File(Constants.FILES_ABSOLUTE_PATH);
//        File[] files = folder.listFiles();
//
//        if (files != null) {
//            for (File f : files) {
//                
//                String fileName = f.getName();
//                
//                if (fileName.substring(fileName.length() - 5).equals(".pdf"))
//                {
//                    f.delete();
//                }
//            }
//        }

        if (selected != null) {
            pdfPath = Constants.FILES_ABSOLUTE_PATH + selected.getTitle() + ".pdf";

            try {
                
//                PdfDocument pdf = new PdfDocument(new PdfWriter(pdfPath));
//                Document document = new Document(pdf);
//                document.add(new Paragraph("Hello World"));
//                document.close();
                
//                PdfWriter writer = new PdfWriter(pdfPath, new FileOutputStream(pdfPath));
//                PdfDocument pdf = new PdfDocument(writer);
//                Document document = new Document(pdf);
//                document.add(new Paragraph(selected.getContent()));                
//                document.close();
                
                Document document = new Document();
                PdfWriter.getInstance(document, new FileOutputStream(pdfPath));
                document.open();

                document.addTitle(selected.getTitle());
                document.addSubject(selected.getDescription());
                document.addKeywords(selected.getDescription());
                document.addAuthor(selected.getUserId().getFirstName() + " " + selected.getUserId().getLastName());
                document.addCreator(selected.getUserId().getFirstName() + " " + selected.getUserId().getLastName());
                document.addCreationDate();

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
        System.out.println("prepare create !!!!!!!!!!!=========");


        return editorSelected;
    }
    
    public Notes prepareEdit() {
        
        editorSelected = getNotesFacade().findById(selected.getId());
        userController.addActivity("Edit Activity");

        return editorSelected;
    }

    public void create() {
        System.out.println("createInNoteController");

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
        userController.addActivity("Delete Activity");
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
        if (user != null)
        {
            int user_id = user.getId();
            items = getFacade().findNotesByUserId(user_id);
            return items;
        }
        
        return new LinkedList<Notes>();
    }
    
    public void shareNote() {
        userController.addActivity("Share Activity");
        String user_name = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("username");
        User currUser = getUserFacade().findByUsername(user_name);
        notificationManager.sendNotificationToUser(this.toShareWith.getEmail(),"Someone shared you a note", currUser.getUsername()+" shared you a note \" " + selected.getTitle() + "\" on " + 
                        "jupiter.cs.vt.edu/TakeNote" + " !"
                );
        Notes exists = getNotesFacade().findSharedByUserIdAndTitle(toShareWith.getId(), selected.getTitle());
        
        if (exists == null)
        {
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
        }
        else
        {
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

    public void initialize() throws IOException {
        System.out.println("initialize editor");

        //prepareCreate();
        String user_name = (String) FacesContext.getCurrentInstance()
                .getExternalContext().getSessionMap().get("username");

        int user_id = (int) FacesContext.getCurrentInstance()
                .getExternalContext().getSessionMap().get("user_id");
        System.out.println(user_id);

        Notes existNote = getNotesFacade().findByUserIdAndTitle(user_id, editorSelected.getTitle());
        if (existNote != null) {
            System.out.println("Same name");
            System.out.println("existNote.getContent()="+existNote.getContent());
            editorSelected = existNote;
        } else {
            User user = getUserFacade().findByUsername(user_name);
            editorSelected.setUserId(user);
            if(editorSelected.getContent() == null)
            {
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

    public void save() {
        System.out.println("save In Notes Controller");

//        String id = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
//                .get("id");
//        selected.setId(Integer.parseInt(id));
//        int note_id = (Integer) FacesContext.getCurrentInstance()
//                .getExternalContext().getSessionMap().get("note_id");
//        User user = getUserFacade().find(user_id);
        //editorSelected.setTitle("test");
//        selected.setUserId(user);
//        String user_name = (String) FacesContext.getCurrentInstance()
//                .getExternalContext().getSessionMap().get("username");

        int user_id = (int) FacesContext.getCurrentInstance()
                .getExternalContext().getSessionMap().get("user_id");
        Notes existNote = getNotesFacade().findByUserIdAndTitle(user_id, editorSelected.getTitle());
        editorSelected = existNote;
        Date currDate = new Date();
//        currDate.getTime();
//        selected.setCreatedTime(currDate);
        editorSelected.setModifiedTime(currDate);
        editorSelected.setContent(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
                .get("content"));
        
        update();
        
        List<Notes> shared = getNotesFacade().findAllSharedNotes(existNote.getUserId().getId(), selected.getTitle());
        
        if (shared != null)
        {
            for (Notes thisNote : shared)
            {
                thisNote.setContent(existNote.getContent());
                
                editorSelected = thisNote;
                
                update();
            }                       
        }
        //System.out.print(content);
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
    
    public String getPDFPath()
    {
        return pdfPath;
    }

    public boolean checkSameUser() {
        
        String user_name = (String) FacesContext.getCurrentInstance()
                .getExternalContext().getSessionMap().get("username");
        
        User user = getUserFacade().findByUsername(user_name);
        
        return (selected != null) && (user != null) && selected.getUserId().equals(user);
    }
}
