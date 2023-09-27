package fastcampus.team7.Livable_officener.global.websocket;

import fastcampus.team7.Livable_officener.domain.User;
import fastcampus.team7.Livable_officener.dto.chat.SendChatDTO;
import fastcampus.team7.Livable_officener.global.exception.NotFoundRoomException;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class WebSocketSessionManager {

    private final Map<Long, Collection<WebSocketSession>> roomIdToSessions = new ConcurrentHashMap<>();

    private final SimpMessagingTemplate messagingTemplate;

    public void sendToRoom(Long roomId, SendChatDTO sendChatDTO) {
        messagingTemplate.convertAndSend("/topic/chat/" + roomId, sendChatDTO);
    }



    public static User getSessionUser(WebSocketSession session) {
        Authentication auth = Objects.requireNonNull((Authentication) session.getPrincipal());
        return (User) auth.getPrincipal();
    }

    public void addSessionToRoom(Long roomId, WebSocketSession session) {
        Collection<WebSocketSession> sessions;
        try {
            sessions = getWebSocketSessions(roomId);
        } catch (NotFoundRoomException e) {
            sessions = Collections.synchronizedList(new ArrayList<>());
            roomIdToSessions.put(roomId, sessions);
            sessions.add(session);
            return;
        }

        User requestUser = getSessionUser(session);
        Optional<WebSocketSession> duplicateUserSession = sessions.stream()
                .filter(sess -> getSessionUser(sess).equals(requestUser))
                .findFirst();
        if (duplicateUserSession.isPresent()) {
            throw new IllegalStateException("웹소켓 세션은 채팅방마다 참여자별로 하나만 연결 가능합니다.");
        }
        sessions.add(session);
    }

    public void closeSessionForUser(Long roomId, User kickedUser) {
        for (WebSocketSession session : getWebSocketSessions(roomId)) {
            if (kickedUser.equals(getSessionUser(session))) {
                removeSessionFromRoom(roomId, session);
                return;
            }
        }
    }

    public void removeSessionFromRoom(Long roomId, WebSocketSession session) {
        Collection<WebSocketSession> sessions = getWebSocketSessions(roomId);
        sessions.remove(session);
        try {
            session.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean nonexistent(Long roomId, User user) {
        for (WebSocketSession session : getWebSocketSessions(roomId)) {
            if (user.equals(getSessionUser(session))) {
                return false;
            }
        }
        return true;
    }

    private Collection<WebSocketSession> getWebSocketSessions(Long roomId) {
        var ret = roomIdToSessions.get(roomId);
        if (ret == null) {
            throw new NotFoundRoomException();
        }
        return ret;
    }
}
