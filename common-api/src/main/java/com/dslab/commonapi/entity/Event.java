package com.dslab.commonapi.entity;

import com.dslab.commonapi.utils.TimeUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @program: DSlab
 * @description: 事件父类
 * @author: 郭晨旭
 * @create: 2023-03-26 18:33
 * @version: 1.0
 **/

@Data
@AllArgsConstructor
public class Event implements Serializable, Cloneable {

    private static final long serialVersionUID = 1324389877898L;
    /**
     * 事件ID
     */
    private Integer eventId;
    /**
     * 日程名称
     */
    @NotBlank(message = "日程名称不能为空")
    private String name;
    /**
     * 日程的形式 (lesson / exam / activity / temporary / clock)
     */
    @NotBlank(message = "日程类型不能为空")
    @Pattern(regexp = "^0|1|2|3|4$", message = "日程类型不正确")
    private String eventType;
    /**
     * 日程是否是线上形式
     * online 的话就不用导航
     */
    @NotBlank(message = "地点类型不能为空")
    private Boolean isOnline;
    /**
     * 参与人数的性质, 是否是集体
     * 课程, 考试, 集体活动是true
     * 临时事务, 闹钟, 个人活动为false
     */
    @NotBlank(message = "人员类型不能为空")
    private Boolean isGroup;
    /**
     * 活动的类型
     * 个人的包括有: 自习、锻炼、外出等
     * 集体活动包括有：班会、小组作业、创新创业、聚餐等
     * 临时事务类型包括有: 购物、洗澡、取外卖、取快递、送取东西等
     */
    private String customType;
    /**
     * 日程地点, 线下建筑物的id
     */
    private Integer buildingId;
    /**
     * 地点名称, 返回的时候需要根据地点id获取一下地点名称
     */
    private String buildingName;
    /**
     * 日程地点, 线上链接
     */
    private String link;
    /**
     * 日程日期
     */
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date date;
    /**
     * 日程起始时间
     * 时间戳格式(毫秒)
     */
    @NotBlank(message = "起始时间不能为空")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;
    /**
     * 日程终止时间
     * 时间戳格式
     */
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;
    /**
     * 持续时间
     */
    private int duration;
    /**
     * 日程的周期
     * 0: 不循环
     * x: 每x天循环一次
     */
    @NotBlank(message = "循环周期不能为空")
    private Integer cycle;
    /**
     * 该日程的状态
     * 1表示启用, 0表示禁用
     */
    @JsonIgnore
    private Integer status;

    public Event() {
    }

    public Event(Date startTime, int cycle) {
        this.startTime = startTime;
        this.cycle = cycle;
    }

    public Event(Integer eventId) {
        this.eventId = eventId;
    }

    public Event(String name) {
        this.name = name;
    }

    public Event(Integer eventId, String name) {
        this.eventId = eventId;
        this.name = name;
    }

    /**
     * 判断是不是课程
     *
     * @return 是返回true, 否则返回false
     */
    public boolean isLesson() {
        return EventType.EVENT_LESSON.getValue().equals(eventType);
    }

    /**
     * 判断是不是考试
     *
     * @return 是返回true, 否则返回false
     */
    public boolean isExam() {
        return EventType.EVENT_EXAM.getValue().equals(eventType);
    }

    /**
     * 判断是不是活动
     *
     * @return 是返回true, 否则返回false
     */
    public boolean isActivity() {
        return EventType.EVENT_ACTIVITY.getValue().equals(eventType);
    }

    /**
     * 判断是不是临时事务
     *
     * @return 是返回true, 否则返回false
     */
    public boolean isTemporary() {
        return EventType.EVENT_TEMPORARY.getValue().equals(eventType);
    }

    /**
     * 判断是不是闹钟
     *
     * @return 是返回true, 否则返回false
     */
    public boolean isClock() {
        return EventType.EVENT_CLOCK.getValue().equals(eventType);
    }

    @Override
    protected Event clone() throws CloneNotSupportedException {
        Event clone = null;
        try {
            clone = (Event) super.clone();

        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
        return clone;
    }

    /**
     * 日程日期加上一个周期
     *
     * @param dif 要加的天数
     * @return 改过日期后的新日程
     */
    public Event addCycle(long dif) throws CloneNotSupportedException {
        Event newEvent = this.clone();
        newEvent.date = TimeUtil.addDate(date, dif);
        newEvent.startTime = TimeUtil.addDate(startTime, dif);
        newEvent.endTime = TimeUtil.addDate(endTime, dif);
        return newEvent;
    }
}
