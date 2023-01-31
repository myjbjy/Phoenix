package com.myjbjy.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * @author myj
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class AdminInfoVO {
    private String id;
    private String username;
    private String remark;
    private String face;
    private LocalDateTime createTime;
    private LocalDateTime updatedTime;
}
