package nsu_bookexchage.Summer_Project.socket;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {

    // 소켓 핸들러
    public final WebSocketHandler webSocketHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // endpoint 설정: endpoint로 요청이 들어오면 소켓 통신 진행, CORS 설정
        registry.addHandler(webSocketHandler, "/ws/chat")
                .setAllowedOrigins("http://localhost:3000")
                .setAllowedOrigins("*");

    }
}
