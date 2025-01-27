package fastcampus.team7.Livable_officener.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import fastcampus.team7.Livable_officener.dto.fcm.FCMNotificationDTO;
import fastcampus.team7.Livable_officener.dto.fcm.FCMSubscribeDTO;
import fastcampus.team7.Livable_officener.global.fcm.FCMTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class FCMService {

    private final FCMTokenRepository fcmTokenRepository;
    private final FirebaseMessaging firebaseMessaging;

    @Transactional
    public void registerFcmToken(FCMSubscribeDTO dto) {
        fcmTokenRepository.save(dto);
    }

    @Transactional
    public void sendFcmNotification(FCMNotificationDTO dto) {
        String fcmToken = getFcmToken(dto.getReceiverEmail());

        Notification notification = dto.makeNotification();
        Message message = Message.builder()
                .setNotification(notification)
                .setToken(fcmToken)
                .build();

        try {
            String messageId = firebaseMessaging.send(message);
            log.info("웹푸시 전송 to {}: {}", dto.getReceiverEmail(), messageId);
        } catch (FirebaseMessagingException e) {
            throw new RuntimeException("웹푸시 전송 실패", e);
        }
    }

    private String getFcmToken(String email) {
        return fcmTokenRepository.find(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 id를 가진 회원의 FCM 토큰이 존재하지 않습니다."));
    }

    @Transactional
    public void unsubscribe(String email) {
        fcmTokenRepository.delete(email);
    }

    @Transactional(readOnly = true)
    public boolean isSubscribed(String email) {
        return fcmTokenRepository.contains(email);
    }
}
