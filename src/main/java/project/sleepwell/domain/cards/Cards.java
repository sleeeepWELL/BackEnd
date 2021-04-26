package project.sleepwell.domain.cards;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
public class Cards {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
//    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") // Spring 에서 자동으로 타입변환을 해줘서 어노테이션을 안써도 되는걸까.. 그렇다면 클라이언트에서 어떤 형식으로 주는 것 까지 먹을까
    private LocalDateTime startSleep;

    @Column(nullable = false)
//    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endSleep;

    @Column(nullable = false)
    private Long totalSleep;

    // createdAt 추가 -> timestamped 확장

    // tag 리스트로 변경
    @Column
    private String tag;

    @Column(nullable = false)
    private Long condition;

    @Column
    private String memo;


    // 생성자 대신에 @Builder를 통해 빌더 클래스를 사용 -> 지금 채워야할 필드의 역할이 무엇인지 정확히 지정
    @Builder
    public Cards(LocalDateTime startSleep, LocalDateTime endSleep, Long totalSleep, String tag , Long condition, String memo) {
        this.startSleep = startSleep;
        this.endSleep = endSleep;
        this.totalSleep = totalSleep;
        this.tag = tag;
        this.condition = condition;
        this.memo = memo;
    }

    public void update(LocalDateTime startSleep, LocalDateTime endSleep, Long totalSleep, String tag ,Long condition, String memo){
        this.startSleep = startSleep;
        this.endSleep = endSleep;
        this.totalSleep = totalSleep;
        this.tag = tag;
        this.condition = condition;
        this.memo = memo;
    }

}
