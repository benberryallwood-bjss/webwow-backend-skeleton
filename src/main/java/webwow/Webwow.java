package webwow;

import webwow.adapters.web.ThingsEndpoint;
import webwow.adapters.web.ThingsEndpointException;

public class Webwow {
    public static void main(String[] commandLineArguments) {
        new Webwow().run();
    }

    private void run() {
        try {
            var thingsEndpoint = new ThingsEndpoint();
            System.out.println("Access at: " + thingsEndpoint.getUri());
        }
        catch(ThingsEndpointException tee) {
            tee.printStackTrace();
        }
    }

}
