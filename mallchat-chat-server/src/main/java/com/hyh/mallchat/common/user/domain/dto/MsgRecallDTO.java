package com.hyh.mallchat.common.user.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MsgRecallDTO {
    private Long msgId;
    private Long roomId;
    private Long recallUid;
}
