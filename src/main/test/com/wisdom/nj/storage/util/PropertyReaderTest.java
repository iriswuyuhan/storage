package com.wisdom.nj.storage.util;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertyReaderTest {
    private static final Logger logger = LoggerFactory.getLogger(PropertyReaderTest.class);
    private PropertyReader pr;

    @Before
    public void setUp() throws Exception {
        pr=PropertyReader.getInstance();
    }

    @Test
    public void getProperty() throws Exception {
        logger.info("by wuyuhan");
        Assert.assertEquals("E:\\",pr.getProperty("root_path"));
    }


}