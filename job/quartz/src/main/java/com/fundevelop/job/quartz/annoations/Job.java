package com.fundevelop.job.quartz.annoations;

import org.quartz.Trigger;
import org.springframework.beans.factory.annotation.Required;

import java.lang.annotation.*;

/**
 * 计划任务注解.
 * <p>描述:定义计划任务注解可用属性及其定义</p>
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江)</a> 创建于 2016/4/29 17:17
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Job {
    /** 任务名称(必须指定)，必须是全局唯一  */
    @Required
    String name();

    /**
     * 任务运行时间表达式(必须指定)
     * <p>表达式格式说明：秒 分 小时 日期 月份 星期 年(可选)<br/>
     * “*” 代表整个时间段<br/>
     * “/”：为特别单位，表示为“每”如“0/15”表示每隔15分钟执行一次,“0”表示为从“0”分开始, “3/20”表示表示每隔20分钟执行一次，“3”表示从第3分钟开始执行<br/>
     * “?”：表示每月的某一天，或第周的某一天，表示不确定的值<br/>
     * “L”：用于每月，或每周，表示为每月的最后一天，或每个月的最后星期几如“6L”表示“每月的最后一个星期五”<br/>
     * “W”：表示为最近工作日，如“15W”放在每月（day-of-month）字段上表示为“到本月15日最近的工作日”<br/>
     * “#”：是用来指定“的”每月第n个工作日,例 在每周（day-of-week）这个字段中内容为"6#3" or "FRI#3" 则表示“每月第三个星期五”<br/>
     * “,”字符：指定数个值<br/>
     * “-”字符：指定一个值的范围<br/>
     * 秒：0-59, - * /<br/>
     * 分：0-59, - * /<br/>
     * 小时：0-23, - * /<br/>
     * 日期：1-31, - * ? / L W C<br/>
     * 月份：1-12 或 JAN-DEC, - * ? /<br/>
     * 星期：1-7 或 SUN-SAT, - * ? / L C #<br/>
     * 年(可选)：留空,1970-2099,- * /
     * </p>
     * <p>配置示例：<br/>
     * 0 0 12 * * ?： 每天中午12点触发 <br/>
     * 0 15 10 ? * *： 每天上午10:15触发 <br/>
     * 0 15 10 * * ?： 每天上午10:15触发 <br/>
     * 0 15 10 * * ? *： 每天上午10:15触发 <br/>
     * 0 15 10 * * ? 2005： 2005年的每天上午10:15触发 <br/>
     * 0 * 14 * * ?： 在每天下午2点到下午2:59期间的每1分钟触发 <br/>
     * 0 0/5 14 * * ?： 在每天下午2点到下午2:55期间的每5分钟触发 <br/>
     * 0 0/5 14,18 * * ?： 在每天下午2点到2:55期间和下午6点到6:55期间的每5分钟触发 <br/>
     * 0 0-5 14 * * ?： 在每天下午2点到下午2:05期间的每1分钟触发 <br/>
     * 0 10,44 14 ? 3 WED： 每年三月的星期三的下午2:10和2:44触发 <br/>
     * 0 15 10 ? * MON-FRI： 周一至周五的上午10:15触发 <br/>
     * 0 15 10 15 * ?： 每月15日上午10:15触发 <br/>
     * 0 15 10 L * ?： 每月最后一日的上午10:15触发 <br/>
     * 0 15 10 ? * 6L： 每月的最后一个星期五上午10:15触发 <br/>
     * 0 15 10 ? * 6L 2002-2005： 2002年至2005年的每月的最后一个星期五上午10:15触发 <br/>
     * 0 15 10 ? * 6#3： 每月的第三个星期五上午10:15触发 <br/>
     * 0 6 * * * *：每天早上6点<br/>
     * 0 0 *&#47;2 * * * ：每两个小时 <br/>
     * 0 0 23-7/2,8 * * * ：晚上11点到早上8点之间每两个小时，早上八点<br/>
     * 0 0 11 4 * 1-3 ：每个月的4号和每个礼拜的礼拜一到礼拜三的早上11点 <br/>
     * 0 0 4 1 1 *：1月1日早上4点 <br/>
     * </p>
     */
    @Required
    String cronExpression();

    /** 任务是否允许并发执行，默认为不允许，即只有当上一次任务执行完后才执行下一次任务 */
    boolean concurrent() default false;

    /** 任务描述 */
    String desc() default "";

    /** 开始时间<br/>
     * 表达式格式说明：yyyy-MM-dd HH:mm:ss
     */
    String startTime() default "";

    /** 结束时间.<br/>
     * 表达式格式说明：yyyy-MM-dd HH:mm:ss
     */
    String endTime() default "";

    /** 优先级. */
    int priority() default Trigger.DEFAULT_PRIORITY;

    /** 日历名称.
     * <p>
     * 日历名称对应的是日历Bean的名称，日历Bean必须实现org.quartz.Calendar接口
     * </p>
     */
    String calendarName() default "";
}
