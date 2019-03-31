package load.gen.rmq;

import com.google.common.collect.Maps;
import load.gen.DataProviderI;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

public class RMQDataProviderImpl implements DataProviderI {
    @Override
    public List<String> getIdsToPurge() {
        return null;
    }

    @Override
    public LinkedHashMap<String, String> getMockedData() {
        LinkedHashMap<String, String> linkedHashMap = Maps.newLinkedHashMap();
        linkedHashMap.put(UUID.randomUUID().toString(), UUID.randomUUID().toString());
        return linkedHashMap;
    }
}
