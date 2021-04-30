package project.sleepwell.domain;

import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@MappedSuperclass   //JPA Entity 클래스들이 BaseTimeEntity 를 상속할 때 필드들도 칼럼으로 인식하게 해줌
@Getter
@EntityListeners(AuditingEntityListener.class)  //Auditing 기능을 포함
public class BaseTimeEntity {

    @CreatedDate
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime modifiedDate;
}
