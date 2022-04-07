package np.com.rabin.securityclient.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Calendar;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
public class PasswordResetToken {
    private static final int EXPIRATION_TIME = 10;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;
    private Date expirationDate;
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "FK_PASSWORD_RESET_TOKEN"))
    private UserEntity userEntity;

    public PasswordResetToken(String token) {
        super();
        this.token = token;

        this.expirationDate = calculateExpirationDate(EXPIRATION_TIME);
    }
    public PasswordResetToken(UserEntity userEntity, String token) {
        super();
        this.token = token;
        this.userEntity = userEntity;
        this.expirationDate = calculateExpirationDate(EXPIRATION_TIME);
    }

    private Date calculateExpirationDate(int expirationTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(new Date().getTime());
        calendar.add(Calendar.MINUTE, expirationTime);
        return new Date(calendar.getTime().getTime());
    }
}
