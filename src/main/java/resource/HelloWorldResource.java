package resource;

import com.google.common.base.Optional;
import com.codahale.metrics.annotation.Timed;
import load.gen.RMQLoadGenImpl;
import load.gen.ThreadedLoadGenerator;
import saurabh.araiyer.Saying;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.concurrent.atomic.AtomicLong;

@Path("/gen-load")
@Produces(MediaType.APPLICATION_JSON)
public class HelloWorldResource {
    private final String template;
    private final String defaultName;
    private final AtomicLong counter;
    private RMQLoadGenImpl rmqLoadGen = new RMQLoadGenImpl();

    public HelloWorldResource(String template, String defaultName) {
        this.template = template;
        this.defaultName = defaultName;
        this.counter = new AtomicLong();
    }

    @GET
    @Timed
    public Saying sayHello(@QueryParam("name") Optional<String> name) {
        final String value = String.format(template, name.or(defaultName));
        return new Saying(counter.incrementAndGet(), value);
    }

    @POST
    @Timed
    @Path("/rmq")
    public Response LoadRMQ(RequestModel requestModel) {

        rmqLoadGen.initialize(true);

        for (int i = 0; i < requestModel.getThreads(); i++) {

            ThreadedLoadGenerator object = new ThreadedLoadGenerator("RMQCommand" , requestModel.getLoad(), requestModel.getTimeInMilis());

            object.run(rmqLoadGen);

        }

        return Response.ok().build();
    }
}