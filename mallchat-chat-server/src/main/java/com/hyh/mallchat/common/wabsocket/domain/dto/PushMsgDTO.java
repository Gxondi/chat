package com.hyh.mallchat.common.wabsocket.domain.dto;

import com.hyh.mallchat.common.common.domain.enums.WSPushTypeEnum;
import com.hyh.mallchat.common.wabsocket.domain.vo.resp.WSBaseResp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PushMsgDTO implements Serializable {
    private List<Long> uidList;
    private WSBaseResp<?> wsBaseMsg;
    private Integer pushType;
    public PushMsgDTO(WSBaseResp<?> wsBaseMsg) {
        this.wsBaseMsg = wsBaseMsg;
        this.pushType = WSPushTypeEnum.ALL.getType();
    }
    public PushMsgDTO(WSBaseResp<?> wsBaseMsg, List<Long> uidList) {
        this.wsBaseMsg = wsBaseMsg;
        this.uidList = uidList;
        this.pushType = WSPushTypeEnum.USER.getType();
    }
    public PushMsgDTO(WSBaseResp<?> wsBaseMsg, Long uid) {
        this.wsBaseMsg = wsBaseMsg;
        this.uidList = Collections.singletonList(uid);
        this.pushType = WSPushTypeEnum.USER.getType();
    }
}
