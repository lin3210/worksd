package com.task;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.service.HongBaoService;
import com.thinkive.base.jdbc.DataRow;
import com.thinkive.base.util.DateHelper;
import com.thinkive.timerengine.Task;

public class HongBaoTask implements Task{
	private static Logger logger = Logger.getLogger(HongBaoTask.class);
	private static HongBaoService hongbaoservice = new HongBaoService();
	@Override
	public void execute() {
		// TODO Auto-generated method stub
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String now = sdf.format(new Date());
		DecimalFormat df = new DecimalFormat("0.00");  
		Date  time = new Date();
		System.out.println(time);
		time.setMonth(time.getMonth()-3);
		String curdate = DateHelper.formatDate(time,"yyyyMMdd");
		logger.info("��ʼ���"+curdate+"��ǰδʹ�ú�����û�");
		if("20150509".equals(curdate)){
			curdate = "20150809";
			List<DataRow> list = new ArrayList();
			list = hongbaoservice.getUserList(curdate);
//			DataRow row1 = new DataRow();
//			row1.set("id", 8399);
//			row1.set("usablesum", 7.9);
//			list.add(row1);
//			DataRow row2 = new DataRow();
//			row2.set("id", 8459);
//			row2.set("usablesum", 1.68);
//			list.add(row2);
			if(list.size()>0){
				for(DataRow dataRow : list){
					//��ѯ��ǰ�û�ע����
					String id = dataRow.getString("id");
					//�鿴��������˻����Ƚ�
					DataRow data = hongbaoservice.getHb(id);
					if(data != null){
						double hb = data.getDouble("money");
						double usablesum = dataRow.getDouble("usablesum");
						if(usablesum >= hb){
							DataRow d = dataRow;
							d.set("usablesum", df.format(usablesum-hb));
							d.set("hongb", 2);
							hongbaoservice.updateUser(d);
							DataRow row = new DataRow();
							row.set("money", -hb);
							row.set("userid", id);
							row.set("type", 6);
							row.set("time", now);
							hongbaoservice.insert(row);
							logger.info("����û���"+id+"����ɹ�");
						}
					}
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			logger.info("�������");
		}
	}
	@SuppressWarnings("deprecation")
	public static void main(String[] args) {
//		new HongBaoTask().execute();
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		Date d = new Date();
//		String time = sdf.format(d);
//		logger.info(time);
//		d.setDate(d.getDate()-1);
//		String time2 = sdf.format(d);
//		logger.info(time2);
		DecimalFormat famt = new DecimalFormat("###,###");
		
		List<DataRow> list= hongbaoservice.getUserJKList();
		int fklv = 65;
		for(DataRow row:list) {
			int jkid =  row.getInt("id");
			int shjk= Integer.parseInt(row.getString("sjsh_money").replaceAll(",", ""));
			System.out.println("jkid:"+jkid);
			DataRow inrow = new DataRow();
			inrow.set("sjds_money", famt.format(shjk * fklv / 100));
			inrow.set("lx", famt.format(shjk * (100 - fklv) / 100));
			inrow.set("id", jkid);
			hongbaoservice.updateUserJK(inrow);
			
		}
	}
}
