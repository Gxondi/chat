package com.hyh.mallchat.common.user.domain.vo.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.bind.annotation.ControllerAdvice;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModifyNameReq {
    @NotNull
    @Length(max = 6, message = "用户名可别取太长，不然我记不住噢")
    private String name;
}
