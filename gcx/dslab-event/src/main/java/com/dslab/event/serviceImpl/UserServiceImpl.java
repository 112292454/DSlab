package com.dslab.event.serviceImpl;

import com.dslab.commonapi.dataStruct.MyHashMap;
import com.dslab.commonapi.entity.Event;
import com.dslab.commonapi.entity.User;
import com.dslab.commonapi.entity.UserType;
import com.dslab.commonapi.services.UserService;
import com.dslab.event.mapper.UserMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @program: DSlab
 * @description: 用户服务类
 * @author: 郭晨旭
 * @create: 2023-05-23 22:38
 * @version: 1.0
 **/

@Service
@DubboService(group = "DSlab",version = "1.0.0",interfaceClass = UserService.class)
public class UserServiceImpl implements UserService {
    private final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Resource
    UserMapper userMapper;
    /**
     * 根据群组id存储用户id的map
     * 一个群组有哪些用户
     */
    private static Map<Integer, List<User>> userGroupIdMap = new MyHashMap<>();
    /**
     * 根据用户id获取用户信息的map
     */
    private static Map<Integer, User> userIdMap = new MyHashMap<>();

    /**
     * 预加载函数
     */
    @PostConstruct
    public void init() {
        List<User> users = userMapper.getAllUsers();
        if (users != null) {
            for (User u : users) {
                // 加载用户
                userIdMap.put(u.getUserId(), u);
                // 加载同组用户
                List<User> list = userGroupIdMap.getOrDefault(u.getGroupId(), new ArrayList<>());
                list.add(u);
                userGroupIdMap.put(u.getGroupId(), list);
            }
        }
        logger.info("-----user service init success!-----");
    }

    /**
     * 选取同一组的用户
     *
     * @param user 管理员
     * @return 同组用户
     */
    @Override
    public List<User> selectSameGroupUsers(User user) {
        return userGroupIdMap.get(user.getGroupId());
    }

    /**
     * 验证用户身份是否可以添加/修改此日程
     * 集体类只能由管理员添加, 个人类只能由学生添加
     *
     * @param u 用户
     * @param e 日程
     * @return 符合条件返回true, 否则返回false
     */
    @Override
    public boolean identifyUser(User u, Event e) {
        if (u.isAdmin()) {
            return e.getIsGroup() && (e.isLesson() || e.isExam() || e.isActivity());
        }
        return !e.getIsGroup() && (e.isActivity() || e.isTemporary() || e.isClock());
    }

    /**
     * 根据用户id加载用户
     *
     * @param userId 用户id
     * @return 用户
     */
    @Override
    public User load(Integer userId) {
        return userIdMap.get(userId);
    }

    /**
     * 根据用户邮箱加载用户
     *
     * @param mail 用户邮箱
     * @return 用户
     */
    @Override
    public User loadByMail(String mail) {
        return userMapper.getByMail(mail);
    }

    /**
     * 判断用户邮箱是否已存在
     *
     * @param mail 用户邮箱
     * @return 用户
     */
    @Override
    public boolean contains(String mail) {
        return userMapper.getByMail(mail) != null;
    }

    /**
     * 用户注册 (只有学生才会注册)
     *
     * @param name     用户名
     * @param mail     用户邮箱
     * @param password 密码
     * @param groupId  组id
     */
    @Override
    public void register(String name, String mail, String password, Integer groupId) {
        User u = new User(name, password, mail, UserType.USER_STUDENT.getValue(), groupId);
        userMapper.add(u);
        u = userMapper.getByMail(mail);
        // 更新内存中的数据
        List<User> groupUsers = userGroupIdMap.getOrDefault(groupId, new ArrayList<>());
        groupUsers.add(u);
        userGroupIdMap.put(groupId, groupUsers);
        userIdMap.put(u.getUserId(), u);
    }

    /**
     * 获取所有的组id
     *
     * @return 组id列表
     */
    @Override
    public List<Integer> getGroups() {
        return userMapper.getGroups();
    }
}
