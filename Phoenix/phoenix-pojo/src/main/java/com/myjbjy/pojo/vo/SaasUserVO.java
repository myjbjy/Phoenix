package com.myjbjy.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author myj
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SaasUserVO {

    private String username;
    private String name;
    private String face;
}
