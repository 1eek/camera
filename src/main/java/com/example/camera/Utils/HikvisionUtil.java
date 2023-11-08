package com.example.camera.Utils;


import cc.eguid.FFmpegCommandManager.FFmpegManager;
import cc.eguid.FFmpegCommandManager.FFmpegManagerImpl;
import com.example.camera.HCNetSDK;
import com.sun.jna.Pointer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class HikvisionUtil {
    public static final HCNetSDK hcNetSDK = HCNetSDK.INSTANCE;

    public static int lUserID = -1;//用户句柄
    public static int lDChannel = -1;  //预览通道号
    private HCNetSDK.NET_DVR_DEVICEINFO_V40 m_strDeviceInfo;
    public static String ip = "192.168.0.237"; //摄像头ip
    protected static short port = 8000; //端口
    private String user = "admin"; //账号
    private String pwd = "qq123456";  //密码
    private static String url = ""; //推流播放地址
    private static String ThreadNum; //推流进程信息
    static int lAlarmHandle = -1;//报警布防句柄
    static HCNetSDK.FMSGCallBack_V31 fMSFCallBack_V31 = null; //报警布防回调函数


    /**
     * 初始化
     */
    @PostConstruct
    public void init() {
        boolean flag = hcNetSDK.NET_DVR_Init();
        String message = flag ? "成功" : "失败";
        log.info("摄像头初始化" + message);
        login_V40(ip, port, user, pwd);
        ThreadNum = startPlugFlow("1921680237", user, pwd, ip, "192.168.0.142", "1935", lDChannel);
        log.info("播放地址为：" + url);
        setAlarm();
    }


    /**
     * 设备登录V40 与V30功能一致
     *
     * @param ip   设备IP
     * @param port SDK端口，默认设备的8000端口
     * @param user 设备用户名
     * @param psw  设备密码
     */
    public void login_V40(String ip, short port, String user, String psw) {
        //注册
        HCNetSDK.NET_DVR_USER_LOGIN_INFO m_strLoginInfo = new HCNetSDK.NET_DVR_USER_LOGIN_INFO();//设备登录信息
        m_strDeviceInfo = new HCNetSDK.NET_DVR_DEVICEINFO_V40();//设备信息

        String m_sDeviceIP = ip;//设备ip地址
        m_strLoginInfo.sDeviceAddress = new byte[HCNetSDK.NET_DVR_DEV_ADDRESS_MAX_LEN];
        System.arraycopy(m_sDeviceIP.getBytes(), 0, m_strLoginInfo.sDeviceAddress, 0, m_sDeviceIP.length());

        String m_sUsername = user;//设备用户名
        m_strLoginInfo.sUserName = new byte[HCNetSDK.NET_DVR_LOGIN_USERNAME_MAX_LEN];
        System.arraycopy(m_sUsername.getBytes(), 0, m_strLoginInfo.sUserName, 0, m_sUsername.length());

        String m_sPassword = psw;//设备密码
        m_strLoginInfo.sPassword = new byte[HCNetSDK.NET_DVR_LOGIN_PASSWD_MAX_LEN];
        System.arraycopy(m_sPassword.getBytes(), 0, m_strLoginInfo.sPassword, 0, m_sPassword.length());

        m_strLoginInfo.wPort = port;
        m_strLoginInfo.bUseAsynLogin = false; //是否异步登录：0- 否，1- 是
        m_strLoginInfo.byLoginMode = 0;  //0- SDK私有协议，1- ISAPI协议
        m_strLoginInfo.write();

        lUserID = hcNetSDK.NET_DVR_Login_V40(m_strLoginInfo, m_strDeviceInfo);
        if (lUserID == -1) {
            log.error("登录失败，错误码为" + hcNetSDK.NET_DVR_GetLastError());
            return;
        } else {
            log.info(ip + ":设备登录成功！");
            //byStartDChan为IP通道起始通道号, 预览回放NVR的IP通道时需要根据起始通道号进行取值
            lDChannel = m_strDeviceInfo.struDeviceV30.byStartChan;
            log.info("通道号为" + lDChannel);
            return;
        }
    }


    private static final FFmpegManager manager = new FFmpegManagerImpl();

    /**
     * 开始推流
     *
     * @param appName       进程名称,为相机ip去"."
     * @param account       登录相机账号
     * @param password      密码
     * @param ip            ip 相机ip
     * @param nginxIp       nginxIp nginx的ip
     * @param nginxPort     nginxIp nginx的端口
     * @param channelNumber 相机的通道号
     * @return String appName
     */
    public static String startPlugFlow(String appName, String account, String password, String ip,
                                       String nginxIp, String nginxPort, int channelNumber) {
        //如果进程存在,则直接返回进程名

        Map<String, String> map = new HashMap<>(10);
        //尝试用TCP方式 进程名
        map.put("appName", appName);
        // 自定义的参数，需要修改ffmpeg源代码
        map.put("rtspTransport", "tcp");
        //组装rtsp流
        map.put("input", "rtsp://" + account + ":" + password + "@" + ip + "/Streaming/Channels/20" + channelNumber);
        //rtmp流.live为nginx-rtmp的配置
        map.put("output", "rtmp://" + nginxIp + ":" + nginxPort + "/live/");
        map.put("codec", "libx264");
        //只推元码流
        map.put("twoPart", "1");
        map.put("fmt", "flv");
        // 执行任务，就是appName，如果执行失败返回为null
        url = "http://" + nginxIp + ":" + nginxPort + "/live?port=1935&app=live&stream=" + appName;
        String url = manager.start(appName, "ffmpeg -i rtsp://" + account + ":" + password + "@" + ip + "/Streaming/Channels/" + channelNumber +
                "02 -f flv -an -vcodec libx264 rtmp://" + nginxIp + ":" + nginxPort + "/live/" + appName);
        return url;


    }

    /**
     * 停止推流
     *
     * @return
     */
    public static boolean stopFlow() {
        return manager.stop(ThreadNum);
    }

    /**
     * 报警布防接口
     *
     * @param
     */
    public static void setAlarm() {
        if (lAlarmHandle < 0)//尚未布防,需要布防
        {
            //报警布防参数设置
            HCNetSDK.NET_DVR_SETUPALARM_PARAM m_strAlarmInfo = new HCNetSDK.NET_DVR_SETUPALARM_PARAM();
            m_strAlarmInfo.dwSize = m_strAlarmInfo.size();
            m_strAlarmInfo.byLevel = 0;  //布防等级
            m_strAlarmInfo.byAlarmInfoType = 1;   // 智能交通报警信息上传类型：0- 老报警信息（NET_DVR_PLATE_RESULT），1- 新报警信息(NET_ITS_PLATE_RESULT)
            m_strAlarmInfo.byDeployType = 0;   //布防类型：0-客户端布防，1-实时布防
            m_strAlarmInfo.write();

            lAlarmHandle = hcNetSDK.NET_DVR_SetupAlarmChan_V41(lUserID, m_strAlarmInfo);
            log.debug("lAlarmHandle: " + lAlarmHandle);
            if (lAlarmHandle == -1) {
                log.error("布防失败，错误码为" + hcNetSDK.NET_DVR_GetLastError());
                return;
            } else {
                log.info("布防成功");
                //设置报警回调函数
                if (fMSFCallBack_V31 == null) {
                    fMSFCallBack_V31 = new FMSGCallBack_V31();
                    Pointer pUser = null;
                    if (!hcNetSDK.NET_DVR_SetDVRMessageCallBack_V31(fMSFCallBack_V31, pUser)) {
                        log.error("设置回调函数失败!");
                        return;
                    } else {
                        log.info("设置回调函数成功!");
                    }
                }

            }
        } else {
            System.out.println("设备已经布防，请先撤防！");
        }


    }


}
