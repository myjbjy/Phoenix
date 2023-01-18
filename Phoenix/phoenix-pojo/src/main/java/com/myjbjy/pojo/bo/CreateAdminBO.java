package com.myjbjy.pojo.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author myj
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CreateAdminBO {

    private String username;
    private String password;
    private String remark;

}
