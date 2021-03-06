package com.contentstack.test;
import com.contentstack.sdk.*;
import com.contentstack.sdk.Error;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;


public class SyncTestCase {

    final Logger logger = LogManager.getLogger(SyncTestCase.class.getName());

    private final Stack stack;
    private int itemsSize = 0;
    private int counter = 0;
    private String dateISO = null;
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public SyncTestCase() throws Exception {
        String API_KEY = "blt477ba55f9a67bcdf";
        String DELIVERY_TOKEN = "cs7731f03a2feef7713546fde5";
        String ENVIRONMENT = "web";
        stack = Contentstack.stack(API_KEY, DELIVERY_TOKEN, ENVIRONMENT);
    }


    @Test
    public void testSyncInit() {

        stack.sync(new SyncResultCallBack() {
            @Override
            public void onCompletion(SyncStack syncStack, Error error) {

                if (error == null) {
                    itemsSize = syncStack.getItems().size();
                    counter = syncStack.getCount();
                    logger.info("sync stack size  :" + syncStack.getItems().size());
                    logger.info("sync stack count  :" + syncStack.getCount());
                    syncStack.getItems().forEach(item -> logger.info(item.toString()));

                    assertEquals(counter, syncStack.getCount());
                }
            }
        });
    }


    @Test
    public void testSyncToken() {
        stack.syncToken("bltbb61f31a70a572e6c9506a", new SyncResultCallBack() {
            @Override
            public void onCompletion(SyncStack syncStack, Error error) {
                if (error == null) {
                    itemsSize = syncStack.getItems().size();
                    counter = syncStack.getCount();
                    assertEquals(itemsSize, syncStack.getItems().size());
                }
            }
        });


    }

    @Test
    public void testPaginationToken() {
        stack.syncPaginationToken("blt7f35951d259183fba680e1", new SyncResultCallBack() {
            @Override
            public void onCompletion(SyncStack syncStack, Error error) {

                if (error == null) {
                    itemsSize += syncStack.getItems().size();
                    counter = syncStack.getCount();
                    logger.info("sync pagination size  :" + syncStack.getItems().size());
                    logger.info("sync pagination count  :" + syncStack.getCount());
                    syncStack.getItems().forEach(item -> logger.info("Pagination" + item.toString()));
                    //assertEquals( itemsSize, itemsSize);
                }
            }
        });
    }


    @Test
    public void testSyncWithDate() throws ParseException {

        final Date start_date = sdf.parse("2018-10-07");
        stack.syncFromDate(start_date, new SyncResultCallBack() {
            @Override
            public void onCompletion(SyncStack syncStack, Error error) {
                if (error == null) {

                    itemsSize = syncStack.getItems().size();
                    counter = syncStack.getCount();
                    for (JSONObject jsonObject1 : syncStack.getItems()) {
                        if (jsonObject1.has("event_at")) {

                            dateISO = jsonObject1.optString("event_at");
                            logger.info("date iso -->" + dateISO);
                            String serverDate = returnDateFromISOString(dateISO);
                            Date dateServer = null;
                            try {
                                dateServer = sdf.parse(serverDate);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            logger.info("dateServer -->" + dateServer);
                            assert dateServer != null;
                            int caparator = dateServer.compareTo(start_date);
                            assertEquals(1, caparator);
                        }
                    }

                    assertEquals(itemsSize, syncStack.getItems().size());
                }


            }
        });
    }


    @Test
    public void testSyncWithContentType() {
        stack.syncContentType("session", new SyncResultCallBack() {
            @Override
            public void onCompletion(SyncStack syncStack, Error error) {
                if (error == null) {
                    itemsSize += syncStack.getItems().size();
                    counter = syncStack.getCount();
                    logger.info("sync content type size  :" + syncStack.getItems().size());
                    logger.info("sync content type count  :" + syncStack.getCount());
                    syncStack.getItems().forEach(item -> logger.info("content type: " + item.toString()));
                    //assertEquals(100, itemsSize);
                }
            }
        });


    }


    @Test
    public void testSyncWithLocale() {

        stack.syncLocale(Language.ENGLISH_UNITED_STATES, new SyncResultCallBack() {
            @Override
            public void onCompletion(SyncStack syncStack, Error error) {

                if (error == null) {
                    counter = syncStack.getCount();
                    ArrayList<JSONObject> items = syncStack.getItems();
                    String dataObject = null;
                    for (JSONObject object : items) {
                        if (object.has("data"))
                            dataObject = object.optJSONObject("data").optString("locale");
                        assert dataObject != null;
                        logger.info("locale dataObject: --> " + dataObject);

                        if (!dataObject.isEmpty()) {
                            logger.info("locale dataObject: --> " + dataObject);
                            assertEquals("en-us", dataObject);
                        }
                    }

                    logger.info("sync stack size  :" + syncStack.getItems().size());
                    logger.info("sync stack count  :" + syncStack.getCount());
                    syncStack.getItems().forEach(item -> logger.info(item.toString()));
                }
            }
        });

    }


    @Test
    public void testPublishType() {

        stack.syncPublishType(Stack.PublishType.entry_published, new SyncResultCallBack() {
            @Override
            public void onCompletion(SyncStack syncStack, Error error) {
                if (error == null) {
                    itemsSize = syncStack.getItems().size();
                    counter = syncStack.getCount();

                    logger.info("publish type==>" + counter);
                    syncStack.getItems().forEach(items -> logger.info("publish type" + items.toString()));

                    assertEquals(itemsSize, syncStack.getItems().size());
                } else {
                    // Error block
                    logger.info("publish type error !");
                }

            }
        });

    }


    @Test
    public void testSyncWithAll() throws ParseException {

        Date start_date = sdf.parse("2018-10-10");
        stack.sync("session", start_date, Language.ENGLISH_UNITED_STATES, Stack.PublishType.entry_published, new SyncResultCallBack() {
            @Override
            public void onCompletion(SyncStack syncStack, Error error) {

                if (error == null) {
                    itemsSize = syncStack.getItems().size();
                    counter = syncStack.getCount();
                    logger.info("stack with all type==>" + counter);
                    syncStack.getItems().forEach(items -> logger.info("sync with all type: " + items.toString()));
                    assertEquals(itemsSize, syncStack.getItems().size());
                }

            }

        });
    }


    @Test
    public void test_get_all_stack_content_types() throws JSONException {
        Stack where_stack = null;
        try {
            where_stack = Contentstack.stack("blt20962a819b57e233", "blt01638c90cc28fb6f", "production");
        } catch (Exception e) {
            e.printStackTrace();
        }

        JSONObject params = new JSONObject();
        params.put("include_snippet_schema", true);
        params.put("limit", 3);

        assert where_stack != null;
        where_stack.getContentTypes(params, new ContentTypesCallback() {
            @Override
            public void onCompletion(ContentTypesModel contentTypesModel, Error error) {
                if (error == null) {
                    logger.debug(contentTypesModel.getResponse().toString());
                }
            }
        });

    }


    @Test
    public void getSingleContentType() throws JSONException {

        Stack where_stack = null;
        try {
            where_stack = Contentstack.stack("blt20962a819b57e233", "blt01638c90cc28fb6f", "production");
        } catch (Exception e) {
            e.printStackTrace();
        }
        assert where_stack != null;
        ContentType contentType = where_stack.contentType("product");
        JSONObject params = new JSONObject();
        params.put("include_snippet_schema", true);
        params.put("limit", 3);
        contentType.fetch(params, new ContentTypesCallback() {
            @Override
            public void onCompletion(ContentTypesModel contentTypesModel, Error error) {
                if (error == null) {
                    logger.info("single content:" + contentTypesModel.getResponse());
                } else {
                    logger.info("Error" + error.getErrorMessage());
                }
            }
        });
    }


    private String returnDateFromISOString(String isoDateString) {
        String[] dateFormat = isoDateString.split("T");
        return dateFormat[0];
    }


    @Test
    public void testShannonQuery() throws Exception {
        Config config = new Config();
        config.setHost("cdn.blz-contentstack.com");
        Stack stack = Contentstack.stack("blt286175c11a6b3f4c", "cs75fa0fd3991b7d602a77d6d2", "qa--legacy--all", config);
        Entry entry = stack.contentType("blog").entry("bltba9f49b1e123ec4f");
        entry.fetch(new EntryResultCallBack() {
            @Override
            public void onCompletion(ResponseType responseType, Error error) {
                logger.info(error);
                //entry.toJSON().getString("content")
                logger.info(entry.toJSON());
            }
        });
    }


    @Test
    public void testJavaExampleApp() {

        try {
            final Stack stack = Contentstack.stack("blt7979d15c28261b93", "cs17465ae5683299db9d259cb6", "production");
            ContentType contentType = stack.contentType("news");
            Query query = contentType.query();
            query.find(new QueryResultsCallBack() {
                @Override
                public void onCompletion(ResponseType responseType, QueryResult queryresult, Error error) {
                    if (error == null) {
                        List<Entry> result = queryresult.getResultObjects();
                        for (Entry entry : result){
                            System.out.println("Entry Title: " + entry.toJSON().getString("title"));
                        }
                    }
                }
            });

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }


}
