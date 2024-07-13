package nsu_bookexchage.Summer_Project.socket;

import lombok.*;

@Builder
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageDTO {
    //== 채팅 DTO ==//
    // 입장, 채팅
    public enum MessageType{
        ENTER, TALK
    }

    private MessageType messageType;    // 메세지 타입
    private Long chatRoomId;        // 방 번호
    private Long senderId;          // 채팅 보낸 사람
    private String message;         // 메시지
}

/**
 * ========프론트와 주고 받아야하는 데이터 형식(JSON)=========
 * {
 *   "messageType": "ENTER",
 *   "chatRoomId": 1,
 *   "senderId": 123@531fsdq123,
 *   "message": "Hello!"
 * }
 */