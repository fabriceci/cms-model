package be.fcip.cms.persistence.model;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "password_reset")
@Getter
@Setter
@EqualsAndHashCode(of = {"id", "token", "expiryDate"})
public class PasswordResetTokenEntity {

    private static final int EXPIRATION = 60;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Access(AccessType.PROPERTY)
    @Column(name = "id", nullable = false, columnDefinition = "SMALLINT(11) UNSIGNED")
    private long id;

    private String token;

    @OneToOne(targetEntity = UserEntity.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private UserEntity user;

    private LocalDateTime expiryDate;

    public PasswordResetTokenEntity() {
        super();
    }

    public PasswordResetTokenEntity(final String token) {
        super();

        this.token = token;
        this.expiryDate = calculateExpiryDate(EXPIRATION);
    }

    public PasswordResetTokenEntity(final String token, final UserEntity user) {
        super();

        this.token = token;
        this.user = user;
        this.expiryDate = calculateExpiryDate(EXPIRATION);
    }

    private LocalDateTime calculateExpiryDate(final int expiryTimeInMinutes) {
        return LocalDateTime.now().plusMinutes(expiryTimeInMinutes);
    }

    public void updateToken(final String token) {
        this.token = token;
        this.expiryDate = calculateExpiryDate(EXPIRATION);
    }
}
