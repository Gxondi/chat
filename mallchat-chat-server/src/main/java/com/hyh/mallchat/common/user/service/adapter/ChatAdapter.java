package com.hyh.mallchat.common.user.service.adapter;

import com.hyh.mallchat.common.chat.domain.entity.Contact;
import com.hyh.mallchat.common.chat.domain.vo.resp.ChatMessageReadResp;
import com.hyh.mallchat.common.common.domain.enums.RoomHotFlagEnum;
import com.hyh.mallchat.common.common.domain.enums.NormalOrNoEnum;
import com.hyh.mallchat.common.common.domain.enums.RoomTypeEnum;
import com.hyh.mallchat.common.chat.domain.entity.Room;
import com.hyh.mallchat.common.chat.domain.entity.RoomFriend;
import org.checkerframework.checker.units.qual.C;

import java.util.Collection;
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
        roomFriend.setStatus(NormalOrNoEnum.NORMAL.getStatus());
        return roomFriend;
    }

    /**
     * 好友uid
     * @param values
     * @param uid
     * @return
     */
    public static List<Long> buildFriendUidSet(Collection<RoomFriend> values, Long uid) {
        return values.stream().map(a -> getFriendUid(a,uid)).collect(Collectors.toList());
    }

    public static Long getFriendUid(RoomFriend roomFriend, Long uid) {

        return roomFriend.getUid1() == uid ? roomFriend.getUid2():roomFriend.getUid1();

    }


    public static List<ChatMessageReadResp> buildMsgRead(List<Contact> list) {
        return list.stream().map(contact -> {
                    ChatMessageReadResp resp = new ChatMessageReadResp();
                    resp.setUid(contact.getUid());
                    return resp;
                }).collect(Collectors.toList());
    }
}
