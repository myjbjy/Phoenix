package com.myjbjy.pojo.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

/**
 * @author myj
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class RegisterLoginBO {

    @NotBlank(message = "手机号不能为空")
    @Length(min = 11,max = 11,message = "手机号长度不正确")
    private String mobile;

    @NotBlank(message = "验证码不能为空")
    private String smsCode;
}
