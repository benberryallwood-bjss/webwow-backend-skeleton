package webwow.adapters.web;

import com.google.gson.Gson;
import com.vtence.molecule.Request;
import com.vtence.molecule.Response;
import com.vtence.molecule.WebServer;
import com.vtence.molecule.http.HttpStatus;
import com.vtence.molecule.routing.Routes;

import java.io.IOException;
import java.util.List;

import static com.vtence.molecule.http.HttpStatus.*;

/**
 * This endpoint class belongs to the web adapter layer.
 *
 * It's responsibility is to know how to handle the web:
 * * How to decode an HTTP request, extract data from it and pass it on to the domain model
 * * How to encode the domain model as HTTP responses in a JSON format
 *
 * This layer does not have any user story logic in it. It only hides web knowledge.
 */
public class ThingsEndpoint {

    private static final String CONTENT_TYPE_JSON = "application/json";
    private WebServer webServer;

    public ThingsEndpoint() {
        this(WebServer.create());
    }

    ThingsEndpoint(WebServer server) {
        this.webServer = server;

        try {
            run();
        }
        catch(IOException ioe) {
            throw new ThingsEndpointException(ioe);
        }
    }

    public String getUri() {
        return webServer.uri() + "/things";
    }

    private void run() throws IOException {
        webServer.route((new Routes() {{
            get("/things").to(request -> fetchAllThings(request));

            get("/things/:id").to(request -> fetchThingById(request));

            post("/things").to(request -> addThing(request));

            delete("/things/:id").to(request -> deleteThing(request));
        }}));
    }

    private Response deleteThing(Request request) {
        int id = Integer.parseInt(request.parameter("id"));

        // NOTE: get some object to delete this thing
        System.out.println("DELETE called with id " + id);

        return Response.of(NO_CONTENT).done();
    }

    private Response fetchThingById(Request request) {
        int id = Integer.parseInt(request.parameter("id"));

        ThingModel model = new ThingModel( "a thing name", id );
        String jsonResponse = new Gson().toJson(model);

        return Response.ok()
                .contentType(CONTENT_TYPE_JSON)
                .done(jsonResponse);
    }

    private Response addThing(Request request) {
        try {
            var thingModel = new Gson().fromJson(request.body(), ThingModel.class);

            // NOTE: we normally call other classes to do something with our request
            System.out.println("Request to add new thing received: " + thingModel.name + ", " + thingModel.number);

            // NOTE: We often return some info to help the client know where to find the new 'thing'
            return Response.of(CREATED);

        } catch (IOException | NullPointerException e) {
            throw new ThingsEndpointException(e);
        }
    }

    private Response fetchAllThings(Request request) {
        var allModels = List.of(
                new ThingModel("thing 1", 101),
                new ThingModel("thing 2", 102));

        String jsonResponse = new Gson().toJson(allModels);
        return Response.ok()
                .contentType(CONTENT_TYPE_JSON)
                .done(jsonResponse);
    }
}