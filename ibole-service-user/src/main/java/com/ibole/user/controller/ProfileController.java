package com.ibole.user.controller;

import com.ibole.annotation.OptLog;
import com.ibole.constant.CommonConst;
import com.ibole.enums.UserEnum;
import com.ibole.pojo.user.User;
import com.ibole.user.service.UserService;
import com.ibole.utils.DesensitizedUtil;
import com.ibole.utils.JsonData;
import com.ibole.utils.OssClientUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 用户画像
 **/

@Api(tags = "用户画像")
@RestController
@RequestMapping(value = "/su/profile", produces = "application/json")
public class ProfileController {

	private final UserService userService;
	// 对象存储工具
	private final OssClientUtil ossClientUtil;

	@Autowired
	public ProfileController(UserService userService,OssClientUtil ossClientUtil) {
		this.userService = userService;
		this.ossClientUtil = ossClientUtil;
	}

	/**
	 * 用户上传头像
	 *
	 * @param user：用户条件
	 * @return boolean
	 * url: ?search={query}{&page,per_page,sort,order}
	 */
	@ApiOperation(value = "用户上传头像", notes = "用户上传头像")
	@ApiImplicitParam(name = "User", value = "用户上传头像", dataType = "Map", paramType = "query")
	@PutMapping("avatar")
	public JsonData updateUserAvatar( MultipartFile file,User user) throws IOException {
        String fileUrl = ossClientUtil.uploadFile(file);
        user.setAvatar(fileUrl);
        userService.updateUserProfile(user);
        return JsonData.success();
    }

	@PutMapping()
	@OptLog(operationType= CommonConst.MODIFY,operationName="更新用户资料")
	public JsonData updateByPrimaryKey(@RequestBody User user) {
        userService.updateUserProfile(user);
        return JsonData.success();
    }

	/**
	 * 查看个人界面
	 * @param id：用户id
	 * @return boolean
	 * url: ?search={query}{&page,per_page,sort,order}
	 */
	@GetMapping(value = "/{userId}")
	public JsonData findByCondition(@PathVariable String userId) {
        User result = userService.findById(userId);
        DesensitizedUtil.mobilePhone(result.getPhone());
        DesensitizedUtil.around(result.getAccount(), 2, 2);
        return JsonData.success(result);
    }

	/**
	 * 修改密码
	 * @param user 实体
	 * @return JsonData
	 */
	@PutMapping("password")
	@OptLog(operationType = CommonConst.MODIFY, operationName = "修改用户密码")
	public JsonData changePassword(@RequestBody User user, String oldPassword) {
        if (oldPassword == null || !oldPassword.equals(user.getPassword())) {
            return new JsonData(false, UserEnum.TWICE_PASSWORD_NOT_MATCH.getCode(), UserEnum.TWICE_PASSWORD_NOT_MATCH.getInfo());
        }
        userService.changePassword(user, oldPassword);
        return JsonData.success();
    }

}