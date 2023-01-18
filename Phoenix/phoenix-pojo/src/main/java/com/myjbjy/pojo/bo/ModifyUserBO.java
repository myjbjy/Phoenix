package com.myjbjy.pojo.bo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.Email;
import java.time.LocalDate;

/**
 * @author myj
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ModifyUserBO {

    private String userId;

    private String face;
    private Integer sex;
    private String nickname;

    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private LocalDate birthday;

    @Email
    private String email;

    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private LocalDate startWorkDate;
    private String position;

}
