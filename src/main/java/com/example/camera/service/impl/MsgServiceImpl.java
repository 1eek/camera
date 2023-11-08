package com.example.camera.service.impl;

import com.example.camera.entity.Msg;
import com.example.camera.mapper.MsgMapper;
import com.example.camera.service.IMsgService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author leek
 * @since 2023-11-07
 */
@Service
public class MsgServiceImpl extends ServiceImpl<MsgMapper, Msg> implements IMsgService {

}
