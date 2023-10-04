package fastcampus.team7.Livable_officener.dto.chat;

import com.fasterxml.jackson.annotation.JsonProperty;
import fastcampus.team7.Livable_officener.domain.RoomParticipant;
import fastcampus.team7.Livable_officener.domain.User;
import fastcampus.team7.Livable_officener.global.constant.Role;
import lombok.*;

import java.util.Objects;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public class GetParticipantDTO {

    private final Long id;
    private final String name;
    private final String companyName;
    private final String profileImage;
    private final boolean amI;
    @JsonProperty("isHost")
    private final boolean isHost;
    private final boolean hasRemitted;
    private final boolean hasReceived;

    public static GetParticipantDTO from(Long userId, RoomParticipant participant) {
        User user = participant.getUser();
        return GetParticipantDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .companyName(user.getCompany().getName())
                .profileImage(user.getProfileImage())
                .amI(Objects.equals(userId, user.getId()))
                .isHost(participant.getRole() == Role.HOST)
                .hasRemitted(participant.getRemittedAt() != null)
                .hasReceived(participant.getReceivedAt() != null)
                .build();
    }
}
