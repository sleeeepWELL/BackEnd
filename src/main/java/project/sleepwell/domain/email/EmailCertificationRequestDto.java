package project.sleepwell.domain.email;

import lombok.Getter;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@Getter
public class EmailCertificationRequestDto {

    private String email;
    private String certificationNumber;

    public EmailCertificationRequestDto(String email, String certificationNumber) {
        this.email = email;
        this.certificationNumber = certificationNumber;
    }
}
