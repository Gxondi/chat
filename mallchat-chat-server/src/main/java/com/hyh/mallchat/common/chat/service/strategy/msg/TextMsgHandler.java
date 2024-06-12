package com.hyh.mallchat.common.chat.service.strategy.msg;

import cn.hutool.core.collection.CollectionUtil;
import com.hyh.mallchat.common.chat.dao.MessageDao;
import com.hyh.mallchat.common.chat.domain.entity.Message;
import com.hyh.mallchat.common.chat.domain.entity.msg.MessageExtra;
import com.hyh.mallchat.common.chat.domain.entity.msg.UrlInfo;
import com.hyh.mallchat.common.chat.domain.enums.MessageStatusEnum;
import com.hyh.mallchat.common.chat.domain.enums.MessageTypeEnum;
import com.hyh.mallchat.common.chat.domain.vo.req.msg.TextMsgReq;
import com.hyh.mallchat.common.chat.domain.vo.resp.TextMsgResp;
import com.hyh.mallchat.common.chat.service.adapter.MessageAdapter;
import com.hyh.mallchat.common.chat.service.cache.MsgCache;
import com.hyh.mallchat.common.common.domain.enums.RoleEnum;
import com.hyh.mallchat.common.common.domain.enums.YesOrNoEnum;
import com.hyh.mallchat.common.common.utils.AssertUtil;
import com.hyh.mallchat.common.common.utils.discover.PrioritizedUrlDiscover;
import com.hyh.mallchat.common.user.domain.entity.User;
import com.hyh.mallchat.common.user.service.IRoleService;
import com.hyh.mallchat.common.user.service.cache.UserCache;
import com.hyh.mallchat.common.user.service.cache.UserInfoCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class TextMsgHandler extends AbstractMsgHandler<TextMsgReq> {
    @Autowired
    private MessageDao messageDao;
    @Autowired
    private MsgCache msgCache;
    @Autowired
    private UserCache userCache;
    @Autowired
    private UserInfoCache userInfoCache;
    @Autowired
    private IRoleService iRoleService;

    @Override
    protected MessageTypeEnum getMsgTypeEnum() {
        return MessageTypeEnum.TEXT;
    }
    private static final PrioritizedUrlDiscover URL_TITLE_DISCOVER = new PrioritizedUrlDiscover();
    @Override
    public void checkMsg(TextMsgReq body, Long roomId, Long uid) {
        //回复消息时，校验
        if (Objects.nonNull(body.getReplyMsgId())) {
            Long replyMsgId = body.getReplyMsgId();
            Message replyMsg = messageDao.getById(replyMsgId);
            AssertUtil.isNotEmpty(replyMsg, "回复的消息不存在");
            AssertUtil.notEqual(replyMsg.getRoomId(), roomId, "回复的消息不在当前房间");
        }
        //校验一下艾特消息
        if(CollectionUtil.isNotEmpty(body.getAtUidList())){
            //去重
            List<Long> atList = body.getAtUidList().stream().distinct().collect(Collectors.toList());
            //去空
            Map<Long, User> userInfoBatch = userInfoCache.getBatch(atList);
            long count = userInfoBatch.values().stream().filter(Objects::nonNull).count();
            //如果@用户不存在，userInfoCache 返回的map中依然存在该key，但是value为null，需要过滤掉再校验
            AssertUtil.equal(count, (long)atList.size(), "艾特用户不存在");
            boolean atAll = body.getAtUidList().contains(0L);
            if (atAll) {
                boolean b = iRoleService.hasPower(uid, RoleEnum.CHAT_MANAGER);
                AssertUtil.isTrue(b, "没有权限");
            }
        }

    }

    @Override
    public void saveMsg(Message message, TextMsgReq body) {
        MessageExtra extra = Optional.ofNullable(message.getExtra()).orElse(new MessageExtra());
        Message update = new Message();
        update.setId(message.getId());
        update.setExtra(extra);
        update.setContent(body.getContent());
        //有回复消息
        if (Objects.nonNull(body.getReplyMsgId())) {
            Integer gapCount = messageDao.getGapCount(message.getRoomId(), body.getReplyMsgId(), message.getId());
            update.setGapCount(gapCount);
            update.setReplyMsgId(body.getReplyMsgId());
        }
        //艾特功能
        if(Objects.nonNull(body.getAtUidList())){
            extra.setAtUidList(body.getAtUidList());
        }
        //判断消息url跳转
        Map<String, UrlInfo> urlContentMap = URL_TITLE_DISCOVER.getUrlContentMap(body.getContent());
        extra.setUrlContentMap(urlContentMap);
        messageDao.updateById(update);
    }
    /**
     * 组装消息体，不同消息，展示消息
     * Adapter messageVo.setBody(msgHandler.showMsg(message));
     * @param msg
     * @return
     */
    @Override
    public Object showMsg(Message msg) {
        TextMsgResp resp = new TextMsgResp();
        resp.setContent(msg.getContent());
        resp.setAtUidList(Optional.ofNullable(msg.getExtra()).map(MessageExtra::getAtUidList).orElse(null));
        //resp.setUrlContentMap();
        Optional<Message> reply = Optional.ofNullable(msg.getReplyMsgId())
                .map(msgCache::getMsg)
                .filter(message -> Objects.equals(message.getStatus(), MessageStatusEnum.NORMAL.getStatus())
                );
        if (reply.isPresent()) {
            Message message = reply.get();
            TextMsgResp.ReplyMsg replyMsgVo = new TextMsgResp.ReplyMsg();

            replyMsgVo.setId(message.getId());
            replyMsgVo.setUid(message.getFromUid());
            replyMsgVo.setType(MessageTypeEnum.REPLY.getType());
            replyMsgVo.setBody(MsgHandlerFactory.getStrategyNoNull(message.getType()).showReplyMsg(message));
            User userInfo = userCache.getUserInfo(message.getFromUid());
            replyMsgVo.setUsername(userInfo.getName());
            replyMsgVo.setCanCallback(YesOrNoEnum.toStatus(Objects.nonNull(msg.getGapCount()) && msg.getGapCount() <= MessageAdapter.CAN_CALLBACK_GAP_COUNT));
            replyMsgVo.setGapCount(message.getGapCount());
            resp.setReply(replyMsgVo);
        }
        return resp;
    }

    @Override
    public Object showReplyMsg(Message msg) {
        return msg.getContent();
    }

    @Override
    public String showContactMsg(Message msg) {
        return null;
    }


    //    @Override
//    public void checkMsg(ChatMessageReq request, Long uid) {
//        TextMsgHandler textMsgHandler = BeanUtil.toBean(request.getBody(), TextMsgHandler.class);
//        AssertUtil.allCheckValidateThrow(textMsgHandler);
//    }
//    /**
//     * 根据策略不同保存消息到extra
//     * @param msg
//     * @param request
//     */
//    @Override
//    public void saveMsg(Message msg, ChatMessageReq request) {
//        //TextMsgHandler body = BeanUtil.toBean(request.getBody(), TextMsgHandler.class);
//        String body = toBean(request);
//        MessageExtra extra = Optional.ofNullable(msg.getExtra()).orElse(new MessageExtra());
//        Message update = new Message();
//        update.setId(msg.getId());
//        update.setExtra(extra);
//        update.setContent(body);
//        messageDao.updateById(update);
//    }
//
//
//    private String toBean(ChatMessageReq request) {
//        Object body = request.getBody();
//        return body.toString();
//    }

}






























