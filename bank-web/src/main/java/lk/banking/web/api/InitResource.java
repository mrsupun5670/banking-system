package lk.banking.web.api;

import jakarta.ejb.EJB;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import lk.banking.core.service.InitService;

@Path("/init")
public class InitResource {

    @EJB
    private InitService initBean;

    @GET
    public Response initialize() {
        initBean.initDatabase();
        return Response.ok("Database initialized").build();
    }
}
