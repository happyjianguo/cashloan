package com.xiji.cashloan.manage.controller;

import com.github.pagehelper.Page;
import com.xiji.cashloan.cl.model.ManageOperatorReportLinkModel;
import com.xiji.cashloan.cl.service.OperatorReportLinkService;
import com.xiji.cashloan.core.common.context.Constant;
import com.xiji.cashloan.core.common.context.Global;
import com.xiji.cashloan.core.common.util.JsonUtil;
import com.xiji.cashloan.core.common.util.RdPage;
import com.xiji.cashloan.core.common.util.ServletUtils;
import com.xiji.cashloan.core.common.util.StringUtil;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Resource;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Auther: king
 * @Date: 2018/12/5 15:32
 * @Description:
 */
@Scope("prototype")
@Controller
public class ManageOperatorReportLinkController extends ManageBaseController{
    @Resource
    private OperatorReportLinkService operatorReportLinkService;
    /**
     * @param current
     * @param pageSize
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/modules/manage/operator/reportLink/page.htm", method = RequestMethod.POST)
    public void page(
        @RequestParam(value = "search", required = false) String search,
        @RequestParam(value = "current") int current,
        @RequestParam(value = "pageSize") int pageSize) throws Exception {

        Map<String, Object> searchMap = new HashMap<>();
        if (!StringUtils.isEmpty(search)) {
            searchMap = JsonUtil.parse(search, Map.class);
        }
        String preFixUrl = Global.getValue("magic_prefix_report_url");
        Page<ManageOperatorReportLinkModel> page = operatorReportLinkService.page(current, pageSize,
            searchMap);

        for (ManageOperatorReportLinkModel linkModel : page) {
            if (linkModel != null) {
                if (StringUtil.isNotEmpty(linkModel.getMessage())) {
                    linkModel.setOperateUrl(preFixUrl+linkModel.getMessage());
                }
            }
        }

        Map<String, Object> result = new HashMap<String, Object>();
        result.put(Constant.RESPONSE_DATA, page);
        result.put(Constant.RESPONSE_DATA_PAGE, new RdPage(page));
        result.put(Constant.RESPONSE_CODE, Constant.SUCCEED_CODE_VALUE);
        result.put(Constant.RESPONSE_CODE_MSG, "查询成功");
        ServletUtils.writeToResponse(response, result);
    }
}
