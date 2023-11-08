package com.example.camera.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * <p>
 * 
 * </p>
 *
 * @author leek
 * @since 2023-11-07
 */
@NoArgsConstructor
@ApiModel(value = "Msg对象", description = "")
public class Msg implements Serializable {




    private static final long serialVersionUID = 1L;

      private Integer id;

      @ApiModelProperty("创建时间")
      private LocalDateTime createTime;

      @ApiModelProperty("视频的绝对路径")
      private String url;

      @ApiModelProperty("摄像头的ip地址")
      private String ip;

      @ApiModelProperty("摄像头的通道号")
      private String port;

      @ApiModelProperty("描述信息")
      private String msg;


    public Msg(LocalDateTime createTime, String url, String ip, String port, String msg) {
        this.createTime = createTime;
        this.url = url;
        this.ip = ip;
        this.port = port;
        this.msg = msg;
    }

    public Integer getId() {
        return id;
    }

      public void setId(Integer id) {
          this.id = id;
      }
    
    public LocalDateTime getCreateTime() {
        return createTime;
    }

      public void setCreateTime(LocalDateTime createTime) {
          this.createTime = createTime;
      }
    
    public String getUrl() {
        return url;
    }

      public void setUrl(String url) {
          this.url = url;
      }
    
    public String getIp() {
        return ip;
    }

      public void setIp(String ip) {
          this.ip = ip;
      }
    
    public String getPort() {
        return port;
    }

      public void setPort(String port) {
          this.port = port;
      }
    
    public String getMsg() {
        return msg;
    }

      public void setMsg(String msg) {
          this.msg = msg;
      }

    @Override
    public String toString() {
        return "Msg{" +
              "id = " + id +
                  ", createTime = " + createTime +
                  ", url = " + url +
                  ", ip = " + ip +
                  ", port = " + port +
                  ", msg = " + msg +
              "}";
    }
}
