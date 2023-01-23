package com.myjbjy.pojo.mq;

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
public class SMSContentQO {
    private String mobile;
    private String content;
}
