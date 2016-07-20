package br.com.rtools.webservice;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Entity
@Table(name = "ws_captcha")
public class WSCaptcha implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "captcha_image")
    private byte[] captchaImage;
    @Column(name = "captcha_text", length = 50)
    private String captchaText;
    @Column(name = "session_id")
    private String sessionId;
    @Column(name = "document", length = 50)
    private String document;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "expires")
    private Date expires;
    @Column(name = "type", length = 50)
    private String type;
    @Column(name = "result", length = 10000)
    private String result;

    public WSCaptcha() {
        this.id = null;
        this.captchaImage = null;
        this.captchaText = null;
        this.sessionId = null;
        this.document = null;
        this.expires = null;
        this.type = null;
        this.result = null;
    }

    public WSCaptcha(Integer id, byte[] captchaImage, String captchaText, String sessionId, String document, Date expires, String type, String result) {
        this.id = id;
        this.captchaImage = captchaImage;
        this.captchaText = captchaText;
        this.sessionId = sessionId;
        this.document = document;
        this.expires = expires;
        this.type = type;
        this.result = result;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public byte[] getCaptchaImage() {
        return captchaImage;
    }

    public void setCaptchaImage(byte[] captchaImage) {
        this.captchaImage = captchaImage;
    }

    public String getCaptchaText() {
        return captchaText;
    }

    public void setCaptchaText(String captchaText) {
        this.captchaText = captchaText;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getDocument() {
        return document;
    }

    public void setDocument(String document) {
        this.document = document;
    }

    public Date getExpires() {
        return expires;
    }

    public void setExpires(Date expires) {
        this.expires = expires;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

}
