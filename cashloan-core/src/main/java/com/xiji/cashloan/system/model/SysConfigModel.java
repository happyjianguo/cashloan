package com.xiji.cashloan.system.model;

import java.util.Map;

import com.xiji.cashloan.system.domain.SysConfig;

/**
 * 
 * 系统参数实体类
 * @author wnb
 * @date 2018/11/27
 * @version 1.0.0
 */
public class SysConfigModel extends SysConfig {

	private static final long serialVersionUID = 1L;

	//系统类别 中文
	private String typeStr;

	public String getTypeStr() {
		return typeStr;
	}

	public void setTypeStr(String typeStr) {
		this.typeStr = typeStr;
	}

	public SysConfigModel getSysModel(SysConfig sys,Map<String, Object> dataMap) {
		SysConfigModel model=new SysConfigModel();
		model.setId(sys.getId());
		model.setCode(sys.getCode());
		model.setCreator(sys.getCreator());
		model.setName(sys.getName());
		model.setRemark(sys.getRemark());
		model.setStatus(sys.getStatus());
		model.setType(sys.getType());
		model.setValue(sys.getValue());
		model.setTypeStr((String)dataMap.get(String.valueOf(sys.getType())));
		return model;
	}
	

}
