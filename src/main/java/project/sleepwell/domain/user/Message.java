package project.sleepwell.domain.user;

import lombok.Data;


// 클라이언트에게 응답 보내기 위해서 만듦 (상태코드, 메세지, 데이터)
@Data
public class Message {

    private StatusEnum status;
    private String message;
    private Object data;

    public Message() {
        this.status = StatusEnum.BAD_REQUEST;   //default 로 설정
        this.data = null;
        this.message = null;
    }
}
