package com.qf.listener;

import com.qf.entity.Goods;
import com.qf.service.ISearchService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * RabbitMQ消息的监听类
 */
@Component
public class RabbitMqListener {

    @Autowired
    private ISearchService searchService;

    @RabbitListener(queues="search_queue")
    public void goodsMsgHandler(Goods goods){
        System.out.println("接收到RabbitMQ的消息");
        //把消息内容同步到索引库
        searchService.insert(goods);
    }
}
