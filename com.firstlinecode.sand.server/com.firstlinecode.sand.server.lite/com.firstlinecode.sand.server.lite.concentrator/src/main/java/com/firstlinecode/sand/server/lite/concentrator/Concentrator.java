package com.firstlinecode.sand.server.lite.concentrator;

import org.apache.ibatis.session.SqlSession;

import com.firstlinecode.sand.server.framework.things.concentrator.IConcentrator;
import com.firstlinecode.sand.server.framework.things.concentrator.Node;

public class Concentrator implements IConcentrator {
	private String deviceId;
	private SqlSession sqlSession;
	
	public Concentrator(String deviceId, SqlSession sqlSession) {
		this.deviceId = deviceId;
		this.sqlSession = sqlSession;
	}

	@Override
	public String[] getNodes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String createNode(Node node) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String confirm(String deviceId) {
		// TODO Auto-generated method stub
		return null;
	}

}