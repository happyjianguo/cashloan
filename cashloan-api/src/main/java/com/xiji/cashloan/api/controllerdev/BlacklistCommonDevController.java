package com.xiji.cashloan.api.controllerdev;

import com.xiji.cashloan.cl.service.ClBorrowService;
import com.xiji.cashloan.cl.service.impl.assist.blacklist.BlacklistBaseTask;
import com.xiji.cashloan.cl.service.impl.assist.blacklist.BlacklistProcess;
import com.xiji.cashloan.cl.service.impl.assist.blacklist.BlacklistUtil;
import com.xiji.cashloan.core.common.web.controller.BaseController;
import com.xiji.cashloan.core.domain.Borrow;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @Auther: king
 * @Date: 2018/11/28 16:31
 * @Description:
 */
@Controller
@Scope("prototype")
public class BlacklistCommonDevController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(BlacklistCommonDevController.class);
    @Resource
    private ClBorrowService clBorrowService;
    private static ExecutorService fixedThreadPool = Executors.newFixedThreadPool(10);

    @RequestMapping(value = "/blacklist/common/submitTask.htm",method = {RequestMethod.POST, RequestMethod.GET})
    public void task(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String borrowId = req.getParameter("borrowId");
            logger.info(borrowId);
            Borrow borrow = clBorrowService.findByPrimary(Long.parseLong(borrowId));
            Map<String, BlacklistProcess> taskMap = BlacklistUtil.getBaseTaskHashMap();
            for (Map.Entry<String, BlacklistProcess> entry : taskMap.entrySet()) {
                BlacklistProcess task = entry.getValue();
                if (task != null) {
                    fixedThreadPool.submit(new BlacklistBaseTask(task,borrow));
                }
            }
            resp.setContentType("application/json");
            resp.setCharacterEncoding("utf8");
            resp.getOutputStream().write("success".getBytes("utf8"));
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            resp.getOutputStream().write(e.getMessage().getBytes("UTF-8"));
        }
    }

}