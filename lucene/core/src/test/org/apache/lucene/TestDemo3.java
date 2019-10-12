/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.lucene;/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.IOUtils;
import org.apache.lucene.util.LuceneTestCase;

/**
 * A very simple demo used in the API documentation (src/java/overview.html).
 *
 * Please try to keep src/java/overview.html up-to-date when making changes
 * to this class.
 */
public class TestDemo3 extends LuceneTestCase {

  public void testDemo() throws IOException {
    Path indexPath = Files.createTempDirectory("tempIndex");
    try (Directory dir = FSDirectory.open(indexPath)) {
      Analyzer analyzer = new StandardAnalyzer();
      try(IndexWriter iw = new IndexWriter(dir, new IndexWriterConfig(analyzer))) {

        Document doc1 = new Document();
        doc1.add(newTextField("fieldname", "dog cat", Field.Store.YES));
        iw.addDocument(doc1);
        Document doc2 = new Document();
        doc2.add(newTextField("fieldname", "dog eate food", Field.Store.YES));
        iw.addDocument(doc2);
        Document doc3 = new Document();
        doc3.add(newTextField("fieldname", "dog eat cat", Field.Store.YES));
        iw.addDocument(doc3);
        Document doc4 = new Document();
        doc4.add(newTextField("fieldname", "dog eat food", Field.Store.YES));
        iw.addDocument(doc4);

      }catch(Exception e){

      }


      // Now search the index.
      try (IndexReader reader = DirectoryReader.open(dir)) {
        IndexSearcher searcher = newSearcher(reader);

        BooleanQuery.Builder bq1 = new BooleanQuery.Builder();
        bq1.add(new TermQuery(new Term("fieldname", "dog")), BooleanClause.Occur.MUST);
        bq1.add(new TermQuery(new Term("fieldname", "cat")), BooleanClause.Occur.MUST);
//        bq1.add(new TermQuery(new Term("fieldname", "eate")), BooleanClause.Occur.MUST);

        Query query = bq1.build();
        TopDocs hits = searcher.search(query, 10);

        for (int i = 0; i < hits.scoreDocs.length; i++) {
          Document hitDoc = searcher.doc(hits.scoreDocs[i].doc);
          System.out.println( hitDoc.get("fieldname"));
        }
      }
    }

    IOUtils.rm(indexPath);
  }
}
