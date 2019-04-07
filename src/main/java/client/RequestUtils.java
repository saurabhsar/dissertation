package client;

import com.sun.jersey.api.client.ClientResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RequestUtils {
    public static void handleErrorDefault(ClientResponse response, String clientName) {

        if (response.getStatus() >= 400 && response.getStatus() < 500) {
            String entity = response.getEntity(String.class);
            if (log.isErrorEnabled()){
                log.error("Client [{}] Request Failed with status :: {}, error : {} ", clientName, response.getStatus(), entity);
            }
            throw new RuntimeException(entity);
        }
        if (response.getStatus() >= 500) {
            String entity = response.getEntity(String.class);
            if (log.isErrorEnabled()) {
                log.error("Client [{}] Request Failed with status :: {}, error : {} ", clientName, response.getStatus(), entity);
            }
            throw new RuntimeException(entity);
        }
    }
}
