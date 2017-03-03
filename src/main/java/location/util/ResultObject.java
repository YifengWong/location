package location.util;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ResultObject {
	
	private String result = null;
	
	private String state = null;
	
	private Object object = null;
	
	
	public ResultObject(final String result, final String state, final Object object) {
		this.result = result;
		this.state = state;
		this.object = object;
	}
	
	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public Object getObject() {
		return object;
	}


	public void setObject(Object object) {
		this.object = object;
	}
	
	
	@JsonIgnore
	public String getJsonString() throws Exception{
		return new ObjectMapper().writeValueAsString(this);
    }

	
}