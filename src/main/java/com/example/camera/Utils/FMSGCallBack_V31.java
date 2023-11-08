
package com.example.camera.Utils;

import com.example.camera.HCNetSDK;
import com.sun.jna.Pointer;


public class FMSGCallBack_V31 implements HCNetSDK.FMSGCallBack_V31 {
    //报警信息回调函数

    @Override
    public boolean invoke(int lCommand, HCNetSDK.NET_DVR_ALARMER pAlarmer, Pointer pAlarmInfo, int dwBufLen, Pointer pUser) {
        AlarmDataParse.alarmDataHandle(lCommand,pAlarmer,pAlarmInfo, dwBufLen,pUser);
        return true;
    }
}







