package resource;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.google.inject.Inject;
import command.MySQLCommand;
import io.dropwizard.hibernate.UnitOfWork;
import load.gen.ThreadedLoadGenerator;
import load.gen.mysql.MySqlLoadGenImpl;
import load.gen.rmq.RMQLoadGenImpl;
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
    public Saying sayHello(@QueryParam("name") String name) {
        final String value = String.format(template, name);
        return new Saying(counter.incrementAndGet(), value);
    }

    @POST
    @Timed
    @Path("/rmq")
    public Response LoadRMQ(RequestModel requestModel) {
        Long initTime = System.currentTimeMillis();
        rmqLoadGen.initialize(requestModel.transactional);

        for (int i = 0; i < requestModel.getThreads(); i++) {

            ThreadedLoadGenerator object = new ThreadedLoadGenerator("RMQCommand" , requestModel.getLoad(),
                    requestModel.getTimeInMilis());

            object.run(rmqLoadGen);

        }
        Long finalTime = System.currentTimeMillis();

        System.out.println(initTime-finalTime);

        return Response.ok(initTime-finalTime).build();
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

    @GET
    @Timed
    @UnitOfWork
    @ExceptionMetered
    @Path("/mysql_internal")
    public Response internalHitMySQL (@QueryParam("transactional") boolean transactional) {
        mySqlLoadGen.initialize(transactional);
        MySQLCommand mySQLCommand = new MySQLCommand(transactional);

        mySQLCommand.run();

        return Response.ok().build();
    }
}