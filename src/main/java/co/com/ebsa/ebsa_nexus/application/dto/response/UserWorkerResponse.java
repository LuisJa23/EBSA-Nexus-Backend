package co.com.ebsa.ebsa_nexus.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserWorkerResponse {
    private Long id;
    private String uuid;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String workType;
    private String documentNumber;
    private String phone;
    private boolean active;
}
