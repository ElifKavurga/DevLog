package bean;

import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;

import java.io.Serializable;

@Named("panelYolBean")
@RequestScoped
public class PanelYolBean implements Serializable {

    public String getKisaYol() {
        FacesContext ctx = FacesContext.getCurrentInstance();
        if (ctx == null || ctx.getViewRoot() == null) {
            return "~/panel";
        }
        String v = ctx.getViewRoot().getViewId();
        if (v == null || v.isBlank()) {
            return "~/panel";
        }
        String s = v.replace(".xhtml", "");
        if (s.startsWith("/")) {
            s = s.substring(1);
        }
        return "~/" + s;
    }
}
