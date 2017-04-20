package com.team6.jsfclasses;

import com.team6.entityclasses.Notes;
import com.team6.jsfclasses.util.JsfUtil;
import com.team6.jsfclasses.util.JsfUtil.PersistAction;
import com.team6.sessionbeans.NotesFacade;

import java.io.Serializable;
import java.util.Date;
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

@Named("notesController")
@SessionScoped
public class NotesController implements Serializable {

    @EJB
    private com.team6.sessionbeans.NotesFacade ejbFacade;
    private List<Notes> items = null;
    private Notes selected;

    public NotesController() {
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
        return ejbFacade;
    }

    public Notes prepareCreate() {
        selected = new Notes();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        System.out.println("aaaaaaaaa");
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("NotesCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("NotesUpdated"));
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

    public void getParams() {

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

}
