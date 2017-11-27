package org.thecuriousdev.demo.skeleton.util;

import com.couchbase.client.java.query.N1qlQueryResult;
import com.couchbase.client.java.query.N1qlQueryRow;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thecuriousdev.demo.skeleton.db.DbOperationType;

public class QueryResultChecker {

  private static final Logger LOG = LoggerFactory.getLogger(QueryResultChecker.class);

  public List<N1qlQueryRow> processQuery(DbOperationType type,
      final Supplier<N1qlQueryResult> supplier) {

    N1qlQueryResult result = supplier.get();

    if (LOG.isTraceEnabled() && result.info() != null) {
      LOG.trace("Metrics from {} contained {}", type, result.info());
    }

    if (result.errors() != null) {
      result.errors().forEach(e -> LOG.warn("Error during {}: {}", type, e));
      throw new RuntimeException("Error occurred during N1QL query");
    }

    return result.allRows() != null ? result.allRows() : Collections.emptyList();
  }
}
