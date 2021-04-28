//package project.sleepwell.domain;
//
//import lombok.Getter;
//import org.springframework.data.annotation.CreatedDate;
//import org.springframework.data.jpa.domain.support.AuditingEntityListener;
//import javax.persistence.EntityListeners;
//import javax.persistence.MappedSuperclass;
//import java.time.LocalDate;
//
//
//@Getter
//@MappedSuperclass // JPA Entity 클래스들이 Timestamped 클래스를 상속할 경우 createdAt 필드도 칼럼으로 인식
//@EntityListeners(AuditingEntityListener.class)
//public abstract class TimeStamped {
//
//    @CreatedDate
//    private LocalDate createdAt;
//}
