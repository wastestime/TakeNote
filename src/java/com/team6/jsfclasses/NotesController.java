package com.team6.jsfclasses;

import com.itextpdf.text.Document;
import com.itextpdf.text.html.simpleparser.HTMLWorker;
import com.itextpdf.text.pdf.PdfWriter;
import com.team6.entityclasses.Notes;
import com.team6.entityclasses.User;
import com.team6.jsfclasses.util.JsfUtil;
import com.team6.jsfclasses.util.JsfUtil.PersistAction;
import com.team6.managers.Constants;
import com.team6.sessionbeans.NotesFacade;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import java.io.Serializable;
import java.io.StringReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
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
    @Inject
    private UserFileController userFileController;
    
    @EJB
    private com.team6.sessionbeans.NotesFacade notesFacade;

    private List<Notes> items = null;
    private List<Notes> searchItems = null;
    private Notes selected;
    private String searchString;
    private String searchField;
    private int toShareWith;

    public NotesController() {
        
        items = new LinkedList<>();
        
        
        Date createdTime = new Date();
        Date modifiedTime = new Date();
        
        Notes note1 = new Notes( 1, "note1", "note1 description", createdTime, modifiedTime);
        Notes note2 = new Notes( 2, "note2", "note2 description", createdTime, modifiedTime);
        Notes note3 = new Notes( 3, "note3", "note3 description", createdTime, modifiedTime);
        Notes note4 = new Notes( 4, "note4", "note4 description", createdTime, modifiedTime);
        note1.setContent("Hello World!1");
        note2.setContent("Hello World!2");
        note3.setContent("Hello World!3");
        note4.setContent("Hello World!4");
        
        User user1 = new User(10, "yomn5", "abcd123", "xx", "Geoffrey", "Masters", "VA", 0, "dog");
        User user2 = new User(11, "yomn6", "abcd123", "xx", "Geoffrey", "Masters", "VA", 0, "dog");
        User user3 = new User(1, "yomn567", "abcd123", "xx", "Geoffrey", "Masters", "VA", 0, "dog");
        User user4 = new User(13, "yomn8", "abcd123", "xx", "Geoffrey", "Masters", "VA", 0, "dog");
        
        note1.setUserId(user1);
        note2.setUserId(user2);
        note3.setUserId(user3);
        note4.setUserId(user4);
        
        Collection<User> ucol = new LinkedList<>();
        ucol.add(user2);
        ucol.add(user3);
        ucol.add(user4);
        
        
        user1.setUserCollection(ucol);
        
        items.add(note1);
        items.add(note2);
        items.add(note3);
        items.add(note4);
        
    }
    
    public void getPDF() {
        
        File folder = new File(Constants.FILES_ABSOLUTE_PATH);
        File[] files = folder.listFiles();
        
        if (files != null)
        {
            for (File f : files)
            {
                f.delete();
            }
        }
        
        if (selected != null)
        {
            String FILE = Constants.FILES_ABSOLUTE_PATH + selected.getTitle() + ".pdf";

            try {

                Document document = new Document();
                PdfWriter.getInstance(document, new FileOutputStream(FILE));
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
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public Notes getSelected() {
        return selected;
    }

    public void setSelected(Notes selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private NotesFacade getFacade() {
        return notesFacade;
    }

    public Notes prepareCreate() {
        selected = new Notes();
        initializeEmbeddableKey();
        
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect("../Editor.xhtml");
        } catch (IOException ex) {
            Logger.getLogger(NotesController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return selected;
    }

    public void create() {
        System.out.println("bbb");
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("NotesCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("NotesUpdated"));
    }

    public String getCurrentNote() {
        if (selected == null) {
            return "Please Start typing!";
        }
        return selected.getContent();
    }
     public String getCurrentTitle() {
        if (selected == null) {
            return "==title==";
        }
        return selected.getTitle();
    }
     
     public String getCurrentDescription() {
          
         if (selected == null)
         {
             return "==description==";
         }
         
         return selected.getDescription();
     }
     
     public String getCurrentUsername() {
         
         if (selected == null)
         {
             return "==username==";
         }
         
         return selected.getUserId().getUsername();
     }
     
     public String getCurrentCreatedTime() {
         
         if (selected == null)
         {
             return "==createdTime==";
         }
         
         return selected.getCreatedTime().toString();
     }
     
     public String getCurrentModifiedTime() {
         
         if (selected == null)
         {
             return "==modifiedTime==";
         }
         
         return selected.getModifiedTime().toString();
     }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("NotesDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Notes> getItems() {
        if (items == null) {
            items = getFacade().findAll();
        }
        return items;
    }

    public void getSavedContent() {
        System.out.println("aaaa1");
        prepareCreate();
//        String id = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
//                .get("id");
//        selected.setId(Integer.parseInt(id));
        selected.setTitle("test");
        Date currDate = new Date();
//        currDate.getTime();
        selected.setCreatedTime(currDate);
        selected.setModifiedTime(currDate);
        selected.setContent(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
                .get("content"));
        create();
        //System.out.print(content);
    }

    private void persist(PersistAction persistAction, String successMessage) {
        if (selected != null) {
            setEmbeddableKeys();
            try {
                if (persistAction != PersistAction.DELETE) {
                    getFacade().edit(selected);
                } else {
                    getFacade().remove(selected);
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
        switch (searchField) {
            case "title":
               
                return getNotesFacade().titleQuery(searchString);
            case "description":

                return getNotesFacade().descriptionQuery(searchString);
            case "username":
                
                return getNotesFacade().usernameQuery(searchString);
            default:

                return getNotesFacade().allQuery(searchString);
        }
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

    public int getToShareWith() {
        return toShareWith;
    }

    public void setToShareWith(int toShareWith) {
        this.toShareWith = toShareWith;
    }

    
    
}
