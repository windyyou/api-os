package com.fnst.cloudapi.pojo.hello;

public class HelloTest {
	
    private long id;
    private String content;
    private String type;
    
    public HelloTest(){
        this.id = 100;
        this.content = "null";
        this.type="null";
    	
    }

    public HelloTest(long id, String content,String type) {
        this.id = id;
        this.content = content;
        this.type=type;
    }

    public long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

	public String getType() {
		return type;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public void setType(String type) {
		this.type = type;
	}
    
}
