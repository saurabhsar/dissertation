package load.gen;

import resource.RequestType;

public interface LoadGenI {
    void initialize(boolean transactional, boolean durable, RequestType requestType);

    void run();
}
