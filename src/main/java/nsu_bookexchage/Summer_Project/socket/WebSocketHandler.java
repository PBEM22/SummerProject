package nsu_bookexchage.Summer_Project.socket;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

@Slf4j
@Getter
@Component
@RequiredArgsConstructor
public class WebSocketHandler extends TextWebSocketHandler {

    // Json으로 변환하기 위한 매퍼
    private final ObjectMapper mapper;

    // 연결되어 있는 웹소켓 세션들 담는 Set
    private final Set<WebSocketSession> sessions = new HashSet<>();

    // chatRoomId: {session1, session2} 형식
    private final Map<Long, Set<WebSocketSession>> chatRoomSessionMap = new HashMap<>();

    // 연결 이후 메서드 동작
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {

        log.info("{} 연결됨", session.getId());

        // 세션 리스트에 추가
        sessions.add(session);

        log.info(session + " 웹소켓 생성 ");

    }

    // 텍스트 메시지 수신했을 때 호출 (메시지 처리 및 응답 생성)
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

        // 실 데이터 확인
        String payload = message.getPayload();
        log.info("payload: " + payload);

        // 페이로드 (데이터) => ChatDTO로 변환
        ChatMessageDTO chatMessageDTO = mapper.readValue(payload, ChatMessageDTO.class);
        log.info("session {}", chatMessageDTO.toString());

        Long chatRoomId = chatMessageDTO.getChatRoomId();

        // 세션이 메모리에 없을시에 새로 만들어줌
        if (!chatRoomSessionMap.containsKey(chatRoomId)){
            chatRoomSessionMap.put(chatRoomId, new HashSet<>());
        }
        Set<WebSocketSession> chatRoomSession = chatRoomSessionMap.get(chatRoomId);

        // Message 타입 확인
        if (chatMessageDTO.getMessageType().equals(ChatMessageDTO.MessageType.ENTER)){
            // session에 넘어온 session값 담기
            chatRoomSession.add(session);
        }

        if (chatRoomSession.size() >= 3){
            removeClosedSession(chatRoomSession);
        }

        // 해당 채팅방에 있는 모든것을 보내줌
        sendMessageToChatRoom(chatMessageDTO, chatRoomSession);

    }

    // 웹소켓 닫혔을 때 호출
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info(session.getId() + " 웹소켓 닫힘 ");
        sessions.remove(session);
    }

    // ====== 채팅 관련 메소드 ======
    private void removeClosedSession(Set<WebSocketSession> chatRoomSession) {
        chatRoomSession.removeIf(sess -> !sessions.contains(sess));
    }

    private void sendMessageToChatRoom(ChatMessageDTO chatMessageDTO, Set<WebSocketSession> chatRoomSession) {
        chatRoomSession.parallelStream().forEach(sess -> sendMessage(sess, chatMessageDTO));//2
    }


    public <T> void sendMessage(WebSocketSession session, T message) {
        try{
            session.sendMessage(new TextMessage(mapper.writeValueAsString(message)));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

}
