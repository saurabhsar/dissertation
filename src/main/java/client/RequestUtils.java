package client;

import com.sun.jersey.api.client.ClientResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RequestUtils {
    public static void handleErrorDefault(ClientResponse response, String clientName) {

        if (response.getStatus() >= 400 && response.getStatus() < 500) {
            doSomething(response, clientName);
        } else if (response.getStatus() >= 500) {
            doSomething(response, clientName);
        } else {
            response.close();
        }
    }

    private static void doSomething(ClientResponse response, String clientName) {
        String entity = null;
        try {
            entity = response.getEntity(String.class);
            if (log.isErrorEnabled()) {
                log.error("Client [{}] Request Failed with status :: {}, error : {} ", clientName, response.getStatus(), entity);
            }
        } finally {
            response.close();
        }
        throw new RuntimeException(entity);
    }
}
