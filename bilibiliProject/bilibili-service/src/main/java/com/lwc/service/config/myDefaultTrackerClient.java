package com.lwc.service.config;

import com.github.tobato.fastdfs.domain.fdfs.StorageNode;
import com.github.tobato.fastdfs.domain.fdfs.StorageNodeInfo;
import com.github.tobato.fastdfs.service.DefaultTrackerClient;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/**
 * ClassName: myDefaultTrackerClient
 * Description:
 *老版本fastDFS存在bug,storage的默认端口为0而不是linux中的23000,所以写个配置类修改一下
 * @Author 林伟朝
 * @Create 2024/10/21 21:57
 */

@Primary
@Component
public class myDefaultTrackerClient extends DefaultTrackerClient {
    @Override
    public StorageNode getStoreStorage(String groupName) {
        StorageNode result=super.getStoreStorage(groupName);
        result.setPort(23000);
        return result;
    }

    @Override
    public StorageNodeInfo getUpdateStorage(String groupName, String filename) {
        StorageNodeInfo result=super.getUpdateStorage(groupName,filename);
        result.setPort(23000);
        return result;
    }
}

