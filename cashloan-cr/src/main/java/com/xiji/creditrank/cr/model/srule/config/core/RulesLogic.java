package com.xiji.creditrank.cr.model.srule.config.core;

import java.util.List;
import java.util.Map;

import com.xiji.creditrank.cr.model.srule.config.rule.Rule;
import com.xiji.creditrank.cr.model.srule.exception.RuleNotFoundException;

/**
 * 定义规则之间的关系接口
 * 单条规则与规则之间的关系，可能很复杂，也可能很简单
 * @author wnb
 * @version 1.0.0
 * @date 2018/11/27
 */
public interface RulesLogic {


    /**
     * 获取整体逻辑关系的最终结果，同时也会将会计算各个规则的匹配结果
     *
     * @return
     */
    boolean doLogic(List<Rule> list);


    /**
     * 获取总规则中的各个规则的匹配结果，long代表各个规则的id，boolean代表各个规则的匹配结果
     *
     * @return
     */
    Map<Long, Boolean> rulesResult() throws RuleNotFoundException;


}
