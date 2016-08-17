package com.fundevelop.plugin.sms.dao;

import com.fundevelop.persistence.jpa.BaseDao;
import com.fundevelop.plugin.sms.entity.SmsEntity;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * 短信信息Dao.
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江)</a> 创建于 2016/4/29 21:38
 */
public interface SmsDao extends BaseDao<SmsEntity, Long> {
    /**
     * 获取待发送短信.
     */
    @Query(value = "select * from fun_sms where status='1' and send_time is null " +
            "union select * from fun_sms where status='1' and send_time<date_add(now(),interval 5 SECOND) " +
            "order by priority desc,create_time asc", nativeQuery = true)
    List<SmsEntity> findWaitSendSms();

    /**
     * 获取待发送短信.
     */
    @Query(value = "select * from fun_sms where status='1' and system=?1 and send_time is null " +
            "union select * from fun_sms where status='1' and system=?1 and send_time<date_add(now(),interval 5 SECOND) " +
            "order by priority desc,create_time asc", nativeQuery = true)
    List<SmsEntity> findWaitSendSms(String system);

    /**
     * 获取待发送短信.
     */
    @Query(value = "select * from fun_sms where status='1' and system=?1 and module=?2 and send_time is null " +
            "union select * from fun_sms where status='1' and system=?1 and module=?2 and send_time<date_add(now(),interval 5 SECOND) " +
            "order by priority desc,create_time asc", nativeQuery = true)
    List<SmsEntity> findWaitSendSms(String system, String module);
}
