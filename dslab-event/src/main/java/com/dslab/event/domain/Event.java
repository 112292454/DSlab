package com.dslab.event.domain;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @program: DSlab
 * @description: 事件父类
 * @author: 郭晨旭
 * @create: 2023-03-26 18:33
 * @version: 1.0
 **/

@Data
public class Event implements Serializable {
    @Serial
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
     * 日程的形式 (lesson / exam / activity / clock / temporary)
     */
    @NotBlank(message = "日程类型不能为空")
    private String eventType;
    /**
     * 日程地点的形式 (online / offline)
     * online 的话就不用导航
     */
    @NotBlank(message = "地点类型不能为空")
    private String positionType;
    /**
     * 参与人数的性质 (personal / group)
     */
    @NotBlank(message = "人员类型不能为空")
    private String memberType;
    /**
     * 日程地点, 线下建筑物的id
     */
    private Integer buildingId;
    /**
     * 日程地点, 线上链接
     */
    private String link;
    /**
     * 日程日期
     */
    private String date;
    /**
     * 日程起始时间
     * 时间戳格式(毫秒)
     */
    @NotBlank(message = "起始时间不能为空")
    private String startTime;
    /**
     * 日程终止时间
     * 时间戳格式
     */
    private String endTime;
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
    private int cycle;

    public Event() {
    }

    public Event(String startTime, int cycle) {
        this.startTime = startTime;
        this.cycle = cycle;
    }

}
