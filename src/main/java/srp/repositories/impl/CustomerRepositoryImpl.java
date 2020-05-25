package srp.repositories.impl;
import srp.models.Customer;
import srp.repositories.CustomerRepository;

import java.util.List;
import java.util.LinkedList;


import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.FindOneAndReplaceOptions;
import com.mongodb.client.model.ReturnDocument;
import static com.mongodb.client.model.Filters.eq;

import org.bson.Document;
import org.bson.types.ObjectId;

public class CustomerRepositoryImpl implements CustomerRepository {

    private static final String COLLECTION_NAME = "customers";
    private static final FindOneAndReplaceOptions UPDATE_OPTIONS = new FindOneAndReplaceOptions()
            .returnDocument(ReturnDocument.AFTER);

    private final MongoCollection<Customer> customers;

    public CustomerRepositoryImpl(MongoDatabase database) {
        this.customers = database.getCollection(COLLECTION_NAME, Customer.class);
    }

    @Override
    public void create(Customer customer) {
        customer.setId(new ObjectId());
        customers.insertOne(customer);
    }

    @Override
    public void delete(String id) {
        customers.deleteOne(new Document("_id", new ObjectId(id)));
    }

    @Override
    public Customer find(String id) {
        return customers.find(eq("_id", new ObjectId(id))).first();
    }

    @Override
    public List<Customer> findAll() {
        List<Customer> result = new LinkedList<>();

        for (Customer customer : customers.find()) {
            result.add(customer);
        }

        return result;
    }

    @Override
    public Customer update(Customer post, String id) {
        return customers.findOneAndReplace(new Document("_id", new ObjectId(id)), post, UPDATE_OPTIONS);
    }
    
}