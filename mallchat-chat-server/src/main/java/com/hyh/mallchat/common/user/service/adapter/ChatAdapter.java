package com.hyh.mallchat.common.user.service.adapter;

import com.hyh.mallchat.common.common.domain.enums.RoomHotFlagEnum;
import com.hyh.mallchat.common.common.domain.enums.RoomNormalOrNoEnum;
import com.hyh.mallchat.common.common.domain.enums.RoomTypeEnum;
import com.hyh.mallchat.common.user.domain.entity.Room;
import com.hyh.mallchat.common.user.domain.entity.RoomFriend;

import java.util.List;
import java.util.stream.Collectors;

public class ChatAdapter {

    public static final String DELIMITER = ",";

    public static String generateRoomKey(List<Long> uidList){
        String roomKey = uidList.stream()
                .sorted()
                .map(String::valueOf)
                .collect(Collectors.joining(DELIMITER));
        return roomKey;
    }

    public static Room buildRoom(RoomTypeEnum roomTypeEnum) {
        Room room = new Room();
        room.setType(roomTypeEnum.getType());
        room.setHotFlag(RoomHotFlagEnum.NO.getType());
        return room;
    }

    public static RoomFriend buildFriendRoom(Long roomId, List<Long> uidList) {
        List<Long> collect = uidList.stream().sorted().collect(Collectors.toList());
        RoomFriend roomFriend = new RoomFriend();
        roomFriend.setRoomId(roomId);
        roomFriend.setRoomKey(generateRoomKey(uidList));
        roomFriend.setUid1(collect.get(0));
        roomFriend.setUid2(collect.get(1));
        roomFriend.setStatus(RoomNormalOrNoEnum.NORMAL.getType());
        return roomFriend;
    }
}
