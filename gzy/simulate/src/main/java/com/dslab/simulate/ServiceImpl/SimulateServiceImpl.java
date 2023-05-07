package com.dslab.simulate.ServiceImpl;

import com.dslab.commonapi.services.SimulateService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

@Service
@DubboService(group = "DSlab",interfaceClass = SimulateService.class)
public class SimulateServiceImpl implements SimulateService {

}
