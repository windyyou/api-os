package com.fnst.cloudapi.service.openstackapi.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fnst.cloudapi.dao.common.NotificationListMapper;
import com.fnst.cloudapi.dao.common.TerminalMapper;
import com.fnst.cloudapi.exception.BusinessException;
import com.fnst.cloudapi.pojo.openstackapi.forgui.NotificationList;
import com.fnst.cloudapi.pojo.openstackapi.forgui.Terminal;
import com.fnst.cloudapi.service.openstackapi.NotificationListService;
import com.fnst.cloudapi.util.UUIDHelper;

@Service("notificationListService")
public class NotificationListServiceImpl implements NotificationListService {

	@Autowired
	private NotificationListMapper notificationListMapper;
	
	@Autowired
	private TerminalMapper terminalMapper;
	
	@Override
	public List<NotificationList> getNotificationLists(Map<String, String> paraMap, String tokenId) {

		List<NotificationList> notificationLists = new ArrayList<>();
		
		notificationLists = notificationListMapper.selectAll();
		
		return notificationLists;
	}

	@Override
	public int insertNotificationList(NotificationList notificationList) throws BusinessException {
		
		String uuid = UUIDHelper.buildUUIDStr();
		notificationList.setId(uuid);
		int flag = notificationListMapper.insertSelective(notificationList);
		if(flag == 1 && notificationList.getTerminals() != null) {
			List<Terminal> terminals = notificationList.getTerminals();
			for(Terminal terminal : terminals) {
				terminal.setNotificationListId(uuid);
				flag = terminalMapper.insertSelective(terminal);
				if(flag == 0) break;
			}
		}
		
		return flag;
	}

	@Override
	public NotificationList selectNotificationListById(String id) throws BusinessException {
		return notificationListMapper.selectByPrimaryKey(id);
	}

	@Override
	public int deleteNotificationListById(String id) throws BusinessException {
		return notificationListMapper.deleteByUpdateDelFlag(id);
	}

	@Override
	public int updateNotificationListById(NotificationList notificationList) throws BusinessException {
		return notificationListMapper.updateByPrimaryKeySelective(notificationList);
	}

	@Override
	public int insertTerminal(Terminal terminal) throws BusinessException {
		return terminalMapper.insertSelective(terminal);
	}

	@Override
	public int deleteTerminal(String id) throws BusinessException {
		return terminalMapper.deleteByUpdateDelFlag(id);
	}
	
}
