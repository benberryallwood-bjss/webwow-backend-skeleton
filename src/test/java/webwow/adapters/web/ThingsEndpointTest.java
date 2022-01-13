package webwow.adapters.web;

import com.vtence.molecule.WebServer;
import com.vtence.molecule.testing.http.Form;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;

import static com.vtence.molecule.testing.http.HttpResponseAssert.assertThat;
import static java.net.http.HttpResponse.BodyHandlers.ofString;

public class ThingsEndpointTest {

    WebServer server = WebServer.create("127.0.0.1", 9999);

    HttpClient client = HttpClient.newHttpClient();
    HttpRequest.Builder request = HttpRequest.newBuilder(server.uri());

    @BeforeEach
    public void startServer() throws IOException {
        ThingsEndpoint rest = new ThingsEndpoint(server);
    }

    @AfterEach
    public void stopServer() throws IOException {
        server.stop();
    }

    @Test
    public void getAllTheThings() throws Exception {
        var response = client.send(request.copy().uri(server.uri().resolve("/things"))
                        .GET().build(),
                ofString());

        assertThat(response).isOK()
                .hasBody("[{\"name\":\"thing 1\",\"number\":101},{\"name\":\"thing 2\",\"number\":102}]");
    }

    @Disabled
    @Test
    public void managingAlbumResources() throws Exception {
        var response = client.send(request.copy().uri(server.uri().resolve("/things"))
                        .header("Content-Type", Form.urlEncoded().contentType())
                        .POST(Form.urlEncoded()
                                .addField("title", "My Favorite Things")
                                .addField("artist", "John Coltrane")).build(),
                ofString());

        assertThat(response).hasStatusCode(201);

        response = client.send(request.copy().uri(server.uri().resolve("/things/1"))
                        .GET().build(),
                ofString());
        assertThat(response).isOK()
                .hasBody("Title: My Favorite Things, Artist: John Coltrane");

        response = client.send(request.copy().uri(server.uri().resolve("/things"))
                        .header("Content-Type", Form.urlEncoded().contentType())
                        .POST(Form.urlEncoded()
                                .addField("title", "Blue Train")
                                .addField("artist", "John Coltrane")).build(),
                ofString());
        assertThat(response).hasStatusCode(201);

        response = client.send(request.copy().uri(server.uri().resolve("/things"))
                        .GET().build(),
                ofString());
        assertThat(response).isOK()
                .hasBody("1: Title: My Favorite Things, Artist: John Coltrane\n" +
                        "2: Title: Blue Train, Artist: John Coltrane\n");

        response = client.send(request.copy().uri(server.uri().resolve("/albums/2"))
                        .header("Content-Type", Form.urlEncoded().contentType())
                        .PUT(Form.urlEncoded()
                                .addField("title", "Kind of Blue")
                                .addField("artist", "Miles Davis")).build(),
                ofString());
        assertThat(response).isOK()
                .hasBody("Title: Kind of Blue, Artist: Miles Davis");

        response = client.send(request.copy().uri(server.uri().resolve("/things/1")).DELETE().build(), ofString());
        assertThat(response).isOK();

        response = client.send(request.copy().uri(server.uri().resolve("/things")).GET().build(), ofString());
        assertThat(response).isOK()
                .hasBody("2: Title: Kind of Blue, Artist: Miles Davis\n");
    }

    @Disabled
    @Test
    public void makingAPostActLikeAnUpdateOrDelete() throws Exception {
        var response = client.send(request.copy().uri(server.uri().resolve("/things"))
                        .header("Content-Type", Form.urlEncoded().contentType())
                        .POST(Form.urlEncoded()
                                .addField("title", "My Favorite Things")
                                .addField("artist", "John Coltrane")).build(),
                ofString());
        assertThat(response).hasStatusCode(201);

        response = client.send(request.copy()
                        .uri(server.uri().resolve("/things/1"))
                        .header("Content-Type", Form.urlEncoded().contentType())
                        .POST(Form.urlEncoded()
                                .addField("_method", "PUT")
                                .addField("title", "Kind of Blue")
                                .addField("artist", "Miles Davis")).build(),
                ofString());
        assertThat(response).isOK();

        response = client.send(request.copy().uri(server.uri().resolve("/things/1"))
                .GET().build(), ofString());
        assertThat(response).isOK()
                .hasBody("Title: Kind of Blue, Artist: Miles Davis");

        response = client.send(request.copy().uri(server.uri().resolve("/things/1"))
                        .header("Content-Type", Form.urlEncoded().contentType())
                        .POST(Form.urlEncoded()
                                .addField("_method", "DELETE")).build(),
                ofString());
        assertThat(response).isOK();

        response = client.send(request.copy().uri(server.uri().resolve("/things"))
                .GET().build(), ofString());
        assertThat(response).isOK()
                .hasBody("Your music library is empty");
    }

    @Disabled
    @Test
    public void askingForAMissingAlbum() throws Exception {
        var response = client.send(request.uri(server.uri().resolve("/things/9999")).build(), ofString());
        assertThat(response).hasStatusCode(404);
    }
}
