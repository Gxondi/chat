package com.hyh.mallchat.common.chat.service.strategy.msg;

import cn.hutool.core.bean.BeanUtil;
import com.hyh.mallchat.common.chat.dao.MessageDao;
import com.hyh.mallchat.common.chat.domain.entity.Message;
import com.hyh.mallchat.common.chat.domain.enums.MessageTypeEnum;
import com.hyh.mallchat.common.chat.domain.vo.req.ChatMessageReq;
import com.hyh.mallchat.common.chat.service.adapter.MessageAdapter;
import com.hyh.mallchat.common.common.utils.AssertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.lang.reflect.ParameterizedType;

/**
 * 消息处理器抽象类
 */
public abstract class AbstractMsgHandler<Req>{

    private Class<Req> bodyClass;
    @Autowired
    private MessageDao messageDao;
    /**
     * 将消息处理器注册到工厂
     */
    @PostConstruct  // 该注解表示在构造函数执行完之后执行
    private void init() {
        ParameterizedType genericSuperclass = (ParameterizedType) this.getClass().getGenericSuperclass();
        this.bodyClass = (Class<Req>) genericSuperclass.getActualTypeArguments()[0];
        MsgHandlerFactory.register(getMsgTypeEnum().getType(), this);
    }

    protected abstract MessageTypeEnum getMsgTypeEnum();

    public abstract void checkMsg(Req body, Long roomId, Long uid);
    public abstract void saveMsg(Message message, Req body);
    public abstract Object showMsg(Message msg);

    /**
     * 被回复时——展示的消息
     */
    public abstract Object showReplyMsg(Message msg);

    /**
     * 会话列表——展示的消息
     */
    public abstract String showContactMsg(Message msg);
    /**
     * 校验并保存消息
     * 将统一化的校验和保存逻辑放在父类中
     * @param request
     * @param uid
     * @return
     */
    @Transactional
    public Long checkAndSendMsg(ChatMessageReq request, Long uid){
        Req body = this.toBean(request.getBody());
        AssertUtil.allCheckValidateThrow(body);
        checkMsg(body, request.getRoomId(),uid);
        Message insert = MessageAdapter.buildMsgSave(request, uid);
        messageDao.save(insert);
        saveMsg(insert, body);
        return insert.getId();
    }
    private Req toBean(Object body) {
        if (bodyClass.isAssignableFrom(body.getClass())) {
            return (Req) body;
        }
        return BeanUtil.toBean(body, bodyClass);
    }


}
