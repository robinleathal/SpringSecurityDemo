package np.com.rabin.securityclient.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String username;
    @Column(length = 60)
    private String password;
    private String role;
    private boolean enabled = false;

}
