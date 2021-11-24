package org.apache.rocketmq.common.statictopic;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.ImmutableList;
import org.apache.rocketmq.remoting.protocol.RemotingSerializable;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

public class TopicQueueMappingTest {

    @Test
    public void testJsonSerialize() {
        LogicQueueMappingItem mappingItem = new LogicQueueMappingItem(1, 2, "broker01", 33333333333333333L, 44444444444444444L, 555555555555555555L, 6666666666666666L, 77777777777777777L);
        String mappingItemJson = JSON.toJSONString(mappingItem) ;
        {
            Map<String, Object> mappingItemMap = JSON.parseObject(mappingItemJson, Map.class);
            Assert.assertEquals(8, mappingItemMap.size());
            Assert.assertEquals(mappingItemMap.get("bname"), mappingItem.getBname());
            Assert.assertEquals(mappingItemMap.get("gen"), mappingItem.getGen());
            Assert.assertEquals(mappingItemMap.get("logicOffset"), mappingItem.getLogicOffset());
            Assert.assertEquals(mappingItemMap.get("startOffset"), mappingItem.getStartOffset());
            Assert.assertEquals(mappingItemMap.get("endOffset"), mappingItem.getEndOffset());
            Assert.assertEquals(mappingItemMap.get("timeOfStart"), mappingItem.getTimeOfStart());
            Assert.assertEquals(mappingItemMap.get("timeOfEnd"), mappingItem.getTimeOfEnd());

        }
        //test the decode encode
        {
            LogicQueueMappingItem mappingItemFromJson = RemotingSerializable.fromJson(mappingItemJson, LogicQueueMappingItem.class);
            Assert.assertEquals(mappingItemJson, RemotingSerializable.toJson(mappingItemFromJson, false));
        }
        TopicQueueMappingDetail mappingDetail = new TopicQueueMappingDetail("test", 1, "broker01", System.currentTimeMillis());
        TopicQueueMappingDetail.putMappingInfo(mappingDetail, 0, ImmutableList.of(mappingItem));

        String mappingDetailJson = JSON.toJSONString(mappingDetail);
        {
            Map  mappingDetailMap = JSON.parseObject(mappingDetailJson);
            Assert.assertTrue(mappingDetailMap.containsKey("currIdMap"));
            Assert.assertEquals(7, mappingDetailMap.size());
            Assert.assertEquals(1, ((JSONObject) mappingDetailMap.get("hostedQueues")).size());
            Assert.assertEquals(1, ((JSONArray)((JSONObject) mappingDetailMap.get("hostedQueues")).get("0")).size());
        }
        {
            TopicQueueMappingDetail mappingDetailFromJson = RemotingSerializable.decode(mappingDetailJson.getBytes(), TopicQueueMappingDetail.class);
            Assert.assertEquals(1, mappingDetailFromJson.getHostedQueues().size());
            Assert.assertEquals(1, mappingDetailFromJson.getHostedQueues().get(0).size());
            Assert.assertEquals(mappingDetailJson, RemotingSerializable.toJson(mappingDetailFromJson, false));
        }
    }
}