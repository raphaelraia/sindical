package br.com.rtools.utilitarios;

import java.io.Serializable;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

@ManagedBean
@ViewScoped
public class GenericaMensagem implements Serializable {

    public static void error(String title, String description) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, title, description));
    }

    public static void fatal(String title, String description) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, title, description));
    }

    public static void info(String title, String description) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, title, description));
    }

    public static void warn(String title, String description) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, title, description));
    }

    public void show(String severity, String title, String description) {
        switch (severity) {
            case "error":
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, title, description));
                break;
            case "fatal":
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, title, description));
                break;
            case "info":
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, title, description));
                break;
            case "warn":
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, title, description));
                break;
        }
    }

    public String getHtmlError(String title, String description) {
        return getHTML("error", title, description, "", true);
    }

    public String getHtmlFatal(String title, String description) {
        return getHTML("fatal", title, description, "", true);
    }

    public String getHtmlInfo(String title, String description) {
        return getHTML("info", title, description, "", true);
    }

    public String getHtmlWarn(String title, String description) {
        return getHTML("warn", title, description, "", true);
    }

    public String getHtmlSuccess(String title, String description) {
        return getHTML("success", title, description, "", true);
    }

    public String getHtmlErrorStyle(String title, String description, String style) {
        return getHTML("error", title, description, style, true);
    }

    public String getHtmlFatalStyle(String title, String description, String style) {
        return getHTML("fatal", title, description, style, true);
    }

    public String getHtmlInfoStyle(String title, String description, String style) {
        return getHTML("info", title, description, style, true);
    }

    public String getHtmlWarnStyle(String title, String description, String style) {
        return getHTML("warn", title, description, style, true);
    }

    public String getHtmlSuccessStyle(String title, String description, String style) {
        return getHTML("success", title, description, style, true);
    }

    public String getHtmlErrorNoIcon(String title, String description) {
        return getHTML("error", title, description, "", false);
    }

    public String getHtmlFatalNoIcon(String title, String description) {
        return getHTML("fatal", title, description, "", false);
    }

    public String getHtmlInfoNoIcon(String title, String description) {
        return getHTML("info", title, description, "", false);
    }

    public String getHtmlWarnNoIcon(String title, String description) {
        return getHTML("warn", title, description, "", false);
    }

    public String getHtmlSuccessNoIcon(String title, String description) {
        return getHTML("success", title, description, "", false);
    }

    public String getHTML(String severity, String title, String description, String style, Boolean icon) {
        if (!title.isEmpty() || !severity.isEmpty()) {
            if (!style.isEmpty()) {
                style = " style=\"" + style + "\"";
            }
            String html = " "
                    + "<div class=\"ui-messages ui-widget margin-top\" aria-live=\"polite\">    "
                    + "     <div class=\"ui-messages-" + severity + " ui-corner-all\" " + style + ">          ";
            if (icon) {
                html += " <span class=\"ui-messages-" + severity + "-icon\"></span> ";
            }
            if (!icon) {
                html += " <ul>  "
                        + "<li> ";
            }
            if (!title.isEmpty()) {
                html += ""
                        + " <span class=\"ui-messages-" + severity + "-summary\" style=\" \">" + title + "</span> ";
            }
            if (!description.isEmpty()) {
                html += " <span class=\"ui-messages-" + severity + "-detail \" >" + description + "</span> ";

            }
            if (!icon) {
                html += "   </li>   "
                        + " </ul>   ";
            }
            html += " </div>  "
                    + " </div>  ";
            return html;
        }
        return "";
    }

    public String getHtmlMessage(String severity, String message) {
        return getHtmlMessage(severity, message, "", false);
    }

    public String getHtmlMessage(String severity, String message, Boolean blink) {
        return getHtmlMessage(severity, message, "", false);
    }
    
    public String getHtmlMessage(String severity, String title, String description) {
        return getHtmlMessage(severity, title, description, false);        
    }
    
    public String getHtmlMessage(String severity, String title, String description, Boolean blink) {
        String blinkString = "";
        switch (severity) {
            case "info":
                severity = "ui-rt-messages-info";
                break;
            case "warn":
                severity = "ui-rt-messages-warn";
                break;
            case "fatal":
                severity = "ui-rt-messages-fatal";
                break;
            case "error":
                severity = "ui-rt-messages-error";
                break;
            case "success":
                severity = "ui-rt-messages-success";
                break;
            case "alert":
                severity = "ui-rt-messages-alert";
                break;
            case "block":
                severity = "ui-rt-messages-block";
                break;
            case "tag":
                severity = "ui-rt-messages-tag";
                break;
            default:
                severity = "ui-rt-messages-info";
                break;
        }
        if (blink != null && blink) {
            blinkString = "ui-rt-blink";
        }
        String html = "";
        html = ""
                + "<div class=\"ui-rt-messages " + severity + "\" >\n"
                + " <p class=\"ui-rt-messages-p " + blinkString + "\">" + title +  " <span class=\"ui-rt-messages-span\"> " + description + " </span></p> "
                + " </div> ";
        return html;
    }
}
