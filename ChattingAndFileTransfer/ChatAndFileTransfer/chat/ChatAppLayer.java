package chat;

import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.util.Arrays;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class ChatAppLayer extends BaseLayer {
	byte[] app_tolen =new byte[2];
	byte[] app_type = new byte[1];
	byte[] app_unused = new byte[1];
	byte[] app_data = new byte[1456];
	long start=0, end;
	Gggui gui = new Gggui();
	boolean ack=true;
	Test test = new Test();
	public ChatAppLayer(String Layer) {
		super(Layer);
		Layername = Layer;
	}

	public boolean Receive(byte[] data){//1460
		//		if(data[1]==2){//�Ϲ����� �޼������� ack �޼������� ����
		//			ack=false;
		//		}else{
		System.arraycopy(data, 4, app_data, 0, 1456);
		System.out.println(new String(app_data));
		test.frame.chatlist.add("��� : "+ new String(app_data));
		//			byte[] send_ack = new byte[1456];
		//			send_thread_ack thread = new send_thread_ack(send_ack);
		//			Thread obj = new Thread(thread);
		//			obj.start();
		//		}
		return false;
	}
	public void setUpperLayer(BaseLayer Layer){
		UpperLayer = Layer;
	}
	public void setUnderLayer(BaseLayer Layer){
		UnderLayer = Layer;
	}

	public  int byteArrayToInt(byte bytes[]) {
		return ((((int)bytes[0] & 0xff) << 24) |
				(((int)bytes[1] & 0xff) << 16) |
				(((int)bytes[2] & 0xff) << 8) |
				(((int)bytes[3] & 0xff)));
	}

	public synchronized boolean Send(byte[] data){//1456
		start = System.currentTimeMillis();
		send_thread thread = new send_thread(data);
		Thread obj = new Thread(thread);
		obj.start();
		//		try {
		//			Thread.sleep(2000);
		//			if(!ack){//Ÿ�Ӿƿ��޼����� �޳� �ȹ޳� üũ
		//				JOptionPane.showMessageDialog(null, "TIME-OUT");
		//				ack=true;
		//			}
		//		} catch (InterruptedException e) {
		//			// TODO Auto-generated catch block
		//			e.printStackTrace();
		//		}
		return false;
	}
	public class send_thread implements Runnable{
		byte[] data;
		send_thread(byte[] data){
			this.data=data;
		}
		@Override
		public void run() {
			byte[] send_data = new byte[1460];
			send_data[0]=1;//tcp���� �Ÿ��� ����
			System.arraycopy(data, 0, send_data, 4, data.length);
			UnderLayer.Send(send_data);		
		}
	}

	public long bytesToLong(byte[] bytes) {
		ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
		buffer.put(bytes);
		buffer.flip();//need flip 
		return buffer.getLong();
	}
	//	end=System.currentTimeMillis();
	//	byte[] end_time_byte = new byte[8];	//���� �ð� byte ����
	//	ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);//����Ʈ ���ۿ� �����ð� long ���� �ְ�
	//	buffer.putLong(end);//�ð� long byteȭ
	//	end_time_byte = buffer.array();//buffer �� byte array�� ������ְ�

}