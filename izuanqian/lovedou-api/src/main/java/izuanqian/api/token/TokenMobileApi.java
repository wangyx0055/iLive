package izuanqian.api.token;

import io.swagger.annotations.ApiOperation;
import izuanqian.BizException;
import izuanqian.DeviceService;
import izuanqian.TokenService;
import izuanqian.api.token.o.vo.MobileArrayVo;
import izuanqian.response.Api;
import izuanqian.user.domain.Mobile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static izuanqian.ApiHeader.HK_TOKEN;

/**
 * Created by PC on 2017/4/5.
 */
@Slf4j
@RestController
@RequestMapping("/api/token/mobile")
@io.swagger.annotations.Api(tags = "token", description = "令牌")
public class TokenMobileApi {

    @Autowired private TokenService tokenService;
    @Autowired private DeviceService deviceService;

    @GetMapping
    @ApiOperation(value = "号码列表", response = MobileArrayVo.class)
    public Api listMobileArray(
            @RequestHeader(HK_TOKEN) String token) {
        boolean hasAnyMobile = tokenService.hasAnyMobile(token);
        if (!hasAnyMobile) {
            throw new BizException(17060801, "please bind your mobile first.");
        }
        List<Mobile> mobiles = tokenService.listMobiles(token);
        if (mobiles.isEmpty()) {
            throw new BizException(17060801, "please bind your mobile first.");
        }
        MobileArrayVo mobileArrayVo = new MobileArrayVo();
        mobiles.stream().map(
                mobile ->
                        mobileArrayVo.getMobiles().add(new MobileArrayVo.MobileVo(mobile)));
        return new Api.Ok("", mobileArrayVo);
    }

    @PostMapping
    @ApiOperation("绑定号码")
    public Api bindMobile(
            @RequestHeader(HK_TOKEN) String token,
            @RequestBody Mobile mobile) {
        deviceService.bindMobile(token, mobile.getMobile());
        return new Api.Ok();
    }

    @PostMapping("/current")
    @ApiOperation("specify current mobile")
    public Api updateCurrentMobile(
            @RequestHeader(HK_TOKEN) String token,
            @RequestBody Mobile mobile) {
        tokenService.specifyCurrentMobile(token, mobile.getId());
        return new Api.Ok();
    }

}