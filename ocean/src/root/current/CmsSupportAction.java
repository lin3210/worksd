package root.current;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.project.service.account.JBDUserService;
import com.project.service.account.JBDcmsService;
import com.thinkive.base.jdbc.DataRow;
import com.thinkive.base.util.SessionHelper;
import com.thinkive.web.base.ActionResult;
import com.thinkive.web.base.BaseAction;

import net.sf.json.JSONObject;

public class CmsSupportAction extends BaseAction {
    private static Logger logger = Logger.getLogger(JBDcmsAction.class);
    private static JBDcmsService jbdcmsService = new JBDcmsService();
    private static JBDUserService jbdUserService = new JBDUserService();
    public CmsSupportAction() {
    }

    public ActionResult doRetrieveAuditors() {
        JSONObject jsonObject = new JSONObject();
        int cmsuserid = SessionHelper.getInt("cmsuserid", getSession());// 后台登录账户
        if (cmsuserid == 0) {
            jsonObject.put("error", -1); // Vui lòng đăng nhập trước
            this.getWriter().write(jsonObject.toString());
            return null;
        }
        //Map<String, List<DataRow>> groupAuditors = jbdUserService.getAllGroupAuditors();
        List<DataRow> auditors = jbdUserService.getAuditors();
        jsonObject.put("auditors", auditors);
        jsonObject.put("error", 0);
        jsonObject.put("msg", "ok");
        this.getResponse().setHeader("content-type", "application/json;charset=utf-8");
        this.getWriter().write(jsonObject.toString());
        return null;
    }
    
    public ActionResult doRetrieveUserRole() {
        JSONObject jsonObject = new JSONObject();
        int cmsuserid = SessionHelper.getInt("cmsuserid", getSession());// 后台登录账户
        if (cmsuserid == 0) {
            jsonObject.put("error", -1); // Vui lòng đăng nhập trước
            this.getWriter().write(jsonObject.toString());
            return null;
        }
        DataRow row = jbdUserService.getUserRoles(cmsuserid);
        jsonObject.put("user", row);
        jsonObject.put("error", 0);
        jsonObject.put("msg", "ok");
        this.getResponse().setHeader("content-type", "application/json;charset=utf-8");
        this.getWriter().write(jsonObject.toString());
        return null;
    }
    
    public ActionResult doRetrieveCollectGroup() {
        JSONObject jsonObject = new JSONObject();
        int cmsuserid = SessionHelper.getInt("cmsuserid", getSession());// 后台登录账户
        if (cmsuserid == 0) {
            jsonObject.put("error", -1); // Vui lòng đăng nhập trước
            this.getWriter().write(jsonObject.toString());
            return null;
        }
        DataRow row = jbdUserService.getUserRoles(cmsuserid);
        int roleId = row.getInt("roleid");
        //M3
        List<Integer> roles = new ArrayList<Integer>();
        if(roleId == 54) {
            roles.add(54);
            roles.add(26);
        } else if(roleId == 50){
            //M1
            roles.add(50);
            roles.add(21);
            roles.add(22);
            roles.add(23);
        } else if (roleId == 51) {
            //M2
            roles.add(51);
            roles.add(24);
        } else if (roleId == 32) {
            //提醒
            roles.add(32);
        } else if (roleId == 1 || roleId == 52){
            //M1
            roles.add(50);
            roles.add(21);
            roles.add(22);
            roles.add(23);
          //M2
            roles.add(51);
            roles.add(24);
            
          //提醒
            roles.add(32);
            // M3
            roles.add(54);
            roles.add(26);
        }else {
            if (cmsuserid == 0) {
                jsonObject.put("error", -1); // Vui lòng đăng nhập trước
                this.getWriter().write(jsonObject.toString());
                return null;
            }
        }
        List<DataRow> list = jbdUserService.getUsersByRoles(roles);
        jsonObject.put("userList", list);
        jsonObject.put("error", 0);
        jsonObject.put("msg", "ok");
        this.getResponse().setHeader("content-type", "application/json;charset=utf-8");
        this.getWriter().write(jsonObject.toString());
        return null;
    }
    
    public ActionResult switchCollectionMember() {
        
        return null;
    }
}
