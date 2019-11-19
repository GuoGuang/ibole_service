package com.ibole.base.controller.backstage;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ibole.base.service.backstage.LoginLogService;
import com.ibole.enums.StatusEnum;
import com.ibole.pojo.QueryVO;
import com.ibole.pojo.base.LoginLog;
import com.ibole.utils.JsonData;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 登录日志
 **/

@Api(tags = "登录日志")
@RestController
@RequestMapping("/loginLog")
public class LoginLogController {

	private final LoginLogService loginLogService;

	@Autowired
	public LoginLogController(LoginLogService loginLogService) {
		this.loginLogService = loginLogService;
	}

	/**
	 * 按照条件查询全部列表
	 * @return Result
	 */
	@GetMapping
	public JsonData findLoginLogByCondition(LoginLog loginLog, QueryVO queryVO){
		IPage<LoginLog> result = loginLogService.findLoginLogByCondition(loginLog,queryVO);
		return new JsonData(true, StatusEnum.OK.getCode(), StatusEnum.OK.getMsg(), result);

	}

	/**
	 * 根据ID查询登录日志
	 * @param id
	 * @return Result
	 */
	@GetMapping(value="/{id}")
	public JsonData findLoginLogByPrimaryKey(@PathVariable String id){
		LoginLog loginLog = loginLogService.findLoginLogByPrimaryKey(id);
		return new JsonData(true, StatusEnum.OK.getCode(), StatusEnum.OK.getMsg(),loginLog);
	}

	/**
	 * 增加登录日志
	 * @param loginLog 登录日志实体
	 * @return JsonData
	 */
	@PostMapping()
	public JsonData insertLoginLog(@RequestBody LoginLog loginLog){
		loginLogService.insertLoginLog(loginLog);
		return new JsonData(true, StatusEnum.OK.getCode(), StatusEnum.OK.getMsg());
	}


	/**
	 * 删除登录日志
	 * @param loginLogIds 要删除的id数组
	 * @return JsonData
	 */
	@DeleteMapping
	public JsonData deleteById(@RequestBody List<String> loginLogIds){
		loginLogService.deleteById(loginLogIds);
		return new JsonData(true, StatusEnum.OK.getCode(), StatusEnum.OK.getMsg());
	}
}