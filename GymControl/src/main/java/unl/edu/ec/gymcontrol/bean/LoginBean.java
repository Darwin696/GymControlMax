package unl.edu.ec.gymcontrol.bean;

import java.io.Serializable;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;

@Named("loginBean")
@SessionScoped
public class LoginBean implements Serializable {
    private static final long serialVersionUID = 1L;
    private String email;
    private String password;
    private boolean loggedIn;

    public String login() {
        if (("admin@apexfitness.com".equalsIgnoreCase(email) || "admin".equalsIgnoreCase(email))
            && "admin123".equals(password)) {
            loggedIn = true;
            return "inicio?faces-redirect=true";
        }
        FacesContext.getCurrentInstance().addMessage(null,
            new FacesMessage(FacesMessage.SEVERITY_ERROR, "Credenciales incorrectas", null));
        return null;
    }

    public String logout() {
        loggedIn = false;
        email = null;
        password = null;
        return "login?faces-redirect=true";
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public boolean isLoggedIn() { return loggedIn; }
}
