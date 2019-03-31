package resource;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.google.common.base.Optional;
import com.codahale.metrics.annotation.Timed;
import com.google.inject.Inject;
import io.dropwizard.hibernate.UnitOfWork;
import load.gen.mysql.MySqlLoadGenImpl;
import load.gen.rmq.RMQLoadGenImpl;
import load.gen.ThreadedLoadGenerator;
import saurabh.araiyer.Saying;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.concurrent.atomic.AtomicLong;

@Path("/gen-load")
@Produces(MediaType.APPLICATION_JSON)
public class LoadGeneratorResource {
    private final String template;
    private final String defaultName;
    private final AtomicLong counter;
    private RMQLoadGenImpl rmqLoadGen = new RMQLoadGenImpl();
    private MySqlLoadGenImpl mySqlLoadGen = new MySqlLoadGenImpl();

    @Inject
    public LoadGeneratorResource() {
        this.template = "Template";
        this.defaultName = "Default";
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

        rmqLoadGen.initialize(requestModel.transactional);

        for (int i = 0; i < requestModel.getThreads(); i++) {

            ThreadedLoadGenerator object = new ThreadedLoadGenerator("RMQCommand" , requestModel.getLoad(),
                    requestModel.getTimeInMilis());

            object.run(rmqLoadGen);

        }

        return Response.ok().build();
    }

    @POST
    @Timed
    @Path("/mysql")
    @UnitOfWork
    @ExceptionMetered
    public Response LoadMySQL(RequestModel requestModel) {

        mySqlLoadGen.initialize(requestModel.transactional);

        for (int i = 0; i < requestModel.getThreads(); i++) {

            ThreadedLoadGenerator object = new ThreadedLoadGenerator("MySQLCommand" , requestModel.getLoad(),
                    requestModel.getTimeInMilis());

            object.run(mySqlLoadGen);

        }

        return Response.ok().build();
    }
}