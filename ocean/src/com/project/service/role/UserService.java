package com.project.service.role;

import java.util.List;

import org.apache.log4j.Logger;

import com.thinkive.base.jdbc.DBPage;
import com.thinkive.base.jdbc.DataRow;
import com.thinkive.base.jdbc.JdbcTemplate;
import com.thinkive.base.jdbc.session.Session;
import com.thinkive.base.service.BaseService;

public class UserService extends BaseService{
	private static Logger logger = Logger.getLogger(UserService.class);
	
	public JdbcTemplate getJdbcTemplate() {
		return getJdbcTemplate("web");
	}
	
/***************************************xiong start	*************************************/
	
	
	/**
	 * xiong-审核人员
	 * @param curPage
	 * @param numPerPage
	 * @return
	 */
	public DBPage selectUserShenheList(int curPage ,int numPerPage){
		String sql = "select user_id ,name, phone ,rolename ,login_times, last_time ,state,u.leaver,u.fdqx from sdcms_user u left join sdcms_user_role r on u.roleid=r.id  where state=1 ";
		
		sql +=" AND roleid=2  order by  u.fdqx desc,user_id " ;
	    return getJdbcTemplate().queryPage(sql, curPage, numPerPage);
	}
	
	/**
	 * xiong-修改审核人员审核权限
	 * @param roleid
	 * @param userid
	 */
	public int updateUserShenhefdqx(int user_id,int fdqx){		
			String sql="update sdcms_user set fdqx="+fdqx+" where user_id="+user_id;
			logger.info("修改审核人员审核权限"+sql);
			return getJdbcTemplate().update(sql);			
	}	
	
	/**
	 * xiong-更改员工请假天数-20190709
	 * @param roleid
	 * @param userid
	 */
	public int updateUserLeaveNum(int user_id,int leavenum){		
			String sql="update sdcms_user set leaver="+leavenum+" where user_id="+user_id;
			logger.info("手动修改催收人员请假天数"+sql);
			return getJdbcTemplate().update(sql);			
	}	
	
	
	/**
	 * xiong-催收人员
	 * @param curPage
	 * @param numPerPage
	 * @return
	 */
	public DBPage selectUserLeavetList(int curPage ,int numPerPage){
		String sql = "select user_id ,name, phone ,rolename ,login_times, last_time ,state,u.leaver from sdcms_user u left join sdcms_user_role r on u.roleid=r.id  where fdqx=1 ";
		
		sql +=" AND (roleid=21 or roleid=22 or roleid=23 or roleid=25 or roleid=50 or roleid=24 or roleid=51 or roleid=26 or roleid=54) order by  u.ROLEID,u.USER_ID" ;
	    return getJdbcTemplate().queryPage(sql, curPage, numPerPage);
	}
	
	/**
	 * xiong-异常登录
	 * @param curPage
	 * @param numPerPage
	 * @return
	 */
	public DBPage selectUserLoginExceptionList(int curPage ,int numPerPage){
//		String sql = "select  su.name,su.USER_ID, sc.ip,sc.createTime,sc.phone,sc.result from sd_cmsip sc left join sdcms_user su on sc.phone = su.phone GROUP BY sc.phone order by sc.id desc";
		 StringBuffer sb = new StringBuffer();
		 sb.append("select  su.name,su.user_id, sc.ip, date_format(sc.createTime,'%Y-%m-%d %h:%m:%s') as time,sc.phone,sc.result from sd_cmsip sc left join sdcms_user su on sc.phone = su.phone ");
		 sb.append(" WHERE  sc.ip NOT IN ('118.69.224.164','116.118.114.241','118.69.191.195','127.0.0.1','0:0:0:0:0:0:0:1') ");
		 sb.append(" GROUP BY sc.phone order by sc.id desc ");
		 logger.info("异常登录sql语句=="+sb.toString());
		 return getJdbcTemplate().queryPage(sb.toString(), curPage, numPerPage);
	}
	
/***************************************xiong end	*************************************/
		
	/**
	 * 分页查询用户
	 * @param curPage
	 * @param numPerPage
	 * @return
	 */
	public DBPage selectUserList(int curPage ,int numPerPage, int type){
		String sql = "select user_id ,name, phone ,rolename ,login_times, last_time ,state from sdcms_user u left join sdcms_user_role r on u.roleid=r.id  where 1 =1 ";
		if(type==1){
			sql +="and u.roleid=10 and state=1";
		}
		sql +=" order by  last_time desc" ;
	    return getJdbcTemplate().queryPage(sql, curPage, numPerPage);
	}
	
	/**
	 * 分页按条件查询用户
	 * @param curPage
	 * @param numPerPage
	 * @return
	 */
	public DBPage selectUserListByCondtion(int curPage ,int numPerPage, int type,String name,String phone){
		String sql = "select user_id ,name, phone ,rolename ,px,login_times,create_date, last_time ,state,kfcxcs from sdcms_user u left join sdcms_user_role r on u.roleid=r.id  where state=1 and (u.roleid=2 or u.roleid=17)";
		if(type==1){
			sql +="and u.roleid=10 and state=1";
		}
		if(!name.equals("")){
			sql+=" and NAME like '%"+name+"%' ";
		}
		if(!phone.equals("")){
			sql+=" and phone like '%"+phone+"%' ";
		}
		sql +=" order by  px" ;
	    return getJdbcTemplate().queryPage(sql, curPage, numPerPage);
	}
	/**
	 * 分页按条件查询用户
	 * @param curPage
	 * @param numPerPage
	 * @return
	 */
	public DBPage selectUserListByCondtionFK(int curPage ,int numPerPage, int type,String name,String phone){
		String sql = "select user_id ,name, phone ,rolename ,px,login_times, last_time ,state,kfcxcs from sdcms_user u left join sdcms_user_role r on u.roleid=r.id  where (u.roleid=1 or u.roleid=4) and user_id<>888 and user_id<>222 and user_id<>6 and user_id<>8 and user_id<>3 and state=1  ";
		if(type==1){
			sql +="and u.roleid=10 and state=1";
		}
		if(!name.equals("")){
			sql+=" and NAME like '%"+name+"%' ";
		}
		if(!phone.equals("")){
			sql+=" and phone like '%"+phone+"%' ";
		}
		sql +=" order by px" ;
		return getJdbcTemplate().queryPage(sql, curPage, numPerPage);
	}
	/**
	 * 分页按条件查询用户
	 * @param curPage
	 * @param numPerPage
	 * @return
	 */
	public DBPage selectUserListByCondtionKFHF(int curPage ,int numPerPage, int type,String name,String phone){
		String sql = "select user_id ,name, phone ,rolename ,px,login_times, last_time ,state,kfcxcs from sdcms_user u left join sdcms_user_role r on u.roleid=r.id  where (u.roleid=27 or u.roleid=32) and state=1  ";
		if(type==1){
			sql +="and u.roleid=10 and state=1";
		}
		if(!name.equals("")){
			sql+=" and NAME like '%"+name+"%' ";
		}
		if(!phone.equals("")){
			sql+=" and phone like '%"+phone+"%' ";
		}
		sql +=" order by px" ;
		return getJdbcTemplate().queryPage(sql, curPage, numPerPage);
	}
	/**
	 * 分页按条件查询用户
	 * @param curPage
	 * @param numPerPage
	 * @return
	 */
	public DBPage selectUserListByCondtionCS(int curPage ,int numPerPage, int type,String name,String phone){
		String sql = "select user_id ,name, phone ,rolename ,px,login_times, last_time ,state,kfcxcs from sdcms_user u left join sdcms_user_role r on u.roleid=r.id  where state=1 and (u.roleid=19 or u.roleid=20 or u.roleid=21 or u.roleid=22 or u.roleid=23 or u.roleid=24 or u.roleid=25 or u.roleid=30 or u.roleid=31 or u.roleid=36 or u.roleid=37 or u.roleid=38 or u.roleid=40 or u.roleid=41 or u.roleid=42 or u.roleid=43 or u.roleid=44 or u.roleid=45 or u.roleid=46 or u.roleid=47 or u.roleid=48 or u.roleid=49) ";
		if(type==1){
			sql +="and u.roleid=10 and state=1";
		}
		if(!name.equals("")){
			sql+=" and NAME like '%"+name+"%' ";
		}
		if(!phone.equals("")){
			sql+=" and phone like '%"+phone+"%' ";
		}
		sql +=" order by px " ;
		return getJdbcTemplate().queryPage(sql, curPage, numPerPage);
	}
	public DBPage selectUserListByCondtionCSM0(int curPage ,int numPerPage, int type,String name,String phone){
		String sql = "select user_id ,name, phone ,rolename ,px,login_times, last_time ,state,kfcxcs from sdcms_user u left join sdcms_user_role r on u.roleid=r.id  where state=1 and (u.roleid=2 or u.roleid=16 or u.roleid=17) ";
		if(type==1){
			sql +="and u.roleid=10 and state=1";
		}
		if(!name.equals("")){
			sql+=" and NAME like '%"+name+"%' ";
		}
		if(!phone.equals("")){
			sql+=" and phone like '%"+phone+"%' ";
		}
		sql +=" order by px " ;
		return getJdbcTemplate().queryPage(sql, curPage, numPerPage);
	}
	public DBPage selectUserListByCondtionCSM1(int curPage ,int numPerPage, int type,String name,String phone){
		String sql = "select user_id ,name, phone ,rolename ,px,login_times, last_time ,state,kfcxcs from sdcms_user u left join sdcms_user_role r on u.roleid=r.id  where state=1 and (u.roleid=21 or u.roleid=22 or u.roleid=23 or u.roleid=25 or u.roleid=50) ";
		if(type==1){
			sql +="and u.roleid=10 and state=1";
		}
		if(!name.equals("")){
			sql+=" and NAME like '%"+name+"%' ";
		}
		if(!phone.equals("")){
			sql+=" and phone like '%"+phone+"%' ";
		}
		sql +=" order by px " ;
		return getJdbcTemplate().queryPage(sql, curPage, numPerPage);
	}
	public DBPage selectUserListByCondtionCSM2(int curPage ,int numPerPage, int type,String name,String phone){
		String sql = "select user_id ,name, phone ,rolename ,px,login_times, last_time ,state,kfcxcs from sdcms_user u left join sdcms_user_role r on u.roleid=r.id  where state=1 and (u.roleid=24 or u.roleid=51)";
		if(type==1){
			sql +="and u.roleid=10 and state=1";
		}
		if(!name.equals("")){
			sql+=" and NAME like '%"+name+"%' ";
		}
		if(!phone.equals("")){
			sql+=" and phone like '%"+phone+"%' ";
		}
		sql +=" order by px " ;
		return getJdbcTemplate().queryPage(sql, curPage, numPerPage);
	}
	public DBPage selectUserListByCondtionCSM3(int curPage ,int numPerPage, int type,String name,String phone){
		String sql = "select user_id ,name, phone ,rolename ,px,login_times, last_time ,state,kfcxcs from sdcms_user u left join sdcms_user_role r on u.roleid=r.id  where state=1 and (u.roleid=26 or u.roleid=54)";
		if(type==1){
			sql +="and u.roleid=10 and state=1";
		}
		if(!name.equals("")){
			sql+=" and NAME like '%"+name+"%' ";
		}
		if(!phone.equals("")){
			sql+=" and phone like '%"+phone+"%' ";
		}
		sql +=" order by px " ;
		return getJdbcTemplate().queryPage(sql, curPage, numPerPage);
	}
	public DBPage selectUserListByCondtionCSM1ZG(int curPage ,int numPerPage, int type,String name,String phone){
		String sql = "select user_id ,name, phone ,rolename ,px,login_times, last_time ,state,kfcxcs from sdcms_user u left join sdcms_user_role r on u.roleid=r.id  where state=1 and (u.roleid=21 or u.roleid=22 or u.roleid=23 or u.roleid=25 or u.roleid=50) ";
		if(type==1){
			sql +="and u.roleid=10 and state=1";
		}
		if(!name.equals("")){
			sql+=" and NAME like '%"+name+"%' ";
		}
		if(!phone.equals("")){
			sql+=" and phone like '%"+phone+"%' ";
		}
		sql +=" order by user_id " ;
		return getJdbcTemplate().queryPage(sql, curPage, numPerPage);
	}
	public DBPage selectUserListByCondtionCSM2ZG(int curPage ,int numPerPage, int type,String name,String phone){
		String sql = "select user_id ,name, phone ,rolename ,px,login_times, last_time ,state,kfcxcs from sdcms_user u left join sdcms_user_role r on u.roleid=r.id  where state=1 and (u.roleid=24 or u.roleid=51)";
		if(type==1){
			sql +="and u.roleid=10 and state=1";
		}
		if(!name.equals("")){
			sql+=" and NAME like '%"+name+"%' ";
		}
		if(!phone.equals("")){
			sql+=" and phone like '%"+phone+"%' ";
		}
		sql +=" order by user_id " ;
		return getJdbcTemplate().queryPage(sql, curPage, numPerPage);
	}
	public DBPage selectUserListByCondtionCSM3ZG(int curPage ,int numPerPage, int type,String name,String phone){
		String sql = "select user_id ,name, phone ,rolename ,px,login_times, last_time ,state,kfcxcs from sdcms_user u left join sdcms_user_role r on u.roleid=r.id  where state=1 and (u.roleid=26 or u.roleid=54)";
		if(type==1){
			sql +="and u.roleid=10 and state=1";
		}
		if(!name.equals("")){
			sql+=" and NAME like '%"+name+"%' ";
		}
		if(!phone.equals("")){
			sql+=" and phone like '%"+phone+"%' ";
		}
		sql +=" order by user_id " ;
		return getJdbcTemplate().queryPage(sql, curPage, numPerPage);
	}
	/**
	 * 分页按条件查询用户
	 * @param curPage
	 * @param numPerPage
	 * @return
	 */
	public DBPage selectCSWBUserListByCondtionCS(int curPage ,int numPerPage, int type,String name,String phone){
		String sql = "select user_id ,name, phone ,rolename ,px,login_times, last_time ,state,kfcxcs from sdcms_user_cs u left join sdcms_user_role_cs r on u.roleid=r.id  where state=1 and u.roleid=1 ";
		
		if(!name.equals("")){
			sql+=" and NAME like '%"+name+"%' ";
		}
		if(!phone.equals("")){
			sql+=" and phone like '%"+phone+"%' ";
		}
		sql +=" order by px " ;
		return getJdbcTemplate().queryPage(sql, curPage, numPerPage);
	}
	/**
	 * 分页按条件查询用户
	 * @param curPage
	 * @param numPerPage
	 * @return
	 */
	public DBPage selectUserListByCondtionTX(int curPage ,int numPerPage, int type,String name,String phone){
		String sql = "select user_id ,name, phone ,rolename ,px,login_times, last_time ,state,kfcxcs from sdcms_user u left join sdcms_user_role r on u.roleid=r.id  where state=1 and (u.roleid=53) ";
		if(type==1){
			sql +="and u.roleid=10 and state=1";
		}
		if(!name.equals("")){
			sql+=" and NAME like '%"+name+"%' ";
		}
		if(!phone.equals("")){
			sql+=" and phone like '%"+phone+"%' ";
		}
		sql +=" order by px " ;
		return getJdbcTemplate().queryPage(sql, curPage, numPerPage);
	}
	
	/**
	 * 查询所有没有角色的客户
	 * @param curPage
	 * @param numPerPage
	 * @param type
	 * @return
	 */
	public List<DataRow> selectNoUserList(){
		String sql = "select user_id ,name, phone ,rolename ,login_times, last_time ,state from sdcms_user u left join sdcms_user_role r on u.roleid=r.id  where ";
		sql +=" u.roleid=100 and state=1";
		sql +=" order by  last_time desc" ;
	    return getJdbcTemplate().query(sql);
	}

	
	/**
	 * 根据角色查询成员
	 * @param curPage
	 * @param numPerPage
	 * @param roleId
	 * @return
	 */
	public DBPage selectUserByRole(int curPage ,int numPerPage,int roleId){
		String sql = "select user_id ,name, phone ,rolename ,login_times, last_time ,state from sdcms_user u left join sdcms_user_role r on u.roleid=r.id  where  u.roleid="+roleId;
		sql +=" order by  last_time desc" ;
		return getJdbcTemplate().queryPage(sql, curPage, numPerPage);
	}
	
	/**
	 * 根据用户ID和角色ID把用户从改角色里修改
	 * @param roleid
	 * @param userid
	 */
	public Boolean updateUserFromRole(int roleid,int userid){
		Session session = getSession("web");
		session.beginTrans();
		try {
			String sql="update sdcms_user set roleid="+roleid+" where user_id="+userid;
			getJdbcTemplate().update(sql);
			session.commitTrans();
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			logger.error(e);
			e.printStackTrace();
			session.rollbackTrans();
		}finally{session.close();}
		return false;
	}
	
	public boolean addUser(String tableName,DataRow dataRow){
		Session session = getSession("web");
		session.beginTrans();
		try {
			getJdbcTemplate().insert(tableName, dataRow);
			session.commitTrans();
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			logger.error(e);
			e.printStackTrace();
			session.rollbackTrans();
		}finally{session.close();}
		return false;
	}
	
	public Boolean updateUser(String name,String phone,int state,int user_id){
		Session session = getSession("web");
		session.beginTrans();
		try {
			String sql="update sdcms_user set name='"+name+"',phone='"+phone+"',state="+state+" where user_id="+user_id;
			getJdbcTemplate().update(sql);
			session.commitTrans();
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			logger.error(e);
			e.printStackTrace();
			session.rollbackTrans();
		}finally{session.close();}
		return false;
	}
	
	public Boolean updateUserPwd(int userid,String pwd){
		Session session=getSession("web");
		session.beginTrans();
		try {
			String sql="update sdcms_user set PASSWORD='"+pwd+"' where user_id="+userid;
			getJdbcTemplate().update(sql);
			session.commitTrans();
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			logger.error(e);
			e.printStackTrace();
			session.rollbackTrans();
		}finally{
			session.close();
		}
		return false;
	}

}
