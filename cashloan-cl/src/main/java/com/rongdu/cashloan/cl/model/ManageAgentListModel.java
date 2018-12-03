package com.rongdu.cashloan.cl.model;

import com.rongdu.cashloan.cl.domain.ProfitAgent;


public class ManageAgentListModel extends ProfitAgent{

	private static final long serialVersionUID = 1L;
	
	private String userName;

	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @param userName the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

}
