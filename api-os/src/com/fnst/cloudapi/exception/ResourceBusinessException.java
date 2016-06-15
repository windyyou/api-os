package com.fnst.cloudapi.exception;

import java.util.Locale;

import com.fnst.cloudapi.util.Message;

public class ResourceBusinessException extends BusinessException {

	private static final long serialVersionUID = -5597846931484161460L;

	private String message = null;


	public ResourceBusinessException(String messageID,String... params) {
		super(messageID,params);
		message = ResourceBusinessException.createMessage(messageID, params, true);
	}

	public ResourceBusinessException(String messageID,Throwable throwable,
			String... params) {
		super(messageID,params, throwable);
		message = ResourceBusinessException.createMessage(messageID, params, true);
	}

	
	@Override
	public String getMessage() {
		return message;
	}

    public String getResponseMessage(){
    	StringBuilder sb = new StringBuilder();
    	sb.append("{");
    	sb.append(this.message);
    	sb.append("}");
    	return sb.toString();
    }
    
	public String getMessage(boolean withMsgId) {
		return ResourceBusinessException.createMessage(getMessageID(),getParams(), withMsgId);
	}

	
	@Override
	public void setMessageID(String messageID) {
		super.setMessageID(messageID);
		message = ResourceBusinessException.createMessage(messageID, getParams(),true);
	}

	
	@Override
	public void setParams(String[] params) {
		super.setParams(params);
		message = ResourceBusinessException.createMessage(getMessageID(), params,true);
	}

	
	private static String createMessage(String messageID, String[] params,
			boolean withMsgId) {
		return Message.getMessage(messageID, Locale.getDefault(),params, withMsgId);
	}
}
