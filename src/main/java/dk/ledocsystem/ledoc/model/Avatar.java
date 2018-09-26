package dk.ledocsystem.ledoc.model;

import lombok.NoArgsConstructor;
import org.pmw.tinylog.Logger;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Transient;
import java.io.UnsupportedEncodingException;
import java.util.Base64;

@Embeddable
@NoArgsConstructor
public class Avatar {

    @Column(name = "avatar")
    private byte[] avatar;

    @Transient
    private String avatarBase64;

    public String getAvatar() {
        byte[] decodedString = new byte[0];
        try {
            if (avatar != null) {
                decodedString = Base64.getDecoder().decode(new String(avatar).getBytes("UTF-8"));
            }
        } catch (UnsupportedEncodingException e) {
            Logger.error(e);
        }
        avatarBase64 = new String(decodedString);
        return avatarBase64;
    }

    public void setAvatar(String avatar) {
        this.avatar = Base64.getEncoder().encode(avatar.getBytes());
    }
}
