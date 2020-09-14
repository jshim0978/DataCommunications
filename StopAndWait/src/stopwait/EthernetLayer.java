package stopwait;

import java.util.ArrayList;



public class EthernetLayer  implements BaseLayer{
	public int nUpperLayerCount = 0;
	public String pLayerName = null;
	public BaseLayer p_UnderLayer = null;
	public ArrayList<BaseLayer> p_aUpperLayer = new ArrayList<BaseLayer>();
	
	private class _ETHERNET_ADDR {
		private byte[] addr = new byte[6];
		
		public _ETHERNET_ADDR() {
			this.addr[0] = (byte) 0x00;
			this.addr[1] = (byte) 0x00;
			this.addr[2] = (byte) 0x00;
			this.addr[3] = (byte) 0x00;
			this.addr[4] = (byte) 0x00;
			this.addr[5] = (byte) 0x00;
			
		}
	}
	private class _ETHERNET_Frame {
		_ETHERNET_ADDR enet_dstaddr;
		_ETHERNET_ADDR enet_srcaddr;
		
		byte[] enet_type;
		byte[] enet_data;
		
		public _ETHERNET_Frame() {
			this.enet_dstaddr = new _ETHERNET_ADDR();
			this.enet_srcaddr = new _ETHERNET_ADDR();
			this.enet_type = new byte[2];
			enet_type[0] = 0x00;
			enet_type[1] = 0x00;
			this.enet_data = null;
		}
	}
	_ETHERNET_Frame EthFrame = new _ETHERNET_Frame();
	
	public byte[] AddEthHeader(_ETHERNET_Frame EthFrame, byte[] input, int length) {
		byte[] buf = new byte[length +14];
		
		byte[] srctemp =  Eth2byte(EthFrame.enet_dstaddr);
		byte[] dsttemp =  Eth2byte(EthFrame.enet_srcaddr);
		byte[] typetemp =  (EthFrame.enet_type);
	
		
		buf[0] = dsttemp[0];
		buf[1] = dsttemp[1];
		buf[2] = dsttemp[2];
		buf[3] = dsttemp[3];
		buf[4] = dsttemp[4];
		buf[5] = dsttemp[5];
		buf[6] = srctemp[0];
		buf[7] = srctemp[1];
		buf[8] = srctemp[2];
		buf[9] = srctemp[3];
		buf[10] = srctemp[4];
		buf[11] = srctemp[5];
		buf[12] = EthFrame.enet_type[0];
		buf[13] = EthFrame.enet_type[1];

		for (int i = 0; i < length; i++)
			buf[14 + i] = input[i];

		return buf;
	}
	
	
	
	
	
	public EthernetLayer(String pName) {
		// super(pName);
		// TODO Auto-generated constructor stub
		pLayerName = pName;
		//ResetHeader();
	}
	
	
	public byte[] RemoveEthHeader(byte[] input, int length) {

		for (int i = 0; i < (input.length - 14); i++) {
			input[i] = input[i+14];
		}
		
		return input;
	}

	public boolean Send(byte[] input, int length) {

		byte[] bytes = AddEthHeader(EthFrame, input, length);
		this.GetUnderLayer().Send(bytes, length +14);
		
		return true;
	}

	

	public synchronized boolean Receive(byte[] input) {
		
		byte[] data;
		
		byte[] temp_src = Eth2byte(EthFrame.enet_srcaddr);
	
		
		if (input[0] == 0xff &&input[1] == 0xff &&input[2] == 0xff &&input[3] == 0xff &&input[4] == 0xff &&input[5] == 0xff ) {
			System.out.println("broadcast");
			data = RemoveEthHeader(input, input.length);
			this.GetUpperLayer(0).Receive(data);
			return true;
		}
		else {
			
			for (int i = 0; i < 4; i++) {
				
				if (input[i] != temp_src[i]) {
					
					System.out.println("declined receive");
					
					return false;
				}
			}
		}
		data = RemoveEthHeader(input, input.length);
		this.GetUpperLayer(0).Receive(data);
		System.out.println("received");
		return true;
	}
	
	
	
	

	@Override
	public void SetUnderLayer(BaseLayer pUnderLayer) {
		if (pUnderLayer == null)
			return;
		this.p_UnderLayer = pUnderLayer;
	}
	@Override
	public void SetUpperLayer(BaseLayer pUpperLayer) {
		if (pUpperLayer == null)
			return;
		this.p_aUpperLayer.add(nUpperLayerCount++, pUpperLayer);		
	}
	@Override
	public String GetLayerName() {		
		return pLayerName;
	}
	@Override
	public BaseLayer GetUnderLayer() {
		if (p_UnderLayer == null)
			
			return null;
		
		return p_UnderLayer;
	}
	@Override
	public BaseLayer GetUpperLayer(int nindex) {
		if (nindex < 0 || nindex > nUpperLayerCount || nUpperLayerCount < 0)
			
			return null;
		return p_aUpperLayer.get(nindex);
		
	}
	@Override
	public void SetUpperUnderLayer(BaseLayer pUULayer) {
		this.SetUpperLayer(pUULayer);
		pUULayer.SetUnderLayer(this);
	}
	
	public byte[] int2Byte(int[] input){
		
		byte[] buf = new byte[6];
		
		
		for (int i = 0; i < buf.length; i++) {
			buf[i]= (byte)input[i];
		}
		return buf;
	}
	public _ETHERNET_ADDR byte2Eth(byte[] input){
		_ETHERNET_ADDR temp = new _ETHERNET_ADDR();
		
		for (int i = 0; i < input.length; i++) {
			temp.addr[i] = input[i];
		}
		return temp;
	}
	public byte[] Eth2byte(_ETHERNET_ADDR input){
		byte[] temp = new byte[6];
		
		for (int i = 0; i < 6; i++) {
			input.addr[i] = temp[i];
		}
		return temp;
	}
	public void SetEnetSrcAddress(int[] srcAddress) {
		// TODO Auto-generated method stub
		EthFrame.enet_srcaddr =  byte2Eth(int2Byte(srcAddress));
	}
	public void SetEnetDstAddress(int[] dstAddress) {
		// TODO Auto-generated method stub
		EthFrame.enet_dstaddr = byte2Eth(int2Byte(dstAddress));
	}

}
