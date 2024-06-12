package com.hyh.mallchat.common.chat.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableField;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hyh.mallchat.common.common.domain.enums.RoomHotFlagEnum;
import com.hyh.mallchat.common.common.domain.enums.RoomTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 房间表
 * </p>
 *
 * @author CondiX
 * @since 2024-05-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("room")
public class Room implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 房间类型 1群聊 2单聊
     */
    @TableField("type")
    private Integer type;

    /**
     * 是否全员展示 0否 1是
     */
    @TableField("hot_flag")
    private Integer hotFlag;

    /**
     * 群最后消息的更新时间（热点群不需要写扩散，只更新这里）
     */
    @TableField("active_time")
    private Date activeTime;

    /**
     * 会话中的最后一条消息id
     */
    @TableField("last_msg_id")
    private Long lastMsgId;

    /**
     * 额外信息（根据不同类型房间有不同存储的东西）
     */
    @TableField("ext_json")
    private String extJson;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;

    /**
     * 修改时间
     */
    @TableField("update_time")
    private Date updateTime;

    @JsonIgnore
    public boolean isHotRoom() {
        return RoomHotFlagEnum.of(this.hotFlag) == RoomHotFlagEnum.YES;
    }
    @JsonIgnore
    public boolean isFriendRoom() {
        return RoomTypeEnum.of(this.type) == RoomTypeEnum.FRIEND;
    }
    @JsonIgnore
    public boolean isGroupRoom() {
        return RoomTypeEnum.of(this.type) == RoomTypeEnum.GROUP;
    }
}
