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
public class UpdateAdminBO {
    private String id;
    private String username;
    private String face;
    private String remark;
}
