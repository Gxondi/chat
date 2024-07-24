package com.hyh.mallchat.common.user.domain.entity;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class IpInfo implements Serializable {
    //注册ip
    private String createIP;
    //注册ip详情
    private IpDetail createIpdetail;
    //最新ip
    private String updateIP;
    //最新ip详情(解析归属地)
    private IpDetail updateIpdetail;
    public void refreshIp(String ip) {
        if (StringUtils.isBlank(ip)){
            return;
        }
        if (StringUtils.isBlank(createIP)){
            createIP = ip;
        }
        updateIP = ip;
    }

    /**
     * 是否需要刷新ip归属地
     * @return
     */
    public String needRefreshIp() {
        boolean notNeedRefresh = Optional.ofNullable(updateIpdetail)
                .map(IpDetail::getIp)
                .filter(ip -> Objects.equals(ip, updateIP))//ip相同
                .isPresent();//有值
        return notNeedRefresh ? null : updateIP;
    }

    public void refreshIpDetail(IpDetail ipDetail) {
        //第一次注册刷新ip
        if(Objects.equals(createIP, ipDetail.getIp())){
            createIpdetail = ipDetail;
        }
        //判断是否是需要刷新最新的ip
        if(Objects.equals(updateIP, ipDetail.getIp())){
            updateIpdetail = ipDetail;
        }
    }
}
