package load.gen;

import java.util.LinkedHashMap;
import java.util.List;

public interface DataProviderI {

    List<String> getIdsToPurge();

    LinkedHashMap<String, String> getMockedData();
}
