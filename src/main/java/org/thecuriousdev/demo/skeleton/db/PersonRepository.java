package org.thecuriousdev.demo.skeleton.db;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.RawJsonDocument;
import com.couchbase.client.java.document.json.JsonArray;
import com.couchbase.client.java.query.N1qlQuery;
import com.couchbase.client.java.query.N1qlQueryRow;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.thecuriousdev.demo.skeleton.db.domain.Person;
import org.thecuriousdev.demo.skeleton.util.QueryResultChecker;
import org.thecuriousdev.demo.skeleton.util.Serializer;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.couchbase.client.java.query.Select.select;
import static com.couchbase.client.java.query.dsl.Expression.x;
import static com.couchbase.client.java.query.dsl.Expression.i;
import static com.couchbase.client.java.query.dsl.Expression.s;


@Repository
public class PersonRepository {

    private static final Logger LOG = LoggerFactory.getLogger(PersonRepository.class);

    private final Bucket bucket;
    private final Serializer<Person> serializer;
    private final QueryResultChecker queryResultChecker;

    private final String peopleWorkingAtCompanyQuery;

    @Autowired
    public PersonRepository(Bucket bucket, ObjectMapper objectMapper) {
        this.bucket = bucket;
        this.serializer = new Serializer<>(objectMapper, Person.class, bucket.name());
        this.queryResultChecker = new QueryResultChecker();

        peopleWorkingAtCompanyQuery = select("*, meta().id, meta().cas")
                .from(i(bucket.name()))
                .where(x("company").eq(x("$1"))
                        .and("type").eq(s(Person.DB_TYPE))).toString();
    }

    public Optional<Person> findById(String name) {
        Optional<JsonDocument> doc = Optional.ofNullable(bucket.get(getPersonDocumentId(name)));

        if (doc.isPresent()) {
            return serializer.deserialize(doc.get());
        }

        return Optional.empty();
    }

    public void save(Person person) {
        Optional<String> json = serializer.seralize(person);
        if (json.isPresent()) {
            bucket.upsert(RawJsonDocument.create(getPersonDocumentId(person.getName()), json.get(), person.getCas()));
            LOG.info("Saved person : {}", person);
        } else {
            LOG.warn("Failed to save user {}", person);
        }
    }

    public void delete(String name) {
        bucket.remove(name);
        LOG.info("Deleted person: {}", name);
    }

    public List<Person> findPeopleWorkingAtCompany(String company) {
        JsonArray params = JsonArray.create().add(company);
        N1qlQuery query = N1qlQuery.parameterized(peopleWorkingAtCompanyQuery, params);
        LOG.info(query.n1ql().toString());
        return queryResultChecker.processQuery(DbOperationType.N1QL_QUERY_GET_PERSONS_BY_COMPANY, () -> bucket.query(query))
                .stream()
                .map(N1qlQueryRow::value)
                .map(serializer::deserialize)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    private String getPersonDocumentId(String name) {
        return ":" + Person.DB_TYPE + ":" + name;
    }
}
