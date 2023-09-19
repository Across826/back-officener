package fastcampus.team7.Livable_officener.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CompanyDTO {

    private Long id;
    private String name;
    private String companyNum;

}
