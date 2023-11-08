package com.example.camera.Utils;


import com.example.camera.HCNetSDK;
import com.example.camera.entity.Msg;
import com.example.camera.service.impl.MsgServiceImpl;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.ByteByReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

import static com.example.camera.Utils.HikvisionUtil.*;


/**
 * @author jiangxin
 * @create 2022-08-15-18:04
 */
@Slf4j
@Component
public class AlarmDataParse {
    static FRealDataCallBack fRealDataCallBack;//预览回调函数实现
    static int lPlay = -1;  //预览句柄
    private static boolean isRecord = false;
    public static MsgServiceImpl msgService;
    @Autowired  //关键3
    public void setMsgServiceImpl (MsgServiceImpl msgService){
        AlarmDataParse.msgService = msgService;
    }


    public static void alarmDataHandle(int lCommand, HCNetSDK.NET_DVR_ALARMER pAlarmer, Pointer pAlarmInfo, int dwBufLen, Pointer pUser) {
        log.debug("报警事件类型： lCommand:" + Integer.toHexString(lCommand));
        //lCommand是传的报警类型
        switch (lCommand) {
            //异常行为检测信息
            case HCNetSDK.COMM_ALARM_RULE:
                HCNetSDK.NET_VCA_RULE_ALARM strVcaAlarm = new HCNetSDK.NET_VCA_RULE_ALARM();
                strVcaAlarm.write();
                Pointer pVCAInfo = strVcaAlarm.getPointer();
                pVCAInfo.write(0, pAlarmInfo.getByteArray(0, strVcaAlarm.size()), 0, strVcaAlarm.size());
                strVcaAlarm.read();
                strVcaAlarm.struRuleInfo.uEventParam.struIntrusion.byDetectionTarget=1;

                switch (strVcaAlarm.struRuleInfo.wEventTypeEx) {
                    case 1: //穿越警戒面 (越界侦测)
                        System.out.println("越界侦测报警发生");
                        strVcaAlarm.struRuleInfo.uEventParam.setType(HCNetSDK.NET_VCA_TRAVERSE_PLANE.class);
                        System.out.println("检测目标："+strVcaAlarm.struRuleInfo.uEventParam.struTraversePlane.byDetectionTarget); //检测目标，0表示所有目标（表示不锁定检测目标，所有目标都将进行检测），其他取值按位表示不同的检测目标：0x01-人，0x02-车
                        //图片保存
                        if ((strVcaAlarm.dwPicDataLen > 0) && (strVcaAlarm.byPicTransType == 0)) {
                            SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
                            String newName = sf.format(new Date());
                            FileOutputStream fout;
                            try {
                                String filename = "../pic/" + newName + "VCA_TRAVERSE_PLANE" + ".jpg";
                                fout = new FileOutputStream(filename);
                                //将字节写入文件
                                long offset = 0;
                                ByteBuffer buffers = strVcaAlarm.pImage.getByteBuffer(offset, strVcaAlarm.dwPicDataLen);
                                byte[] bytes = new byte[strVcaAlarm.dwPicDataLen];
                                buffers.rewind();
                                buffers.get(bytes);
                                fout.write(bytes);
                                fout.close();
                            } catch (FileNotFoundException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                        break;
                    case 2: //目标进入区域
                        System.out.println("目标进入区域报警发生");
                        strVcaAlarm.struRuleInfo.uEventParam.setType(HCNetSDK.NET_VCA_AREA.class);
                        System.out.println("检测目标："+strVcaAlarm.struRuleInfo.uEventParam.struArea.byDetectionTarget);
                        //图片保存
                        if ((strVcaAlarm.dwPicDataLen > 0) && (strVcaAlarm.byPicTransType == 0)) {
                            SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
                            String newName = sf.format(new Date());
                            FileOutputStream fout;
                            try {
                                String filename = "../pic/" + newName + "_TargetEnter" + ".jpg";
                                fout = new FileOutputStream(filename);
                                //将字节写入文件
                                long offset = 0;
                                ByteBuffer buffers = strVcaAlarm.pImage.getByteBuffer(offset, strVcaAlarm.dwPicDataLen);
                                byte[] bytes = new byte[strVcaAlarm.dwPicDataLen];
                                buffers.rewind();
                                buffers.get(bytes);
                                fout.write(bytes);
                                fout.close();
                            } catch (FileNotFoundException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                        break;
                    case 3: //目标离开区域
                        System.out.println("目标离开区域报警触发");
                        strVcaAlarm.struRuleInfo.uEventParam.setType(HCNetSDK.NET_VCA_AREA.class);
                        System.out.println("检测目标："+strVcaAlarm.struRuleInfo.uEventParam.struArea.byDetectionTarget);
                        //图片保存
                        if ((strVcaAlarm.dwPicDataLen > 0) && (strVcaAlarm.byPicTransType == 0)) {
                            SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
                            String newName = sf.format(new Date());
                            FileOutputStream fout;
                            try {
                                String filename = "../pic/" + newName + "_TargetLeave" + ".jpg";
                                fout = new FileOutputStream(filename);
                                //将字节写入文件
                                long offset = 0;
                                ByteBuffer buffers = strVcaAlarm.pImage.getByteBuffer(offset, strVcaAlarm.dwPicDataLen);
                                byte[] bytes = new byte[strVcaAlarm.dwPicDataLen];
                                buffers.rewind();
                                buffers.get(bytes);
                                fout.write(bytes);
                                fout.close();
                            } catch (FileNotFoundException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                        break;
                    case 4: //周界入侵
                        log.info("周界入侵报警发生");
                        strVcaAlarm.struRuleInfo.uEventParam.setType(HCNetSDK.NET_VCA_INTRUSION.class);
                        log.debug("检测目标："+strVcaAlarm.struRuleInfo.uEventParam.struIntrusion.byDetectionTarget);
                        captureJPEGPicture(lUserID,1,4,2,"E:\\save");
                        getRecord("周界入侵");

                        break;
                    case 5: //徘徊
                        log.info("徘徊事件触发");
                        break;
                    case 8: //快速移动(奔跑)，
                        log.info("快速移动(奔跑)事件触发");
                        break;
                    case 20: //倒地检测
                        log.info("倒地事件触发");
                        break;
                    case 45: //持续检测
                        log.info("持续检测事件触发");
                    default:
                        log.info("行为事件类型:" + strVcaAlarm.struRuleInfo.wEventTypeEx);
                        break;
                }
                break;


            case HCNetSDK.COMM_ALARM_V30:  //移动侦测、视频丢失、遮挡、IO信号量等报警信息(V3.0以上版本支持的设备)
                HCNetSDK.NET_DVR_ALARMINFO_V30 struAlarmInfo = new HCNetSDK.NET_DVR_ALARMINFO_V30();
                struAlarmInfo.write();
                Pointer pAlarmInfo_V30 = struAlarmInfo.getPointer();
                pAlarmInfo_V30.write(0, pAlarmInfo.getByteArray(0, struAlarmInfo.size()), 0, struAlarmInfo.size());
                struAlarmInfo.read();
                System.out.println("报警类型：" + struAlarmInfo.dwAlarmType);  // 3-移动侦测
                break;

            default:
                log.info("报警类型" + Integer.toHexString(lCommand));
                break;
        }
    }
    /**
     * 设备抓图（无预览）
     * 单帧设备抓取保存的图片为JPG，具体注意问题需要详细查看【设备网络sdk使用手册】
     * @param lUserID: 登录设备的id
     * @param lChannel： 设备通道
     * @param lPicSize： 图片大小
     * @param lPicQuality： 图片质量
     * @param sPicbuf： 图片保存路径
     * @return
     */
    public static void captureJPEGPicture(int lUserID, int lChannel, int lPicSize, int lPicQuality, String sPicbuf)
    {

        HCNetSDK.NET_DVR_JPEGPARA lpJpegPara = new HCNetSDK.NET_DVR_JPEGPARA();
        lpJpegPara.wPicSize = (short)lPicSize;
        lpJpegPara.wPicQuality = (short)lPicQuality;
        // 参数必须写入到结构体，否则在保存图片的时候异常图片
        lpJpegPara.write();
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
        String newDIrName = sf.format(new Date()).substring(0,8);
        String newPicName = sf.format(new Date());
        // 创建文件名称
        String datePath = sPicbuf+"\\"+newDIrName;
        // 判断文件夹是否创建
        File file = new File(datePath);
        if (!file.exists()){
            file.mkdirs();
        }
        // 文件名称
        String imageName = newPicName+".jpg";
        // 全路径
        String path = datePath+"\\"+imageName;
        // 开始抓图
        if (!hcNetSDK.NET_DVR_CaptureJPEGPicture(lUserID, lChannel, lpJpegPara, path.getBytes()))
        {
            log.error("{失败编码:"+hcNetSDK.NET_DVR_GetLastError()+"}");
        }else {
            log.info( "抓图成功！"+path);
        }
    }

    /**
     * 获取录像
     */
    public static int getRecord(String message){
        log.info("准备录像");
        //预览成功后 调用接口使视频资源保存到文件中
        String fileName = new Date().getTime()+"";
        HCNetSDK.NET_DVR_PREVIEWINFO strClientInfo = new HCNetSDK.NET_DVR_PREVIEWINFO();
        strClientInfo.read();
        strClientInfo.hPlayWnd = 0;  //窗口句柄，从回调取流不显示一般设置为空
        strClientInfo.lChannel = lDChannel;  //通道号
        strClientInfo.dwStreamType=0; //0-主码流，1-子码流，2-三码流，3-虚拟码流，以此类推
        strClientInfo.dwLinkMode=0; //连接方式：0- TCP方式，1- UDP方式，2- 多播方式，3- RTP方式，4- RTP/RTSP，5- RTP/HTTP，6- HRUDP（可靠传输） ，7- RTSP/HTTPS，8- NPQ
        strClientInfo.bBlocked=1;
        strClientInfo.write();
        lPlay = hcNetSDK.NET_DVR_RealPlay_V40(lUserID,strClientInfo,fRealDataCallBack,null);
        if (lPlay == -1) {
            int iErr = hcNetSDK.NET_DVR_GetLastError();
            log.error("取流失败" + iErr);
            return -1;
        }
        if(isRecord){
            log.info("正在录像");
            return 0;
        }
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
        String newDIrName = sf.format(new Date()).substring(0,8);
        // 创建文件名称
        String datePath = "E:\\save"+"\\"+newDIrName;
        // 判断文件夹是否创建
        File file = new File(datePath);
        if (!file.exists()){
            file.mkdirs();
        }
        if (!hcNetSDK.NET_DVR_SaveRealData_V30(lPlay, 2, datePath +"\\"+ fileName + ".mp4")) {
            log.error("保存视频文件到文件夹失败 错误码为:  " + hcNetSDK.NET_DVR_GetLastError());
            return -1;
        }
        log.info("开始录像");
        try {
            Thread.sleep(1000*5);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }finally {
            if(hcNetSDK.NET_DVR_StopSaveRealData(lPlay)){
                log.info("保存成功"+datePath+fileName);

              //  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                LocalDateTime localDateTime = LocalDateTime.now();
                Msg msg = new Msg(localDateTime,datePath+fileName+".mp4",ip, Integer.toString(port),message);

                msgService.save(msg);
            }else{
                log.error("保存失败"+hcNetSDK.NET_DVR_GetLastError());
            }
        }
        return 1;
    }


    static class FRealDataCallBack implements HCNetSDK.FRealDataCallBack_V30 {
        //预览回调
        int count = 0;

        @Override
        public void invoke(int lRealHandle, int dwDataType, ByteByReference pBuffer, int dwBufSize, Pointer pUser) {
            if (count == 100) {//降低打印频率
                System.out.println("码流数据回调...dwBufSize=" + dwBufSize);
                count = 0;
            }
            count++;

        }
    }
}
