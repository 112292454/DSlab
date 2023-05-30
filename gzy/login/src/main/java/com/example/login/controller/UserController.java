package com.example.login.controller;

import com.dslab.commonapi.entity.User;
import com.dslab.commonapi.services.UserService;
import com.dslab.commonapi.vo.Result;
import com.example.login.utils.JwtUtils;
import io.swagger.annotations.*;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.Pattern;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @description user
 * @author gzy
 * @date 2022-11-09
 */
@RestController
@RequestMapping(value = "/user")
@Api
public class UserController {

	@Resource
	private UserService userService;

	@Resource
	private JwtUtils jwtUtils;

	@PostMapping("/login")
	@ApiResponses({
			@ApiResponse(code = 200,message = "生成的token"),
			@ApiResponse(code = 401,message = "密码与邮箱不对应")
	})
	@ApiOperation(value = "登陆接口",notes="login返回一个token，访问需要带着该token，一小时失效，最后10%有效期登陆会刷新token")
	public Result<String> login(
			@RequestBody Map<String,String> map){
		String mail=map.get("mail"),password=map.get("password");
		/*
		@Pattern (regexp = "(\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*)|^$") @RequestParam("mail") String mail,
		@Pattern (regexp = "[\\w\\d]{6,20}") @RequestParam("password") String password
		*/
		/*TODO:校验合法*/
		/*TODO:验证码*/
		User user=userService.loadByMail(mail);


		if(user==null||!user.getPassword().equals(password)){
			return Result.error(HttpServletResponse.SC_UNAUTHORIZED,"登陆验证未通过");
		}else{
			Map<String,Object> data=new HashMap<>();
			//data.put("userID", user.getUserId());
			data.put("mail",user.getMail());
			String token= jwtUtils.createJwt(user.getUserId()+"",user.getUsername(),data);
			return Result.<String>success("登陆成功").data(token);
		}
	}

	@ApiOperation(value ="注册用简化接口，至少提供用户名、密码、邮箱。无返回数据，200即注册成功")
	@GetMapping("/register")
	@ResponseBody
	public Result<String> register(
			@ApiParam(value = "姓名") @Pattern (regexp = "[\\w\\d 一-龟]{2,20}") @RequestParam("name") String name,
			@ApiParam(value = "邮箱") @Pattern (regexp = "(\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*)|^$") @RequestParam("mail") String mail,
			@ApiParam(value = "密码") @Pattern (regexp = "[\\w\\d]{6,20}") @RequestParam("password") String password) {

		/*TODO:验证码*/
		if(userService.contains(mail)) throw new IllegalArgumentException("用户已存在");
		userService.register(name, mail, password);
		return Result.success();
	}

	/**
	 * 新增
	 * @author gzy
	 * @date 2022/11/09
	 **/
	@RequestMapping("/insert")
	@ApiOperation(value ="注册用完全接口，提供完整的用户信息（也可用作注册之后，完善信息的接口）")
	public Object insert(User user){
		return userService.insert(user);
	}


}