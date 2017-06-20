package entities;

import java.io.Serializable;

public class AgentType implements Serializable {

	private static final long serialVersionUID = -1431717179567069604L;
	
	private String name;
	private String module;
	
	public AgentType() {
	}
	
	public AgentType(String name) {
		this.name = name;
	}

	public AgentType(String name, String module) {
		this.name = name;
		this.module = module;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof AgentType) {
			AgentType type = (AgentType) obj;
			if(type.getName().equals(name))
				return true;
			else
				return false;
		}
		return super.equals(obj);
	}

}
