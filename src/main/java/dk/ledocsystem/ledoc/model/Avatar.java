package dk.ledocsystem.ledoc.model;

import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Transient;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Embeddable
@NoArgsConstructor
public class Avatar {

    @Column(name = "avatar")
    private byte[] avatar;

    @Transient
    private String avatarBase64;

    public String getAvatar() {
        if (avatar != null) {
            byte[] decodedString = Base64.getDecoder().decode(new String(avatar).getBytes(StandardCharsets.UTF_8));
            avatarBase64 = new String(decodedString);
        }
        return avatarBase64;
    }

    public void setAvatar(String avatar) {
        this.avatar = Base64.getEncoder().encode(avatar.getBytes());
    }
}
