package srp;

import io.javalin.Javalin;
import srp.config.DBConnectionManager;
import srp.controllers.impl.CustomerControllerImpl;
import srp.repositories.impl.CustomerRepositoryImpl;

public class App {

    private final DBConnectionManager manager;
    private final CustomerControllerImpl customerController;

    public App() {
        this.manager = new DBConnectionManager();
        CustomerRepositoryImpl customerRepositoryImpl = new CustomerRepositoryImpl(this.manager.getDatabase());
        this.customerController = new CustomerControllerImpl(customerRepositoryImpl);
    }

    public static void main(String[] args) {
        new App().startup();
    }

    public void startup() {
        Javalin server = Javalin.create().start(7000);

        server.get("/", ctx -> ctx.result("Hello World"));

        // Get specific customer
        server.get("api/customer/:id", this.customerController::find);

        server.delete("api/customer/:id", this.customerController::delete);

        // List users, filtered using query parameters
        server.get("api/customers", this.customerController::findAll);

        // Add new user
        server.post("api/customer", this.customerController::create);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            this.manager.closeDatabase();
            server.stop();
        }));

    }
}