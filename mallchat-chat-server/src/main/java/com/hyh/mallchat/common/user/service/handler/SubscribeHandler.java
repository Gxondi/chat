package com.hyh.mallchat.common.user.service.handler;

import com.hyh.mallchat.common.user.service.WxMsgService;
import com.hyh.mallchat.common.user.service.adapter.TextBuilder;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 关注事件处理
 * @author <a href="https://github.com/binarywang">Binary Wang</a>
 */
@Component
public class SubscribeHandler extends AbstractHandler {
    @Autowired
    private WxMsgService wxMsgService;
    /**
     * 处理公众号被关注事件
     */
    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage,
                                    Map<String, Object> context, WxMpService weixinService,
                                    WxSessionManager sessionManager) throws WxErrorException {
        this.logger.info("新关注用户 OPENID: " + wxMessage.getFromUser());
        WxMpXmlOutMessage responseResult = null;
        try {
            responseResult = wxMsgService.scan(wxMessage);;
        } catch (Exception e) {
            this.logger.error(e.getMessage(), e);
        }

        if (responseResult != null) {
            return responseResult;
        }
        try {
        } catch (Exception e) {
            this.logger.error(e.getMessage(), e);
        }
        return new TextBuilder().build("感谢关注", wxMessage);
    }

    /**
     * 处理特殊请求，比如如果是扫码进来的，可以做相应处理
     */
    private WxMpXmlOutMessage handleSpecial(WxMpService weixinService, WxMpXmlMessage wxMessage)
            throws Exception {
        return null;
    }

}
