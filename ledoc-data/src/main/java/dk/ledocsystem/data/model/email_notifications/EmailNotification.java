package dk.ledocsystem.data.model.email_notifications;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@ToString
@Entity
@Table(name = "email_notifications")
@DynamicInsert
@DynamicUpdate
@TypeDef(name = "JsonDataUserType", typeClass = JsonDataUserType.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmailNotification {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "email_notifications_seq")
    @SequenceGenerator(name = "email_notifications_seq", sequenceName = "email_notifications_seq")
    private Long id;

    @Column(nullable = false)
    private String recipient;

    @Column(nullable = false, length = 100)
    private String emailKey;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EmailNotificationStatus status = EmailNotificationStatus.NEW;

    @ColumnDefault("0")
    @Column(nullable = false)
    private Short retries;

    @Column(nullable = false)
    @Type(type = "JsonDataUserType")
    private Map<String, Object> model;

    /**
     * @param recipient Destination email address
     * @param emailKey  Name of the template and key recipient lookup corresponding email subject
     */
    public EmailNotification(@NonNull String recipient, @NonNull String emailKey) {
        this(recipient, emailKey, new HashMap<>());
    }

    /**
     * @param recipient Destination email address
     * @param emailKey  Name of the template and key recipient lookup corresponding email subject
     * @param model     Key-value pairs representing attributes and values recipient be inserted into template
     */
    public EmailNotification(@NonNull String recipient, @NonNull String emailKey, @NonNull Map<String, Object> model) {
        this.recipient = recipient;
        this.emailKey = emailKey;
        this.model = model;
    }

}
