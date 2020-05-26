package srp;

import io.javalin.Javalin;
import srp.config.DBConnectionManager;
import srp.controllers.impl.CustomerControllerImpl;
import srp.controllers.impl.OrderControllerImpl;
import srp.repositories.impl.CustomerRepositoryImpl;
import srp.repositories.impl.OrderRepositoryImpl;

public class App {

    private final DBConnectionManager manager;
    private final CustomerControllerImpl customerController;
    private final OrderControllerImpl orderController;

    public App() {
        this.manager = new DBConnectionManager();
        CustomerRepositoryImpl customerRepositoryImpl = new CustomerRepositoryImpl(this.manager.getDatabase());
        this.customerController = new CustomerControllerImpl(customerRepositoryImpl);

        OrderRepositoryImpl orderRepositoryImpl = new OrderRepositoryImpl(this.manager.getDatabase());
        this.orderController = new OrderControllerImpl(orderRepositoryImpl);
    }

    public static void main(String[] args) {
        new App().startup();
    }

    public void startup() {
        Javalin server = Javalin.create().start(7000);

        server.get("api/customer/:id", this.customerController::find);
        server.delete("api/customer/:id", this.customerController::delete);
        server.get("api/customers", this.customerController::findAll);
        server.post("api/customer", this.customerController::create);

        server.post("api/order", this.orderController::create);
        server.get("api/order/:id", this.orderController::find);
        server.get("api/orders", this.orderController::findAll);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            this.manager.closeDatabase();
            server.stop();
        }));

    }
}