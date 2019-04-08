package load.gen.mysql;

import client.ClientUtil;
import client.RequestUtils;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import load.gen.LoadGenI;

import javax.ws.rs.core.MediaType;

public class MySqlLoadGenImpl implements LoadGenI {

    private Boolean versioned;
    private static Client client;
    private static boolean initialized;

    @Override
    public void initialize(boolean versioned) {
        this.versioned = versioned;
        if (!initialized) {
            client = ClientUtil.buildClient();
            initialized = true;
        }
    }

    @Override
    public void run() {

        ClientResponse response = null;

        try {
            response = client.resource("http://localhost:1730/test/gen-load/mysql_internal")
                    .queryParam("transactional", versioned.toString())
                    .accept(MediaType.APPLICATION_JSON_TYPE)
                    .get(ClientResponse.class);
            RequestUtils.handleErrorDefault(response, "Mysql");
        } catch (Exception ignored) {

        }
    }
}
