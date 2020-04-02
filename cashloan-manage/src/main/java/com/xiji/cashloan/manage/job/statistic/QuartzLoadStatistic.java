package com.xiji.cashloan.manage.job.statistic;

import com.xiji.cashloan.cl.domain.statistic.LoadStatisticData;
import com.xiji.cashloan.cl.service.statistic.LoadStatisticDataService;
import com.xiji.cashloan.core.common.util.DateUtil;
import com.xiji.cashloan.manage.domain.QuartzInfo;
import com.xiji.cashloan.manage.domain.QuartzLog;
import com.xiji.cashloan.manage.service.QuartzInfoService;
import com.xiji.cashloan.manage.service.QuartzLogService;
import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import tool.util.BeanUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @auther : wnb
 * @date : 2019/2/18
 * @describe :放款统计
 */

@Component
@Lazy(value = false)
public class QuartzLoadStatistic implements Job {
    private static final Logger logger = Logger.getLogger(QuartzLoadStatistic.class);


    /**
     * 添加放款统计
     * @return
     */
    public String insertLoadStatistic(){
        logger.info("进入放款统计数据.....");
        LoadStatisticDataService loadStatisticDataService = (LoadStatisticDataService) BeanUtil.getBean("loadStatisticDataService");
        Date lateDate = loadStatisticDataService.getLateDate();

        Map<String,Object> params = new HashMap<>();

        Date dateBefore = DateUtil.getDateBefore(-1,new Date());

        DateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd");
        String endDateStr = dateFormat.format(dateBefore);
        List<LoadStatisticData> loadStatisticDataList;
        params.put("endDate",endDateStr);
        if (lateDate != null) {
            String startDateStr = dateFormat.format(lateDate);
            params.put("startDate", startDateStr);
        }

        loadStatisticDataList = loadStatisticDataService.listLoadStatisticData(params);
        for(LoadStatisticData loadStatisticData :loadStatisticDataList ){
            loadStatisticData.setCreateTime(new Date());
            loadStatisticDataService.insert(loadStatisticData);
        }

        return "成功统计了"+loadStatisticDataList.size()+"天放款数据报表";
    }

    @Override
    public void execute(JobExecutionContext context){
        QuartzInfoService quartzInfoService = (QuartzInfoService) BeanUtil.getBean("quartzInfoService");
        QuartzLogService quartzLogService = (QuartzLogService) BeanUtil.getBean("quartzLogService");
        // 查询当前任务信息
        QuartzInfo quartzInfo = quartzInfoService.findByCode("insertLoadStatistic");
        Map<String, Object> qiData = new HashMap<>();
        qiData.put("id", quartzInfo.getId());

        QuartzLog quartzLog = new QuartzLog();
        quartzLog.setQuartzId(quartzInfo.getId());
        quartzLog.setStartTime(DateUtil.getNow());
        try {
            String remark = insertLoadStatistic();
            quartzLog.setTime(DateUtil.getNow().getTime() - quartzLog.getStartTime().getTime());
            quartzLog.setResult("10");
            quartzLog.setRemark(remark);
            qiData.put("succeed", quartzInfo.getSucceed() + 1);
        } catch (Exception e) {
            quartzLog.setResult("20");
            qiData.put("fail", quartzInfo.getFail() + 1);
            logger.error(e.getMessage(), e);
        } finally {
            logger.info("保存放款统计数据定时任务执行记录");
            quartzLogService.save(quartzLog);
            quartzInfoService.update(qiData);
        }
    }
}
