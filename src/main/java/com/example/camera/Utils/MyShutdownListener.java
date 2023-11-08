package com.example.camera.Utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;

import static com.example.camera.Utils.AlarmDataParse.lPlay;
import static com.example.camera.Utils.HikvisionUtil.*;

@Slf4j
public class MyShutdownListener implements ApplicationListener<ContextClosedEvent> {
    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        // 执行清理操作

        boolean flag = hcNetSDK.NET_DVR_Cleanup();
        String message = flag ? "成功" : "失败";
        log.info("摄像头关闭资源" + message);
        if (stopFlow()) {
            log.info("停止视频流成功");
        } else {
            log.info("停止视频流失败");
        }
        if (hcNetSDK.NET_DVR_StopRealPlay(lPlay))
            log.info("停止视频流成功");
        else {
            log.warn("停止视频流失败");
        }
        if (lAlarmHandle > -1) {
            if (hcNetSDK.NET_DVR_CloseAlarmChan(lAlarmHandle)) {
                log.info("撤防成功");
            } else {
                log.warn("撤防失败");
            }
        }

        if (lUserID > -1) {
            if (hcNetSDK.NET_DVR_Logout(lUserID)) {
                System.out.println("注销成功");
            }
        }
        System.out.println("执行清理操作，在应用程序关闭之前");
    }
}