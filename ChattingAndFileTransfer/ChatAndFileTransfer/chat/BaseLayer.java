package chat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public abstract class BaseLayer {
	String Layername;
	BaseLayer UpperLayer,UnderLayer,UpperLayer1,UnderLayer1;
	public BaseLayer(String Layer){
		Layername = Layer;
	}
	public void setUpperLayer(BaseLayer layer){
		UpperLayer = layer;
	}
	public void setUnderLayer(BaseLayer layer){
		UnderLayer = layer;
	}
	public BaseLayer getUpperLayer(){
		return UpperLayer;
	}
	public BaseLayer getUnderLayer(){
		return UnderLayer;
	}
	public String getLayerName(){
		return Layername;
	}
	
	public boolean Send(byte[] data){
		return false;
	}
	public boolean Receive(byte[] data) {
		return false;
	}
	public String Receive1(byte[] data){
		return "";
	}
	public File Receive2(byte[] data){
		return new File("");
	}
}
